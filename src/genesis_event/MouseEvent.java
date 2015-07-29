package genesis_event;

import genesis_util.Vector3D;

import java.util.ArrayList;
import java.util.List;

/**
 * MouseEvents are used in informing mouseListeners
 * 
 * @author Mikko Hilpinen
 * @since 17.11.2014
 */
public class MouseEvent implements Event
{
	// ATTRIBUTES	---------------------------------------
	
	private final MouseButton button;
	private final MouseEventType type;
	private final MouseButtonEventType buttonEvent;
	private final MouseMovementEventType movementType;
	private final MouseButtonEventScale scale;
	
	private final Vector3D position;
	private final double duration, wheelTurn;
	private final int wheelTurnInt;
	
	
	// CONSTRUCTOR	---------------------------------------
	
	/**
	 * Creates a new MouseButtonEvent with the given information
	 * 
	 * @param type The type of the button originated event
	 * @param button The mouse button that originated the event
	 * @param position The mouse position when the event occurred
	 * @param duration How many steps the event lasted
	 */
	public MouseEvent(MouseButtonEventType type, MouseButton button, 
			Vector3D position, double duration)
	{
		// Initializes attributes
		this.button = button;
		this.type = MouseEventType.BUTTON;
		this.buttonEvent = type;
		this.movementType = MouseMovementEventType.NONE;
		this.position = position;
		this.duration = duration;
		this.scale = MouseButtonEventScale.NONE;
		this.wheelTurn = 0;
		this.wheelTurnInt = 0;
	}
	
	/**
	 * Creates a new MouseMovementEvent with the given information
	 * 
	 * @param position The mouse position when the event occurred
	 * @param duration How many steps the event lasted
	 */
	public MouseEvent(Vector3D position, double duration)
	{
		// Initializes attributes
		this.button = MouseButton.NONE;
		this.type = MouseEventType.MOVEMENT;
		this.buttonEvent = MouseButtonEventType.NONE;
		this.movementType = MouseMovementEventType.NONE;
		this.position = position;
		this.duration = duration;
		this.scale = MouseButtonEventScale.NONE;
		this.wheelTurn = 0;
		this.wheelTurnInt = 0;
	}
	
	/**
	 * Creates a new MouseWheelEvent with the given information
	 * @param wheelTurn How much the mouse wheel turned
	 * @param wheelTurnInt How much the mouse wheel turned (in complete notches)
	 * @param position Where the mouse was when the wheel turned
	 * @param duration How many steps the event lasted
	 */
	public MouseEvent(double wheelTurn, int wheelTurnInt, Vector3D position, 
			double duration)
	{
		// Initializes attributes
		this.button = MouseButton.NONE;
		this.type = MouseEventType.WHEEL;
		this.buttonEvent = MouseButtonEventType.NONE;
		this.movementType = MouseMovementEventType.NONE;
		this.position = position;
		this.duration = duration;
		this.scale = MouseButtonEventScale.NONE;
		this.wheelTurn = wheelTurn;
		this.wheelTurnInt = wheelTurnInt;
	}
	
	private MouseEvent(MouseEvent other, MouseButtonEventScale scale)
	{
		this.button = other.getButton();
		this.type = other.getType();
		this.buttonEvent = other.getButtonEventType();
		this.movementType = other.getMovementEventType();
		this.position = other.getPosition();
		this.duration = other.getDuration();
		this.scale = scale;
		this.wheelTurn = other.getWheelTurn();
		this.wheelTurnInt = other.getWheelTurnInt();
	}
	
	private MouseEvent(MouseEvent other, MouseMovementEventType movementType)
	{
		this.button = other.getButton();
		this.type = other.getType();
		this.buttonEvent = other.getButtonEventType();
		this.movementType = movementType;
		this.position = other.getPosition();
		this.duration = other.getDuration();
		this.scale = other.getButtonEventScale();
		this.wheelTurn = other.getWheelTurn();
		this.wheelTurnInt = other.getWheelTurnInt();
	}
	
	
	// IMPLEMENTED METHODS	-------------------------------
	
