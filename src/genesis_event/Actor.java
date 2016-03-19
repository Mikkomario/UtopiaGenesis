package genesis_event;

import utopia.inception.handling.Handled;

/**
 * Each object implementing this interface will be considered an active creature 
 * that needs to perform its own actions during each step. This acting is 
 * done separately from the drawing.<p>
 * 
 * The actors often won't be acting indefinitely and each actor can tell whether 
 * it will be still acting or not. Actors can also stop acting momentarily.
 * 
 * Each actor should also be able to respond to a try to end its existence
 *
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public interface Actor extends Handled
{	
	/**
	 * This is the actors action, which will be called at each step
	 * 
	 * @param duration How many steps passed since the last update.<br>
	 * Under normal circumstances this should be 1 but in programs that run 
	 * slower or faster than usual it might vary
	 */
	public void act(double duration);
}
