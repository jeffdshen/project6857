package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.jeffdshen.project6857.core.board.*;
import io.github.jeffdshen.project6857.core.net.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Shruthi on 5/10/2015.
 */
public class InitScreen implements Screen {
    private GameMain game;

    BitmapFont font;
    DragAndDrop dragDrop;
    Stage stage;
    Actor sidebar;
    TextButton.TextButtonStyle buttonStyle;

    Map<Piece, Integer> defaultPieces;
    InitBoard initBoard;
    final Skin uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
    Table dialogTable;

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
    public static Map<DragAndDrop.Source, Piece> sourceMap = new HashMap<>();

    // in pixels
    private int stageWidth;
    private int stageHeight;
    private int tileSize;
    private int borderSize;
    // in units
    private int boardWidth;
    private int boardHeight;
    private int playerHeight;

    TextButton startGameButton;
    TextButton randomBoardButton;
    TextField ip;

    boolean isServer;

    // constructor
    public InitScreen(GameMain game, int stageWidth, int stageHeight, int tileSize, int borderSize, int boardWidth, int boardHeight, int playerHeight){
        // keep reference to game
        this.game = game;

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
        dialogTable = new Table();

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
        topbar.setBounds(0, boardHeight * tileSize, stageWidth, stageHeight - (boardHeight * tileSize));
        stage.addActor(topbar);

        // fill topbar with text and buttons
        createButtons();

        // get pieces and initboard
        defaultPieces = InitBoard.getDefaultPieces();
        initBoard = new InitBoard(boardWidth, boardHeight, playerHeight, defaultPieces);

        // set board - either only set enemy pieces or set all pieces on board
        placeEnemyPieces(); // place unmoving enemy pieces on the board
        placeRemainingPieces(); // place all remaining pieces in the sidebar
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private void tileStage(){
        // create the tiled board
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Image tile = new Image(new Texture(Gdx.files.internal("tile.png")));
                tile.setBounds(tileSize * x, tileSize * y, tileSize, tileSize);
                stage.addActor(tile);

                dragDrop.addTarget(new DragAndDrop.Target(tile) {
                    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        return true;
                    }

                    public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {}

                    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        Actor currentTile = getActor();
                        Piece piece = sourceMap.get(source);
                        int xPos = (int) currentTile.getX();
                        int yPos = (int) currentTile.getY();

                        if (initBoard.setPiece(xPos/tileSize, yPos/tileSize, piece)) {
                            source.getActor().setPosition(xPos + borderSize, yPos + borderSize);
                        }

                        if (initBoard.noRemainingPieces()) {
                            startGameButton.setDisabled(false);
                            randomBoardButton.setDisabled(true);
                        }
                    }
                });

            }
        }
    }

    private void placeEnemyPieces() {
        // create (their) enemy pieces and place them on board, cannot be dragged
        for (int xpos = 0; xpos < boardWidth; xpos++) {
            for (int ypos = 0; ypos < boardHeight; ypos++) {
                Piece piece = initBoard.getPiece(xpos, ypos);
                if (piece != null && piece.getRank() == Rank.UNKNOWN) {
                    Image coin = new Image(new Texture(Gdx.files.internal(rankMap.get(piece.getRank()))));
                    coin.setColor(typeMap.get(piece.getType()));
                    int coinSize = tileSize - (2 * borderSize);
                    coin.setBounds((tileSize * xpos) + borderSize, (tileSize * ypos) + borderSize, coinSize, coinSize);
                    stage.addActor(coin);
                }
            }
        }
    }

    private void placeRemainingPieces() {
        // create (my) remaining pieces and place them in sidebar
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

                final DragAndDrop.Source source = new DragAndDrop.Source(coin) {
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
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
                    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
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

        // make sidebar a target to drag pieces back to (if they need to be moved off the board)
        dragDrop.addTarget(new DragAndDrop.Target(sidebar) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {return true;}

            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
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
    }

    private void createButtonStyle() {
        //font fix
        font.setScale(2);

        //create the button style
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.043f, 0.645f, .668f, 1f)); //11 165 171
        pixmap.fill();
        Texture downbutton = new Texture(pixmap);

        pixmap.setColor(new Color(0.043f, 0.645f, .668f, 0.5f)); //11 165 171
        pixmap.fill();
        Texture upbutton = new Texture(pixmap);

        pixmap.setColor(new Color(0.008f, 0.108f, 0.113f, 1f)); //2, 27, 29
        pixmap.fill();
        Texture disabledbutton = new Texture(pixmap);
        pixmap.dispose();

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.disabledFontColor = Color.GRAY;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(upbutton));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(downbutton));
        buttonStyle.disabled = new TextureRegionDrawable(new TextureRegion(disabledbutton));
        buttonStyle.checked = new TextureRegionDrawable(new TextureRegion(upbutton));
    }

    private void createButtons() {
        createButtonStyle();
        // startgamebutton
        startGameButton = new TextButton("start game", buttonStyle);
        startGameButton.setBounds(10, 805, 200, 70);
        stage.addActor(startGameButton);
        startGameButton.setDisabled(true);
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (initBoard.noRemainingPieces()) chooseRole();
            }
        });

        //randomboardbutton
        randomBoardButton = new TextButton("start - random board", buttonStyle);
        randomBoardButton.setBounds(220, 805, 300, 70);
        stage.addActor(randomBoardButton);
        randomBoardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                randomPlace();
            }
        });
    }

    private void randomPlace() {
        int x = 0;
        int y = 0;
        for (Map.Entry<Piece,Integer> entry: initBoard.getRemainingPieces().entrySet()) {
            final Piece piece = entry.getKey();
            int occurrence = entry.getValue();
            for (int q = 0; q < occurrence; q++) {
                while (!initBoard.setPiece(x, y, piece)) {
                    if (x < 9) {
                        x++;
                    } else {
                        x = 0;
                        y += 1;
                    }
                }
            }
        }
        if (initBoard.noRemainingPieces()) chooseRole();
    }

    private void chooseRole() {
        // create pixmap and background
        Pixmap pixmap = new Pixmap(stageWidth, stageHeight - (boardHeight*tileSize), Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.015f, 0.217f, 0.225f, 1f)); //4, 55, 58
        pixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(
                new Texture(pixmap)));

        // create buttons
        TextButton clientButton = new TextButton("Client", buttonStyle);
        clientButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isServer = false;
                getIP();
            }
        });
        TextButton serverButton = new TextButton("Server", buttonStyle);
        serverButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isServer = true;
                getIP();
            }
        });

        // create dialog (as a table object)
        dialogTable.setBackground(background);
        dialogTable.setBounds(stageWidth * 3 / 8, stageHeight * 3 / 8, stageWidth / 4, stageHeight / 4);
        dialogTable.add(new Label("Choose a role:", uiSkin));
        dialogTable.row();
        dialogTable.add(clientButton).size(dialogTable.getWidth()/3, 60).pad(20, 10, 20, 10);
        dialogTable.add(serverButton).size(dialogTable.getWidth()/3, 60).pad(20, 10, 20, 10);

        stage.addActor(dialogTable);
    }

    private void getIP() {
        ip = new TextField("", uiSkin);
        if (isServer) {
            startPlay();
        } else {
            TextButton submitIpButton = new TextButton("Submit", buttonStyle);
            submitIpButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!ip.getText().equals("")) {
                        startPlay();
                    }
                }
            });

            dialogTable.clearChildren();
            dialogTable.debug();
            dialogTable.add(new Label("Enter Server IP:", uiSkin)).row();
            dialogTable.add(ip).width(100).row();
            dialogTable.add(submitIpButton).size(dialogTable.getWidth() / 3, 60).pad(20, 10, 20, 10);
        }
    }

    private void startPlay() {
        // ask for an IP
        String serverIp = ip.getText();
        if (isServer) {
            try {
                serverIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        // set up Fairplay
        Fairplay alice;
        Fairplay bob;
        FairplayAlternator fairplayAlternator;
        Socket socket = null;
        if (isServer) {
            // you are bob
            bob = new Fairplay();
            alice = new Fairplay(serverIp);
            fairplayAlternator = new FairplayAlternator(alice, bob, false);
            Server server = new Server(1234);
            try {
                server.connect();
                socket = server.socket;
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            // you are alice
            alice = new Fairplay();
            bob = new Fairplay(serverIp);
            fairplayAlternator = new FairplayAlternator(alice, bob, true);
            Client client = new Client(serverIp, 1234);
            try {
                client.connect();
                socket = client.socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // create connection and playsrceen
        try {
            Connection connection = new Connection(socket, initBoard.getBoard(), playerHeight, fairplayAlternator);
            Board board = new Board(initBoard.getBoard(), connection);
            connection.setBoard(board);

            PlayScreen playScreen = new PlayScreen(game, stageWidth, stageHeight, tileSize, borderSize, boardWidth, boardHeight, playerHeight, board);
            game.setPlayScreen(playScreen);
            game.setScreen(playScreen);

            // create new thread with connection as a runnable
            Thread thread = new Thread(connection);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
