package genesis_test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import genesis_event.AbstractMouseListenerHandler;
import genesis_event.Drawable;
import genesis_event.GenesisHandlerType;
import genesis_event.MouseEvent;
import genesis_event.MouseEvent.MouseButton;
import genesis_event.MouseEvent.MouseButtonEventScale;
import genesis_event.MouseEvent.MouseButtonEventType;
import genesis_event.MouseListener;
import genesis_event.StepHandler;
import genesis_util.HelpMath;
import genesis_util.Transformable;
import genesis_util.Transformation;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;
import genesis_video.PanelMouseListenerHandler;
import genesis_video.GamePanel.ScalingPolicy;
import genesis_video.SplitPanel.ScreenSplit;
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
public class GenesisDependencyTest
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
				false, ScreenSplit.HORIZONTAL);
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
					0.8), Vector3D.zeroVector(), 5);
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
						Vector3D.zeroVector()) < 100;
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
}
