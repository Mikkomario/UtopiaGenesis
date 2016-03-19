package genesis_event;

import utopia.inception.handling.Handler;
import utopia.inception.handling.HandlerType;

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
	 */
	public ActorHandler()
	{
		// Initializes attributes
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public void act(double steps)
	{
		// Updates the steplength and informs objects
		this.laststeplength = steps;
		handleObjects(true);
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
