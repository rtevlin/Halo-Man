package com.haloman.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.haloman.game.entity.Components;
import com.haloman.game.entity.Components.CollisionComponent;
import com.haloman.game.entity.Components.PositionComponent;
import com.haloman.game.entity.Components.RenderableComponent;

public class RenderingSystem extends IteratingSystem {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private float elapsedTime = 0;

    @SuppressWarnings("unchecked")
	public RenderingSystem() {
    	super(Family.all(PositionComponent.class, RenderableComponent.class).get());
    	batch = new SpriteBatch();
    	shapeRenderer = new ShapeRenderer();
    }

    protected void processEntity(Entity entity, float deltaTime) {		
    	elapsedTime += deltaTime;
    	PositionComponent position = Components.mappers.POSITION.get(entity);
        RenderableComponent renderable = Components.mappers.RENDERABLE.get(entity);
        CollisionComponent collision = Components.mappers.COLLISION.get(entity);
        boolean flipped = renderable.flipped;
        TextureRegion region = renderable.animation.getKeyFrame(elapsedTime, renderable.looping);
        
        batch.begin();
        batch.draw(region, 
        		(flipped ? position.x + region.getRegionWidth() : position.x), 
        		position.y, 
        		(flipped ? -region.getRegionWidth() : region.getRegionWidth()),
        		region.getRegionHeight());
        batch.end();
        
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(position.x + collision.rectangle.x, position.y + collision.rectangle.y, collision.rectangle.width, collision.rectangle.height);
        shapeRenderer.end();
    }

}
