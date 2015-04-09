package io.github.jeffdshen.project6857.html;

import io.github.jeffdshen.project6857.core.GameMain;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GameMainHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new GameMain();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
