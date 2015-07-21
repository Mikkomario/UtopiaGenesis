package genesis_test;

import java.awt.Color;
import java.awt.Graphics2D;

import genesis_event.GenesisHandlerType;
import genesis_event.MouseEvent;
import genesis_event.MouseEvent.MouseButtonEventType;
import genesis_event.MouseEvent.MouseEventType;
import genesis_event.MouseListener;
import genesis_event.Drawable;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.MultiEventSelector;
import genesis_event.SimpleHandled;
import genesis_util.DepthConstants;
import genesis_util.StateOperator;
import genesis_util.Vector3D;

/**
 * MousePositionDrawer draws the mouse position on screen and is used for testing
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class MousePositionDrawer extends SimpleHandled implements Drawable, MouseListener
{
	// ATTRIBUTES	-------------------------------------
	
	private Vector3D lastPressPosition, lastMousePosition;
	private boolean mouseIsDown;
	private MultiEventSelector<MouseEvent> selector;
	private double wheelTurn;
	
	
	// CONSTRUCTOR	-------------------------------------
	
	/**
	 * Creates a new tester
	 * @param handlers The handlers used in the test
	 */
	public MousePositionDrawer(HandlerRelay handlers)
	{
		super(handlers);
		
		this.lastPressPosition = Vector3D.zeroVector();
		this.lastMousePosition = Vector3D.zeroVector();
		this.mouseIsDown = false;
		this.wheelTurn = 0;
		
		// Listens to mouse presses & releases as well as mouse move
		this.selector = new MultiEventSelector<>();
		this.selector.addOption(MouseEvent.createButtonStateChangeSelector());
		this.selector.addOption(MouseEvent.createMouseMoveSelector());
		this.selector.addOption(MouseEvent.createMouseWheelSelector());
		
		getHandlingOperators().setShouldBeHandledOperator(GenesisHandlerType.DRAWABLEHANDLER, 
				new StateOperator(true, true));
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------

	@Override
	public void onMouseEvent(MouseEvent event)
	{
		// Updates mouse position
		this.lastMousePosition = event.getPosition();
		
		// May also update other mouse statuses
		if (event.getButtonEventType() == MouseButtonEventType.PRESSED)
		{
			this.lastPressPosition = event.getPosition();
			this.mouseIsDown = true;
		}
		else if (event.getButtonEventType() == MouseButtonEventType.RELEASED)
			this.mouseIsDown = false;
		
		if (event.getType() == MouseEventType.WHEEL)
			this.wheelTurn += event.getWheelTurn();
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

	@Override
	public void drawSelf(Graphics2D g2d)
	{
		g2d.setColor(Color.RED);
		
		// If mouse is pressed, draws a line between the two points
		if (this.mouseIsDown)
		{
			g2d.drawLine(this.lastPressPosition.getFirstInt(), 
					this.lastPressPosition.getSecondInt(), 
					this.lastMousePosition.getFirstInt(), 
					this.lastMousePosition.getSecondInt());
		}
		// Otherwise draws a circle around the mouse
		else
			g2d.drawOval(this.lastMousePosition.getFirstInt() - 10, 
					this.lastMousePosition.getSecondInt() - 10 - (int) this.wheelTurn * 10, 20, 20);
	}

	@Override
	public int getDepth()
	{
		return DepthConstants.NORMAL;
	}
}
