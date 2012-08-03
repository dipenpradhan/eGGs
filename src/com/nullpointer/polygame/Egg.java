package com.nullpointer.polygame;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Egg {

	Scene mScene;
	TextureRegion eggTextureRegion;
	float slingX, slingY, density,elasticity, friction, scale=0.4f;
	Sprite eggSprite;
	FixtureDef eggFixtureDef;
	float delta_x,delta_y;
	boolean eggLaunched=false,eggHitsNest=false;
	float maxDistance=100;
	Body eggBody;
	String eggName;
	int eggBounce=0;
	
	
	public int getEggBounce() {
		return eggBounce;
	}

	public void setEggBounce(int eggBounce) {
		this.eggBounce = eggBounce;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Sprite getEggSprite() {
		return eggSprite;
	}

	public void setEggSprite(Sprite eggSprite) {
		this.eggSprite = eggSprite;
	}

	public void setDelta_x(float delta_x) {
		this.delta_x = delta_x;
	}

	public void setDelta_y(float delta_y) {
		this.delta_y = delta_y;
	}

	public Egg(Scene mScene,TextureRegion eggTextureRegion,float x, float y, float scale,String name, float _slingX,float _slingY, float density,float elasticity, float friction){//float density,float elasticity, float friction){
		
		this.eggTextureRegion=eggTextureRegion;
		this.mScene=mScene;
		this.density=density;
		this.elasticity=elasticity;
		this.friction=friction;
		this.scale=scale;
		this.slingX=_slingX;
		this.slingY=_slingY;
		this.eggName=name;
		
		
		Sprite eggSprite=new Sprite(x, y, eggTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
			Log.d("TAG","eggloc "+this.getX()+ " "+this.getY());
				
			if (pSceneTouchEvent.isActionUp()) {
				eggLaunched= true;
			}
			
			
				if (!eggLaunched) {
					delta_x = pSceneTouchEvent.getX() - (slingX) - this.getWidth() / 2;
					delta_y = pSceneTouchEvent.getY() - (slingY) - this.getHeight() / 2;

					if ((Math.pow(delta_x, 2) + Math.pow(delta_y, 2)) > Math.pow(maxDistance, 2)) {
						float angle = (float) Math.atan2(delta_y, delta_x);
						Log.d("TAG","egg angle "+Math.toDegrees(angle));
						
						this.setPosition((slingX) + (float) (maxDistance * Math.cos(angle)), (slingY) + (float) (maxDistance * Math.sin(angle)));

					} else {

						this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					}
					
				
						

				}
		

				return true;
			}
		
		
		};
		
		eggSprite.setUserData(name);
		eggSprite.setScale(scale);
		mScene.attachChild(eggSprite);
		mScene.registerTouchArea(eggSprite);
				
		this.eggSprite=eggSprite;
		
		
		
	}
	
	
	
	public boolean isEggHitsNest() {
		return eggHitsNest;
	}

	public void setEggHitsNest(boolean eggHitsNest) {
		this.eggHitsNest = eggHitsNest;
	}

	public void createEggBody(PhysicsWorld pWorld)
	{
		FixtureDef eggFixtureDef=PhysicsFactory.createFixtureDef(density, elasticity, friction);
		Body eggBody=PhysicsFactory.createCircleBody(pWorld, this.getEggSprite(), BodyType.DynamicBody, eggFixtureDef);
		eggBody.setUserData(eggName);		
			
		pWorld.registerPhysicsConnector(new PhysicsConnector(this.getEggSprite(), eggBody));
		this.eggBody=eggBody;
		
	}
	
	

	public String getEggName()
	{
		return eggName;
	}
	
	public Body getEggBody()
	{
		return this.eggBody;
	}
	
	public float getDelta_x()
	{
		return this.delta_x;
	}
	public float getDelta_y()
	{
		return this.delta_y;
	}
	
	
	
	public boolean isEggLaunched()
	{
		return eggLaunched;
	}
	
	public void setEggLaunched(boolean b)
	{
		eggLaunched=b;
	}
	
	
	
	
}