	@Override
	public List<Event.Feature> getFeatures()
	{
		// Collects the event's information into a list an returns it
		List<Event.Feature> information = new ArrayList<Event.Feature>();
		
		information.add(getButton());
		information.add(getType());
		information.add(getButtonEventType());
		information.add(getMovementEventType());
		information.add(getButtonEventScale());
		
		return information;
	}
	
	
	// GETTERS & SETTERS	-------------------------------
	
	/**
	 * @return How much the mouse wheel turned
	 */
	public final double getWheelTurn()
	{
		return this.wheelTurn;
	}
	
	/**
	 * @return How much the mouse wheel turned (in complete notches)
	 */
	public final int getWheelTurnInt()
	{
		return this.wheelTurnInt;
	}
	
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
	public final Vector3D getPosition()
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
	 * Returns a mouseEvent that has the the same features as this one except the given 
	 * mouse button scale.
	 * 
	 * @param scale The mouse button scale the event will have
	 * @return An event similar to this with the given button event scale
	 */
	protected MouseEvent withScale(MouseButtonEventScale scale)
	{
		return new MouseEvent(this, scale);
	}
	
	/**
	 * Returns a mouseEvent that has the same features as this except the given mouse 
	 * movement type.
	 * 
	 * @param movementType The movement type the new event will have
	 * @return An event similar to this with the given movement type
	 */
	protected MouseEvent withMovementType(MouseMovementEventType movementType)
	{
		return new MouseEvent(this, movementType);
	}
	
	/**
	 * @return A selector that accepts mouse button events
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createButtonEventSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				new StrictEventSelector<>();
		selector.addRequiredFeature(MouseEventType.BUTTON);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse movement events
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createMovementEventSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				new StrictEventSelector<>();
		selector.addRequiredFeature(MouseEventType.MOVEMENT);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse button state change events (presses & releases)
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createButtonStateChangeSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				createButtonEventSelector();
		selector.addUnacceptableFeature(MouseButtonEventType.DOWN);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse button state change events that occur in an 
	 * object's local scale.
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createLocalButtonStateChangeSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				createButtonStateChangeSelector();
		selector.addRequiredFeature(MouseButtonEventScale.LOCAL);
		return selector;
	}
	
	/**
	 * @return A selector that accepts mouse entering and mouse exiting events
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createEnterExitSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				createMovementEventSelector();
		selector.addUnacceptableFeature(MouseMovementEventType.MOVE);
		selector.addUnacceptableFeature(MouseMovementEventType.OVER);
		return selector;
	}
	
	/**
	 * @return A selector that accepts only the mouse move event
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createMouseMoveSelector()
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				createMovementEventSelector();
		selector.addRequiredFeature(MouseMovementEventType.MOVE);
		return selector;
	}
	
	/**
	 * @param requiredButton The mouse button that should originate the selected events.
	 * @return A selector that accepts only events caused by a single mouse button
	 */
	public static StrictEventSelector<MouseEvent, MouseEvent.Feature> createMouseButtonSelector(MouseButton requiredButton)
	{
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				createButtonEventSelector();
		selector.addRequiredFeature(requiredButton);
		return selector;
	}
	
	/**
	 * @return A mouseEventSelector that only selects mouse wheel events
	 */
	public static StrictEventSelector<MouseEvent, Feature> createMouseWheelSelector()
	{
		StrictEventSelector<MouseEvent, Feature> selector = new StrictEventSelector<>();
		selector.addRequiredFeature(MouseEventType.WHEEL);
		return selector;
	}
	
	/**
	 * Creates an eventSelector that accepts mouse button events of a certain type
	 * @param type The type of mouse button event accepted
	 * @return A selector that only accepts mouse button events of the given type
	 */
	public static StrictEventSelector<MouseEvent, Feature> createButtonEventTypeSelector(
			MouseButtonEventType type)
	{
		StrictEventSelector<MouseEvent, Feature> selector = createButtonEventSelector();
		selector.addRequiredFeature(type);
		return selector;
	}
	
	
	// SUBCLASSES	---------------------------------------
	
	/**
	 * A wrapper for elements that describe AdvancedMouseEvents
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	public static interface Feature extends Event.Feature
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
		MOVEMENT,
		/**
		 * The event was originated by the mouse wheel scrolling
		 */
		WHEEL;
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
