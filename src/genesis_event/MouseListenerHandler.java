package genesis_event;

import genesis_event.MouseEvent.MouseButtonEventType;
import genesis_event.MouseEvent.MouseEventType;
import genesis_util.Vector3D;
import utopia.inception.event.EventSelector;
import utopia.inception.event.MultiEventSelector;

/**
 * Informs multiple mouselisteners about the mouse's movements and button status
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 */
public class MouseListenerHandler extends AbstractMouseListenerHandler 
	implements MouseListener
{
	// ATTRIBUTES	---------------------------------------
	
	private MultiEventSelector<MouseEvent> selector;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new empty mouselistenerhandler
	 */
	public MouseListenerHandler()
	{
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	-------------------------------------------------

	@Override
	public void onMouseEvent(MouseEvent event)
	{
		// Updates mouse status
		setMousePosition(event.getPosition());
		
		// Listens to button releases & presses as well
		if (event.getType() == MouseEventType.BUTTON)
		{
			boolean newState = false;
			if (event.getButtonEventType() == MouseButtonEventType.PRESSED)
				newState = true;
			
			setButtonState(event.getButton(), newState);
		}
		
		// As well as mouse wheel turning
		if (event.getType() == MouseEventType.WHEEL)
			informMouseWheelTurn(event.getWheelTurn(), event.getWheelTurnInt());
	}

	@Override
	public EventSelector<MouseEvent> getMouseEventSelector()
	{
		return this.selector;
	}

	@Override
	public boolean isInAreaOfInterest(Vector3D testedPosition)
	{
		// Handlers aren't interested in special areas
		return false;
	}
	
	
	// OTHER METHODS	----------------------------------
	
	private void initialize()
	{		
		// The handler accepts the mouse move event as well as mouse button events and mouse 
		// wheel events
		this.selector = new MultiEventSelector<>();
		
		this.selector.addOption(MouseEvent.createButtonStateChangeSelector());
		this.selector.addOption(MouseEvent.createMouseMoveSelector());
		this.selector.addOption(MouseEvent.createMouseWheelSelector());
	}
}
