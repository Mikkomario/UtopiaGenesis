package genesis_video;

import genesis_event.DrawableHandler;
import genesis_util.DepthConstants;
import genesis_util.Vector3D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * Gamepanel is a single panel in a main panel that draws numerous drawable objects.
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see MainPanel
 */
public class GamePanel extends JPanel
{
	// ATTRIBUTES ---------------------------------------------------------
	
	private static final long serialVersionUID = -6510794439938372969L;
	
	private Vector3D scaling;
	private DrawableHandler drawer;
	private boolean clearPrevious;
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new game panel
	 * @param size the initial size of the panel in pixels. The in-game size of the panel can 
	 * further be modified by scaling.
	 */
	public GamePanel(Dimension size)
	{
		// Initializes attributes
		this.scaling = Vector3D.identityVector();
		this.clearPrevious = true;
		
		this.drawer = new DrawableHandler(true, DepthConstants.NORMAL, 5);
		
		//Formats the panel
		setLayout(null);
		setSize(size);
		setVisible(true);
		setBackground(Color.WHITE);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// The panel draws all stuff inside it
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform previousTransform = g2d.getTransform();
		
		// Scales the area of drawing
		if (!this.scaling.equals(Vector3D.identityVector()))
			g2d.scale(this.scaling.getFirst(), this.scaling.getSecond());
		
		// Clears the former drawings (optional)
		if (this.clearPrevious)
		{
			g2d.clearRect(0, 0, getWidth(), getHeight());
		
			// Draws the background as well
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		g.setColor(Color.BLACK);
		this.drawer.drawSelf(g2d);
		
		g2d.setTransform(previousTransform);
	}
	
	
	// OTHER METHODS ---------------------------------------------------
	
	/**
	 * Changes whether the previous drawings should be cleared before new are drawn
	 * @param clearEnabled Should previous drawings be cleared before new are drawn
	 */
	public void setClearEnabled(boolean clearEnabled)
	{
		this.clearPrevious = clearEnabled;
	}
	
	/**
	 * @return The drawablehandler that draws the content of this panel
	 */
	public DrawableHandler getDrawer()
	{
		return this.drawer;
	}
	
	/**
	 * Scales the panel's in-game size, keeping the same resolution. The scaling is relative 
	 * to the former scaling of the panel
	 * @param scaling How much the panel is scaled
	 */
	public void scale(Vector3D scaling)
	{
		// The scaling is relative to the former scaling
		setScale(this.scaling.times(scaling));
	}
	
	/**
	 * Scales the panel's in-game size, keeping the same resolution.
	 * @param newScaling The panel's new scaling
	 */
	public void setScale(Vector3D newScaling)
	{
		// Remembers the scaling
		this.scaling = newScaling;
	}
	
	/**
	 * Changes the panel's resolution, keeping the same in-game size
	 * @param newSize The new resolution of the panel
	 */
	public void scaleToFill(Vector3D newSize)
	{
		Vector3D oldSize = new Vector3D(getWidth(), getHeight());
		Vector3D scaling = newSize.dividedBy(oldSize);
		
		setSize(newSize.toDimension());
		scale(scaling);
	}
}
