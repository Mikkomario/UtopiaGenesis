package genesis_event;

import java.util.ArrayList;
import java.util.List;

import utopia.inception.event.Event;
import utopia.inception.event.StrictEventSelector;

/**
 * KeyEvents are used in informing KeyListeners
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class KeyEvent implements Event
{
	// ATTRIBUTES	-----------------------------------
	
	private final int key;
	private final KeyEventType eventType;
	private final ContentType contentType;
	private final double duration;
	
	/**
	 * The left arrow keycode
	 */
	public final static int LEFT = 37;
	/**
	 * The up arrow keycode
	 */
	public final static int UP = 38;
	/**
	 * The right arrow keycode
	 */
	public final static int RIGHT = 39;
	/**
	 * The down arrow keycode
	 */
	public final static int DOWN = 40;
	/**
	 * The ctrl keycode, same for both left and right control
	 */
	public final static int CTRL = 17;
	/**
	 * The shift keycode, same for both left and right shift
	 */
	public final static int SHIFT = 16;
	/**
	 * The backspace keycode
	 */
	public final static int BACKSPACE = 8;
	/**
	 * The caps lock keycode
	 */
	public final static int CAPSLOCK = 20;
	/**
	 * The delete keycode
	 */
	public final static int DELETE = 127;
	/**
	 * The enter keycode
	 */
	public final static int ENTER = 10;
	/**
	 * The home keycode
	 */
	public final static int HOME = 36;
	/**
	 * The end keycode
	 */
	public final static int END = 35;
	/**
	 * The page up keycode
	 */
	public final static int PGUP = 33;
	/**
	 * The page down keycode
	 */
	public final static int PGDOWN = 34;
	/**
	 * The escape keycode
	 */
	public final static int ESCAPE = 27;
	
	
	// CONSTRUCTOR	-----------------------------------
	
	/**
	 * Creates a new keyEvent with the given information
	 * 
	 * @param key The key (or keyCode) that originated the event
	 * @param eventType The type of the event
	 * @param contentType The type of the content (key)
	 * @param duration How long the event lasted
	 */
	public KeyEvent(int key, KeyEventType eventType, ContentType contentType, 
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
	public static StrictEventSelector<KeyEvent, Feature> createEventTypeSelector(KeyEventType eventType)
	{
		StrictEventSelector<KeyEvent, Feature> selector = new StrictEventSelector<>();
		selector.addRequiredFeature(eventType);
		return selector;
	}
	
	/**
	 * @return A selector that accepts button presses as well as button releases
	 */
	public static StrictEventSelector<KeyEvent, Feature> createButtonStateChangeSelector()
	{
		StrictEventSelector<KeyEvent, Feature> selector = new StrictEventSelector<>();
		selector.addUnacceptableFeature(KeyEventType.DOWN);
		return selector;
	}
	
	
	// INTERFACES	------------------------------------------
	
	/**
	 * Features can only describe KeyEvents
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
