package com.nullpointer.polygame;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameActivity extends BaseGameActivity {

	// Camera Parameters
	private static final short CAMERA_WIDTH = 480;
	private static final short CAMERA_HEIGHT = 800;
	private RepeatingSpriteBackground mBackground;
	private SmoothCamera mCamera;
	private Sprite mEggSprite;
	private PhysicsWorld mPhysicsWorld;
	private Scene mScene;
	private Body mEggBody, mNestBody;
	private boolean eggTouched, eggFloating = true, nestHitEdge, eggHitsNest,spawnNewEgg;
	private float nestVelocityX = 5f;
	private TextureRegion mEgg1TextureRegion, mEgg2TextureRegion, mEgg3TextureRegion, mEgg4TextureRegion, mNestTextureRegion, mSlingTextureRegion,mCloudTextureRegion,mParallaxTextureRegion;
	private float spawnLocX = 186, spawnLocY = 500;
	private float shootAngle;
	private float impulseFactor=0.5f;
	private ChangeableText mScoreChangeableText;
	private Font mScoreFont;
	//private float delta_x, delta_y;
	private EggManager mEggManager;
	private Rectangle nestRect;
	Sprite mNestSprite;
	private int eggCount=0,eggType=1;
	private int mScore;
	private BitmapTextureAtlas mAutoParallaxBackgroundAtlas,mCloudAtlas,mBirdAtlas;
	private AutoParallaxBackground mAutoBackground;

	
	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public Engine onLoadEngine() {
		this.mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 200, 200, 1.0f);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		// engineOptions.setNeedsMusic(true).setNeedsSound(true);

		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		BitmapTextureAtlas mBAtlas = new BitmapTextureAtlas(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		mSlingTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "slingshot.png", 0, 0);
		mEgg1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "egg1.png", 256, 0);
		mEgg2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "egg2.png", 384, 0);
		mEgg3TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "egg3.png", 640, 0);
		mEgg4TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "egg4.png", 768, 0);
		
		mNestTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBAtlas, this.getApplicationContext(), "nest2.png", 256, 128);

		mBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getEngine().getTextureManager(), new AssetBitmapTextureAtlasSource(this,
				"gfx/background2.png"));

		this.getEngine().getTextureManager().loadTexture(mBAtlas);
		loadScore();
		loadParallax();
		mEggManager=new EggManager();
	}

	@Override
	public Scene onLoadScene() {
		mScene = new Scene();

		mScene.setBackground(this.mBackground);

		mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		mScene.registerUpdateHandler(mPhysicsWorld);

		mPhysicsWorld.setContactListener(mCollisionListener);
		HUD mHUD = new HUD();

		// Create the Score text showing how many points the player scoredand
		// attach to HUD
		mScoreChangeableText = new ChangeableText(35, 5, mScoreFont, "Score: 0", "Score: XXXX".length());
		mScoreChangeableText.setColor(0.3f, 0.5f, 0);
		mScoreChangeableText.setScale(1.5f);
		mScoreChangeableText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		mScoreChangeableText.setAlpha(0.9f);

		
		
		mHUD.attachChild(mScoreChangeableText);
		
		mCamera.setHUD(mHUD);
	initParallax();
		
		/*
		mEggSprite = new Sprite(spawnLocX, spawnLocY, mEgg1TextureRegion) {
			float oldX, oldY, newX, newY, maxDistance = 150;

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (eggFloating) {
					delta_x = pSceneTouchEvent.getX() - (spawnLocX) - this.getWidth() / 2;// Math.abs(pSceneTouchEvent.getX()-spawnLocX);
					delta_y = pSceneTouchEvent.getY() - (spawnLocY) - this.getHeight() / 2;// Math.abs(pSceneTouchEvent.getY()-spawnLocY);

					if ((Math.pow(delta_x, 2) + Math.pow(delta_y, 2)) > Math.pow(maxDistance, 2)) {
						float angle = (float) Math.atan2(delta_y, delta_x);
						shootAngle = angle;
						this.setPosition((spawnLocX) + (float) (maxDistance * Math.cos(angle)), (spawnLocY) + (float) (maxDistance * Math.sin(angle)));

					} else {

						this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					}

				}
				if (pSceneTouchEvent.isActionDown()) {
					eggTouched = true;

				}
				if (pSceneTouchEvent.isActionMove()) {

				}
				if (pSceneTouchEvent.isActionUp()) {
					eggFloating = false;
				}

				return true;
			}
		};
		mEggSprite.setScale(0.4f);
*/
		// mPhysicsWorld.registerPhysicsConnector(new
		// PhysicsConnector(mEggSprite, mEggBody));

		mNestSprite = new Sprite(112, 150, mNestTextureRegion);
		mNestSprite.setScale(0.8f);

		Sprite mSlingSprite = new Sprite(125, 400, mSlingTextureRegion);
		mSlingSprite.setScale(0.5f);

		FixtureDef mNestDef = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);

		nestRect = new Rectangle(mNestSprite.getX(), mNestSprite.getY() + mNestSprite.getHeight() / 2, mNestSprite.getWidth(), mNestSprite.getHeight() / 2);
		nestRect.setScale(0.8f);
		nestRect.setVisible(false);

		mNestSprite.setScaleY(0.3f);

		mNestBody = PhysicsFactory.createBoxBody(mPhysicsWorld, mNestSprite, BodyType.DynamicBody, mNestDef);
		mNestBody.setUserData("nest");
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mNestSprite, mNestBody));
		mNestSprite.setScaleY(0.8f);
		mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
				Log.d("TAG", "scene touch");
				return true;
			}
		});
		// mEggSprite=new Sprite(200, 200, mEgg1TextureRegion);
		mScene.attachChild(mNestSprite);
		mScene.attachChild(mSlingSprite);
		//mScene.attachChild(mEggSprite);
		
		mScene.attachChild(nestRect);
