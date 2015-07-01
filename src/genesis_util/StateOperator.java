package genesis_util;

/**
 * StateOperator keeps track of an (object's) boolean state. The state may or may not be 
 * changeable.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class StateOperator
{
	// ATTRIBUTES	----------------------------------------
	
	private boolean state, mutable;
	private StateOperatorListenerHandler listenerHandler;
	
	
	// CONSTRUCTOR	----------------------------------------
	
	/**
	 * Creates a new stateOperator with the given attributes
	 * @param initialState The initial state the operator receives
	 * @param mutable Can the operator's state be changed
	 */
	public StateOperator(boolean initialState, boolean mutable)
	{
		// Initializes attributes
		this.state = initialState;
		this.mutable = mutable;
		this.listenerHandler = null;
	}
	
	
	// OTHER METHODS	-----------------------------------
	
	// TODO: Add a possibility to bind the operator to another operator?
	
	/**
	 * This may or may not change the object's state to the given new state.
	 * @param newState The new state the object may receive
	 */
	public void setState(boolean newState)
	{
		if (this.mutable && getState() != newState)
		{
			// Informs the new state before it is applied since the latter may prevent the 
			// former
			getListenerHandler().onStateChange(this, newState);
			this.state = newState;
		}
	}
	
	/**
	 * @return the object's state
	 */
	public boolean getState()
	{
		return this.state;
	}
	
	/**
	 * @return The stateListenerHandler that informs object's about the changes in this 
	 * stateOperator
	 */
	public StateOperatorListenerHandler getListenerHandler()
	{
		// Only initializes the listener Handler when it is required
		if (this.listenerHandler == null)
			this.listenerHandler = new StateOperatorListenerHandler(false);
		
		return this.listenerHandler;
	}
	
	/**
	 * Makes the object mutable or immutable
	 * @param mutable Can the operator's state be changed
	 */
	protected void setMutable(boolean mutable)
	{
		this.mutable = mutable;
	}
}