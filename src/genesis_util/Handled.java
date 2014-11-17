package genesis_util;

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
}
