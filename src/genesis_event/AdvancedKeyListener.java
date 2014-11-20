package genesis_event;

import genesis_util.StateOperator;

/**
 * Keylisteners are interested in the user's activities on the keyboard and 
 * should be notified when a key is pressed, down or released.<br>
 * Remember to add the listener to a KeyListenerHandler
 *
 * @author Mikko Hilpinen.
 * @since 28.11.2012.
 * @see KeyListenerHandler
 */
public interface AdvancedKeyListener extends Handled
{
	/**
	 * This method is called when a desired event occurs. Only Key events selected by the 
	 * listener's selector should be informed.
	 * @param event The event that occurred
	 */
	public void onKeyEvent(AdvancedKeyEvent event);
	
	/**
	 * @return A keyEventSelector that determines whether the listener is interested in certain 
	 * events.
	 */
	public EventSelector<AdvancedKeyEvent> getKeyEventSelector();
	
	/**
	 * @return A stateOperator that determines whether the listener is currently interested 
	 * in keyboard events.
	 */
	public StateOperator getListensToKeyEventsOperator();
}
