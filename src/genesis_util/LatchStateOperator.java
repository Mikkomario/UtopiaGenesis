package genesis_util;

/**
 * LatchStateOperator is a StateOperator that can be changed once and then becomes immutable
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class LatchStateOperator extends StateOperator implements StateOperatorListener
{
	// ATTRIBUTES	---------------------------------------
	
	private StateOperator isDeadStateOperator;
	
	
	// CONSTRUCTOR	---------------------------------------
	
	/**
	 * Creates a new latchStateOperator with the given initial state
	 * @param initialState The state of the operator in the beginning
	 */
	public LatchStateOperator(boolean initialState)
	{
		super(initialState, true);
		
		// Initializes attributes
		this.isDeadStateOperator = new StateOperator(false, false);
		getListenerHandler().add(this);
	}
	
	
	// IMPLEMENTED METHODS	------------------------------

	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		if (source == this)
			setMutable(false);
	}

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadStateOperator;
	}
}
