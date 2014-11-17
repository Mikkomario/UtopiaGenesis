package genesis_util;

/**
 * StateListenerHandler informs multiple stateListeners about state information it receives.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class StateOperatorListenerHandler extends Handler implements StateOperatorListener
{
	// ATTRIBUTES	------------------------------------
	
	private StateOperator lastSource;
	private boolean lastState;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new empty stateListenerHandler
	 * 
	 * @param autodeath Will the stateListenerHandler die once it runs out of living listeners
	 * @param superhandler The StateListenerHandler that will handle this handler (optional)
	 */
	public StateOperatorListenerHandler(boolean autodeath, StateOperatorListenerHandler superhandler)
	{
		super(autodeath, superhandler);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.STATEHANDLER;
	}

	@Override
	protected boolean handleObject(Handled h)
	{
		// Informs the object about the stateChange
		((StateOperatorListener) h).onStateChange(this.lastSource, this.lastState);
		
		return true;
	}

	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		// Informs the objects about the stateChange
		this.lastSource = source;
		this.lastState = newState;
		
		handleObjects();
		
		this.lastSource = null;
	}
	
	
	// OTHER METHODS	-----------------------------------
	
	/**
	 * Adds a stateListener to the informed listeners
	 * 
	 * @param s the stateListener that will be added to the informed listeners
	 */
	public void addStateListener(StateOperatorListener s)
	{
		addHandled(s);
	}
}
