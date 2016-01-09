package genesis_test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import genesis_event.Drawable;
import genesis_event.EventSelector;
import genesis_event.Handled;
import genesis_event.HandlerRelay;
import genesis_event.MouseEvent;
import genesis_event.MouseListener;
import genesis_event.StepHandler;
import genesis_util.Line;
import genesis_util.SimpleHandled;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

/**
 * MouseListenerTest tests the capabilities of AdvancedMouseListener and related features
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class GenesisTest
{
	// CONSTRUCTOR	----------------------------------------------
	
	private GenesisTest()
	{
		// The interface is static
	}

	
	// MAIN METHOD	---------------------------------------------
	
	/**
	 * Starts the test
	 * @param args
	 */
	public static void main(String[] args)
	{
		GameWindow window = new GameWindow(new Vector3D(800, 600), "Test", true, 120, 20);
		GamePanel panel = window.getMainPanel().addGamePanel();
		panel.setBackground(Color.BLACK);
		
		// Uses mouseHandler and DrawableHandler by default (drawer is available after the 
		// panel has been created)
		HandlerRelay handlers = HandlerRelay.createDefaultHandlerRelay(window, panel);
		
		Handled drawer = new MousePositionDrawer(handlers);
		new KeyTester(handlers, drawer);
		
		// Creates a performance monitor as well
		new TextPerformanceMonitor(1000, window.getStepHandler());
		new StepHandler.PerformanceAccelerator(100, window.getStepHandler());
		
		new CircleLineIntersectionTest(handlers);
	}
	
	
	// SUBCLASSES	---------------------
	
	private static class CircleLineIntersectionTest extends SimpleHandled implements Drawable, 
			MouseListener
	{
		// ATTRIBUTES	-----------------
		
		private Line lastMouseLine;
		private List<Vector3D> lastIntersectionPoints;
		private EventSelector<MouseEvent> selector;
		
		
		// CONSTRUCTOR	-----------------
		
		public CircleLineIntersectionTest(HandlerRelay handlers)
		{
			super(handlers);
			
			this.lastMouseLine = new Line(new Vector3D(1));
			this.lastIntersectionPoints = new ArrayList<>();
			this.selector = MouseEvent.createMouseMoveSelector();
		}
		
		
		// IMPLEMENTED METHODS	---------

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			g2d.setColor(Color.WHITE);
			this.lastMouseLine.draw(g2d);
			g2d.drawOval(50, 50, 100, 100);
			g2d.setColor(Color.BLUE);
			for (Vector3D intersection : this.lastIntersectionPoints)
			{
				intersection.drawAsPoint(5, g2d);
			}
		}

		@Override
		public int getDepth()
		{
			return 0;
		}

		@Override
		public void onMouseEvent(MouseEvent event)
		{
			this.lastMouseLine = new Line(event.getPosition());
			this.lastIntersectionPoints = this.lastMouseLine.circleIntersection2D(
					new Vector3D(100, 100), 50, true);
		}

		@Override
		public EventSelector<MouseEvent> getMouseEventSelector()
		{
			return this.selector;
		}

		@Override
		public boolean isInAreaOfInterest(Vector3D position)
		{
			return false;
		}
	}
}