//			mScene.registerTouchArea(mEggSprite);

		initWalls();

		mScene.registerUpdateHandler(gameLoop);

		mScene.setTouchAreaBindingEnabled(true);
		mEggManager.add(new Egg(mScene, mEgg1TextureRegion, spawnLocX, spawnLocY,0.4f,"egg"+eggCount,spawnLocX,spawnLocY, 0.5f	, 0.4f, 1.0f));
		return mScene;
	}

	
	
	
	private ContactListener mCollisionListener=new ContactListener() {

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			// TODO Auto-generated method stub

		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			// TODO Auto-generated method stub

		}

		@Override
		public void endContact(Contact contact) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beginContact(Contact contact) {

			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();

			String bodyNameA = bodyA.getUserData().toString();
			String bodyNameB = bodyB.getUserData().toString();

			if ((bodyNameA.contains("wall") && bodyNameB.equalsIgnoreCase("nest")) || (bodyNameB.contains("wall") && bodyNameA.equalsIgnoreCase("nest"))) {
				nestHitEdge = true;
			}
			if ((bodyNameA.contains("wall_bottom") && bodyNameB.contains("egg"))
					|| (bodyNameB.contains("wall_bottom") && bodyNameA.contains("egg"))) {
				
				mEggManager.findEggByName(bodyNameB).setEggBounce(mEggManager.findEggByName(bodyNameB).getEggBounce()+1);
				
				//spawnNewEgg=true;
			}

			if ((bodyNameA.contains("egg") && bodyNameB.equalsIgnoreCase("nest"))
					|| (bodyNameB.contains("egg") && bodyNameA.equalsIgnoreCase("nest"))) {

				if (bodyA.getPosition().y > bodyB.getPosition().y)
					if (Math.abs(bodyA.getPosition().x - bodyB.getPosition().x) < 50 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT) {

						mEggManager.findEggByName(bodyNameB).setEggHitsNest(true);
					}
			}

		}
	};
	
	
	
	
	
	
	
	private IUpdateHandler gameLoop = new IUpdateHandler() {
		Random temp = new Random();

		@Override
		public void onUpdate(float pSecondsElapsed) {
			mNestBody.applyForce(0, -SensorManager.GRAVITY_EARTH, mNestBody.getPosition().x, mNestBody.getPosition().y);
			mNestBody.setLinearVelocity(nestVelocityX, 0);

			if (nestHitEdge) {
				nestVelocityX = -nestVelocityX;
				nestHitEdge = false;
			}

			
		
			if ((mEggManager.findEggByName("egg"+eggCount).isEggHitsNest())||(mEggManager.findEggByName("egg"+eggCount).getEggBounce()>1)) {
				
				if(mEggManager.findEggByName("egg"+eggCount).isEggHitsNest())
				{mScore += 10;}
				
				
				mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(mEggManager.findEggByName("egg"+eggCount).getEggSprite())); 
				mPhysicsWorld.destroyBody(mEggManager.findEggByName("egg"+eggCount).getEggBody());
				mScene.detachChild(mEggManager.findEggByName("egg"+eggCount).getEggSprite());
				
				mEggManager.findEggByName("egg"+eggCount).setEggHitsNest(false);
				
				eggCount++;
				eggType++;
				if(eggType>4)eggType=1;
				
				switch(eggType)
				{
				
				case 1:mEggManager.add(new Egg(mScene, mEgg1TextureRegion, spawnLocX, spawnLocY, 0.4f, "egg"+eggCount, spawnLocX, spawnLocY, 0.5f	, 0.4f, 1.0f));
						impulseFactor=0.4f;
						break;
				case 2:mEggManager.add(new Egg(mScene, mEgg2TextureRegion, spawnLocX, spawnLocY, 0.4f, "egg"+eggCount, spawnLocX, spawnLocY, 0.5f, 0.2f, 1.0f));
					impulseFactor=0.2f;
						break;
				case 3:mEggManager.add(new Egg(mScene, mEgg3TextureRegion, spawnLocX, spawnLocY, 0.4f, "egg"+eggCount, spawnLocX, spawnLocY, 0.5f, 0.8f, 1.0f));
					impulseFactor=0.8f;
						break;
				case 4:mEggManager.add(new Egg(mScene, mEgg4TextureRegion, spawnLocX, spawnLocY, 0.4f, "egg"+eggCount, spawnLocX, spawnLocY, 0.5f, 0.6f, 0.5f));
					impulseFactor=0.6f;
						break;
				}
				
				
				
				//mScene.reset();
				//WeldJointDef weldJDef = new WeldJointDef();
				//weldJDef.initialize(mEggManager.findEggByName("egg1").getEggBody(), mNestBody, mNestBody.getWorldCenter());
				//mPhysicsWorld.createJoint(weldJDef);
				//mEggManager.findEggByName("egg1").setEggHitsNest(false);
			}

			mScoreChangeableText.setText("Score: " + mScore);
		
			if(mEggManager.findEggByName("egg"+eggCount).isEggLaunched())
			{
				Egg egg=mEggManager.findEggByName("egg"+eggCount);
				
				egg.createEggBody(mPhysicsWorld);
				
				float delta_x=egg.getDelta_x();
				float delta_y=egg.getDelta_y();
				
				float temp = (float) Math.atan2(delta_y, delta_x);
				Log.d("TAG", "shoot  angle " + temp);
				egg.getEggBody().applyLinearImpulse(-Math.abs(delta_x) * impulseFactor * (float) Math.cos(temp), -Math.abs(delta_y) * impulseFactor* (float) Math.sin(temp),
						egg.getEggBody().getPosition().x,egg.getEggBody().getPosition().y);
				
				mScene.unregisterTouchArea(egg.getEggSprite());
				egg.setEggLaunched(false);
				
			}
			/*
			if (!eggFloating) {
				// /mCamera.setZoomFactor(0.5f);
				FixtureDef mEggDef = PhysicsFactory.createFixtureDef(0.5f, 1.0f, 1.0f);
				mEggBody = PhysicsFactory.createCircleBody(mPhysicsWorld, mEggSprite, BodyType.DynamicBody, mEggDef);
				mEggBody.setUserData("egg");
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mEggSprite, mEggBody));

				Log.d("TAH", "deltax: " + delta_x + " deltay: " + delta_y);

				float temp = (float) Math.atan2(delta_y, delta_x);
				Log.d("TAG", "shoot  angle " + temp);
				mEggBody.applyLinearImpulse(-Math.abs(delta_x) * 0.5f * (float) Math.cos(temp), -Math.abs(delta_y) * 0.5f * (float) Math.sin(temp),
						mEggBody.getPosition().x, mEggBody.getPosition().y);
				eggFloating = true;
				// mEggBody.applyForce(0, -SensorManager.GRAVITY_EARTH,
				// mEggBody.getPosition().x, mEggBody.getPosition().y);
				Log.d("TAG", " egg xy: " + mEggBody.getPosition().x + " " + mEggBody.getPosition().y + " nest xy: " + mNestBody.getPosition().x + " "
						+ mNestBody.getPosition().y);
			}
*/
			if (eggTouched) {
				// mEggBody.applyLinearImpulse(0, -12, mEggBody.getPosition().x,
				// mEggBody.getPosition().y);

				// mEggBody.applyLinearImpulse(0, -10, mEggBody.getPosition().x,
				// mEggBody.getPosition().y);
				eggTouched = false;
			}
			// else mEggBody.setLinearVelocity(0, 0);
			// mNestSprite.setPosition(nestRect.getX(),
			// nestRect.getY()-nestRect.getHeight());
		}

		@Override
		public void reset() {

		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("action", "touch action down");
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			eggTouched = true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			eggTouched = false;
		}

		return super.onTouchEvent(event);
	}

	public void initWalls() {
		FixtureDef mWallDef = PhysicsFactory.createFixtureDef(0.5f, 0.5f, 0.5f);
		// left,top,right,bottom
		Rectangle wallLeft = new Rectangle(-1, 0, 1f, CAMERA_HEIGHT);
		Rectangle wallRight = new Rectangle(CAMERA_WIDTH, 0, 1f, CAMERA_HEIGHT);
		Rectangle wallTop = new Rectangle(0, -1, CAMERA_WIDTH, 1f);
		Rectangle wallBottom = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 1f);

		PhysicsFactory.createBoxBody(mPhysicsWorld, wallLeft, BodyType.StaticBody, mWallDef).setUserData("wall_left");
		PhysicsFactory.createBoxBody(mPhysicsWorld, wallRight, BodyType.StaticBody, mWallDef).setUserData("wall_right");
		PhysicsFactory.createBoxBody(mPhysicsWorld, wallTop, BodyType.StaticBody, mWallDef).setUserData("wall_top");
		PhysicsFactory.createBoxBody(mPhysicsWorld, wallBottom, BodyType.StaticBody, mWallDef).setUserData("wall_bottom");

		// mPhysicsWorld.registerPhysicsConnector(new
		// PhysicsConnector(wallBottom, wallBottomBody));

		// mEggSprite=new Sprite(200, 200, mEgg1TextureRegion);
		mScene.attachChild(wallLeft);
		mScene.attachChild(wallRight);
		mScene.attachChild(wallTop);
		mScene.attachChild(wallBottom);

	}
	
	
	public void loadScore()
	{	FontFactory.setAssetBasePath("font/");
		BitmapTextureAtlas mScoreTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mScoreFont = FontFactory.createFromAsset(mScoreTextureAtlas, this, "AltamonteNF.ttf", 32, true, Color.WHITE);

		this.getEngine().getTextureManager().loadTexture(mScoreTextureAtlas);
		this.getEngine().getFontManager().loadFont(mScoreFont);
	}
	public void loadParallax(){
		mAutoParallaxBackgroundAtlas=new BitmapTextureAtlas(1024, 1024,TextureOptions.BILINEAR_PREMULTIPLYALPHA );
		mParallaxTextureRegion= BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundAtlas,this.getApplicationContext(),"background2.png",0,0);
		mAutoBackground=new AutoParallaxBackground(100.0f, 120.0f, 150.0f, 100);

		
		mCloudAtlas= new BitmapTextureAtlas(512, 512,TextureOptions.BILINEAR_PREMULTIPLYALPHA );
		mCloudTextureRegion=BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCloudAtlas,this.getApplicationContext(),"clouds1.png",0,0);
	
		this.getEngine().getTextureManager().loadTexture(mAutoParallaxBackgroundAtlas);
		this.getEngine().getTextureManager().loadTexture(mCloudAtlas);

	}

	public void initParallax(){
		
		Sprite mSprite= new Sprite(1.0f, 0, this.mCloudTextureRegion);
		mSprite.setScale(0.5f);
		mAutoBackground.attachParallaxEntity(new ParallaxEntity(-0.7f, new Sprite(0, 0, this.mParallaxTextureRegion)));
		mAutoBackground.attachParallaxEntity(new ParallaxEntity(-0.3f,mSprite));
		
		mScene.setBackground(this.mAutoBackground);

	}
	

}
