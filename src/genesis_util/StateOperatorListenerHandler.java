package genesis_util;

/**
 * StateListenerHandler informs multiple stateListeners about state information it receives.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class StateOperatorListenerHandler extends Handler<StateOperatorListener> implements StateOperatorListener
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
	public StateOperatorListenerHandler(boolean autodeath, 
			StateOperatorListenerHandler superhandler)
	{
		super(autodeath);
		
		if (superhandler != null)
			superhandler.add(this);
	}
	
	/**
	 * Creates a new empty handler
	 * @param autoDeath Will the handler die once it empties
	 * @param superHandlers The handlerRelay that holds the handlers that will handle this 
	 * handler
	 */
	public StateOperatorListenerHandler(boolean autoDeath, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
	}
	
	/**
	 * Creates a new empty handler
	 * 
	 * @param autoDeath Will the handler die once it empties
	 */
	public StateOperatorListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.STATEHANDLER;
	}

	@Override
	protected boolean handleObject(StateOperatorListener l)
	{
		// Informs the object about the stateChange
		l.onStateChange(this.lastSource, this.lastState);
		
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
}
