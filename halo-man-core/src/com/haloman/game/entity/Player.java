package com.haloman.game.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.haloman.game.entity.Components.CollisionComponent;
import com.haloman.game.entity.Components.OwnerComponent;
import com.haloman.game.entity.Components.PositionComponent;
import com.haloman.game.entity.Components.RenderableComponent;
import com.haloman.game.entity.Components.VelocityComponent;
import com.haloman.game.input.InputListener;
import com.haloman.game.resource.Assets;

public class Player extends GameObject implements InputListener {
	
	private PlayerState state;
	private float speed = 100.0f;
	
	private static Map<PlayerState, Animation> stateAnimations = new HashMap<PlayerState, Animation>();
	private static Map<PlayerState, Rectangle> stateCollisions = new HashMap<PlayerState, Rectangle>();
	
	static {
		stateAnimations.put(PlayerState.NEUTRAL, Assets.cheifNeutral);
		stateAnimations.put(PlayerState.RUNNING, Assets.cheifRunning);
		stateAnimations.put(PlayerState.AIRBORNE, Assets.cheifAirborne);
		stateAnimations.put(PlayerState.SLIDING, Assets.cheifSliding);
		
		Rectangle verticalRect = new Rectangle(7, 0, 18, 24);
		Rectangle horiztonalRect = new Rectangle(5, 0, 22, 15);
		stateCollisions.put(PlayerState.NEUTRAL, verticalRect);
		stateCollisions.put(PlayerState.RUNNING, verticalRect);
		stateCollisions.put(PlayerState.AIRBORNE, verticalRect);
		stateCollisions.put(PlayerState.SLIDING, horiztonalRect);
	}
	
	private enum PlayerState {
		NEUTRAL,
		RUNNING,
		AIRBORNE,
		SLIDING
	}
	
	public Player() {
		this.add(new PositionComponent(40.0f,150.0f));
		this.add(new VelocityComponent());
		this.add(new RenderableComponent(Assets.cheifNeutral));
		this.add(new CollisionComponent(new Rectangle(7, 0, 18, 24)));
		this.add(new OwnerComponent(this));
		
		this.registerState(null, null);
		
	}
	
	public <T> void registerState(String state, Consumer<T> function) {
		
	}

	@Override
	public void inputPressed(int key) {
		if(key == Keys.LEFT || key == Keys.RIGHT) {
			if(!state.equals(PlayerState.AIRBORNE)) {
				changeState(PlayerState.RUNNING);
			}
		}
		if(key == Keys.LEFT) {
			Components.mappers.VELOCITY.get(this).x = -speed;
			Components.mappers.RENDERABLE.get(this).flipped = true;
		}
		else if(key == Keys.RIGHT) {
			Components.mappers.VELOCITY.get(this).x = speed;
			Components.mappers.RENDERABLE.get(this).flipped = false;
		}
		else if(key == Keys.SPACE) {
			if(!state.equals(PlayerState.AIRBORNE)) {
				Components.mappers.VELOCITY.get(this).y = 400.0f;
			}
		}
	}

	@Override
	public void inputReleased(int key) {
		VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
		if((key == Keys.LEFT && velocity.x < 0) || (key == Keys.RIGHT && velocity.x > 0)) {
			velocity.x = 0f;
			changeState(PlayerState.NEUTRAL);
		}
		else if(key == Keys.SPACE) {
			if(velocity.y > 0f) {
				Components.mappers.VELOCITY.get(this).y = 0f;
			}
		}
		else if(key == Keys.CONTROL_LEFT) {
			changeState(PlayerState.SLIDING);
		}
	}
	
	private void changeState(PlayerState newState) {
		this.state = newState;
		Animation animation = stateAnimations.get(state);
		Rectangle collisionRect = stateCollisions.get(state);
		if(animation != null) {
			Components.mappers.RENDERABLE.get(this).animation = animation;
			Components.mappers.COLLISION.get(this).rectangle = collisionRect;
		}
	}
	
	@Override
	public void update(String str) {
		if("AIRBORNE".equals(str)) {
			changeState(PlayerState.AIRBORNE);
		}
		else if("GROUNDED".equals(str)) {
			VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
			if(velocity.x == 0) {
				changeState(PlayerState.NEUTRAL);
			}
			else {
				changeState(PlayerState.RUNNING);
			}
		}
	}

}
