package utopia.genesis.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utopia.genesis.event.Drawable;
import utopia.inception.handling.Handled;
import utopia.inception.util.ConnectedHandled;

/**
 * An object can use a drawer to draw stuff on screen according to its transformation(s).
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @param <T> The type of object that uses this drawer
 */
public abstract class DependentDrawer<T extends Transformable & Handled> extends 
		ConnectedHandled<T> implements Drawable, Transformable
{
	// ATTRIBUTES	------------------------------
	
	private Transformation transformation = Transformation.IDENTITY;
	private int depth;
	private float alpha = 1;
	private Vector3D origin;
	
	
	// CONSTRUCTOR	-----------------------------
	
	/**
	 * Creates a new drawer. The drawer's visibility will depend from the user's activity.
	 * @param user The user that will use the drawer. The drawer's state (activity and visibility) 
	 * will be tied to that of the user.
	 * @param origin The origin the drawer uses
	 * @param initialDepth How deep the drawer draws stuff
	 */
	public DependentDrawer(T user, Vector3D origin, int initialDepth)
	{
		super(user);
		this.depth = initialDepth;
		this.origin = origin;
	}
	
	
	// ABSTRACT METHODS	----------------------------
	
	/**
	 * Draws the stuff this drawer is supposed to draw. The transformations have already been 
	 * applied at this point so the subclass shouldn't apply them again.
	 * @param g2d The graphics object that does the actual drawing
	 */
	protected abstract void drawSelfBasic(Graphics2D g2d);
	
	
	// IMPLEMENTED METHODS	------------------------

	@Override
	public void drawSelf(Graphics2D g2d)
	{
		if (getAlpha() != 1)
			Drawable.setDrawAlpha(g2d, getAlpha());
		
		AffineTransform lastTransform = g2d.getTransform();
		// Applies combined transformation
		g2d.transform(getCombinedTransformation().toAffineTransform());
		// Applies origin as well
		g2d.translate(-getOrigin().getX(), -getOrigin().getY());
		
		drawSelfBasic(g2d);
		
		g2d.setTransform(lastTransform);
		
		if (getAlpha() != 1)
			Drawable.setDrawAlpha(g2d, 1);
	}

	@Override
	public int getDepth()
	{
		return this.depth;
	}

	/**
	 * Changes the drawer's drawing depth
	 * @param depth The drawer's new drawing depth
	 */
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	@Override
	public Transformation getTransformation()
	{
		return this.transformation;
	}

	@Override
	public void setTrasformation(Transformation t)
	{
		this.transformation = t;
	}
	
	
	// GETTERS & SETTERS	--------------------------
	
	/**
	 * @return The opacity / alpha value of the object [0, 1]
	 */
	public float getAlpha()
	{
		return this.alpha;
	}
	
	/**
	 * Changes the object's opacity / alpha
	 * @param alpha The new alpha / opacity value of the object [0, 1]
	 */
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
		
		if (getAlpha() < 0)
			this.alpha = 0;
		if (getAlpha() > 1)
			this.alpha = 1;
	}
	
	/**
	 * @return The origin used in the drawing
	 */
	public Vector3D getOrigin()
	{
		return this.origin;
	}
	
	/**
	 * Changes the drawer's origin
	 * @param origin The origin used in the drawing
	 */
	public void setOrigin(Vector3D origin)
	{
		this.origin = origin;
	}
	
	
	// OTHER METHODS	------------
	
	/**
	 * @return The object's transformation, affected by the master object's transformation
	 */
	public Transformation getCombinedTransformation()
	{
		return getMaster().getTransformation().transform(getTransformation());
	}
	
	/**
	 * Modifies the drawer's transformation according to the provided transformation
	 * @param t How the drawer's transformation is transformed
	 */
	public void transform(Transformation t)
	{
		setTrasformation(getTransformation().plus(t));
	}
}
