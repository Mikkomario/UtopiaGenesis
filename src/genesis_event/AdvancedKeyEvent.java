package genesis_event;

import java.util.ArrayList;
import java.util.List;

/**
 * KeyEvents are used in informing AdvancedKeyListeners
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class AdvancedKeyEvent implements Event
{
	// ATTRIBUTES	-----------------------------------
	
	private final int key;
	private final KeyEventType eventType;
	private final ContentType contentType;
	private final double duration;
	
	
	// CONSTRUCTOR	-----------------------------------
	
	/**
	 * Creates a new keyEvent with the given information
	 * 
	 * @param key The key (or keyCode) that originated the event
	 * @param eventType The type of the event
	 * @param contentType The type of the content (key)
	 * @param duration How long the event lasted
	 */
	public AdvancedKeyEvent(int key, KeyEventType eventType, ContentType contentType, 
			double duration)
	{
		// Initializes attributes
		this.key = key;
		this.eventType = eventType;
		this.contentType = contentType;
		this.duration = duration;
	}

	@Override
	public List<Event.Feature> getFeatures()
	{
		List<Event.Feature> features = new ArrayList<Event.Feature>();
		
		features.add(getEventType());
		features.add(getContentType());
		
		return features;
	}
	
	
	// GETTERS & SETTERS	---------------------------------
	
	/**
	 * @return The key (or key code) that originated the event
	 */
	public int getKey()
	{
		return this.key;
	}
	
	/**
	 * @return The key that originated the event. Represented as a character
	 */
	public char getKeyChar()
	{
		return (char) this.key;
	}
	
	/**
	 * @return The type of action that originated the event
	 */
	public KeyEventType getEventType()
	{
		return this.eventType;
	}
	
	/**
	 * @return The type of the content (key)
	 */
	public ContentType getContentType()
	{
		return this.contentType;
	}
	
	/**
	 * @return How long the event lasted
	 */
	public double getDuration()
	{
		return this.duration;
	}
	
	
	// OTHER METHODS	-------------------------------------
	
	/**
	 * Creates a selector that only selects events of the given type
	 * @param eventType The type of event the selector accepts
	 * @return A selector that only accepts button events of the given type
	 */
	public static StrictEventSelector<AdvancedKeyEvent, Feature> createEventTypeSelector(KeyEventType eventType)
	{
		StrictEventSelector<AdvancedKeyEvent, Feature> selector = new StrictEventSelector<>();
		selector.addRequiredFeature(eventType);
		return selector;
	}
	
	/**
	 * @return A selector that accepts button presses as well as button releases
	 */
	public static StrictEventSelector<AdvancedKeyEvent, Feature> createButtonStateChangeSelector()
	{
		StrictEventSelector<AdvancedKeyEvent, Feature> selector = new StrictEventSelector<>();
		selector.addUnacceptableFeature(KeyEventType.DOWN);
		return selector;
	}
	
	
	// INTERFACES	------------------------------------------
	
	/**
	 * Features can only describe advancedKeyEvents
	 * @author Mikko Hilpinen
	 * @since 18.11.2014
	 */
	public interface Feature extends Event.Feature
	{
		// Used as a wrapper
	}

	
	// ENUMERATIONS	-------------------------------------------
	
	/**
	 * The type of the action that originated this event.
	 * 
	 * @author Mikko Hilpinen
	 * @since 18.11.2014
	 */
	public enum KeyEventType implements Feature
	{
		/**
		 * The key was just recently pressed down
		 */
		PRESSED, 
		/**
		 * The key was just recently released from a down state
		 */
		RELEASED, 
		/**
		 * The key is being kept down
		 */
		DOWN;
	}
	
	/**
	 * The type of content held in this event, as some keys use key codes.
	 * 
	 * @author Mikko Hilpinen
	 * @since 18.11.2014
	 */
	public enum ContentType implements Feature
	{
		/**
		 * The event was originated by a normal key press
		 */
		KEY, 
		/**
		 * The event was originated by a coded key
		 */
		KEYCODE;
	}
}
