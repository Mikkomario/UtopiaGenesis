package genesis_test;

import java.awt.Color;
import java.awt.Graphics2D;

import genesis_event.AdvancedMouseEvent;
import genesis_event.AdvancedMouseEvent.MouseButtonEventType;
import genesis_event.AdvancedMouseListener;
import genesis_event.Drawable;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.MultiEventSelector;
import genesis_util.DepthConstants;
import genesis_util.LatchStateOperator;
import genesis_util.StateOperator;
import genesis_util.Vector2D;

/**
 * MousePositionDrawer draws the mouse position on screen and is used for testing
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class MousePositionDrawer implements Drawable, AdvancedMouseListener
{
	// ATTRIBUTES	-------------------------------------
	
	private Vector2D lastPressPosition, lastMousePosition;
	private boolean mouseIsDown;
	private StateOperator isDeadOperator, isActiveOperator;
	private MultiEventSelector<AdvancedMouseEvent> selector;
	
	
	// CONSTRUCTOR	-------------------------------------
	
	/**
	 * Creates a new tester
	 * @param handlers The handlers used in the test
	 */
	public MousePositionDrawer(HandlerRelay handlers)
	{
		this.lastPressPosition = Vector2D.zeroVector();
		this.lastMousePosition = Vector2D.zeroVector();
		this.mouseIsDown = false;
		
		this.isDeadOperator = new LatchStateOperator(false);
		this.isActiveOperator = new StateOperator(true, true);
		
		// Listens to mouse presses & releases as well as mouse move
		this.selector = new MultiEventSelector<>();
		this.selector.addOption(AdvancedMouseEvent.createButtonStateChangeSelector());
		this.selector.addOption(AdvancedMouseEvent.createMouseMoveSelector());
		
		handlers.addHandled(this);
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}

	@Override
	public void onMouseEvent(AdvancedMouseEvent event)
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
	}

	@Override
	public EventSelector<AdvancedMouseEvent> getMouseEventSelector()
	{
		return this.selector;
	}

	@Override
	public boolean isInAreaOfInterest(Vector2D position)
	{
		return false;
	}

	@Override
	public StateOperator getListensToMouseEventsOperator()
	{
		return this.isActiveOperator;
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
					this.lastMousePosition.getSecondInt() - 10, 20, 20);
	}

	@Override
	public StateOperator getIsVisibleStateOperator()
	{
		return this.isActiveOperator;
	}

	@Override
	public int getDepth()
	{
		return DepthConstants.NORMAL;
	}

	@Override
	public void setDepth(int depth)
	{
		// Fail
	}
}
