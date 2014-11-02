package genesis_graphic;

import genesis_logic.Handled;

import java.awt.Graphics2D;

/**
 * All objects which implement this class can be drawn. Each also has a depth 
 * that specifies the layer to which the object is drawn. Drawables can also be 
 * made temporarily and permanently invisible.
 *
 * @author Mikko Hilpinen.
 *         Created 26.11.2012.
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
	 * @return Should the object be drawn at this time
	 */
	public boolean isVisible();
	
	/**
	 * Tries to set the object visible
	 */
	public void setVisible();
	
	/**
	 * Tries to momentarily make the object invisible
	 */
	public void setInvisible();
	
	/**
	 * @return How deep should the object be drawn (object with positive depth 
	 * are drawn to the bottom, objects with negative depth are drawn to the top)
	 * @see genesis_graphic.DepthConstants
	 */
	public int getDepth();
	
	/**
	 * Tries to change the objects depth
	 * 
	 * @param depth The object's new depth (negative = top, positive = bottom)
	 * @return Was the object's depth successfully changed
	 * @see genesis_graphic.DepthConstants
	 */
	public boolean setDepth(int depth);
}
