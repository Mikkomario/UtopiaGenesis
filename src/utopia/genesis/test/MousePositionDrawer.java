package utopia.genesis.test;

import java.awt.Color;
import java.awt.Graphics2D;

import utopia.genesis.event.Drawable;
import utopia.genesis.event.GenesisHandlerType;
import utopia.genesis.event.MouseEvent;
import utopia.genesis.event.MouseListener;
import utopia.genesis.event.MouseEvent.MouseButton;
import utopia.genesis.event.MouseEvent.MouseButtonEventType;
import utopia.genesis.event.MouseEvent.MouseEventType;
import utopia.genesis.util.DepthConstants;
import utopia.genesis.util.Line;
import utopia.genesis.util.Vector3D;
import utopia.inception.event.EventSelector;
import utopia.inception.event.MultiEventSelector;
import utopia.inception.util.SimpleHandled;

/**
 * MousePositionDrawer draws the mouse position on screen and is used for testing
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
	 */
	public MousePositionDrawer()
	{
		this.lastPressPosition = Vector3D.ZERO;
		this.lastMousePosition = Vector3D.ZERO;
		this.mouseIsDown = false;
		this.wheelTurn = 0;
		
		// Listens to mouse presses & releases as well as mouse move
		this.selector = new MultiEventSelector<>();
		this.selector.addOption(MouseEvent.createButtonStateChangeSelector());
		this.selector.addOption(MouseEvent.createMouseMoveSelector());
		this.selector.addOption(MouseEvent.createMouseWheelSelector());
		
		getHandlingOperators().addOperatorForType(GenesisHandlerType.DRAWABLEHANDLER);
		
		System.out.println(getHandlingOperators().getShouldBeHandledOperator(
				GenesisHandlerType.MOUSEHANDLER).isMutable());
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
			
			if (event.getButton() == MouseButton.MIDDLE)
				getHandlingOperators().getShouldBeHandledOperator(
						GenesisHandlerType.DRAWABLEHANDLER).setState(false);
		}
		else if (event.getButtonEventType() == MouseButtonEventType.RELEASED)
		{
			this.mouseIsDown = false;
			
			if (event.getButton() == MouseButton.MIDDLE)
				getHandlingOperators().getShouldBeHandledOperator(
						GenesisHandlerType.DRAWABLEHANDLER).setState(true);
		}
		
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
			Line line = new Line(this.lastPressPosition, this.lastMousePosition);
			
			if (line.toVector().isParallerWith(new Vector3D(2, 2)))
				g2d.setColor(Color.GREEN);
			
			line.draw(g2d);
			
			// Also draws the normal
			new Line(line.getStart(), 
					line.getStart().plus(line.toVector().normal2D().times(100))).draw(g2d);
			/*
			g2d.drawLine(this.lastPressPosition.getXInt(), 
					this.lastPressPosition.getYInt(), 
					this.lastMousePosition.getXInt(), 
					this.lastMousePosition.getYInt());
					*/
		}
		// Otherwise draws a circle around the mouse
		else
			g2d.drawOval(this.lastMousePosition.getXInt() - 10, 
					this.lastMousePosition.getYInt() - 10 - (int) this.wheelTurn * 10, 
					20, 20);
	}

	@Override
	public int getDepth()
	{
		return DepthConstants.NORMAL;
	}
}
