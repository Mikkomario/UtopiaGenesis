package utopia.genesis.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utopia.genesis.event.AbstractMouseListenerHandler;
import utopia.genesis.event.Actor;
import utopia.genesis.event.Drawable;
import utopia.genesis.event.GenesisHandlerType;
import utopia.genesis.event.MouseEvent;
import utopia.genesis.event.MouseListener;
import utopia.genesis.event.StepHandler;
import utopia.genesis.event.MouseEvent.MouseButton;
import utopia.genesis.event.MouseEvent.MouseButtonEventScale;
import utopia.genesis.event.MouseEvent.MouseButtonEventType;
import utopia.genesis.util.DependentDrawer;
import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Transformable;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.PanelMouseListenerHandler;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.event.EventSelector;
import utopia.inception.event.StrictEventSelector;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.state.DependentStateOperator;
import utopia.inception.util.ConnectedHandled;
import utopia.inception.util.SimpleHandled;

/**
 * This class tests the dependencies in stateOperators and transformations
 * @author Mikko Hilpinen
 * @since 5.8.2015
 */
class GenesisDependencyTest
{
	// CONSTRUCTOR	--------------------
	
	private GenesisDependencyTest()
	{
		// The interface is static
	}
	
	
	// MAIN METHODS	--------------------
	
