package genesis_event;

import genesis_event.AdvancedMouseEvent.MouseButtonEventType;
import genesis_event.AdvancedMouseEvent.MouseEventType;
import genesis_util.StateOperator;
import genesis_util.Vector2D;

/**
 * Informs multiple mouselisteners about the mouse's movements and button status
 *
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 */
public class MouseListenerHandler extends AbstractMouseListenerHandler 
	implements AdvancedMouseListener
{
	// ATTRIBUTES	---------------------------------------
	
	private MultiEventSelector<AdvancedMouseEvent> selector;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new empty mouselistenerhandler
	 *
	 * @param autodeath Will the handler die when it runs out of listeners
	 * @param actorhandler The actorhandler that will handle this handler 
	 * (optional)
	 * @param superhandler The mouselistenerhandler that will inform this 
	 * handler (optional)
	 */
	public MouseListenerHandler(boolean autodeath, 
			ActorHandler actorhandler, MouseListenerHandler superhandler)
	{
		super(autodeath, actorhandler);
		
		initialize();
		
		// Tries to add the object to the second handler
		if (superhandler != null)
			superhandler.add(this);
	}
	
	/**
	 * Creates a new MouseListenerHandler
	 * 
	 * @param autoDeath Will the handler die automatically once it becomes empty again
	 * @param superHandlers The HandlerRelay that holds the handlers that will handle this handler
	 */
	public MouseListenerHandler(boolean autoDeath, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
		
		initialize();
	}
	
	/**
	 * Creates a new empty mouseListenerHandler
	 * @param autoDeath Will the handler die once it empties
	 */
	public MouseListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	-------------------------------------------------

	@Override
	public void onMouseEvent(AdvancedMouseEvent event)
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
	}

	@Override
	public EventSelector<AdvancedMouseEvent> getMouseEventSelector()
	{
		return this.selector;
	}
	
	@Override
	public StateOperator getListensToMouseEventsOperator()
	{
		return getIsActiveStateOperator();
	}

	@Override
	public boolean isInAreaOfInterest(Vector2D testedPosition)
	{
		// Handlers aren't interested in special areas
		return false;
	}
	
	
	// OTHER METHODS	----------------------------------
	
	private void initialize()
	{
		// The handler accepts the mouse move event as well as global mouse button events
		this.selector = new MultiEventSelector<>();
		
		this.selector.addOption(AdvancedMouseEvent.createButtonStateChangeSelector());
		this.selector.addOption(AdvancedMouseEvent.createMouseMoveSelector());
	}
}
