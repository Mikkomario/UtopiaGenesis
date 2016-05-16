package utopia.genesis.event;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import utopia.inception.handling.Handled;

/**
 * All objects which implement this class can be drawn. Each also has a depth 
 * that specifies the layer to which the object is drawn. Drawables can also be 
 * made temporarily and permanently invisible.
 *
 * @author Mikko Hilpinen.
 * @since 26.11.2012.
 */
public interface Drawable extends Handled
{
	/**
	 * Draws the object
	 *
	 * @param g2d The graphics object that will draw the object
	 */
	public void drawSelf(Graphics2D g2d);
	
	/**
	 * @return How deep should the object be drawn (object with positive depth 
	 * are drawn to the bottom, objects with negative depth are drawn to the top)
	 * @see utopia.genesis.util.DepthConstants
	 */
	public int getDepth();
	
	/**
	 * Changes the drawing alpha value of a graphics object
	 * @param g2d A graphics object
	 * @param alpha The alpha set to the graphics object
	 */
	public static void setDrawAlpha(Graphics2D g2d, float alpha)
	{
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}
}
