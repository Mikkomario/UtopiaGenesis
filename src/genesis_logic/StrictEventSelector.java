package genesis_logic;

import java.util.ArrayList;
import java.util.List;

/**
 * MouseEventSelectors select only certain MouseEvents they receive, based on their attributes. 
 * Selectors can be used for picking the events that interest the user.
 * 
 * @author Mikko Hilpinen
 * @since 17.11.2014
 */
public class StrictEventSelector implements EventSelector
{
	// ATTRIBUTES	-------------------------------------
	
	private final List<Event.Feature> requiredFeatures, unnacceptableFeatures;
	
	
	// CONSTRUCTOR	-------------------------------------
	
	/**
	 * Creates a new MouseEventSelector. The required features should be added separately.
	 */
	public StrictEventSelector()
	{
		// Initializes attributes
		this.requiredFeatures = new ArrayList<Event.Feature>();
		this.unnacceptableFeatures = new ArrayList<Event.Feature>();
	}

	
	// OTHER METHODS	---------------------------------
	
	/**
	 * Adds a new feature to the features required for selection. Watch out for 
	 * exclusive features
	 * 
	 * @param feature The feature the event must have in order to be selected
	 */
	public void addRequiredFeature(Event.Feature feature)
	{
		if (feature != null && !this.requiredFeatures.contains(feature) && 
				!this.unnacceptableFeatures.contains(feature))
			this.requiredFeatures.add(feature);
	}
	
	/**
	 * Adds a new feature to the features that are not acceptable for selection.
	 * @param feature The feature which makes an event unacceptable.
	 */
	public void addUnacceptableFeature(Event.Feature feature)
	{
		if (feature != null && !this.unnacceptableFeatures.contains(feature) && 
				!this.requiredFeatures.contains(feature))
			this.unnacceptableFeatures.add(feature);
	}
	
	@Override
	public boolean selects(Event event)
	{
		// Checks if the event has all the required features
		List<Event.Feature> features = event.getFeatures();
		
		for (Event.Feature requirement : this.requiredFeatures)
		{
			if (!features.contains(requirement))
				return false;
		}
		
		for (Event.Feature unacceptable : this.unnacceptableFeatures)
		{
			if (features.contains(unacceptable))
				return false;
		}
		
		return true;
	}
	
	
	// FACTORIES	---------------------------------------
	
	/**
	 * @return A selector that accepts all mouse events
	 */
	public static StrictEventSelector createAllAcceptingSelector()
	{
		return new StrictEventSelector();
	}
}
