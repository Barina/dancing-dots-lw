package droid.barina.lwengine;

import java.util.Random;
import javax.microedition.khronos.opengles.GL10;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import android.graphics.PointF;

public class Dot extends Entity
{
	public static TextureRegion TEXTURE_REGION;
	private static Random random = new Random();
	private final float initX, initY, maxVel;
	private float velocityX, velocityY;
	private final Sprite sprite;

	public Sprite getSprite()
	{
		return this.sprite;
	}

	public Dot(float pX, float pY)
	{
		this(pX, pY, 30);
	}

	public Dot(float pX, float pY, float maxVel)
	{
		super();
		this.initX = pX;
		this.initY = pY;
		this.maxVel = maxVel;
		this.velocityX = this.maxVel * random.nextFloat();
		this.velocityY = this.maxVel * random.nextFloat();
		this.sprite = new Sprite(pX, pY, 16, 16, TEXTURE_REGION);
		this.sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// this.sprite.setRotation(180);
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append("current dot position at ");
		string.append(getSprite().getX());
		string.append("x");
		string.append(getSprite().getY());
		string.append(" current dot velocity ");
		string.append(this.velocityX);
		string.append("x");
		string.append(this.velocityY);
		return string.toString();
	}

	@Override
	protected void onManagedDraw(GL10 arg0, Camera arg1)
	{
		// TODO Auto-generated method stub\
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		float x = getSprite().getX();
		float y = getSprite().getY();
		if(x == this.initX && this.velocityX == 0 && y == this.initY && this.velocityY == 0)
			return;
		if(x > this.initX)
			this.velocityX -= 90f * pSecondsElapsed;
		else
			this.velocityX += 90f * pSecondsElapsed;
		if(y > this.initY)
			this.velocityY -= 90f * pSecondsElapsed;
		else
			this.velocityY += 90f * pSecondsElapsed;
		if(this.velocityX > this.maxVel)
			this.velocityX = this.maxVel;
		else
			if(this.velocityX < -this.maxVel)
				this.velocityX = -this.maxVel;
		if(this.velocityY > this.maxVel)
			this.velocityY = this.maxVel;
		else
			if(this.velocityY < -this.maxVel)
				this.velocityY = -this.maxVel;
		getSprite().setVelocity(this.velocityX, this.velocityY);
	}

	public void moveTo(float x, float y)
	{
		getSprite().setPosition(x, y);
	}

	/**
	 * Will move the dot away from a specific point on its axis by a specific
	 * distance.
	 * 
	 * @author Haim Shalom
	 * @since 22.05.2014
	 * @param x1
	 *            The ground x coordinate.
	 * @param y1
	 *            The ground y coordinate.
	 * @param distance
	 *            The distance between ground point to the target.
	 */
	public void moveAway(float x1, float y1, double distance)
	{
		float x2 = sprite.getX();
		float y2 = sprite.getY();
		// double m = (y2 - y1) / (x2 - x1);
		double d = Math.sqrt((Math.pow(x1 - x2, 2)) + Math.pow(y1 - y2, 2));
		PointF newPoint = new PointF((int)(x1 + ((distance / d) * (x2 - x1))), (int)(y1 + ((distance / d) * (y2 - y1))));
		this.moveTo(newPoint.x, newPoint.y);;
	}
}