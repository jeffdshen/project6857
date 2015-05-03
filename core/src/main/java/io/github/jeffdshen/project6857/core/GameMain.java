package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.jeffdshen.project6857.core.board.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameMain implements ApplicationListener {
	Image tile;
    BitmapFont font;
    SpriteBatch batch;
    DragAndDrop dragDrop;
    Stage stage;
    Map<Piece, Integer> defaultPieces;
    InitBoard initBoard;
    TextButton button;
    TextButton.TextButtonStyle buttonStyle;

    private static final Map<Rank,String> rankMap = Collections.unmodifiableMap(
            new HashMap<Rank, String>() {{
                put(Rank.ONE, "one.png");
                put(Rank.TWO, "two.png");
                put(Rank.THREE, "three.png");
                put(Rank.FOUR, "four.png");
                put(Rank.FIVE, "five.png");
                put(Rank.BOMB, "bomb.png");
                put(Rank.FLAG, "flag.png");
                put(Rank.UNKNOWN, "enemy.png");
            }}
    );
    private static final Map<PieceType,Color> typeMap = Collections.unmodifiableMap(
            new HashMap<PieceType,Color>() {{
                put(PieceType.ROCK, new Color(0.515f, 0.691f, 1f, 1f));
                put(PieceType.PAPER, new Color(0.578f, 0.906f, 0.441f, 1f));
                put(PieceType.SCISSORS, new Color(0.906f, 0.355f, 0.26f, 1f));
                put(PieceType.FLAG, new Color(0.969f, 0.953f, 1f, 1f));
                put(PieceType.UNKNOWN, new Color(0.027f, 0.223f, 0.398f, 1f));
            }}
    );
    public static Map<Source, Piece> sourceMap = new HashMap<>();

    final int boardWidth = 10;
    final int boardHeight = 10;
    final int playerHeight = 4;
    final int tileSize = 80;
    final int borderSize = 2;
    final int stageWidth = 1200;
    final int stageHeight = 800;

    boolean boardSet = false;

	@Override
	public void create () {
        dragDrop = new DragAndDrop();
        batch = new SpriteBatch();

        stage = new Stage(new FitViewport(stageWidth, stageHeight));
        Gdx.input.setInputProcessor(stage);

        // create tiled board
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                tile = new Image(new Texture(Gdx.files.internal("tile.png")));
                tile.setBounds(tileSize * x, tileSize * y, tileSize, tileSize);
                stage.addActor(tile);

                dragDrop.addTarget(new Target(tile) {
                    public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                        return true;
                    }

                    public void reset(Source source, Payload payload) {}

                    public void drop(Source source, Payload payload, float x, float y, int pointer) {
                        Actor currentTile = getActor();
                        Piece piece = sourceMap.get(source);
                        int xPos = (int) currentTile.getX();
                        int yPos = (int) currentTile.getY();

                        if (initBoard.setPiece(xPos/tileSize, yPos/tileSize, piece)) {
                            source.getActor().setPosition(xPos + borderSize, yPos + borderSize);
                        }

                        if (initBoard.noRemainingPieces()) {
                            boardSet = true;
                            System.out.println("board is set");
                        }
                    }
                });

            }
        }

        // get pieces and initboard
        defaultPieces = InitBoard.getDefaultPieces();
        initBoard = new InitBoard(boardWidth, boardHeight, playerHeight, defaultPieces);

        // create enemy pieces and place them on board
        for (int xpos = 0; xpos < boardWidth; xpos++){
            for (int ypos = 0; ypos < boardHeight; ypos++) {
                Piece piece = initBoard.getPiece(xpos, ypos);
                if (piece != null && piece.getRank() == Rank.UNKNOWN) {
                        Image coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                        coin.setColor(typeMap.get(piece.getType()));
                        int coinSize = tileSize - (2*borderSize);
                        coin.setBounds((tileSize*xpos)+borderSize, (tileSize*ypos)+borderSize, coinSize, coinSize);
                        stage.addActor(coin);
                }
            }
        }

        // create sidebar for remaining (not placed) player pieces
        Actor sidebar = new Actor();
        sidebar.setBounds(stageWidth - (4*tileSize), 0, (4*tileSize), stageHeight);
        stage.addActor(sidebar);
        dragDrop.addTarget(new Target(sidebar) {
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {return true;}

            public void reset(Source source, Payload payload) {}

            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                int xStage = (int) (x + (getActor().getX()));
                int yStage = (int) y;
                int xTile = xStage%tileSize;
                int yTile = yStage%tileSize;
                // only allow drop if drop doesn't happen on tile borders
                if (xTile >= borderSize && xTile < (tileSize-borderSize)
                        && yTile >= borderSize && yTile < (tileSize - borderSize)) {
                    xStage = xStage - xTile + borderSize;
                    yStage = yStage - yTile + borderSize;
                    source.getActor().setPosition(xStage, yStage);
                }
            }
        });

        // create player pieces and place them in sidebar
        int x = (int) (sidebar.getX());
        int y = 0;
        for (Map.Entry<Piece,Integer> entry: initBoard.getRemainingPieces().entrySet()) {
            final Piece piece = entry.getKey();
            int occurrence = entry.getValue();
            for (int q = 0; q < occurrence; q++) {
                Image coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                coin.setColor(typeMap.get(piece.getType()));
                int coinSize = tileSize - (2*borderSize);
                coin.setBounds(x+borderSize, y+borderSize, coinSize, coinSize);
                stage.addActor(coin);

                final Source source = new Source(coin) {
                    public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        Payload payload = new Payload();
                        Actor currentCoin = getActor();
                        currentCoin.setVisible(false);

                        float xpos = currentCoin.getX() - borderSize;
                        float ypos = currentCoin.getY() - borderSize;

                        if (currentCoin.getX() < boardWidth*tileSize) {
                            initBoard.removePiece((int) (xpos / tileSize), (int) (ypos / tileSize));
                        }

                        Image dragCoin = new Image(((Image) currentCoin).getDrawable());
                        dragCoin.setColor(currentCoin.getColor());
                        dragCoin.setSize(currentCoin.getHeight(), currentCoin.getWidth());

                        payload.setDragActor(dragCoin);
                        dragDrop.setDragActorPosition(-(dragCoin.getWidth() / 2), dragCoin.getHeight() / 2);
                        return payload;
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                        this.getActor().setVisible(true);
                    }
                };
                sourceMap.put(source, piece);
                dragDrop.addSource(source);

                // change x and y to place next coin
                x += tileSize;
                if (x >= stageWidth) {
                    y += tileSize;
                    x = (int) (sidebar.getX());
                }
            }
        }

        //printBoard();

	}

    private void printBoard() {
        Piece[][] pBoard = initBoard.getBoard();
        for (Piece[] row: pBoard) {
            for (Piece piece: row) {
                if (piece == null) {
                    System.out.print("null null | ");
                } else {
                    System.out.print(piece.getRank() + " " + piece.getType() + " | ");
                }
            }
            System.out.println();
        }
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        /*batch.begin();
        font.setScale(2);
        font.draw(batch, "HELLO", 1000, 400);
        batch.end();*/
	}

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
