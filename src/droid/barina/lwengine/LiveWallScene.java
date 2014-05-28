package droid.barina.lwengine;

import java.util.ArrayList;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import droid.barina.dotslw.DancingDotsLiveWall;
import droid.barina.dotslw.DancingDotsLiveWallSettings;

public class LiveWallScene extends Scene
{
	private static Sprite BG_SPRITE;

	public static void LoadResources(Context context)
	{
		TextureRegion textureRegion = TextureRegionFactory.createFromAsset(DancingDotsLiveWall.GET_TEXTURE(), context, "bg_light.png", 0, 0);
		BG_SPRITE = new Sprite(0, 0, DancingDotsLiveWall.CAMERA_WIDTH, DancingDotsLiveWall.CAMERA_HEIGHT, textureRegion);
		BG_SPRITE.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	private static LiveWallScene sharedInstance;

	public static LiveWallScene getSharedInstance()
	{
		if(sharedInstance == null)
			sharedInstance = new LiveWallScene();
		return sharedInstance;
	}

	public static void releaseSharedInstance()
	{
		sharedInstance = null;
	}

	private ArrayList<Dot> dots;
	private float distance;

	public float getDistance()
	{
		return distance;
	}

	public void setDistance(float value)
	{
		this.distance = value;
	}

	private ColorBackground background;
	private int gap;

	private LiveWallScene()
	{
		super(2);
		getLayer(0).addEntity(BG_SPRITE);
		dots = new ArrayList<Dot>();
		this.gap = (int)(DancingDotsLiveWall.CAMERA_WIDTH * .5f);
		setBackground(background = new ColorBackground(0.7f, 0.5f, 0.0f));
	}

	public void RePopulateDots()
	{
		KillAllDots();
		int width = DancingDotsLiveWall.CAMERA_WIDTH, height = DancingDotsLiveWall.CAMERA_HEIGHT;
		Random r = new Random();
		for(int i = 0 ; i < width / gap ; i++)
		{
			for(int j = 0 ; j < height / gap ; j++)
			{
				Dot dot = new Dot(i * gap + gap * 0.5f, j * gap + gap * 0.5f, 20 + r.nextInt(20));
				dot.moveTo(DancingDotsLiveWall.CAMERA_WIDTH * .5f + (-50 + r.nextInt(100)), DancingDotsLiveWall.CAMERA_HEIGHT * .5f + (-50 + r.nextInt(100)));
				this.dots.add(dot);
				getLayer(1).addEntity(dot.getSprite());
				registerPostFrameHandler(dot);
			}
		}
	}

	public void SetBackgroundColor(int color)
	{
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		float[] rgb = DancingDotsLiveWallSettings.HSVtoRGB(hsv);
		this.background = new ColorBackground(rgb[0], rgb[1], rgb[2]);
		setBackground(this.background);
	}

	public void Animate(float posX, float posY)
	{
		int camWidth = DancingDotsLiveWall.CAMERA_WIDTH, camHeight = DancingDotsLiveWall.CAMERA_HEIGHT;
		int screenWidth = DancingDotsLiveWall.GET_SCREEN_WIDTH(), screenHeight = DancingDotsLiveWall.GET_SCREEN_HEIGHT();
		float deltaX = posX / screenWidth, deltaY = posY / screenHeight;
		posX = camWidth * deltaX;
		posY = camHeight * deltaY;
		for(Dot dot : this.dots)
		{
			float x2 = dot.getSprite().getX(), y2 = dot.getSprite().getY();
			double distance = Math.sqrt((posX - x2) * (posX - x2) + (posY - y2) * (posY - y2));
			if(distance <= this.distance)
				dot.moveAway(posX, posY, this.distance);
		}
	}

	public void KillAllDots()
	{
		for(Dot dot : this.dots)
			getLayer(1).removeEntity(dot.getSprite());
		this.dots.clear();
	}

	public void setGap(int gap)
	{
		Log.d("LiveWallScene", "Old gap " + this.gap + " new gap " + gap);
		if(this.gap != gap)
		{
			this.gap = gap;
			RePopulateDots();
		}
	}

	public void SetBackgroundShadesVisible(boolean pVisible)
	{
		BG_SPRITE.setVisible(pVisible);
	}
}