package genesis_logic;

import genesis_logic.AdvancedMouseEvent.MouseButton;
import genesis_logic.AdvancedMouseEvent.MouseButtonEventScale;
import genesis_logic.AdvancedMouseEvent.MouseButtonEventType;
import genesis_logic.AdvancedMouseEvent.MouseEventType;
import genesis_logic.AdvancedMouseEvent.MouseMovementEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * MouseEventSelectors select only certain MouseEvents they receive, based on their attributes. 
 * Selectors can be used for picking the events that interest the user.
 * 
 * @author Mikko Hilpinen
 * @since 17.11.2014
 */
public class StrictMouseEventSelector implements MouseEventSelector
{
	// ATTRIBUTES	-------------------------------------
	
	private final List<AdvancedMouseEvent.Feature> requiredFeatures, unnacceptableFeatures;
	
	
	// CONSTRUCTOR	-------------------------------------
	
	/**
	 * Creates a new MouseEventSelector. The required features should be added separately.
	 */
	public StrictMouseEventSelector()
	{
		// Initializes attributes
		this.requiredFeatures = new ArrayList<AdvancedMouseEvent.Feature>();
		this.unnacceptableFeatures = new ArrayList<AdvancedMouseEvent.Feature>();
	}

	
	// OTHER METHODS	---------------------------------
	
	/**
	 * Adds a new feature to the features required for selection. Watch out for 
	 * exclusive features
	 * 
	 * @param feature The feature the event must have in order to be selected
	 */
	public void addRequiredFeature(AdvancedMouseEvent.Feature feature)
	{
		if (feature != null && !this.requiredFeatures.contains(feature) && 
				!this.unnacceptableFeatures.contains(feature))
			this.requiredFeatures.add(feature);
	}
	
	/**
	 * Adds a new feature to the features that are not acceptable for selection.
	 * @param feature The feature which makes an event unacceptable.
	 */
	public void addUnacceptableFeature(AdvancedMouseEvent.Feature feature)
	{
		if (feature != null && !this.unnacceptableFeatures.contains(feature) && 
				!this.requiredFeatures.contains(feature))
			this.unnacceptableFeatures.add(feature);
	}
	
	@Override
	public boolean selects(AdvancedMouseEvent event)
	{
		// Checks if the event has all the required features
		List<AdvancedMouseEvent.Feature> features = event.getEventFeatures();
		
		for (AdvancedMouseEvent.Feature requirement : this.requiredFeatures)
		{
			if (!features.contains(requirement))
				return false;
		}
		
		for (AdvancedMouseEvent.Feature unacceptable : this.unnacceptableFeatures)
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
	public static StrictMouseEventSelector createAllAcceptingSelector()
	{
		return new StrictMouseEventSelector();
	}
	
	/**
	 * @return A selector that accepts mouse button events
	 */
	public static StrictMouseEventSelector createButtonEventSelector()
	{
		StrictMouseEventSelector selector = new StrictMouseEventSelector();
		selector.addRequiredFeature(MouseEventType.BUTTON);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse movement events
	 */
	public static StrictMouseEventSelector createMovementEventSelector()
	{
		StrictMouseEventSelector selector = new StrictMouseEventSelector();
		selector.addRequiredFeature(MouseEventType.MOVEMENT);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse button state change events (presses & releases)
	 */
	public static StrictMouseEventSelector createButtonStateChangeSelector()
	{
		StrictMouseEventSelector selector = createButtonEventSelector();
		selector.addUnacceptableFeature(MouseButtonEventType.DOWN);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse button state change events that occur in an 
	 * object's local scale.
	 */
	public static StrictMouseEventSelector createLocalButtonStateChangeSelector()
	{
		StrictMouseEventSelector selector = createButtonStateChangeSelector();
		selector.addRequiredFeature(MouseButtonEventScale.LOCAL);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse entering and mouse exiting events
	 */
	public static StrictMouseEventSelector createEnterExitSelector()
	{
		StrictMouseEventSelector selector = createMovementEventSelector();
		selector.addUnacceptableFeature(MouseMovementEventType.MOVE);
		return selector;
	}
	
	/**
	 * @return A selector that accepts only the mouse move event
	 */
	public static StrictMouseEventSelector createMouseMoveSelector()
	{
		StrictMouseEventSelector selector = createMovementEventSelector();
		selector.addRequiredFeature(MouseMovementEventType.MOVE);
		return selector;
	}
	
	/**
	 * @param requiredButton The mouse button that should originate the selected events.
	 * @return A selector that accepts only events caused by a single mouse button
	 */
	public static StrictMouseEventSelector createMouseButtonSelector(MouseButton requiredButton)
	{
		StrictMouseEventSelector selector = createButtonEventSelector();
		selector.addRequiredFeature(requiredButton);
		return selector;
	}
}
