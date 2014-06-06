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
	private final float initX, initY, maxVel, accelerate;
	private float baseX, baseY, velocityX, velocityY;
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
		this(pX, pY, maxVel, 90);
	}

	public Dot(float pX, float pY, float maxVel, float accelerate)
	{
		super();
		this.baseX = this.initX = pX;
		this.baseY = this.initY = pY;
		this.accelerate = accelerate;
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
//		float x = getSprite().getX();
//		float y = getSprite().getY();
//		if(x == getBaseX() && this.velocityX == 0 && y == getBaseY() && this.velocityY == 0)
//			return;
//		if(x > getBaseX())
//			this.velocityX -= 90f * pSecondsElapsed;
//		else
//			this.velocityX += 90f * pSecondsElapsed;
//		if(y > getBaseY())
//			this.velocityY -= 90f * pSecondsElapsed;
//		else
//			this.velocityY += 90f * pSecondsElapsed;
//		if(this.velocityX > this.maxVel)
//			this.velocityX = this.maxVel;
//		else
//			if(this.velocityX < -this.maxVel)
//				this.velocityX = -this.maxVel;
//		if(this.velocityY > this.maxVel)
//			this.velocityY = this.maxVel;
//		else
//			if(this.velocityY < -this.maxVel)
//				this.velocityY = -this.maxVel;
//		getSprite().setVelocity(this.velocityX, this.velocityY);
//		final float step = pSecondsElapsed;
//		PointF delta = getDistanceFromBase();
//		Vector2 velocity = getChaserEntity().getBody().getLinearVelocity();
//		if(chaserCenterX() > chasedEntity.getCenter().x)
//		{// Within every case dividing by the Delta calculated above.
//			if(velocity.x > -(maxVelocity.x / delta.x))
//				velocity.x -= step;
//		}
//		else
//		{
//			if(velocity.x < maxVelocity.x / delta.x)
//				velocity.x += step;
//		}
//		if(chaserCenterY() > chasedEntity.getCenter().y)
//		{
//			if(velocity.y > -(maxVelocity.y / delta.y))
//				velocity.y -= step;
//		}
//		else
//		{
//			if(velocity.y < maxVelocity.y / delta.y)
//				velocity.y += step;
//		}
//		getChaserEntity().getBody().setLinearVelocity(velocity);
		PointF delta = getDistanceMultiplierFromBase();
		float x = getCenterX();
		float y = getCenterY();
		if(x >= getBaseX())
		{
			if(velocityX > -(maxVel / delta.x))
				this.velocityX -= accelerate * pSecondsElapsed;
		}
		else
		{
			if(velocityX < maxVel / delta.x)
				this.velocityX += accelerate * pSecondsElapsed;
		}
		if(y >= getBaseY())
		{
			if(velocityY > -(maxVel / delta.y))
				this.velocityY -= accelerate * pSecondsElapsed;
		}
		else
		{
			if(velocityY < maxVel / delta.y)
				this.velocityY += accelerate * pSecondsElapsed;
		}
		getSprite().setVelocity(velocityX, velocityY);
		//TODO: fix god damn stupid asshole Y velocity!!!
	}

	public void moveTo(float x, float y)
	{
		getSprite().setPosition(x, y);
	}

	public float getCenterX()
	{
		return getSprite().getX() + getSprite().getWidth() * .5f;
	}

	public float getCenterY()
	{
		return getSprite().getY() + getSprite().getHeight() * .5f;
	}

	public PointF getCenterPosition()
	{
		PointF center = new PointF();
		center.x = getCenterX();
		center.y = getCenterY();
		return center;
	}

	public float getInitX()
	{
		return initX;
	}

	public float getInitY()
	{
		return initY;
	}

	public float getBaseX()
	{
		return baseX;
	}

	public void setBaseX(float baseX)
	{
		this.baseX = baseX;
	}

	public float getBaseY()
	{
		return baseY;
	}

	public void setBaseY(float baseY)
	{
		this.baseY = baseY;
	}

	public void resetBaseToInitialValues()
	{
		this.baseX = this.initX;
		this.baseY = this.initY;
	}

	public PointF getDistanceMultiplierFromBase()
	{
		PointF dis = new PointF(), delta = new PointF();
		// Retrieving distance per axis.
		dis.x = Math.abs(getBaseX() - getSprite().getX());
		dis.y = Math.abs(getBaseY() - getSprite().getY());
		if(dis.x > dis.y)// Detect which is smaller then calculate its percents
							// from the bigger distance
		{
			delta.x = 1;
			delta.y = dis.x / dis.y;
		}
		else
		{
			delta.y = 1;
			delta.x = dis.y / dis.x;
		}
		return delta;
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
		this.moveTo(newPoint.x, newPoint.y);
	}
}