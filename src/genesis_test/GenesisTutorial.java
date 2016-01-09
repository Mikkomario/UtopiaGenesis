package genesis_test;

import java.awt.Color;
import java.awt.Graphics2D;

import genesis_event.Actor;
import genesis_event.ActorHandler;
import genesis_event.Drawable;
import genesis_event.DrawableHandler;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.KeyEvent;
import genesis_event.KeyEvent.KeyEventType;
import genesis_event.MouseEvent.MouseButton;
import genesis_event.MouseEvent.MouseButtonEventType;
import genesis_event.MouseEvent.MouseEventType;
import genesis_event.KeyListener;
import genesis_event.KeyListenerHandler;
import genesis_event.MouseEvent;
import genesis_event.MouseListener;
import genesis_event.MouseListenerHandler;
import genesis_event.MultiEventSelector;
import genesis_util.DepthConstants;
import genesis_util.HelpMath;
import genesis_util.SimpleHandled;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

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
		// Opening a window
		Vector3D resolution = new Vector3D(1360, 768);
		GameWindow window = new GameWindow(resolution, "Genesis Tutorial", true, 120, 20);
		
		// Changing background color
		GamePanel panel = window.getMainPanel().addGamePanel();
		panel.setBackground(Color.BLACK);
		
		// Setting up a HandlerRelay
		HandlerRelay handlers = new HandlerRelay();
		handlers.addHandler(new DrawableHandler(false, panel.getDrawer()));
		
		// Modifying the HandlerRelay
		handlers.addHandler(new KeyListenerHandler(false, window.getHandlerRelay()));
		
		// Mouse to the relay
		handlers.addHandler(new MouseListenerHandler(false, window.getHandlerRelay()));
		
		// Finishing the HandlerRelay
		handlers.addHandler(new ActorHandler(false, window.getStepHandler()));
		
		// Creating a visual object
		new TestObject(handlers, resolution.dividedBy(2), resolution, 50);
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
			super(handlers);
			
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
					new Bullet(this.handlers, this.position, 
							HelpMath.lenDir(20, HelpMath.pointDirection(this.position, 
							this.lastMousePosition)), this.resolution, this.radius / 5);
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
		
		public Bullet(HandlerRelay handlers, Vector3D position, Vector3D velocity, 
				Vector3D resolution, int radius)
		{
			// Shooting bullets
			super(handlers);
			
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