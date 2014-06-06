package droid.barina.dotslw;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.opengl.view.RenderSurfaceView.Renderer;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import droid.barina.andengineforum.IOffsetsChanged;
import droid.barina.lwengine.Dot;
import droid.barina.lwengine.LiveWallScene;

public class DancingDotsLiveWall extends BaseLiveWallpaperService implements IOffsetsChanged
{
	// ===========================================================
	// Constants
	// ===========================================================
	// Camera Constants
	public static final int CAMERA_WIDTH = 1080;
	public static final int CAMERA_HEIGHT = 1920;
	private static int SCREEN_WIDTH, SCREEN_HEIGHT;
	private static Texture TEXTURE;
	// ===========================================================
	// Fields
	// ===========================================================
	private LiveWallScene wallScene;
	private SharedPreferences settings;
	Camera mCamera, mPortraitCamera, mLandscapeCamera;

	// msg's...
	// ===========================================================
	// Constructors
	// ===========================================================
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static final int GET_SCREEN_WIDTH()
	{
		return SCREEN_WIDTH;
	}

	public static final int GET_SCREEN_HEIGHT()
	{
		return SCREEN_HEIGHT;
	}

	public static Texture GET_TEXTURE()
	{
		return TEXTURE;
	}

	public LiveWallScene getLiveWallScene()
	{
		if(this.wallScene == null)
			this.wallScene = LiveWallScene.getSharedInstance();
		return this.wallScene;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine()
	{
		WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		SCREEN_WIDTH = display.getWidth();
		SCREEN_HEIGHT = display.getHeight();
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions eo = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		eo.getRenderOptions().disableExtensionVertexBufferObjects();
		return new org.anddev.andengine.engine.Engine(eo);
	}

	@Override
	public Engine onCreateEngine()
	{
		return new MyBaseWallpaperGLEngine(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		int orient = newConfig.orientation;
		switch (orient)
		{
			case Configuration.ORIENTATION_PORTRAIT:
				Log.d("", "screen orientation changed to portrait");
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				Log.d("", "screen orientation changed to landscape");
				break;
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		// this.settings = PreferenceManager.getDefaultSharedPreferences(this);
		this.settings = getSharedPreferences(getString(R.string.shared_prefs_name), 0);
	}

	@Override
	public void onLoadResources()
	{
		// Set the Base Texture Path
		TextureRegionFactory.setAssetBasePath("gfx/");
		TEXTURE = new Texture(2048, 2048, TextureOptions.DEFAULT);
		getEngine().getTextureManager().loadTexture(TEXTURE);
		LiveWallScene.LoadResources(getApplicationContext());
		Dot.TEXTURE_REGION = TextureRegionFactory.createFromAsset(TEXTURE, getApplicationContext(), "dot_bg.png", CAMERA_WIDTH + 1, 0);
	}

	@Override
	public Scene onLoadScene()
	{
		getLiveWallScene().RePopulateDots();
		return getLiveWallScene();
	}

	@Override
	public void onLoadComplete()
	{}

	@Override
	public void onTap(final int pX, final int pY)
	{
		getLiveWallScene().Animate(pX, pY);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// Map<String, ?> map = settings.getAll();
		// Set<String> keys = map.keySet();
		// Log.d("DancingDotsLiveWall",
		// "======== list of keys and values from preference object: ========");
		// for(String key : keys)
		// {
		// String value = map.get(key).toString();
		// Log.d("DancingDotsLiveWall", key + " = " + value + " value is " +
		// value.getClass());
		// }
		// Log.d("DancingDotsLiveWall",
		// "=================================================================");
		int color = 0, gap = 0, radius = 0, def = 0;
		color = settings.getInt(getString(R.string.key_bgcolor_setting), getResources().getColor(R.color.default_bgcolor));
		def = getResources().getInteger(R.integer.default_gap);
		gap = Integer.parseInt(settings.getString(getString(R.string.key_gap_setting), "" + def));
		def = getResources().getInteger(R.integer.default_radius);
		radius = Integer.parseInt(settings.getString(getString(R.string.key_radius_setting), "" + def));
		boolean defDraw = getResources().getString(R.string.default_drawshades).equals("true");
		boolean drawshades = settings.getBoolean(getString(R.string.key_drawshades_setting), defDraw);
		getLiveWallScene().SetBackgroundColor(color);
		getLiveWallScene().setGap(gap);
		getLiveWallScene().setDistance(radius);
		getLiveWallScene().SetBackgroundShadesVisible(drawshades);
		Log.d("DancingDotsLiveWall", "color " + color + ". gap " + gap + ". radius " + radius);
	}

	@Override
	public void onDestroy()
	{
		getLiveWallScene().KillAllDots();
		this.wallScene = null;
		LiveWallScene.releaseSharedInstance();
		getEngine().getTextureManager().unloadTexture(TEXTURE);
		super.onDestroy();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	@Override
	public void offsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
	{
		//TODO: fix overall position
		if(mCamera != null)
		{
			// Emulator has 3 screens
			mCamera.setCenter(((960 * xOffset) - 240), mCamera.getCenterY());
			/*
			 * formel mCamera.setCenter(( (Camera-WIDTH * (screensCount-1)) *
			 * xOffset ) - (Camera-WIDTH / 2) ,mCamera.getCenterY() );
			 */
		}
//		getLiveWallScene().SetBackgroundPosition(xPixelOffset, 0);
		getLiveWallScene().rebaseDotsPosition(xPixelOffset, yPixelOffset);
		Log.d(LiveWallScene.TAG, "offset params: xOffset = "+xOffset+
				" yOffset = "+yOffset+
				" xOffsetStep = "+xOffsetStep+
				" yOffsetStep = "+yOffsetStep+ 
				" xPixelOffset = "+xPixelOffset+
				" yPixelOffset = "+yPixelOffset);
	}

	public class MyBaseWallpaperGLEngine extends GLEngine
	{
		// ===========================================================
		// Fields
		// ===========================================================
		private Renderer mRenderer;
		private IOffsetsChanged mOffsetsChangedListener = null;

		// ===========================================================
		// Constructors
		// ===========================================================
		public MyBaseWallpaperGLEngine(IOffsetsChanged pOffsetsChangedListener)
		{
			this.setEGLConfigChooser(false);
			this.mRenderer = new RenderSurfaceView.Renderer(DancingDotsLiveWall.this.mEngine);
			this.setRenderer(this.mRenderer);
			this.setRenderMode(RENDERMODE_CONTINUOUSLY);
			this.mOffsetsChangedListener = pOffsetsChangedListener;
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		@Override
		public Bundle onCommand(final String pAction, final int pX, final int pY, final int pZ, final Bundle pExtras, final boolean pResultRequested)
		{
			if(pAction.equals(WallpaperManager.COMMAND_TAP))
			{
				DancingDotsLiveWall.this.onTap(pX, pY);
			}
			else
				if(pAction.equals(WallpaperManager.COMMAND_DROP))
				{
					DancingDotsLiveWall.this.onDrop(pX, pY);
				}
			return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested);
		}

		@Override
		public void onResume()
		{
			super.onResume();
			DancingDotsLiveWall.this.getEngine().onResume();
			DancingDotsLiveWall.this.onResume();
		}

		@Override
		public void onPause()
		{
			super.onPause();
			DancingDotsLiveWall.this.getEngine().onPause();
			DancingDotsLiveWall.this.onPause();
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			if(this.mRenderer != null)
			{
				// mRenderer.release();
			}
			this.mRenderer = null;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
		{
			// TODO Auto-generated method stub
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
			if(this.mOffsetsChangedListener != null)
				this.mOffsetsChangedListener.offsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
		}
	}
}