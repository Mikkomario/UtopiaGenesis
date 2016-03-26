package utopia.genesis.test;

import java.awt.Color;
import java.awt.Graphics2D;

import utopia.genesis.event.AbstractMouseListenerHandler;
import utopia.genesis.event.Actor;
import utopia.genesis.event.Drawable;
import utopia.genesis.event.KeyEvent;
import utopia.genesis.event.KeyListener;
import utopia.genesis.event.MainKeyListenerHandler;
import utopia.genesis.event.MouseEvent;
import utopia.genesis.event.MouseListener;
import utopia.genesis.event.StepHandler;
import utopia.genesis.event.KeyEvent.KeyEventType;
import utopia.genesis.event.MouseEvent.MouseButton;
import utopia.genesis.event.MouseEvent.MouseButtonEventType;
import utopia.genesis.event.MouseEvent.MouseEventType;
import utopia.genesis.util.DepthConstants;
import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.PanelKeyListenerHandler;
import utopia.genesis.video.PanelMouseListenerHandler;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.event.EventSelector;
import utopia.inception.event.MultiEventSelector;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.util.SimpleHandled;

/**
 * This is the example code for the Utopia tutorial's Genesis part
 * @author Mikko Hilpinen
 * @since 26.7.2015
 */
public class GenesisTutorial
{
	// CONSTRUCTOR	--------------------
	
	private GenesisTutorial()
	{
		// The interface is static
	}

	
	// MAIN METHOD	--------------------
	
	/**
	 * Starts the program
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Vector3D resolution = new Vector3D(1360, 768);
		
		// Creates the window and panel first
		GameWindow window = new GameWindow(resolution.toDimension(), "Genesis Tutorial", false, 
				ScreenSplit.HORIZONTAL);
		GamePanel panel = new GamePanel(resolution, ScalingPolicy.PROJECT, 120);
		window.addGamePanel(panel);
		
		// Changing background color
		panel.setBackground(Color.BLACK);
		
		// Creating handlers (mouse, key, step)
		StepHandler stepHandler = new StepHandler(120, 10);
		AbstractMouseListenerHandler mouseHandler = new PanelMouseListenerHandler(panel, false);
		MainKeyListenerHandler keyHandler = new PanelKeyListenerHandler(window);
		
		stepHandler.add(mouseHandler);
		stepHandler.add(keyHandler);
		
		// Setting up a HandlerRelay
		HandlerRelay handlers = new HandlerRelay();
		handlers.addHandler(stepHandler);
		handlers.addHandler(panel.getDrawer());
		handlers.addHandler(keyHandler);
		handlers.addHandler(mouseHandler);
		
		// Creating a visual object
		handlers.add(new TestObject(handlers, resolution.dividedBy(2), resolution, 50));
		
		// Starts the game
		stepHandler.start();
	}
	
	
	// SUBCLASSES	--------------------
	
	private static class TestObject extends SimpleHandled implements Drawable, KeyListener, 
			MouseListener
	{
		// ATTRIBUTES	----------------
		
		// Creating a visual object
		private Vector3D position;
		private int radius;
		
		// Moving with wasd
		private EventSelector<KeyEvent> keyEventSelector;
		
		// Reacting to mouse movement
		private Vector3D lastMousePosition;
		private EventSelector<MouseEvent> mouseEventSelector;
		
		// Shooting bullets
		private HandlerRelay handlers;
		private Vector3D resolution;
		
		
		// CONSTRUCTOR	----------------
		
		public TestObject(HandlerRelay handlers, Vector3D position, Vector3D resolution, 
				int radius)
		{
			// Creating a visual object
			this.position = position;
			this.radius = radius;
			this.handlers = handlers;
			this.resolution = resolution;
			
			// Moving with wasd
			this.keyEventSelector = KeyEvent.createEventTypeSelector(KeyEventType.DOWN);
			
			// Listening to mouse button presses
			MultiEventSelector<MouseEvent> selector = new MultiEventSelector<>();
			selector.addOption(MouseEvent.createButtonEventTypeSelector(
					MouseButtonEventType.PRESSED));
			
			// Reacting to mouse movement
			selector.addOption(MouseEvent.createMouseMoveSelector());
			this.mouseEventSelector = selector;
			this.lastMousePosition = Vector3D.zeroVector();
		}
		
		
		// IMPLEMENTED METHODS	--------

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			// Creating a visual object
			g2d.setColor(Color.WHITE);
			this.position.drawAsPoint(this.radius, g2d);
			
			// Reacting to mouse movement
			g2d.drawLine(this.position.getFirstInt(), this.position.getSecondInt(), 
					this.lastMousePosition.getFirstInt(), 
					this.lastMousePosition.getSecondInt());
		}

		@Override
		public int getDepth()
		{
			// Creating a visual object
			return DepthConstants.NORMAL;
		}

		@Override
		public void onKeyEvent(KeyEvent event)
		{
			// Moving with wasd
			double speed = 5 * event.getDuration();
			
			if (event.getKeyChar() == 'a')
				this.position = this.position.plus(new Vector3D(-speed, 0));
			else if (event.getKeyChar() == 'd')
				this.position = this.position.plus(new Vector3D(speed, 0));
			else if (event.getKeyChar() == 'w')
				this.position = this.position.plus(new Vector3D(0, -speed));
			else if (event.getKeyChar() == 's')
				this.position = this.position.plus(new Vector3D(0, speed));
		}

		@Override
		public EventSelector<KeyEvent> getKeyEventSelector()
		{
			// Moving with wasd
			return this.keyEventSelector;
		}

		@Override
		public void onMouseEvent(MouseEvent event)
		{
			// Reacting to mouse movement
			this.lastMousePosition = event.getPosition();
			
			// Listening to mouse presses
			if (event.getType() == MouseEventType.BUTTON)
			{
				if (event.getButton() == MouseButton.RIGHT)
					this.position = this.lastMousePosition;
				// Shooting bullets
				else
					this.handlers.add(new Bullet(this.position, 
							HelpMath.lenDir(20, HelpMath.pointDirection(this.position, 
							this.lastMousePosition)), this.resolution, this.radius / 5));
			}
		}

		@Override
		public EventSelector<MouseEvent> getMouseEventSelector()
		{
			// Reacting to mouse movement
			return this.mouseEventSelector;
		}

		@Override
		public boolean isInAreaOfInterest(Vector3D position)
		{
			// Reacting to mouse movement
			return false;
		}
	}
	
	private static class Bullet extends SimpleHandled implements Drawable, Actor
	{
		// ATTRIBUTES	----------------------
		
		// Shooting bullets
		private Vector3D position, resolution, velocity;
		private int radius;
		
		
		// CONSTRUCTOR	----------------------
		
		public Bullet(Vector3D position, Vector3D velocity, Vector3D resolution, int radius)
		{
			// Shooting bullets
			this.position = position;
			this.resolution = resolution;
			this.velocity = velocity;
			this.radius = radius;
		}
		
		
		// IMPLEMENTED METHODS	--------------

		@Override
		public void act(double duration)
		{
			// Shooting bullets
			this.position = this.position.plus(this.velocity.times(duration));
			
			if (!HelpMath.pointIsInRange(this.position, new Vector3D(-32, -32), 
					this.resolution.plus(new Vector3D(32, 32))))
				getIsDeadStateOperator().setState(true);
		}

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			// Shooting bullets
			g2d.setColor(Color.RED);
			this.position.drawAsPoint(this.radius, g2d);
		}

		@Override
		public int getDepth()
		{
			// Shooting bullets
			return DepthConstants.NORMAL - 5;
		}
	}
}
