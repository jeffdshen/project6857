package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.jeffdshen.project6857.core.board.Board;
import io.github.jeffdshen.project6857.core.board.Piece;
import io.github.jeffdshen.project6857.core.board.PieceType;
import io.github.jeffdshen.project6857.core.board.Rank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shruthi on 5/10/2015.
 */
public class PlayScreen implements Screen{
    private GameMain game;

    // in pixels
    private int stageWidth;
    private int stageHeight;
    private int tileSize;
    private int borderSize;
    // in units
    private int boardWidth;
    private int boardHeight;
    private int playerHeight;

    BitmapFont font;
    DragAndDrop dragDrop;
    Stage stage;
    Actor sidebar;
    TextButton.TextButtonStyle buttonStyle;

    Board board;

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

    public PlayScreen(GameMain game, int stageWidth, int stageHeight, int tileSize, int borderSize, int boardWidth, int boardHeight, int playerHeight, Board board){
        // keep reference to game
        this.game = game;

        //set board
        this.board = board;

        // set boundary variables
        this.stageWidth = stageWidth;
        this.stageHeight = stageHeight;
        this.tileSize = tileSize;
        this.borderSize = borderSize;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.playerHeight = playerHeight;

        dragDrop = new DragAndDrop();
        font = new BitmapFont();
        font.setScale(2);

        stage = new Stage(new FitViewport(stageWidth, stageHeight));
        Gdx.input.setInputProcessor(stage);

        // create tiled board
        tileStage();

        // create sidebar
        sidebar = new Actor();
        sidebar.setBounds(stageWidth - (4 * tileSize), 0, (4 * tileSize), stageHeight);
        stage.addActor(sidebar);

        // create topbar
        Pixmap pixmap = new Pixmap(stageWidth, stageHeight - (boardHeight*tileSize), Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.015f, 0.217f, 0.225f, 1f)); //4, 55, 58
        pixmap.fill();
        Image topbar = new Image(new Texture(pixmap));
        topbar.setBounds(0, boardHeight * tileSize, stageWidth, stageHeight - (boardHeight*tileSize));
        pixmap.dispose();
        stage.addActor(topbar);

        // fill topbar with text and buttons
        //createButtons();

        placePieces();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void tileStage() {
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Image tile = new Image(new Texture(Gdx.files.internal("tile.png")));
                tile.setBounds(tileSize * x, tileSize * y, tileSize, tileSize);
                stage.addActor(tile);

                dragDrop.addTarget(new DragAndDrop.Target(tile) {
                    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        return true;
                    }

                    public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    }

                    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        /*Actor currentTile = getActor();
                        Piece piece = sourceMap.get(source);
                        int xPos = (int) currentTile.getX();
                        int yPos = (int) currentTile.getY();

                        if (initBoard.setPiece(xPos/tileSize, yPos/tileSize, piece)) {
                            source.getActor().setPosition(xPos + borderSize, yPos + borderSize);
                        }

                        if (initBoard.noRemainingPieces()) {
                            startGameButton.setDisabled(false);
                            randomBoardButton.setDisabled(true);
                        }*/
                    }
                });

            }
        }
    }

    private void placePieces() {
        for (int xpos = 0; xpos < boardWidth; xpos++) {
            for (int ypos = 0; ypos < boardHeight; ypos++) {
                Piece piece = board.getPiece(xpos, ypos);
                if (piece != null) {
                    Image coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                    coin.setColor(typeMap.get(piece.getType()));
                    int coinSize = tileSize - (2 * borderSize);
                    coin.setBounds((tileSize * xpos) + borderSize, (tileSize * ypos) + borderSize, coinSize, coinSize);
                    stage.addActor(coin);
                }
            }
        }
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

    @Override
    public void show() {
        // called when this screen is set as the screen with game.setScreen();
    }

    @Override
    public void hide() {
        // called when current screen changes from this to a different screen
    }
}
