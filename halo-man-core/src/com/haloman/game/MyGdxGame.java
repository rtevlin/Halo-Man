package com.haloman.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.haloman.game.entity.Player;
import com.haloman.game.resource.Assets;
import com.haloman.game.system.MovementSystem;
import com.haloman.game.system.RenderingSystem;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	private Engine engine;
	private Player player;
	FPSLogger fpsLogger;

	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;

	@Override
	public void create() {
		Assets.load();
		
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false,w,h);
        camera.update();
        tiledMap = new TmxMapLoader().load("data/test2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		engine = new Engine();
		MovementSystem movementSystem = new MovementSystem(tiledMap);
		RenderingSystem renderingSystem = new RenderingSystem();

		engine.addSystem(movementSystem);
		engine.addSystem(renderingSystem);

		player = new Player();
		engine.addEntity(player);

		Gdx.input.setInputProcessor(this);

		fpsLogger = new FPSLogger();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
		
		engine.update(Gdx.graphics.getDeltaTime());
		// fpsLogger.log();
	}

	@Override
	public boolean keyDown(int keycode) {
		player.inputPressed(keycode);

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		player.inputReleased(keycode);

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
