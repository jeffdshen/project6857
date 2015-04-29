package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import io.github.jeffdshen.project6857.core.board.InitBoard;
import io.github.jeffdshen.project6857.core.board.Piece;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameMain implements ApplicationListener {
	Image tile;
    Image coin;
    DragAndDrop dragDrop;
    Stage stage;
    Map<Piece, Integer> defaultPieces;
    InitBoard initBoard;

    private static final Map<Rank,String> rankMap = Collections.unmodifiableMap(
            new HashMap<Rank, String>() {{
                put(Rank.ONE, "one.png");
                put(Rank.TWO, "two.png");
                put(Rank.THREE, "three.png");
                put(Rank.FOUR, "four.png");
                put(Rank.FIVE, "five.png");
                put(Rank.BOMB, "bomb.png");
                put(Rank.FLAG, "flag.png");
            }}
    );
    private static final Map<PieceType,Color> typeMap = Collections.unmodifiableMap(
            new HashMap<PieceType,Color>() {{
                put(PieceType.ROCK, new Color(0.515f, 0.691f, 1f, 1f));
                put(PieceType.PAPER, new Color(0.578f, 0.906f, 0.441f, 1f));
                put(PieceType.SCISSORS, new Color(0.906f, 0.355f, 0.26f, 1f));
                put(PieceType.FLAG, new Color(0.969f, 0.953f, 1f, 1f));
                put(PieceType.UNKNOWN, Color.GRAY);
            }}
    );
    public static Map<Source, Piece> sourceMap = new HashMap<>();

    final int boardWidth = 10;
    final int boardHeight = 10;
    final int playerHeight = 4;
    final int tileSize = 80;
    final int borderSize = 2;

	@Override
	public void create () {
        System.out.println((Color.GREEN).a);
        dragDrop = new DragAndDrop();

        stage = new Stage();
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

                    public void reset(Source source, Payload payload) {
                    }

                    public void drop(Source source, Payload payload, float x, float y, int pointer) {
                        Actor currentTile = getActor();
                        Piece piece = sourceMap.get(source);
                        int xPos = (int) currentTile.getX();
                        int yPos = (int) currentTile.getY();

                        if (initBoard.setPiece(xPos/tileSize, yPos/tileSize, piece)) {
                            source.getActor().setPosition(xPos + borderSize, yPos + borderSize);
                        }
                    }
                });

            }
        }

        // get pieces and initboard
        defaultPieces = InitBoard.getDefaultPieces();
        initBoard = new InitBoard(boardWidth, boardHeight, playerHeight, defaultPieces);

        // create pieces and set them on stage
        int x = 11;
        int y = 0;
        for (Map.Entry<Piece,Integer> entry: initBoard.getRemainingPieces().entrySet()) {
            final Piece piece = entry.getKey();
            int occurrence = entry.getValue();
            for (int q = 0; q < occurrence; q++) {
                coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                coin.setColor(typeMap.get(piece.getType()));
                coin.setBounds((80*x)+2, (80*y)+2, 80-4, 80-4);
                stage.addActor(coin);

                final Piece currentPiece = piece;
                final Source source = new Source(coin) {

                    public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        Payload payload = new Payload();
                        Actor currentCoin = getActor();
                        currentCoin.setVisible(false);

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
                sourceMap.put(source, currentPiece);
                dragDrop.addSource(source);

                // TODO: this is terrible please kill it
                if (x == 14) {
                    y += 1;
                    x = 11;
                } else {
                    x += 1;
                }
            }
        }

        System.out.println(sourceMap.size());

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
	}

    @Override
    public void resize (int width, int height) {
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
