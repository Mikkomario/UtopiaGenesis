package utopia.genesis.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import utopia.genesis.event.AbstractMouseListenerHandler;
import utopia.genesis.event.Drawable;
import utopia.genesis.event.MainKeyListenerHandler;
import utopia.genesis.event.MouseEvent;
import utopia.genesis.event.MouseListener;
import utopia.genesis.event.StepHandler;
import utopia.genesis.util.Line;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.WindowKeyListenerHandler;
import utopia.genesis.video.PanelMouseListenerHandler;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.event.EventSelector;
import utopia.inception.handling.Handled;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.util.SimpleHandled;

/**
 * MouseListenerTest tests the capabilities of MouseListener and related features
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
class GenesisTest
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
		// Creates the display
		GameWindow window = new GameWindow(new Dimension(800, 600), "Test", false, true, 
				ScreenSplit.HORIZONTAL);
		GamePanel panel = new GamePanel(new Vector3D(800, 600), ScalingPolicy.PROJECT, 120);
		panel.setBackground(Color.BLACK);
		window.getMainPanel().addGamePanel(panel);
		
		// Creates the handlers (step, mouse, key)
		StepHandler stepHandler = new StepHandler(120, 10);
		AbstractMouseListenerHandler mouseHandler = new PanelMouseListenerHandler(panel, false);
		MainKeyListenerHandler keyHandler = new WindowKeyListenerHandler(window);
		stepHandler.add(mouseHandler);
		stepHandler.add(keyHandler);
		
		HandlerRelay handlers = new HandlerRelay();
		handlers.addHandler(stepHandler);
		handlers.addHandler(mouseHandler);
		handlers.addHandler(keyHandler);
		handlers.addHandler(panel.getDrawer());
		
		Handled drawer = new MousePositionDrawer();
		handlers.add(drawer);
		handlers.add(new KeyTester(handlers, drawer));
		
		// Creates a performance monitor as well
		//new TextPerformanceMonitor(1000, stepHandler);
		//new StepHandler.PerformanceAccelerator(100, stepHandler);
		
		handlers.add(new CircleLineIntersectionTest());
		
		stepHandler.start();
	}
	
	
	// SUBCLASSES	---------------------
	
	private static class CircleLineIntersectionTest extends SimpleHandled implements Drawable, 
			MouseListener
	{
		// ATTRIBUTES	-----------------
		
		private Line lastMouseLine;
		private List<Vector3D> lastIntersectionPoints;
		private EventSelector selector;
		
		
		// CONSTRUCTOR	-----------------
		
		public CircleLineIntersectionTest()
		{
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
		public EventSelector getMouseEventSelector()
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
