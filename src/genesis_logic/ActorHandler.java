package genesis_logic;

import genesis_util.GenesisHandlerType;
import genesis_util.Handled;
import genesis_util.Handler;
import genesis_util.HandlerType;
import genesis_util.StateOperator;

/**
 * The object from this class will control multiple actors, calling their 
 * act-methods and removing them when necessary
 *
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class ActorHandler extends Handler implements Actor
{
	// ATTRIBUTES	------------------------------------------------------
	
	private double laststeplength;
	private StateOperator isActiveOperator;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new actorhandler. Actors must be added manually later
	 *
	 * @param autodeath Will the handler die if there are no living actors to be handled
	 * @param superhandler The handler that will call the act-event of the object (optional)
	 */
	public ActorHandler(boolean autodeath, ActorHandler superhandler)
	{
		super(autodeath, superhandler);
		
		// Initializes attributes
		this.laststeplength = 0;
		this.isActiveOperator = new AnyHandledIsActiveOperator();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	public StateOperator getIsActiveStateOperator()
	{
		return this.isActiveOperator;
	}
	
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
	protected boolean handleObject(Handled h)
	{
		// Calls the act method of active handleds
		Actor a = (Actor) h;
		
		if (a.getIsActiveStateOperator().getState())
			a.act(this.laststeplength);
		
		return true;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Adds a new actor to the handled actors
	 *
	 * @param a The actor to be added
	 */
	public void addActor(Actor a)
	{
		addHandled(a);
	}
	
	
	// SUBCLASSES	------------------------------------
	
	private class AnyHandledIsActiveOperator extends ForAnyHandledsOperator
	{
		// CONSTRUCTOR	--------------------------------
		
		public AnyHandledIsActiveOperator()
		{
			super(true);
		}
		
		
		// IMPLEMENTED METHODS	------------------------

		@Override
		protected void changeHandledState(Handled h, boolean newState)
		{
			((Actor) h).getIsActiveStateOperator().setState(newState);
		}

		@Override
		protected boolean getHandledState(Handled h)
		{
			return ((Actor) h).getIsActiveStateOperator().getState();
		}
	}
}
