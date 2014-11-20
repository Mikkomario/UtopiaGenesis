package genesis_event;

import genesis_event.AdvancedMouseEvent.MouseButton;
import genesis_event.AdvancedMouseEvent.MouseButtonEventScale;
import genesis_event.AdvancedMouseEvent.MouseButtonEventType;
import genesis_event.AdvancedMouseEvent.MouseMovementEventType;
import genesis_util.StateOperator;
import genesis_util.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class handles the informing of mouseListeners. It does not actively find 
 * any new information though, which must be done through the subclasses.
 *
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 */
public abstract class AbstractMouseListenerHandler extends Handler<AdvancedMouseListener> implements Actor
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private Vector2D currentMousePosition;
	private HashMap<MouseButtonEventType, HashMap<MouseButton, Boolean>> mouseButtonStates;
	private HashMap<MouseMovementEventType, List<AdvancedMouseListener>> movementEventTargets;
	
	private double lastStepDuration;
	
	private StateOperator isActiveOperator;
	
	
	// CONSTRUCTOR	-------------------------------------------------------

	/**
	 * Creates a new empty mouselistenerhandler
	 *
	 * @param autodeath Will the handler die when it runs out of living listeners
	 * @param actorhandler The ActorHandler that will make the handler inform 
	 * its listeners (optional)
	 */
	public AbstractMouseListenerHandler(boolean autodeath, ActorHandler actorhandler)
	{
		super(autodeath);
		
		// Initializes attributes
		initialize();
		
		if (actorhandler != null)
			actorhandler.add(this);
	}
	
	/**
	 * Creates a new empty mouselistenerhandler
	 * @param autoDeath Will the handler die when it runs out of living listeners
	 * @param superHandlers The handlerRelay that holds the handlers that will handle this handler
	 */
	public AbstractMouseListenerHandler(boolean autoDeath, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
		
		initialize();
	}
	
	/**
	 * Creates a new empty mouseListenerHandler
	 * @param autoDeath Will the handler die once it runs out of handleds
	 */
	public AbstractMouseListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		initialize();
	}

	
	// IMPLEMENTED METHODS	-----------------------------------------------
	
	@Override
	public StateOperator getIsActiveStateOperator()
	{
		return this.isActiveOperator;
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.MOUSEHANDLER;
	}
	
	@Override
	public void act(double steps)
	{
		this.lastStepDuration = steps;
		
		// Informs the objects
		informObjectsAboutMouseButtonEvents(new AdvancedMouseEvent(MouseButtonEventType.NONE, 
				MouseButton.NONE, this.currentMousePosition, steps));
		informObjectsAboutMouseEnterExit(new AdvancedMouseEvent(this.currentMousePosition, 
				steps));
	}
	
	@Override
	protected boolean handleObject(AdvancedMouseListener l)
	{
		// Handles mouse move event
		
		// Checks if informing is needed
		if (!l.getListensToMouseEventsOperator().getState())
			return true;
		
		// Updates mouse-enter and mouse-exit	
		// Checks if entered
		if (!this.movementEventTargets.get(MouseMovementEventType.OVER).contains(l) && 
				!this.movementEventTargets.get(MouseMovementEventType.ENTER).contains(l) 
				&& l.isInAreaOfInterest(getMousePosition()))
			this.movementEventTargets.get(MouseMovementEventType.ENTER).add(l);

		// Checks if exited
		else if (this.movementEventTargets.get(MouseMovementEventType.OVER).contains(l) && 
				!this.movementEventTargets.get(MouseMovementEventType.EXIT).contains(l) && 
				!l.isInAreaOfInterest(getMousePosition()))
		{
			this.movementEventTargets.get(MouseMovementEventType.OVER).remove(l);
			this.movementEventTargets.get(MouseMovementEventType.EXIT).add(l);
		}
		
		// Informs the listener about the move event
		informObjectAboutMouseEvent(l, new AdvancedMouseEvent(getMousePosition(), 
				this.lastStepDuration).withMovementType(MouseMovementEventType.MOVE));
		
		return true;
	}
	
	
	// GETTERS & SETTERS	----------------------------------------------
	
	/**
	 * @return The current position of the mouse
	 */
	public Vector2D getMousePosition()
	{
		return this.currentMousePosition;
	}
	
	/**
	 * @param button The mouse button that might be down
	 * @return Is the given mouse button currently pressed down
	 */
	public boolean buttonIsDown(MouseButton button)
	{
		return this.mouseButtonStates.get(MouseButtonEventType.DOWN).get(button);
	}
	
	/**
	 * Informs the handler about a mouse button's new status
	 * 
	 * @param button The button whose status is updated
	 * @param isDown Is the given button currently pressed down
	 */
	public void setButtonState(MouseButton button, boolean isDown)
	{
		if (buttonIsDown(button) != isDown)
		{
			this.mouseButtonStates.get(MouseButtonEventType.DOWN).put(button, isDown);
			
			if (isDown)
				this.mouseButtonStates.get(MouseButtonEventType.PRESSED).put(button, true);
			else
				this.mouseButtonStates.get(MouseButtonEventType.RELEASED).put(button, true);
		}
	}
	
	/**
	 * Informs the object about the mouse's current position
	 *
	 * @param newMousePosition the new mouse position to be set
	 */
	public void setMousePosition(Vector2D newMousePosition)
	{		
		if (!getMousePosition().equals(newMousePosition))
		{
			this.currentMousePosition = newMousePosition;
			
			// Informs the objects
			handleObjects();
		}
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	private void informObjectsAboutMouseEnterExit(AdvancedMouseEvent baseEvent)
	{
		handleObjects(new MouseMovementEventOperator(baseEvent));
		
		// After the entered -event has been informed, over -event will be informed in the 
		// future instead
		List<AdvancedMouseListener> entered = 
				this.movementEventTargets.get(MouseMovementEventType.ENTER);
		if (!entered.isEmpty())
		{
			this.movementEventTargets.get(MouseMovementEventType.OVER).addAll(entered);
			entered.clear();
		}
		
		this.movementEventTargets.get(MouseMovementEventType.EXIT).clear();
	}
	
	private void informObjectsAboutMouseButtonEvents(AdvancedMouseEvent baseEvent)
	{
		handleObjects(new MouseButtonEventOperator(baseEvent));
		
		for (MouseButtonEventType buttonEvent : this.mouseButtonStates.keySet())
		{
			if (buttonEvent != MouseButtonEventType.DOWN)
			{
				for (MouseButton button : this.mouseButtonStates.get(buttonEvent).keySet())
				{
					this.mouseButtonStates.get(buttonEvent).put(button, false);
				}
			}
		}
	}
	
	private static void informObjectAboutMouseEvent(AdvancedMouseListener listener, 
			AdvancedMouseEvent event)
	{
		// Checks if the event should be given to the listener
		if (listener.getMouseEventSelector().selects(event))
			listener.onMouseEvent(event);
	}
	
	private void initialize()
	{
		// Initializes attributes
		this.currentMousePosition = new Vector2D(0, 0);
		this.lastStepDuration = 0;
		
		this.isActiveOperator = new AnyHandledListensMouseOperator(false);
		
		this.mouseButtonStates = new HashMap<MouseButtonEventType, HashMap<MouseButton, 
				Boolean>>();
		this.movementEventTargets = new HashMap<MouseMovementEventType, 
				List<AdvancedMouseListener>>();
		
		for (MouseButtonEventType type : MouseButtonEventType.values())
		{
			this.mouseButtonStates.put(type, new HashMap<MouseButton, Boolean>());
			
			for (MouseButton button : MouseButton.values())
			{
				this.mouseButtonStates.get(button).put(button, false);
			}
		}
		
		for (MouseMovementEventType type : MouseMovementEventType.values())
		{
			if (type != MouseMovementEventType.NONE && type != MouseMovementEventType.MOVE)
				this.movementEventTargets.put(type, new ArrayList<AdvancedMouseListener>());
		}
	}
	
	
	// SUBCLASSES	-------------------------------------------
	
	/**
	 * This StateOperator checks all the Handleds and is true if any of them listens to the 
	 * mouse. This operator may allow state changes and it may not.
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	protected class AnyHandledListensMouseOperator extends ForAnyHandledsOperator
	{
		// CONSTRUCTOR	---------------------------------------
		
		/**
		 * Creates a new StateOperator
		 * 
		 * @param mutable can the state of the handleds be changed through this operator
		 */
		public AnyHandledListensMouseOperator(boolean mutable)
		{
			super(mutable);
		}
		
		
		// IMPLEMENTED METHODS	-------------------------------

		@Override
		protected void changeHandledState(AdvancedMouseListener l, boolean newState)
		{
			l.getListensToMouseEventsOperator().setState(newState);
		}

		@Override
		protected boolean getHandledState(AdvancedMouseListener l)
		{
			return l.getListensToMouseEventsOperator().getState();
		}
	}
	
	private abstract class MouseEventOperator extends HandlingOperator
	{
		// ATTRIBUTES	---------------------------------------
		
		private AdvancedMouseEvent baseEvent;
		
		
		// CONSTRUCTOR	---------------------------------------
		
		public MouseEventOperator(AdvancedMouseEvent baseEvent)
		{
			this.baseEvent = baseEvent;
		}
		
		
		// GETTERS & SETTERS	-------------------------------
		
		protected AdvancedMouseEvent getBaseEvent()
		{
			return this.baseEvent;
		}
	}
	
	private class MouseMovementEventOperator extends MouseEventOperator
	{	
		// CONSTRUCTOR	---------------------------------------
		
		public MouseMovementEventOperator(AdvancedMouseEvent baseEvent)
		{
			super(baseEvent);
		}
		
		
		// IMPLEMENTED METHODS	-------------------------------
		
		@Override
		protected boolean handleObject(AdvancedMouseListener l)
		{
			// Checks if informing is needed
			if (!l.getListensToMouseEventsOperator().getState())
				return true;
			
			for (MouseMovementEventType movementType : 
					AbstractMouseListenerHandler.this.movementEventTargets.keySet())
			{
				if (AbstractMouseListenerHandler.this.movementEventTargets.get(movementType).contains(l))
				{
					informObjectAboutMouseEvent(l, getBaseEvent().withMovementType(movementType));
					break;
				}
			}
			
			return true;
		}
	}
	
	private class MouseButtonEventOperator extends MouseEventOperator
	{
		// CONSTRUCTOR	----------------------------------------
		
		public MouseButtonEventOperator(AdvancedMouseEvent baseEvent)
		{
			super(baseEvent);
		}
		
		
		// IMPLEMENTED METHODS	--------------------------------

		@Override
		protected boolean handleObject(AdvancedMouseListener l)
		{
			// Checks if informing is needed
			if (!l.getListensToMouseEventsOperator().getState())
				return true;
			
			// Checks the event scale
			MouseButtonEventScale scale = MouseButtonEventScale.GLOBAL;
			if (l.isInAreaOfInterest(getBaseEvent().getPosition()))
				scale = MouseButtonEventScale.LOCAL;
			
			HashMap<MouseButtonEventType, HashMap<MouseButton, Boolean>> buttonStates = 
					AbstractMouseListenerHandler.this.mouseButtonStates;
			for (MouseButtonEventType buttonEventType : buttonStates.keySet())
			{
				for (MouseButton button : buttonStates.get(buttonEventType).keySet())
				{
					if (buttonStates.get(buttonEventType).get(button))
						informObjectAboutMouseEvent(l, new AdvancedMouseEvent(buttonEventType, 
								button, getBaseEvent().getPosition(), 
								getBaseEvent().getDuration()).withScale(scale));
				}
			}
			
			return true;
		}
		
	}
}
