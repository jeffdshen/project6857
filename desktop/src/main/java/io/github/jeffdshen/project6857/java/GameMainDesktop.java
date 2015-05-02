package io.github.jeffdshen.project6857.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.jeffdshen.project6857.core.GameMain;

public class GameMainDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 900;
        config.height = 600;
		new LwjglApplication(new GameMain(), config);
	}
}
