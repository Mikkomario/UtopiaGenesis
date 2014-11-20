package genesis_event;

import genesis_util.StateOperator;
import genesis_util.Vector2D;

/**
 * Mouselisteners are interested in the mouse's movements and button presses.<br>
 * Remember to add the object to a MouseListenerHandler
 *
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 * @see MouseListenerHandler
 */
public interface AdvancedMouseListener extends Handled
{
	/**
	 * This method is called when a desired event occurs. Only Mouse events selected by the 
	 * listener's selector should be informed.
	 * @param event The recent mouse event
	 */
	public void onMouseEvent(AdvancedMouseEvent event);
	
	/**
	 * @return A mouseEventSelector that determines which mouse events the listener is 
	 * interested in
	 */
	public EventSelector<AdvancedMouseEvent> getMouseEventSelector();
	
	/**
	 * Tell's whether the given position is in the object's special area of interest
	 * 
	 * @param position The position that is being tested for being special
	 * @return Is the position in the object's area of special interest (if applicable)
	 */
	public boolean isInAreaOfInterest(Vector2D position);
	
	/**
	 * @return The stateOperator that determines whether the object is interested in 
	 * mouse events.
	 */
	public StateOperator getListensToMouseEventsOperator();
}
