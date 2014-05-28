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
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import droid.barina.lwengine.Dot;
import droid.barina.lwengine.LiveWallScene;

public class DancingDotsLiveWall extends BaseLiveWallpaperService
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
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions eo = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		eo.getRenderOptions().disableExtensionVertexBufferObjects();
		return new org.anddev.andengine.engine.Engine(eo);
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
	protected void onTap(final int pX, final int pY)
	{
		getLiveWallScene().Animate(pX, pY);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
//		Map<String, ?> map = settings.getAll();
//		Set<String> keys = map.keySet();
//		Log.d("DancingDotsLiveWall", "======== list of keys and values from preference object: ========");
//		for(String key : keys)
//		{
//			String value = map.get(key).toString();
//			Log.d("DancingDotsLiveWall", key + " = " + value + " value is " + value.getClass());
//		}
//		Log.d("DancingDotsLiveWall", "=================================================================");
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
}