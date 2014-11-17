package genesis_util;

import java.util.HashMap;

/**
 * HandlerRelays keep track of different types of Handlers and provides access to them.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public class HandlerRelay
{
	// ATTRIBUTES	---------------------------------------------
	
	private HashMap<HandlerType, Handler> handlers;
	
	
	// CONSTRUCTOR	---------------------------------------------
	
	/**
	 * Creates a new empty handlerRelay
	 */
	public HandlerRelay()
	{
		// Initializes attributes
		this.handlers = new HashMap<HandlerType, Handler>();
	}
	
	
	// OTHER METHODS	------------------------------------------
	
	/**
	 * Adds the given handler to the relay
	 * 
	 * @param h The handler that will be added to the relay
	 * @param transferPreviousContent If there is already a handler of that type in the relay, 
	 * will its contents be moved into the new one
	 * @param killPrevious If there is already a handler of that type in the relay, will it 
	 * be killed in the process
	 */
	public void addHandler(Handler h, boolean transferPreviousContent, boolean killPrevious)
	{
		HandlerType type = h.getHandlerType();
		
		// If there already is a handler of the given type, things get more complicated
		if (containsHandlerOfType(type))
		{
			Handler other = this.handlers.get(type);
			
			// If the previous handleds need to be moved, does that
			if (transferPreviousContent)
				h.transferHandledsFrom(other);
			// Kills the previous handler if necessary
			if (killPrevious)
				other.getIsDeadStateOperator().setState(true);
		}
		
		this.handlers.put(type, h);
	}
	
	/**
	 * Replaces a previous handler with a new one. The contents of the previous handler will 
	 * transferred and it will be killed.
	 * 
	 * @param newHandler The hadler that will be added to the relay
	 */
	public void replaceHandler(Handler newHandler)
	{
		addHandler(newHandler, true, true);
	}
	
	/**
	 * Adds the given handler to the relay. This method expects that there is no handler 
	 * of the same type in this relay already (in which case the old handler is removed from 
	 * the relay).
	 * @param h The handler that will be added to the relay.
	 */
	public void addHandler(Handler h)
	{
		addHandler(h, false, false);
	}
	
	/**
	 * @param type The type of the handler that could be included in this relay
	 * @return Does this relay hold a handler of the given type
	 */
	public boolean containsHandlerOfType(HandlerType type)
	{
		return this.handlers.containsKey(type);
	}
	
	/**
	 * Adds a handled to all handlers in the relay that happen to support it. If none of the 
	 * handlers support this handled, no change is made.
	 * @param h The handled that may be added to some of the handlers.
	 */
	public void addHandled(Handled h)
	{
		for (HandlerType type : this.handlers.keySet())
		{
			if (type.getSupportedHandledClass().isInstance(h))
				this.handlers.get(type).addHandled(h);
		}
	}
	
	/**
	 * Removes the handled from any handler in this relay.
	 * @param h The handled that will be removed from the handler(s) of this relay.
	 */
	public void removeHandled(Handled h)
	{
		for (HandlerType type : this.handlers.keySet())
		{
			if (type.getSupportedHandledClass().isInstance(h))
				this.handlers.get(type).removeHandled(h);
		}
	}
	
	/**
	 * Returns a handler of the given type from this relay.
	 * @param type The type of the desired handler.
	 * @return a handler with the given type or null if the relay doesn't contain a handler 
	 * of the given type.
	 */
	public Handler getHandler(HandlerType type)
	{
		return this.handlers.get(type);
	}
}
