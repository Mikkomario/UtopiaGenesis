package genesis_logic;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiMouseEventSelector selects an event if it matches any of its internal requirement 
 * collections (selectors).
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class MultiMouseEventSelector implements MouseEventSelector
{
	// ATTRIBUTES	-------------------------------------------
	
	private List<MouseEventSelector> selectors;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a eventSelector that doesn't accept any events. Additional selectors / options
	 * can be added manually
	 */
	public MultiMouseEventSelector()
	{
		// Initializes attributes
		this.selectors = new ArrayList<MouseEventSelector>();
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------

	@Override
	public boolean selects(AdvancedMouseEvent event)
	{
		for (MouseEventSelector selector : this.selectors)
		{
			if (selector.selects(event))
				return true;
		}
		
		return false;
	}
	
	
	// OTHER METHODS	---------------------------------
	
	/**
	 * Adds a new option to the accepted 
	 * @param selector The selector that will work as an option for selection
	 */
	public void addOption(MouseEventSelector selector)
	{
		if (!this.selectors.contains(selector))
			this.selectors.add(selector);
	}
}
