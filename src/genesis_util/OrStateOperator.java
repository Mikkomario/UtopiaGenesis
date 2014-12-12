package genesis_util;

/**
 * This stateOperator depends from multiple stateOperators and has true state if any of those 
 * operators has a true state.
 * 
 * @author Mikko Hilpinen
 * @since 11.12.2014
 */
public class OrStateOperator extends AndStateOperator
{
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new operator
	 * @param firstRequirement The first operator this one is dependent from
	 * @param secondRequirement The second operator this one is dependent from
	 */
	public OrStateOperator(StateOperator firstRequirement,
			StateOperator secondRequirement)
	{
		super(firstRequirement, secondRequirement);
	}

	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	public boolean getState()
	{
		for (StateOperator master : getMasters())
		{
			if (master.getState())
				return true;
		}
		
		return false;
	}
}
