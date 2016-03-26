package utopia.genesis.event;

import utopia.genesis.util.Vector3D;
import utopia.inception.event.EventSelector;
import utopia.inception.handling.Handled;

/**
 * Mouselisteners are interested in the mouse's movements and button presses.<br>
 * Remember to add the object to a MouseListenerHandler
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 * @see MouseListenerHandler
 */
public interface MouseListener extends Handled
{
	/**
	 * This method is called when a desired event occurs. Only Mouse events selected by the 
	 * listener's selector should be informed.
	 * @param event The recent mouse event
	 */
	public void onMouseEvent(MouseEvent event);
	
	/**
	 * @return A mouseEventSelector that determines which mouse events the listener is 
	 * interested in
	 */
	public EventSelector<MouseEvent> getMouseEventSelector();
	
	/**
	 * Tell's whether the given position is in the object's special area of interest
	 * 
	 * @param position The position that is being tested for being special
	 * @return Is the position in the object's area of special interest (if applicable)
	 */
	public boolean isInAreaOfInterest(Vector3D position);
}
