package com.haloman.game.input;

import com.badlogic.gdx.Input.Keys;

public interface InputListener {
	public void inputPressed(int key);
	
	public void inputReleased(int key);
}
