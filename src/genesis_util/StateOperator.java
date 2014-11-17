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
		this.listenerHandler = new StateOperatorListenerHandler(false, null);
	}
	
	
	// OTHER METHODS	-----------------------------------
	
	/**
	 * This may or may not change the object's state to the given new state.
	 * @param newState The new state the object may receive
	 */
	public void setState(boolean newState)
	{
		if (this.mutable && getState() != newState)
		{
			this.state = newState;
			getListenerHandler().onStateChange(this, newState);
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

