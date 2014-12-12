package genesis_util;

import java.util.ArrayList;
import java.util.List;

/**
 * This stateOperator depends from multiple other stateOperators. It won't affect those 
 * operators, however. In order for the state to be true, the other operators' states also 
 * need to be true.
 * 
 * @author Mikko Hilpinen
 * @since 11.12.2014
 */
public class AndStateOperator extends StateOperator
{
	// ATTRIBUTES	------------------------------
	
	private List<StateOperator> masters;
	
	
	// CONSTRUCTOR	------------------------------
	
	/**
	 * Creates a new operator that depends from the given operators
	 * @param firstRequirement The first operator that affects this operator
	 * @param secondRequirement The second operator that affects this operator
	 */
	public AndStateOperator(StateOperator firstRequirement, StateOperator secondRequirement)
	{
		super(false, false);
		
		// Initializes attributes
		this.masters = new ArrayList<>();
		
		if (firstRequirement != null)
			this.masters.add(firstRequirement);
		if (secondRequirement != null)
			this.masters.add(secondRequirement);
	}
	
	
	// IMPLEMENTED METHODS	-------------------------
	
	@Override
	public boolean getState()
	{
		// If any of the masters is in false state, returns false
		for (StateOperator master : this.masters)
		{
			if (!master.getState())
				return false;
		}
		
		return true;
	}
	
	
	// OTHER METHODS	-----------------------------
	
	/**
	 * Adds a new stateOperator this one is dependent from
	 * @param operator The operator this operator will depend from
	 */
	public void addMasterOperator(StateOperator operator)
	{
		if (!this.masters.contains(operator))
			this.masters.add(operator);
	}
	
	/**
	 * Removes a stateOperator from ones this depends from
	 * @param operator The operator this one will no longer depend from
	 */
	public void removeMasterOperator(StateOperator operator)
	{
		this.masters.remove(operator);
	}
	
	/**
	 * @return The stateOperators this one is dependent from
	 */
	protected List<StateOperator> getMasters()
	{
		return this.masters;
	}
}
