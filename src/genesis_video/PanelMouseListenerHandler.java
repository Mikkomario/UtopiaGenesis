package genesis_video;

import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import genesis_event.MainMouseListenerHandler;
import genesis_util.Vector3D;

/**
 * This mouse handler originates new mouse events based on the awt mouse events 
 * it receives.
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 */
public class PanelMouseListenerHandler extends MainMouseListenerHandler 
		
{
	// ATTRIBUTES	---------------------
	
	private GamePanel panel;
	private boolean readOutsideCoordinates;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new panel mouse listener handler. Remember to add the handler to a working 
	 * actor handler
	 * @param panel The panel this handler receives its events from
	 * @param onlyEventsInsidePanel Should the listener only register events inside the panel 
	 * (true), or outside events as well (false)
	 */
	public PanelMouseListenerHandler(GamePanel panel, boolean onlyEventsInsidePanel)
	{
		this.panel = panel;
		this.readOutsideCoordinates = !onlyEventsInsidePanel;
		
		// If only panel be listened, adds the listeners to the game panel
		MouseEventReceiver eventListener = new MouseEventReceiver();
		if (onlyEventsInsidePanel)
		{
			panel.addMouseListener(eventListener);
			panel.addMouseWheelListener(eventListener);
			panel.addMouseMotionListener(new MouseMotionReceiver());
		}
		// Otherwise the handler handles mouse motion listening while the event listener 
		// receives its events from the root panel
		else
		{	
			Container rootPanel = panel.getTopLevelAncestor();
			if (rootPanel == null)
				rootPanel = panel;
			
			rootPanel.addMouseListener(eventListener);
			rootPanel.addMouseWheelListener(eventListener);
		}
	}
	
	
	// IMPLEMENTED METHODS	--------
	
	@Override
	public void act(double steps)
	{
		// May update the mouse coordinates as well
		if (this.readOutsideCoordinates)
		{
			Point mousePointOnScreen = MouseInfo.getPointerInfo().getLocation();
			
			// The screen / panel position affects the end point
			Vector3D mousePoint = pointInPanel(new Vector3D(mousePointOnScreen), 
					this.panel);
			// Scaling is applied as well
			mousePoint = mousePoint.dividedBy(this.panel.getScaling());
			
			// Updates the mouse coordinate information
			setMousePosition(mousePoint);
		}
		
		super.act(steps);
	}
	
	
	// OTHER METHODS	------------
	
	private Vector3D getGameWorldPoint(Point point)
	{
		return new Vector3D(point).times(PanelMouseListenerHandler.this.panel.getScaling());
	}
	
	private Vector3D pointInPanel(Vector3D point, Container panel)
	{
		// The panel position affects the end point
		Vector3D endPoint = point.minus(new Vector3D(panel.getLocation()));
		
		// Takes the parent panel position into account as well. Uses recursion.
		Container parent = panel.getParent();
		if (parent == null)
			return endPoint;
		else
			return pointInPanel(endPoint, parent);
	}
	
	
	// NESTED CLASSES	------------
	
	private class MouseEventReceiver implements MouseListener, MouseWheelListener
	{
		// IMPLEMENTED METHODS	-------------
		
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
			// Informs the mouse status (scaling affects the mouse coordinates)
			setMouseStatus(getGameWorldPoint(e.getPoint()), true, e.getButton());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// Informs the mouse status (scaling affects the mouse coordinates)
			setMouseStatus(getGameWorldPoint(e.getPoint()), false, e.getButton());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			setMousePosition(getGameWorldPoint(e.getPoint()));
			informMouseWheelTurn(e.getPreciseWheelRotation(), e.getWheelRotation());
		}
	}
	
	private class MouseMotionReceiver implements MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent e)
		{
			// Ignored
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			setMousePosition(getGameWorldPoint(e.getPoint()));
		}
	}
}