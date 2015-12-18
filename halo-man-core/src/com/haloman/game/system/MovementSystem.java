package com.haloman.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.haloman.game.entity.Components;
import com.haloman.game.entity.Components.CollisionComponent;
import com.haloman.game.entity.Components.PositionComponent;
import com.haloman.game.entity.Components.VelocityComponent;
import com.haloman.game.entity.MovingObject;
import com.haloman.game.state.Position;

public class MovementSystem extends IteratingSystem {

	private TiledMap map = null;
	
	private static final float MAX_VELOCITY = 500.0f;

	@SuppressWarnings("unchecked")
	public MovementSystem(TiledMap map) {
		super(Family.all(PositionComponent.class, VelocityComponent.class).get());
		this.map = map;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VelocityComponent velocity = Components.mappers.VELOCITY.get(entity);		
		PositionComponent position = Components.mappers.POSITION.get(entity);
		CollisionComponent collision = Components.mappers.COLLISION.get(entity);
		MovingObject owner = (MovingObject)Components.mappers.OWNER.get(entity).owner;

		float oldX = position.x;
		float oldY = position.y;
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;

		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get(0);
		float tileW = collisionLayer.getTileWidth();
		float tileH = collisionLayer.getTileHeight();

		int cellX = (int)(( (position.x + collision.rectangle.x) + (collision.rectangle.width / 2) ) / tileW);
		int cellY = (int)(( (position.y + collision.rectangle.y) + (collision.rectangle.height / 2) ) / tileH);

		Rectangle collisionRectangle = new Rectangle(position.x + collision.rectangle.x,
				position.y + collision.rectangle.y, collision.rectangle.width, collision.rectangle.height);

		if (velocity.x > 0) {
			// moving right
			Block block = checkRightBlocks(collisionLayer, cellX, cellY, collisionRectangle);
			if(block != null) {
				position.x = oldX;
				velocity.x = 0f;
				collisionRectangle.x = position.x + collision.rectangle.x;
				cellX = (int)(( (position.x + collision.rectangle.x) + (collision.rectangle.width / 2) ) / tileW);
			}
		} else if (velocity.x < 0) {
			// moving left
			Block block = checkLeftBlocks(collisionLayer, cellX, cellY, collisionRectangle);
			if(block != null) {
				position.x = oldX;
				velocity.x = 0f;
				collisionRectangle.x = position.x + collision.rectangle.x;
				cellX = (int)(( (position.x + collision.rectangle.x) + (collision.rectangle.width / 2) ) / tileW);
			}
		}
		
		// check for hitting head 
		if(velocity.y > 0) {
			Block block = checkTopBlocks(collisionLayer, cellX, cellY, collisionRectangle);
			if(block != null) {
				velocity.y = 0;
				position.y = oldY;
			}
		}

		// check for grounded
		Block block = checkBottomBlocks(collisionLayer, cellX, cellY, collisionRectangle);
		if(block != null && velocity.y <= 0) {
			velocity.y = 0;
			position.y = block.rectangle.y + tileH - 1;
			if(oldY != position.y) {
				owner.notifyPositionUpdate(Position.GROUNDED, position.x - oldX, position.y - oldY);
			}
			else {
				owner.notifyPositionUpdate(null, position.x - oldX, position.y - oldY);
			}
		} else {
			owner.notifyPositionUpdate(Position.AIRBORNE, position.x - oldX, position.y - oldY);
			if(velocity.y <= MAX_VELOCITY) {
				velocity.y -= 20.0f;
			}
		}
	}

	private static Block checkBottomBlocks(TiledMapTileLayer collisionLayer, int cellX, int cellY, Rectangle collisionRectangle) {
		Block block = checkBlock(collisionLayer, cellX, cellY, collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX, cellY - 1, collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX - 1, cellY - 1, collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX + 1, cellY - 1, collisionRectangle);
		if(block != null) {
			return block;
		}

		return null;
	}
	
	private static Block checkTopBlocks(TiledMapTileLayer collisionLayer, int cellX, int cellY, Rectangle collisionRectangle) {		
		Block block = checkBlock(collisionLayer, cellX, cellY + 1, collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX + 1, cellY + 1, collisionRectangle);
		if(block != null) {
			return block;
		}
	
		return null;
	}
	
	private static Block checkRightBlocks(TiledMapTileLayer collisionLayer, int cellX, int cellY, Rectangle collisionRectangle) {
		Block block = checkBlock(collisionLayer, cellX + 1, cellY , collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX + 1, cellY + 1, collisionRectangle);
		if(block != null) {
			return block;
		}

		return null;
	}
	
	private static Block checkLeftBlocks(TiledMapTileLayer collisionLayer, int cellX, int cellY, Rectangle collisionRectangle) {		
		Block block = checkBlock(collisionLayer, cellX - 1, cellY, collisionRectangle);
		if(block != null) {
			return block;
		}
		
		block = checkBlock(collisionLayer, cellX - 1, cellY + 1, collisionRectangle);
		if(block != null) {
			return block;
		}

		return null;
	}
	
	public static Block checkBlock(TiledMapTileLayer collisionLayer, int cellX, int cellY, Rectangle collisionRectangle) {
		float tileW = collisionLayer.getTileWidth();
		float tileH = collisionLayer.getTileHeight();
		Cell cell = collisionLayer.getCell(cellX, cellY);
		boolean blocked = false;
		if (cell != null) {
			blocked = true;
		}
		Rectangle cellRectangle = new Rectangle((cellX) * tileW, (cellY) * tileH, tileW, tileH);
		if (blocked && (overlaps(collisionRectangle, cellRectangle))) {
			return new Block(cellRectangle);
		}
		return null;
	}
	
	public static boolean overlaps (Rectangle r1, Rectangle r2) {
	  return r1.x <= r2.x + r2.width && r1.x + r1.width >= r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y;
	}

	private static class Block {
		public Rectangle rectangle;

		public Block(Rectangle rectangle) {
			this.rectangle = rectangle;
		}
	}
}