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
import com.haloman.game.state.Position;

public class Player extends MovingObject implements InputListener {
	
	/** current action state of the entity */
	private PlayerState state;
	
	/** speed for regular movement (ie. running) */
	private float speed = 100.0f;
	
	/** speed for sliding */
	private float slideSpeed = 200.0f;
	
	/** distance that has been traveled while sliding */
	private float slideTravel = 0f;
	
	/** which direction the entity is facing. 1 for right and -1 for left */
	private float directionX = 1;
	
	/** state of input keys */
	private boolean[] keys = new boolean[150];
	
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
		keys[key] = true;
		VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
		boolean changedDirX = false;
		if(key == Keys.LEFT) {
			changedDirX = directionX == 1;
			directionX = -1;
		}
		else if(key == Keys.RIGHT) {
			changedDirX = directionX == -1;
			directionX = 1;
		}
		
		if(key == Keys.LEFT || key == Keys.RIGHT) {
			if(!this.isAirborne() || (this.isSliding() && changedDirX)) {
				changeState(PlayerState.RUNNING);
			}
			// allows horizontal momentum to be while continuing in the same direction (for example a slide jump) 
			if((Math.abs(velocity.x) < speed) || changedDirX || PlayerState.RUNNING.equals(state)) {
				velocity.x = speed * directionX;
			}
			Components.mappers.RENDERABLE.get(this).flipped = directionX < 0;
		}
		
		else if(key == Keys.SPACE) {
			if(!this.isAirborne()) {
				Components.mappers.VELOCITY.get(this).y = 400.0f;
			}
		}
		else if(key == Keys.CONTROL_LEFT) {
			if(!this.isAirborne() && !this.isSliding()) {
				changeState(PlayerState.SLIDING);
			}
		}
	}

	@Override
	public void inputReleased(int key) {
		keys[key] = false;
		VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
		
		// -------- Release left/right movement -----------
		if((key == Keys.LEFT && velocity.x <= 0) || (key == Keys.RIGHT && velocity.x >= 0)) {
			velocity.x = 0f;
			if(!this.isAirborne()) {
				changeState(PlayerState.NEUTRAL);
			}
		}
		
		// -------- Release jump -----------
		// vertical velocity is removed when you release therefore allowing you to begin falling after release
		else if(key == Keys.SPACE) {
			if(velocity.y > 0f) {
				Components.mappers.VELOCITY.get(this).y = 0f;
			}
		}
	}
	
	private void changeState(PlayerState newState) {
		slideTravel = 0;
		this.state = newState;
		
		VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
		if(PlayerState.NEUTRAL.equals(newState)) {
			velocity.x = 0f;
		}
		else if(PlayerState.RUNNING.equals(newState)) {
			velocity.x = speed * directionX;
		}
		else if(PlayerState.SLIDING.equals(newState)) {
			velocity.x = slideSpeed * directionX;
		}
		
		Animation animation = stateAnimations.get(state);
		Rectangle collisionRect = stateCollisions.get(state);
		if(animation != null) {
			Components.mappers.RENDERABLE.get(this).animation = animation;
			Components.mappers.COLLISION.get(this).rectangle = collisionRect;
		}
	}
	
	@Override
	public void notifyPositionUpdate(Position position, float deltaX, float deltaY) {
		VelocityComponent velocity = Components.mappers.VELOCITY.get(this);
		
		// Player is currently sliding
		if(this.isSliding()) {
			slideTravel += deltaX;
			if(deltaX == 0f || Math.abs(slideTravel) >= 100f) {
				// slide is over if you didnt move at all horizontally (collision), or you moved the maximum slide distance
				changeState(PlayerState.NEUTRAL);
			}
		}
		
		// Player has *just become* airborne
		if(Position.AIRBORNE.equals(position)) {
			changeState(PlayerState.AIRBORNE);
		}
		
		// Player has *just become* grounded
		else if(Position.GROUNDED.equals(position)) {
			// you land on the ground while holding a direction
			if(keys[Keys.LEFT] || keys[Keys.RIGHT]) {
				changeState(PlayerState.RUNNING);
			}
			// you land on the ground without moving
			else {
				changeState(PlayerState.NEUTRAL);
			}
		}
		
		// horizontal velocity could become 0 in the event that you hit a wall while running, but you may still be holding the
		// directional you want to go - so update the velocity again
		if(velocity.x == 0 && (keys[Keys.LEFT] || keys[Keys.RIGHT])) {
			velocity.x = speed * directionX;
		}
	}
	
	private boolean isAirborne() {
		return PlayerState.AIRBORNE.equals(this.state);
	}
	
	private boolean isSliding() {
		return PlayerState.SLIDING.equals(this.state);
	}
}