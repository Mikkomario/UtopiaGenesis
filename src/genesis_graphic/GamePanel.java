package genesis_graphic;

import genesis_util.Vector2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Gamepanel is a single panel in the gamescreen that draws numerous drawables. 
 * Gamepanels are used in GameWindows
 * 
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see GameWindow
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel
{
	// ATTRIBUTES ---------------------------------------------------------
	
	private Vector2D dimensions, scaling;
	private DrawableHandler drawer;
	private boolean clearPrevious;
	
	/**
	 * Should the previous drawings on a panel be cleared before new image will be drawn
	 */
	public static boolean clearDisabled = false;
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new panel
	 * @param dimensions How large the panel is by default scaling of 1
	 */
	public GamePanel(Vector2D dimensions)
	{
		// Initializes attributes
		this.scaling = Vector2D.identityVector();
		this.dimensions = dimensions;
		this.clearPrevious = true;
		
		this.drawer = new DrawableHandler(false, true, DepthConstants.NORMAL, 5);
		
		//Let's format our panel
		this.formatPanel();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public void paintComponent(Graphics g)
	{
		// The panel draws all stuff inside it
		Graphics2D g2d = (Graphics2D) g;
		
		// Scales the area of drawing
		if (!this.scaling.equalsApproximately(Vector2D.identityVector()))
			g2d.scale(this.scaling.getFirst(), this.scaling.getSecond());
		
		// Clears the former drawings
		if (!clearDisabled && this.clearPrevious)
			g2d.clearRect(0, 0, (int) this.dimensions.getFirst(), 
					(int) this.dimensions.getSecond());
		
		this.drawer.drawSelf(g2d);
	}
	
	
	// PRIVATE METHODS ---------------------------------------------------
	
	private void formatPanel()
	{
		//Let's set the panel's size...
		this.setSizes(this.dimensions);
		//And make it visible
		this.setVisible(true);
	}
	
	
	// OTHER METHODS ---------------------------------------------------
	
	/**
	 * Previous images won't be removed before a new image is drawn after this method has 
	 * been called.
	 */
	public void disableClear()
	{
		this.clearPrevious = false;
	}
	
	/**
	 * Previous images will be cleared away before a new image is drawn. This is the default 
	 * state of a panel, although using {@link #clearDisabled} overrides this.
	 */
	public void enableClear()
	{
		this.clearPrevious = true;
	}
	
	/**
	 * Changes the size of the game panel.
	 * @param dimensions The new sizes of the panel (in pixels)
	 */
	public void setSizes(Vector2D dimensions)
	{
		this.setSize(dimensions.toDimension());
		Dimension preferred = dimensions.toDimension();
		this.setPreferredSize(preferred);
		this.setMinimumSize(preferred);
		this.setMaximumSize(preferred);
	}
	
	/**
	 * Changes the panel's background color.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setBackgroundColor(int red, int green, int blue)
	{
		this.setBackground(new Color(red, green, blue));
	}
	
	/**
	 * Makes the panel visible.
	 */
	public void makeVisible()
	{
		this.setVisible(true);
	}
	
	/**
	 * Makes the panel invisible.
	 */
	public void makeInvisible()
	{
		this.setVisible(false);
	}
	
	/**
	 * @return The drawablehandler that draws the content of this panel
	 */
	public DrawableHandler getDrawer()
	{
		return this.drawer;
	}
	
	/**
	 * Scales the panel, keeping the same resolution but changing the size 
	 * of the area. The scaling is relative to the former scaling of the panel
	 * @param scaling How much the panel is scaled
	 */
	protected void scale(Vector2D scaling)
	{
		// The scaling is relative to the former scaling
		setScale(this.scaling.times(scaling));
	}
	
	/**
	 * Scales the panel, keeping the same resolution but changing the size 
	 * of the area
	 * @param newScaling The panel's new scaling
	 */
	protected void setScale(Vector2D newScaling)
	{
		// Remembers the scaling
		this.scaling = newScaling;
		
		// Resizes the panel
		setSizes(this.dimensions.times(this.scaling));
	}
}
