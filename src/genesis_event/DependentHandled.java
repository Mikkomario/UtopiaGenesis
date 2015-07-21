package genesis_event;

import genesis_event.HandlerRelay;
import genesis_util.HandlingStateOperatorRelay;
import genesis_util.StateOperator;

/**
 * A Dependent Handled depend from other another object's states.
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object this one depends from
 * @since 2.12.2014
 */
public class DependentHandled<T extends Handled> implements Handled
{
	// ATTRIBUTES	---------------------------------
	
	private T master;
	private StateOperator separateIsDeadOperator;
	private HandlingStateOperatorRelay separateHandlingOperators;
	
	
	// CONSTRUCTOR	---------------------------------
	
	/**
	 * Creates a new object. The object will be dependent from the given master object.
	 * 
	 * @param master The object this object depends from.
	 * @param handlers The handlers that will handle this object (optional)
	 */
	public DependentHandled(T master, HandlerRelay handlers)
	{
		// Initializes attributes
		setMaster(master);
		
		// Adds the object to the handler(s)
		if (handlers != null)
			handlers.addHandled(this);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		if (getMaster() != null)
			return this.master.getIsDeadStateOperator();
		else
			return this.separateIsDeadOperator;
	}
	
	@Override
	public HandlingStateOperatorRelay getHandlingOperators()
	{
		if (getMaster() != null)
			return this.master.getHandlingOperators();
		else
			return this.separateHandlingOperators;
	}
	
	
	// GETTERS & SETTERS	----------------------------
	
	/**
	 * Changes this object's master, making it depend from another object instead
	 * @param newMaster The new master object for this object to depend from
	 */
	public void setMaster(T newMaster)
	{
		this.master = newMaster;
		
		// If the new master is null, has to use own stateOperators (= dies)
		if (newMaster == null)
		{
			this.separateHandlingOperators = new HandlingStateOperatorRelay(
					new StateOperator(false, false));
			this.separateIsDeadOperator = new StateOperator(true, false);
		}
	}
	
	/**
	 * Separates the object from its master. The object will automatically die.
	 */
	public void separate()
	{
		setMaster(null);
	}
	
	/**
	 * @return The object this object depends from
	 */
	protected T getMaster()
	{
		return this.master;
	}
}
