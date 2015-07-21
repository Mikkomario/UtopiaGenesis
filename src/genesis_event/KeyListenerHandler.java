package genesis_event;

/**
 * This class informs a group of keylisteners about the key events
 *
 * @author Mikko Hilpinen.
 * @since 14.12.2012.
 */
public class KeyListenerHandler extends Handler<KeyListener> implements 
		KeyListener
{
	// ATTRIBUTES	---------------------------------
	
	private EventSelector<KeyEvent> selector;
	
	private KeyEvent lastEvent;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new empty keylistenerhandler. Listeners must be added manually
	 *
	 * @param autodeath Will the handler die when it runs out of living handleds
	 * @param superhandler The handler that will handle this handler (optional)
	 */
	public KeyListenerHandler(boolean autodeath, KeyListenerHandler superhandler)
	{
		super(autodeath);
		
		// Initializes attributes
		initialize();
		
		if (superhandler != null)
			superhandler.add(this);
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
	public void onKeyEvent(genesis_event.KeyEvent event)
	{
		// Inform the listeners about the event
		this.lastEvent = event;
		handleObjects();
		this.lastEvent = null;
	}

	@Override
	public EventSelector<genesis_event.KeyEvent> getKeyEventSelector()
	{
		return this.selector;
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.KEYHANDLER;
	}
	
	@Override
	protected boolean handleObject(KeyListener l)
	{
		// Only informs active listeners
		informListenerAboutKeyEvent(l, this.lastEvent);
		
		return true;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	private void initialize()
	{
		// Initializes attributes
		// The handler listens to all keyboard events
		this.selector = new StrictEventSelector<KeyEvent, KeyEvent.Feature>();
		
		this.lastEvent = null;
	}
	
	/**
	 * Informs a listener about a keyEvent, but only if the listener listens to such events
	 * 
	 * @param listener The listener that might be interested in the event
	 * @param event The event that the listener may be informed about
	 */
	protected static void informListenerAboutKeyEvent(KeyListener listener, KeyEvent event)
	{
		if (listener.getKeyEventSelector().selects(event))
			listener.onKeyEvent(event);
	}
}
