package genesis_logic;

import genesis_util.GenesisHandlerType;
import genesis_util.Handled;
import genesis_util.Handler;
import genesis_util.HandlerRelay;
import genesis_util.HandlerType;
import genesis_util.StateOperator;

/**
 * This class informs a group of keylisteners about the key events
 *
 * @author Mikko Hilpinen.
 * @since 14.12.2012.
 */
public class KeyListenerHandler extends Handler implements 
		AdvancedKeyListener
{
	// ATTRIBUTES	---------------------------------
	
	private StateOperator listensToKeysOperator;
	private EventSelector<AdvancedKeyEvent> selector;
	
	private AdvancedKeyEvent lastEvent;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new empty keylistenerhandler. Listeners must be added manually
	 *
	 * @param autodeath Will the handler die when it runs out of living handleds
	 * @param superhandler The handler that will handle this handler (optional)
	 */
	public KeyListenerHandler(boolean autodeath, KeyListenerHandler superhandler)
	{
		super(autodeath, superhandler);
		
		// Initializes attributes
		initialize();
	}
	
	/**
	 * Creates a new empty KeyListenerHandler
	 * 
	 * @param autoDeath Will the handler die once it becomes empty
	 * @param superHandlers The HandlerRelay that holds the Handlers that will handle this Handler
	 */
	public KeyListenerHandler(boolean autoDeath, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
		
		initialize();
	}
	
	/**
	 * Creates a new empty KeyListenerHandler
	 * 
	 * @param autoDeath Will the handler die once it becomes empty
	 */
	public KeyListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------

	@Override
	public void onKeyEvent(genesis_logic.AdvancedKeyEvent event)
	{
		// Inform the listeners about the event
		this.lastEvent = event;
		handleObjects();
		this.lastEvent = null;
	}

	@Override
	public EventSelector<genesis_logic.AdvancedKeyEvent> getKeyEventSelector()
	{
		return this.selector;
	}
	
	@Override
	public StateOperator getListensToKeyEventsOperator()
	{
		return this.listensToKeysOperator;
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.KEYHANDLER;
	}
	
	@Override
	protected boolean handleObject(Handled h)
	{
		// Only informs active listeners
		AdvancedKeyListener l = (AdvancedKeyListener) h;
		
		if (!l.getListensToKeyEventsOperator().getState())
			return true;
		
		informListenerAboutKeyEvent(l, this.lastEvent);
		
		return true;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Adds a new listener to the informed listeners
	 *
	 * @param k The KeyListener added
	 */
	public void addKeyListener(AdvancedKeyListener k)
	{
		addHandled(k);
	}
	
	private void initialize()
	{
		// Initializes attributes
		this.listensToKeysOperator = new AnyHandledListensKeyEventsOperator();
		// The handler listens to all keyboard events
		this.selector = new StrictEventSelector<AdvancedKeyEvent, AdvancedKeyEvent.Feature>();
		
		this.lastEvent = null;
	}
	
	/**
	 * Informs a listener about a keyEvent, but only if the listener listens to such events
	 * 
	 * @param listener The listener that might be interested in the event
	 * @param event The event that the listener may be informed about
	 */
	protected static void informListenerAboutKeyEvent(AdvancedKeyListener listener, AdvancedKeyEvent event)
	{
		if (listener.getKeyEventSelector().selects(event))
			listener.onKeyEvent(event);
	}
	
	
	// SUBCLASSES	-------------------------------------------------------
	
	private class AnyHandledListensKeyEventsOperator extends ForAnyHandledsOperator
	{
		// CONSTRUCTOR	-------------------------------------
		
		public AnyHandledListensKeyEventsOperator()
		{
			super(true);
		}

		
		// IMPLEMENTED METHODS	------------------------------
		
		@Override
		protected void changeHandledState(Handled h, boolean newState)
		{
			((AdvancedKeyListener) h).getListensToKeyEventsOperator().setState(newState);
		}

		@Override
		protected boolean getHandledState(Handled h)
		{
			return ((AdvancedKeyListener) h).getListensToKeyEventsOperator().getState();
		}
	}
}
