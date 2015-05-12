package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.jeffdshen.project6857.core.board.*;
import io.github.jeffdshen.project6857.core.net.EncodingProtocol;

import java.util.*;

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
    Actor[][] tileArray;
    Actor[][] pieceArray;
    TextButton.TextButtonStyle buttonStyle;
    public static Map<DragAndDrop.Source, Piece> sourceMap = new HashMap<>();
    private Object targetLock = new Object();

    Board board;
    Round lastRound;

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
        this.lastRound = board.getLastRound();

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
        tileArray = new Actor [boardWidth][boardHeight];
        Table tileTable = new Table();
        tileTable.setBounds(0, 0, boardWidth * tileSize, boardHeight * tileSize);
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Image tile = new Image(new Texture(Gdx.files.internal("tile.png")));
                tile.setSize(tileSize, tileSize);
                tileTable.add(tile);
                tileArray[x][boardHeight-y-1] = tile;
            }
            tileTable.row();
        }
        stage.addActor(tileTable);
    }

    private void placePieces() {
        pieceArray = new Actor [boardWidth][boardHeight];
        for (int xpos = 0; xpos < boardWidth; xpos++) {
            for (int ypos = 0; ypos < boardHeight; ypos++) {
                Piece piece = board.getPiece(xpos, ypos);
                if (piece != null) {
                    Rank rank = piece.getRank();

                    Image coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                    coin.setColor(typeMap.get(piece.getType()));
                    int coinSize = tileSize - (2 * borderSize);
                    coin.setBounds((tileSize * xpos) + borderSize, (tileSize * ypos) + borderSize, coinSize, coinSize);
                    stage.addActor(coin);
                    pieceArray[xpos][boardHeight-ypos-1] = coin;

                    if (rank != Rank.UNKNOWN && rank != Rank.FLAG && rank != Rank.BOMB) {
                        DragAndDrop.Source source = createPieceSource(coin);
                        sourceMap.put(source, piece);
                        dragDrop.addSource(source);
                    }
                }
            }
        }
    }

    private DragAndDrop.Source createPieceSource(Actor coin) {
        final DragAndDrop.Source source = new DragAndDrop.Source(coin) {
            private ArrayList<DragAndDrop.Target> validTargets;
            private Location startLoc;

            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Actor currentCoin = getActor();
                currentCoin.setVisible(false);

                int xpos = (int) ((currentCoin.getX() - borderSize)/tileSize);
                int ypos = (int) ((currentCoin.getY() - borderSize)/tileSize);
                startLoc = new Location(xpos, ypos);

                validTargets = new ArrayList<>();
                //synchronized (targetLock) {
                    getValidTargets(startLoc, validTargets);
                //}

                Image dragCoin = new Image(((Image) currentCoin).getDrawable());
                dragCoin.setColor(currentCoin.getColor());
                dragCoin.setSize(currentCoin.getHeight(), currentCoin.getWidth());

                payload.setDragActor(dragCoin);
                dragDrop.setDragActorPosition(-(dragCoin.getWidth() / 2), dragCoin.getHeight() / 2);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                this.getActor().setVisible(true);
                //synchronized (targetLock) {
                    for (DragAndDrop.Target oldTarget : validTargets) {
                        dragDrop.removeTarget(oldTarget);
                    }
                //}
            }

            private void getValidTargets(final Location startLoc, ArrayList<DragAndDrop.Target> validTargets) {
                for (final Direction dir: new Direction[] {Direction.FORWARD,Direction.RIGHT, Direction.BACKWARD, Direction.LEFT}){
                    Location possibleLoc = startLoc.add(dir);
                    if (board.inBoard(possibleLoc)) {
                        Actor possibleTile = tileArray[possibleLoc.getX()][possibleLoc.getY()];
                        DragAndDrop.Target target = new DragAndDrop.Target(possibleTile) {
                            Direction toHere = dir;
                            Location start = startLoc;

                            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                return true;
                            }

                            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            }

                            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                //synchronized (targetLock) {
                                    Actor currentTile = getActor();
                                    Piece piece = sourceMap.get(source);
                                    int xPos = (int) currentTile.getX();
                                    int yPos = (int) currentTile.getY();

                                    board.makeMyMove(startLoc, dir);
                                    while(Objects.equals(board.getLastRound(), lastRound)) {
                                        // just keep swimming
                                        // possibly some sort of loading indicator
                                        VerificationResult verificationResult = board.getVerificationResult();
                                        if (verificationResult != null) {
                                            System.out.println(verificationResult.getMessage());
                                            verificationResult.getException().printStackTrace();
                                            System.exit(1);
                                        }
                                    }
                                    updateBoard(source);
                                //}
                            }
                        };
                        validTargets.add(target);
                        dragDrop.addTarget(target);
                    }
                }
            }
        };

        return source;
    }

    private void updateBoard(DragAndDrop.Source source) {
        // dissect lastRound to update the board
        lastRound = board.getLastRound();
        System.out.println(EncodingProtocol.encodeMove(lastRound.getMyMove()));
        System.out.println(EncodingProtocol.encodeMove(lastRound.getTheirMove()));
        System.out.println(lastRound.getMyStatus().getCompare());
        System.out.println(lastRound.getTheirStatus().getCompare());

        // pull both pieces off the board
        Location myStart = lastRound.getMyMove().getStart();
        Location myEnd = lastRound.getMyMove().getEnd();
        Actor myCoin = pieceArray[myStart.getX()][flip(myStart.getY())];
        pieceArray[myStart.getX()][flip(myStart.getY())] = null;

        Location theirStart = lastRound.getTheirMove().getStart();
        Location theirEnd = lastRound.getTheirMove().getEnd();
        Actor theirCoin = pieceArray[theirStart.getX()][flip(theirStart.getY())];
        pieceArray[theirStart.getX()][flip(theirStart.getY())] = null;

        switch (lastRound.getMyStatus().getCompare()) {
            case WIN:
                // place piece on board, remove any piece there if necessary.
                myCoin.setPosition((myEnd.getX()*tileSize) + borderSize, (myEnd.getY()*tileSize) + borderSize);

                if (pieceArray[myEnd.getX()][flip(myEnd.getY())] != null) {
                    pieceArray[myEnd.getX()][flip(myEnd.getY())].remove();
                }

                pieceArray[myEnd.getX()][flip(myEnd.getY())] = myCoin;
                break;
            case LOSS:
                myCoin.remove();
                break;
            case TIE:
                myCoin.remove();

                // kill losing coin
                if (pieceArray[myEnd.getX()][flip(myEnd.getY())] != null) {
                    pieceArray[myEnd.getX()][flip(myEnd.getY())].remove();
                }
                break;
            case GAMEWIN:
                // win or tie
                break;
            case GAMELOSS:
                // impossible
                break;
        }

        switch (lastRound.getTheirStatus().getCompare()) {
            case WIN:
                // place piece on board, remove any piece there if necessary.
                theirCoin.setPosition((theirEnd.getX()*tileSize) + borderSize, (theirEnd.getY()*tileSize) + borderSize);

                if (pieceArray[theirEnd.getX()][flip(theirEnd.getY())] != null) {
                    pieceArray[theirEnd.getX()][flip(theirEnd.getY())].remove();
                }

                pieceArray[theirEnd.getX()][flip(theirEnd.getY())] = theirCoin;
                break;
            case LOSS:
                theirCoin.remove();
                break;
            case TIE:
                theirCoin.remove();

                // kill losing coin
                if (pieceArray[theirEnd.getX()][flip(theirEnd.getY())] != null) {
                    pieceArray[theirEnd.getX()][flip(theirEnd.getY())].remove();
                }
                break;
            case GAMEWIN:
                // lose or tie
                break;
            case GAMELOSS:
                // impossible
                break;
        }
    }

    private int flip(int y) {
        return boardHeight - y - 1;
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
