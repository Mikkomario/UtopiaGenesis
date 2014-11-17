package genesis_logic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * MouseEvents are used in informing mouseListeners
 * 
 * @author Mikko Hilpinen
 * @since 17.11.2014
 */
public class AdvancedMouseEvent
{
	// ATTRIBUTES	---------------------------------------
	
	private final MouseButton button;
	private final MouseEventType type;
	private final MouseButtonEventType buttonEvent;
	private final MouseMovementEventType movementType;
	private final MouseButtonEventScale scale;
	
	private final Point2D.Double position;
	private final double duration;
	
	
	// CONSTRUCTOR	---------------------------------------
	
	/**
	 * Creates a new MouseButtonEvent with the given information
	 * 
	 * @param type The type of the button originated event
	 * @param button The mouse button that originated the event
	 * @param position The mouse position when the event occurred
	 * @param duration How many steps the event lasted
	 */
	public AdvancedMouseEvent(MouseButtonEventType type, MouseButton button, 
			Point2D.Double position, double duration)
	{
		// Initializes attributes
		this.button = button;
		this.type = MouseEventType.BUTTON;
		this.buttonEvent = type;
		this.movementType = MouseMovementEventType.NONE;
		this.position = position;
		this.duration = duration;
		this.scale = MouseButtonEventScale.NONE;
	}
	
	/**
	 * Creates a new MouseMovementEvent with the given information
	 * 
	 * @param position The mouse position when the event occurred
	 * @param duration How many steps the event lasted
	 */
	public AdvancedMouseEvent(Point2D.Double position, double duration)
	{
		// Initializes attributes
		this.button = MouseButton.NONE;
		this.type = MouseEventType.MOVEMENT;
		this.buttonEvent = MouseButtonEventType.NONE;
		this.movementType = MouseMovementEventType.NONE;
		this.position = position;
		this.duration = duration;
		this.scale = MouseButtonEventScale.NONE;
	}
	
	private AdvancedMouseEvent(AdvancedMouseEvent other, MouseButtonEventScale scale)
	{
		this.button = other.getButton();
		this.type = other.getType();
		this.buttonEvent = other.getButtonEventType();
		this.movementType = other.getMovementEventType();
		this.position = other.getPosition();
		this.duration = other.getDuration();
		this.scale = scale;
	}
	
	private AdvancedMouseEvent(AdvancedMouseEvent other, MouseMovementEventType movementType)
	{
		this.button = other.getButton();
		this.type = other.getType();
		this.buttonEvent = other.getButtonEventType();
		this.movementType = movementType;
		this.position = other.getPosition();
		this.duration = other.getDuration();
		this.scale = other.getButtonEventScale();
	}
	
	
	// GETTERS & SETTERS	-------------------------------
	
	/**
	 * @return The mouse button that originated the event
	 */
	public final MouseButton getButton()
	{
		return this.button;
	}
	
	/**
	 * @return The type of event in question
	 */
	public final MouseEventType getType()
	{
		return this.type;
	}
	
	/**
	 * @return The type of button action that originated the event
	 */
	public final MouseButtonEventType getButtonEventType()
	{
		return this.buttonEvent;
	}
	
	/**
	 * @return The type of mouse movement that originated the event
	 */
	public final MouseMovementEventType getMovementEventType()
	{
		return this.movementType;
	}
	
	/**
	 * @return The scale of the button event
	 */
	public final MouseButtonEventScale getButtonEventScale()
	{
		return this.scale;
	}
	
	/**
	 * @return The mouse's position when the event was originated
	 */
	public final Point2D.Double getPosition()
	{
		return this.position;
	}
	
	/**
	 * @return How many steps the event lasted
	 */
	public final double getDuration()
	{
		return this.duration;
	}
	
	
	// OTHER METHODS	-----------------------------------

	/**
	 * @return The basic attributes / describing features of this MouseEvent
	 */
	protected List<Feature> getEventFeatures()
	{
		// Collects the event's information into a list an returns it
		List<Feature> information = new ArrayList<Feature>();
		
		information.add(getButton());
		information.add(getType());
		information.add(getButtonEventType());
		information.add(getMovementEventType());
		
		return information;
	}
	
	/**
	 * Returns a mouseEvent that has the the same features as this one except the given 
	 * mouse button scale.
	 * 
	 * @param scale The mouse button scale the event will have
	 * @return An event similar to this with the given button event scale
	 */
	protected AdvancedMouseEvent withScale(MouseButtonEventScale scale)
	{
		return new AdvancedMouseEvent(this, scale);
	}
	
	/**
	 * Returns a mouseEvent that has the same features as this except the given mouse 
	 * movement type.
	 * 
	 * @param movementType The movement type the new event will have
	 * @return An event similar to this with the given movement type
	 */
	protected AdvancedMouseEvent withMovementType(MouseMovementEventType movementType)
	{
		return new AdvancedMouseEvent(this, movementType);
	}
	
	
	// SUBCLASSES	---------------------------------------
	
	/**
	 * A wrapper for elements that describe AdvancedMouseEvents
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	protected static interface Feature
	{
		// This interface is used as a wrapper
	}
	
	
	// ENUMERATIONS	---------------------------------------
	
	/**
	 * MouseButton tells which mouse button originated the event
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static enum MouseButton implements Feature
	{
		/**
		 * The left mouse button
		 */
		LEFT,
		/**
		 * The right mouse button
		 */
		RIGHT,
		/**
		 * The middle mouse button
		 */
		MIDDLE,
		/**
		 * Neither of the mouse buttons / mouse button not applicable
		 */
		NONE;
	}
	
	/**
	 * MouseEventType tells which overall type of action originated the event
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static enum MouseEventType implements Feature
	{
		/**
		 * The event was originated by a mouse button related action
		 */
		BUTTON,
		/**
		 * The event was originated by mouse movement
		 */
		MOVEMENT;
	}
	
	/**
	 * MouseButtonEventType tells which type of mouse button action originated the event
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static enum MouseButtonEventType implements Feature
	{
		/**
		 * The mouse button was just pressed down
		 */
		PRESSED,
		/**
		 * The mouse button was just released
		 */
		RELEASED,
		/**
		 * The mouse button was kept down
		 */
		DOWN,
		/**
		 * None of the mouse button events is applicable / originated the event
		 */
		NONE;
	}
	
	/**
	 * MouseMovementEventType tells which kind of mouse movement action originated the event
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static enum MouseMovementEventType implements Feature
	{
		/**
		 * The mouse entered the object's area of interest
		 */
		ENTER,
		/**
		 * The mouse exited the object's area of interest
		 */
		EXIT,
		/**
		 * The mouse moved over the object's area of interest
		 */
		OVER,
		/**
		 * The mouse's position was changed
		 */
		MOVE,
		/**
		 * None of the mouse movement events is applicable / originated the event
		 */
		NONE;
	}
	
	/**
	 * MouseButtonEventScale tells where the mouse button event originated from
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static enum MouseButtonEventScale implements Feature
	{
		/**
		 * The mouse button was activated anywhere
		 */
		GLOBAL,
		/**
		 * The mouse button was activated in the object's area of interest
		 */
		LOCAL,
		/**
		 * The event wasn't originated by a mouse button anywhere
		 */
		NONE;
	}
}
