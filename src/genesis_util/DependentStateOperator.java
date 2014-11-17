package genesis_util;

/**
 * DependentStateOperator copies its state from another stateOperator.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class DependentStateOperator extends StateOperator implements
		StateOperatorListener
{
	// ATTRIBUTES	---------------------------------------
	
	private StateOperator isDeadOperator;
	
	
	// CONSTRUCTOR	---------------------------------------
	
	/**
	 * Creates a new StateOperator that copies its state from the given source
	 * @param parent The stateOperator from which the state is copied from
	 */
	public DependentStateOperator(StateOperator parent)
	{
		super(parent != null ? parent.getState() : false, false);
		
		// Initializes attributes
		this.isDeadOperator = new LatchStateOperator(false);
		
		// Adds the object to the handler
		if (parent != null)
			parent.getListenerHandler().addStateListener(this);
	}
	
	/**
	 * Creates a new StateOperator that changes its state according to the changes in the 
	 * given handler.
	 * 
	 * @param parent The handler that informs the operator about state changes
	 * @param initialState The state the operator has before the handler gives any information
	 */
	public DependentStateOperator(StateOperatorListenerHandler parent, boolean initialState)
	{
		super(initialState, false);
		
		// Initializes attributes
		this.isDeadOperator = new LatchStateOperator(false);
		
		if (parent != null)
			parent.addStateListener(this);
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------

	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		setMutable(true);
		setState(newState);
		setMutable(false);
	}

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}
}
