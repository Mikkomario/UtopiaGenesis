package genesis_event;

import genesis_util.StateOperator;
import genesis_util.HandlingStateOperatorRelay;

/**
 * This is the superinterface for all of the objects that can be handled
 * (actor, drawable, ect.). Each handled can be either living or dead. Once a handled is 
 * killed, each Handler will actively try to remove it.
 *
 * @author Mikko Hilpinen.
 * @since 8.12.2012.
 */
public interface Handled
{
	/**
	 * @return The stateOperator that tells whether the Handled is dead (or alive).
	 */
	public StateOperator getIsDeadStateOperator();
	
	/**
	 * @return A collection of StateOperators that describe if the Handled should be handled 
	 * by a handler of a certain type.
	 */
	public HandlingStateOperatorRelay getHandlingOperators();
}
