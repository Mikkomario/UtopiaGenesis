package genesis_event;

import genesis_util.Killable;
import genesis_util.HandlingStateOperatorRelay;

/**
 * This is the superinterface for all of the objects that can be handled
 * (actor, drawable, ect.). Each handled can be either living or dead. Once a handled is 
 * killed, each Handler will actively try to remove it.
 *
 * @author Mikko Hilpinen.
 * @since 8.12.2012.
 */
public interface Handled extends Killable
{
	/**
	 * @return A collection of StateOperators that describe if the Handled should be handled 
	 * by a handler of a certain type.
	 */
	public HandlingStateOperatorRelay getHandlingOperators();
}
