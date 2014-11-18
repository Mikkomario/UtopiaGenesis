package genesis_logic;

import genesis_logic.AdvancedMouseEvent.MouseButtonEventScale;
import genesis_logic.AdvancedMouseEvent.MouseButtonEventType;
import genesis_logic.AdvancedMouseEvent.MouseEventType;
import genesis_util.HandlerRelay;
import genesis_util.StateOperator;

import java.awt.geom.Point2D;

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
	public boolean isInAreaOfInterest(Point2D.Double testedPosition)
	{
		// Handlers are interested in all areas
		return true;
	}
	
	
	// OTHER METHODS	----------------------------------
	
	private void initialize()
	{
		// The handler accepts the mouse move event as well as global mouse button events
		this.selector = new MultiEventSelector<AdvancedMouseEvent>();
		
		StrictEventSelector<AdvancedMouseEvent, AdvancedMouseEvent.Feature> globalButtonSelector = 
				AdvancedMouseEvent.createButtonStateChangeSelector();
		globalButtonSelector.addRequiredFeature(MouseButtonEventScale.GLOBAL);
		this.selector.addOption(globalButtonSelector);
		this.selector.addOption(AdvancedMouseEvent.createMouseMoveSelector());
	}
}
