package com.haloman.game.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

public abstract class Components {
	private Components() {
	}

	public static ComponentMappers mappers = new ComponentMappers();

	public static class PositionComponent implements Component {
		public float x = 0.0f;
		public float y = 0.0f;
		
		public PositionComponent() {
			
		}

		public PositionComponent(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class VelocityComponent implements Component {
		public float x = 0.0f;
		public float y = 0.0f;
	}

	public static class CollisionComponent implements Component {
		public Rectangle rectangle;

		public CollisionComponent(Rectangle rectangle) {
			this.rectangle = rectangle;
		}
	}

	public static class RenderableComponent implements Component {
		public Animation animation;
		public boolean looping;
		public boolean flipped;

		public RenderableComponent(Animation animation) {
			this(animation, true);
		}

		public RenderableComponent(Animation animation, boolean looping) {
			this.animation = animation;
			this.looping = looping;
		}
	}
	
	public static class OwnerComponent implements Component {
		public GameObject owner;
		public OwnerComponent(GameObject owner) {
			this.owner = owner;
		}
	}

	public static class ComponentMappers {
		private ComponentMappers() {
		}

		public final ComponentMapper<PositionComponent> POSITION = ComponentMapper.getFor(PositionComponent.class);
		public final ComponentMapper<VelocityComponent> VELOCITY = ComponentMapper.getFor(VelocityComponent.class);
		public final ComponentMapper<RenderableComponent> RENDERABLE = ComponentMapper.getFor(RenderableComponent.class);
		public final ComponentMapper<CollisionComponent> COLLISION = ComponentMapper.getFor(CollisionComponent.class);
		public final ComponentMapper<OwnerComponent> OWNER = ComponentMapper.getFor(OwnerComponent.class);
	}

}
