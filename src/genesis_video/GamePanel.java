package genesis_video;

import genesis_event.DrawableHandler;
import genesis_event.MainMouseListenerHandler;
import genesis_util.DepthConstants;
import genesis_util.Vector3D;
import utopia.inception.handling.HandlerRelay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * Gamepanel is a single panel in a main panel that draws numerous drawable objects.
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see MainPanel
 */
public class GamePanel extends JPanel implements ComponentListener
{
	// ATTRIBUTES ---------------------------------------------------------
	
	private static final long serialVersionUID = -6510794439938372969L;
	
	private Vector3D scaling, gameWorldSize, originalGameWorldSize;
	private DrawableHandler drawer;
	private boolean clearPrevious;
	private ScalingPolicy scalingPolicy;
	
	private HandlerRelay handlerRelay;
	// TODO: Move mouse listening here, add a separate handler relay
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new game panel
	 * @param gameWorldSize The size of the game world viewed through this panel
	 * @param scalingPolicy How the panel's in-game world should be scaled when the panel's 
	 * aspect ratio changes
	 */
	public GamePanel(Vector3D gameWorldSize, ScalingPolicy scalingPolicy)
	{
		// Initializes attributes
		this.scaling = Vector3D.identityVector();
		this.clearPrevious = true;
		this.gameWorldSize = gameWorldSize;
		this.originalGameWorldSize = gameWorldSize;
		this.scalingPolicy = scalingPolicy;
		
		this.handlerRelay = new HandlerRelay();
		this.drawer = new DrawableHandler(true, DepthConstants.NORMAL, 5);
		this.handlerRelay.addHandler(this.drawer);
		// TODO: Also add mouse handling
		
		//Formats the panel
		setLayout(null);
		setSize(gameWorldSize.toDimension());
		setVisible(true);
		setBackground(Color.WHITE);
		
		addComponentListener(this);
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
	
	@Override
	public void componentResized(ComponentEvent e)
	{
		// On each resize, the scaling is adjusted accordingly
		if (equals(e.getComponent()))
			calculateScaling();
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		// Does nothing
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		// Does nothing
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		// Does nothing
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
	 * @return The current in-game size of the panel
	 */
	public Vector3D getGameWorldSize()
	{
		return this.gameWorldSize;
	}
	
	/**
	 * @return The preffered in-game size of the panel. This is achieved if the panel's 
	 * aspect ratio is that of the this vector's
	 */
	public Vector3D getPrefferedGameWorldSize()
	{
		return this.originalGameWorldSize;
	}
	
	/**
	 * Changes the in-game size of the panel. The resulting in-game size may vary depending 
	 * from the aspect ratio of the panel.
	 * @param gameWorldSize The new preferred game-world size for the component
	 */
	public void setGameWorldSize(Vector3D gameWorldSize)
	{
		this.originalGameWorldSize = gameWorldSize;
		
		// Calculates the new scaling
		calculateScaling();
	}
	
	private void calculateScaling()
	{
		Vector3D size = new Vector3D(getWidth(), getHeight());
		
		// If the size has different aspect ratio than the original game world size, 
		// calculates a new game world size to use. Extends the game world size beyond normal 
		// on weird resolutions
		if (this.scalingPolicy == ScalingPolicy.PROJECT)
			// Projects the original game world size over the new size and uses that
			this.gameWorldSize = this.originalGameWorldSize.vectorProjection(size);
		else
		{
			double originalXYRatio = this.originalGameWorldSize.getFirst() / 
					this.originalGameWorldSize.getSecond();
			double newXYRatio = size.getFirst() / size.getSecond();
			
			boolean preserveX = originalXYRatio > newXYRatio;
			if (this.scalingPolicy == ScalingPolicy.CROP)
				preserveX = !preserveX;
			
			if (preserveX)
				// (x1, y1 * y2/x2)
				this.gameWorldSize = new Vector3D(this.originalGameWorldSize.getFirst(), 
						this.originalGameWorldSize.getSecond() * size.getSecond() / size.getFirst());
			else if (!preserveX)
				// (x1 * x2/y2, y1)
				this.gameWorldSize = new Vector3D(this.originalGameWorldSize.getFirst() * 
						size.getFirst() / size.getSecond(), this.originalGameWorldSize.getSecond());
		}
		
		// Calculates the scaling used in drawing
		this.scaling = size.dividedBy(this.gameWorldSize);
	}
	
	
	// ENUMERATIONS	----------------
	
	/**
	 * The scaling policy determines how the in-game size of the panel is handled when the 
	 * panel's aspect ratio differs from that of the preferred game world size's
	 * @author Mikko Hilpinen
	 * @since 20.3.2016
	 */
	public static enum ScalingPolicy
	{
		/**
		 * The resulting game world size will always be at least as large as the preferred 
		 * game world size.
		 */
		EXTEND,
		/**
		 * The resulting game world size will at maximum be as large as the preferred game 
		 * world size
		 */
		CROP,
		/**
		 * Vector projection is used when determining the actual game world size. The game 
		 * world size may be cut horizontally and increased vertically, or vice versa, when 
		 * necessary. The total area of the in game world is preserved.
		 */
		PROJECT
	}
	
	
	// NESTED CLASSES	---------------
	
	/**
	 * This mouse handler also originates new mouse events based on the awt mouse events 
	 * it receives
	 * @author Unto Solala & Mikko Hilpinen
	 * @since 8.8.2013
	 */
	private static class PanelMouseListenerHandler extends MainMouseListenerHandler 
			implements MouseListener, MouseWheelListener
	{
		// 
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			Vector3D mousePosition = new Vector3D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), true, e.getButton());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Vector3D mousePosition = new Vector3D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), false, e.getButton());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			GameWindow.this.mainmousehandler.setMousePosition(getScaledPoint(
					new Vector3D(e.getPoint())));
			GameWindow.this.mainmousehandler.informMouseWheelTurn(e.getPreciseWheelRotation(), 
					e.getWheelRotation());
		}
	}
}
