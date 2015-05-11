package io.github.jeffdshen.project6857.core;

import com.badlogic.gdx.Game;

public class GameMain extends Game {
    // in pixels
    final int stageWidth = 1200;
    final int stageHeight = 880;
    final int tileSize = 80;
    final int borderSize = 2;
    // in units
    final int boardWidth = 10;
    final int boardHeight = 10;
    final int playerHeight = 4;

    /*boolean boardSet = false;

    TextButton startGameButton;
    TextButton randomBoardButton;*/

    InitScreen initScreen;
    PlayScreen playScreen;

    @Override
	public void create () {
        initScreen = new InitScreen(this, stageWidth, stageHeight, tileSize, borderSize, boardWidth, boardHeight, playerHeight);
        setScreen(initScreen);
    }

    public void setPlayScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

	@Override
	public void dispose () {
	}
}
