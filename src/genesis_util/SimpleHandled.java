package genesis_util;

import genesis_event.Handled;
import genesis_event.HandlerRelay;

/**
 * This class is a simple implementation of the Handled interface and can be used as a 
 * superclass for other objects. The class implements the basic Handled functions.
 * @author Mikko Hilpinen
 * @since 21.7.2015
 */
public class SimpleHandled implements Handled
{
	// ATTRIBUTES	-------------------------
	
	private StateOperator isDeadOperator;
	private HandlingStateOperatorRelay handlingOperators;
	
	
	// CONSTRUCTOR	------------------------
	
	/**
	 * Creates a new object and adds it to the provided HandlerRelay
	 * @param handlers The handlers that will handle this object (optional)
	 */
	public SimpleHandled(HandlerRelay handlers)
	{
		this.isDeadOperator = new LatchStateOperator(false);
		this.handlingOperators = new HandlingStateOperatorRelay(new StateOperator(true, true));
		
		if (handlers != null)
			handlers.addHandled(this);
	}

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}

	@Override
	public HandlingStateOperatorRelay getHandlingOperators()
	{
		return this.handlingOperators;
	}

	
	// ACCESSORS	-----------------------
	
	/**
	 * Changes the stateOperator that defines whether the object is considered alive or dead
	 * @param operator The new stateOperator for liveliness
	 */
	protected void setIsDeadOperator(StateOperator operator)
	{
		this.isDeadOperator = operator;
	}
}
