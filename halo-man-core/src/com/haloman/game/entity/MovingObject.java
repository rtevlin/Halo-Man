package com.haloman.game.entity;

import com.haloman.game.state.Position;

public abstract class MovingObject extends GameObject {
	public void notifyPositionUpdate(Position position, float deltaX, float deltaY) {
		// purposely blank
	}
}
