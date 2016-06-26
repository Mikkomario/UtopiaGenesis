package utopia.genesis.event;

import utopia.inception.handling.Handled;

/**
 * Each object implementing this interface will be considered an active creature 
 * that needs to perform its own actions during certain intervals. This acting is 
 * done separately from the drawing.<p>
 * 
 * The actors often won't be acting indefinitely and each actor can tell whether 
 * it will be still acting or not. Actors can also stop acting momentarily.
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public interface Actor extends Handled
{	
	/**
	 * This is the actor's action, which will be called at certain intervals
	 * @param millis How many milliseconds passed since the last update.
	 */
	public void act(double millis);
}
