package genesis_graphic;

import genesis_util.Handled;
import genesis_util.StateOperator;

import java.awt.Graphics2D;

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
	 * @return The stateOperator that determines whether the object should be drawn
	 */
	public StateOperator getIsVisibleStateOperator();
	
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
	 * @see genesis_graphic.DepthConstants
	 */
	public void setDepth(int depth);
}
