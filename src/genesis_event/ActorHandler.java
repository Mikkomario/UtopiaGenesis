package genesis_event;

/**
 * The object from this class will control multiple actors, calling their 
 * act-methods and removing them when necessary
 *
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class ActorHandler extends Handler<Actor> implements Actor
{
	// ATTRIBUTES	------------------------------------------------------
	
	private double laststeplength;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new actorhandler. Actors must be added manually later
	 *
	 * @param autodeath Will the handler die if there are no living actors to be handled
	 * @param superhandler The handler that will call the act-event of the object (optional)
	 */
	public ActorHandler(boolean autodeath, ActorHandler superhandler)
	{
		super(autodeath);
		
		// Initializes attributes
		initialize();
		
		if (superhandler != null)
			superhandler.add(this);
	}
	
	/**
	 * Creates a new empty ActorHandler
	 * 
	 * @param autoDeath Will the Handler die once it becomes empty
	 * @param superHandlers The HandlerRelay that holds the handlers that will handle this Handler
	 */
	public ActorHandler(boolean autoDeath, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
		
		initialize();
	}
	
	/**
	 * Creates a new empty ActorHandler
	 * 
	 * @param autoDeath Will the Handler die once it becomes empty
	 */
	public ActorHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public void act(double steps)
	{
		// Updates the steplength and informs objects
		this.laststeplength = steps;
		handleObjects();
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.ACTORHANDLER;
	}
	
	@Override
	protected boolean handleObject(Actor a)
	{
		// Calls the act method of active handleds		
		a.act(this.laststeplength);
		
		return true;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	private void initialize()
	{
		// Initializes attributes
		this.laststeplength = 0;
	}
}
