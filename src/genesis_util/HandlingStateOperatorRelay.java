package genesis_util;

import genesis_event.HandlerType;

import java.util.HashMap;
import java.util.Map;

/**
 * HandlingStateOperatorRelay is a collection of stateOperators. The relay offers different 
 * StateOperators for different Handlers, allowing both simplicity and versatility.
 * @author Mikko Hilpinen
 * @since 21.7.2015
 */
public class HandlingStateOperatorRelay
{
	// ATTRIBUTES	--------------------------
	
	private Map<HandlerType, StateOperator> operators;
	private StateOperator defaultOperator;
	
	
	// CONSTRUCTOR	--------------------------
	
	/**
	 * Creates a new operator relay that uses the following stateOperator when others are 
	 * not defined)
	 * @param defaultOperator The stateOperator used when another operator hasn't been 
	 * specified
	 */
	public HandlingStateOperatorRelay(StateOperator defaultOperator)
	{
		this.defaultOperator = defaultOperator;
		this.operators = new HashMap<>();
	}
	
	
	// ACCESSORS	------------------------------
	
	/**
	 * @return The operator that is used when there's no specified StateOperator for a 
	 * HandlerType
	 */
	public StateOperator getDefaultOperator()
	{
		return this.defaultOperator;
	}
	
	/**
	 * Changes the default StateOperator used
	 * @param operator The new stateOperator that will be used as a default option
	 */
	public void setDefaultOperator(StateOperator operator)
	{
		this.defaultOperator = operator;
	}
	
	
	// OTHER METHODS	-----------------------
	
	/**
	 * Finds the operator that defines whether the object should be handled by a certain 
	 * handler (if applicable)
	 * @param handlerType The type of handler that may handle the object
	 * @return The operator that defines whether that handler should handle the object
	 */
	public StateOperator getShouldBeHandledOperator(HandlerType handlerType)
	{
		StateOperator operator = this.operators.get(handlerType);
		if (operator == null)
			return getDefaultOperator();
		else
			return operator;
	}
	
	/**
	 * Changes the StateOperator used for the provided Handler type
	 * @param operator The new stateOperator that will be used
	 * @param type The handler type the operator will be used for
	 */
	public void setShouldBeHandledOperator(HandlerType type, StateOperator operator)
	{
		if (operator != null && type != null)
			this.operators.put(type, operator);
	}
	
	/**
	 * Changes the state of all the mutable operators in the relay
	 * @param newState The new state the mutable operators will have
	 */
	public void setAllStates(boolean newState)
	{
		for (StateOperator operator : this.operators.values())
		{
			operator.setState(newState);
		}
		this.defaultOperator.setState(newState);
	}
}
