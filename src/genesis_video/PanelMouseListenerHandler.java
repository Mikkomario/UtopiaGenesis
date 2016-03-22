package genesis_video;

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
 * it receives. The handler only receives mouse events inside a certain game panel
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 */
public class PanelMouseListenerHandler extends MainMouseListenerHandler 
		
{
	// ATTRIBUTES	---------------------
	
	private GamePanel panel;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new panel mouse listener handler. Remember to add the handler to a working 
	 * actor handler
	 * @param panel The panel this handler receives its events from
	 */
	public PanelMouseListenerHandler(GamePanel panel)
	{
		this.panel = panel;
		panel.addMouseListener(new MouseEventReceiver());
	}
	
	
	// NESTED CLASSES	------------
	
	private class MouseEventReceiver implements MouseListener, MouseWheelListener, MouseMotionListener
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
		
		
		// OTHER METHODS	------------
		
		private Vector3D getGameWorldPoint(Point point)
		{
			return new Vector3D(point).times(PanelMouseListenerHandler.this.panel.getScaling());
		}
	}
}