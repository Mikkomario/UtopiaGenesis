package utopia.genesis.event;

import utopia.inception.event.EventSelector;
import utopia.inception.event.StrictEventSelector;
import utopia.inception.handling.Handler;
import utopia.inception.handling.HandlerType;

/**
 * This class informs a group of keylisteners about the key events
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
	 */
	public KeyListenerHandler()
	{
		// Initializes attributes
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------

	@Override
	public void onKeyEvent(utopia.genesis.event.KeyEvent event)
	{
		// Inform the listeners about the event
		this.lastEvent = event;
		// Only informs active listeners
		handleObjects(true);
		this.lastEvent = null;
	}

	@Override
	public EventSelector<utopia.genesis.event.KeyEvent> getKeyEventSelector()
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
		informListenerAboutKeyEvent(l, this.lastEvent);
		return true;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	private void initialize()
	{
		// Initializes attributes
		// The handler listens to all keyboard events
		this.selector = new StrictEventSelector<KeyEvent>();
		this.lastEvent = null;
	}
	
	/**
	 * Informs a listener about a keyEvent, but only if the listener listens to such events
	 * @param listener The listener that might be interested in the event
	 * @param event The event that the listener may be informed about
	 */
	protected static void informListenerAboutKeyEvent(KeyListener listener, KeyEvent event)
	{
		if (listener.getKeyEventSelector().selects(event))
			listener.onKeyEvent(event);
	}
}