	/**
	 * Starts the interactive test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		// Creates the display
		GameWindow window = new GameWindow(new Dimension(1360, 768), "Genesis dependency test", 
				false, true, ScreenSplit.HORIZONTAL);
		GamePanel panel = new GamePanel(new Vector3D(1360, 768), ScalingPolicy.PROJECT, 120);
		panel.setBackground(Color.PINK);
		window.getMainPanel().addGamePanel(panel);
		
		// Creates the handlers
		StepHandler stepHandler = new StepHandler(120, 10);
		AbstractMouseListenerHandler mouseHandler = new PanelMouseListenerHandler(panel, false);
		stepHandler.add(mouseHandler);
		
		// Creates the handler relay
		HandlerRelay handlers = new HandlerRelay();
		handlers.addHandler(stepHandler);
		handlers.addHandler(panel.getDrawer());
		handlers.addHandler(mouseHandler);
		
		// Creates the test node
		handlers.add(new TestNode(handlers, null, new Vector3D(150, 150)));
		
		// Creates the rotating boxes
		TestRotator rotator = new TestRotator(new Vector3D(600, 300));
		handlers.add(rotator, 
				new TestRotatingBoxDrawer(rotator, 32, new Vector3D(50, 0)), 
				new TestRotatingBoxDrawer(rotator, 16, new Vector3D(-50, 0)));
		
		// Starts the game
		stepHandler.start();
	}
	
	
	// SUBCLASSES	-------------------
	
	private static class TestNode extends SimpleHandled implements Transformable, Drawable, 
			MouseListener
	{
		// ATTRIBUTES	--------------------
		
		private Transformation ownTransformation;
		private EventSelector<MouseEvent> selector;
		private TestNode parent;
		private int childAmount;
		private HandlerRelay handlers;
		
		
		// CONSTRUCTOR	-------------------
		
		public TestNode(HandlerRelay handlers, TestNode parent, Vector3D relativePosition)
		{
			super();
			
			this.parent = parent;
			this.ownTransformation = new Transformation(relativePosition, new Vector3D(0.8, 
					0.8), Vector3D.ZERO, 5);
			this.handlers = handlers;
			
			this.childAmount = 0;
			StrictEventSelector<MouseEvent, MouseEvent.Feature> localPressSelector = 
					new StrictEventSelector<>();
			localPressSelector.addRequiredFeature(MouseButtonEventType.PRESSED);
			localPressSelector.addRequiredFeature(MouseButtonEventScale.LOCAL);
			this.selector = localPressSelector;
			
			handlers.add(new TestDependentBlob(this));
			
			if (parent != null)
			{
				setIsDeadOperator(new DependentStateOperator(parent.getIsDeadStateOperator()));
				parent.childAmount ++;
			}
		}
		
		
		// IMPLEMENTED METHODS	------------------

		@Override
		public void onMouseEvent(MouseEvent event)
		{
			// On left pressed, creates a new child
			if (event.getButton() == MouseButton.LEFT)
			{
				System.out.println(this.handlers.getHandler(
						GenesisHandlerType.DRAWABLEHANDLER).getHandledNumber());
				Vector3D relativePos = new Vector3D(this.childAmount * 150, 150);
				this.handlers.add(new TestNode(this.handlers, this, relativePos));
			}
			// On right pressed, kills itself
			else if (event.getButton() == MouseButton.RIGHT)
			{
				if (this.parent != null)
					System.out.println(
							this.parent.getIsDeadStateOperator().getListenerHandler().getHandledNumber());
				getIsDeadStateOperator().setState(true);
			}
		}

		@Override
		public EventSelector<MouseEvent> getMouseEventSelector()
		{
			return this.selector;
		}

		@Override
		public boolean isInAreaOfInterest(Vector3D position)
		{
			if (getTransformation() != null)
				return HelpMath.pointDistance2D(getTransformation().inverseTransform(position), 
						Vector3D.ZERO) < 100;
			return false;
		}

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			AffineTransform lastTransform = getTransformation().transform(g2d);
			
			g2d.setColor(Color.BLACK);
			g2d.drawOval(-50, -50, 100, 100);
			
			g2d.setTransform(lastTransform);
		}

		@Override
		public int getDepth()
		{
			return 0;
		}

		@Override
		public Transformation getTransformation()
		{
			if (this.parent == null)
				return this.ownTransformation;
			else
				return this.parent.getTransformation().transform(this.ownTransformation);
		}

		@Override
		public void setTrasformation(Transformation t)
		{
			this.ownTransformation = t;
		}
	}
	
	private static class TestDependentBlob extends ConnectedHandled<TestNode> implements 
			Drawable
	{
		// CONSTRUCTOR	---------------
		
		public TestDependentBlob(TestNode master)
		{
			super(master);
		}
		
		
		// IMPLEMENTED METHODS	-------

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			AffineTransform lastTransform = getMaster().getTransformation().transform(g2d);
			g2d.setColor(Color.BLUE);
			g2d.drawRect(-30, -30, 60, 60);
			g2d.setTransform(lastTransform);
		}

		@Override
		public int getDepth()
		{
			return 1;
		}
	}
	
	private static class TestRotator extends SimpleHandled implements Actor, Transformable
	{
		// ATTRIBUTES	--------------
		
		private Transformation transformation;
		
		
		// CONSTRUCTOR	--------------
		
		public TestRotator(Vector3D position)
		{
			this.transformation = new Transformation(position);
		}
		
		
		// IMPLEMENTED METHODS	------
		
		@Override
		public void act(double duration)
		{
			setTrasformation(getTransformation().rotated(duration));
		}

		@Override
		public Transformation getTransformation()
		{
			return this.transformation;
		}

		@Override
		public void setTrasformation(Transformation t)
		{
			this.transformation = t;
		}
	}
	
	private static class TestRotatingBoxDrawer extends DependentDrawer<TestRotator> implements Actor
	{
		// ATTRIBUTES	----------------
		
		private int diameter;
		
		
		// CONSTRUCTOR	----------------
		
		public TestRotatingBoxDrawer(TestRotator user, int diameter, Vector3D relativeStartPosition)
		{
			super(user, new Vector3D(diameter / 2, diameter / 2), 0);
			setTrasformation(new Transformation(relativeStartPosition));
			this.diameter = diameter;
		}
		
		
		// IMPLEMENTED METHODS	-------

		@Override
		protected void drawSelfBasic(Graphics2D g2d)
		{
			g2d.drawRect(0, 0, this.diameter, this.diameter);
		}

		@Override
		public void act(double duration)
		{
			setTrasformation(getTransformation().rotated(duration));
		}
	}
}
