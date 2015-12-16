package com.haloman.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
	public static TextureAtlas cheifRunningAtlas;
	public static Animation cheifRunning;
	
	public static TextureAtlas cheifNeutralAtlas;
	public static Animation cheifNeutral;
	
	public static TextureAtlas cheifAirborneAtlas;
	public static Animation cheifAirborne;
	
	public static TextureAtlas cheifSlidingAtlas;
	public static Animation cheifSliding;
	
	public static void load() {
		cheifRunningAtlas = new TextureAtlas(Gdx.files.internal("data/cheif-running-packed/cheif-running.atlas"));
		cheifRunning = new Animation(1/10f, cheifRunningAtlas.getRegions());
		
		cheifNeutralAtlas = new TextureAtlas(Gdx.files.internal("data/cheif-neutral-packed/cheif-neutral.atlas"));
		cheifNeutral = new Animation(1/10f, cheifNeutralAtlas.getRegions());
		
		cheifAirborneAtlas = new TextureAtlas(Gdx.files.internal("data/cheif-airborne-packed/cheif-airborne.atlas"));
		cheifAirborne = new Animation(1/10f, cheifAirborneAtlas.getRegions());
		
		cheifSlidingAtlas = new TextureAtlas(Gdx.files.internal("data/cheif-sliding-packed/cheif-sliding.atlas"));
		cheifSliding = new Animation(1/10f, cheifSlidingAtlas.getRegions());
	}
	
}
