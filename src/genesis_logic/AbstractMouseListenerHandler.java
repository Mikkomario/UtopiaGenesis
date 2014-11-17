package genesis_logic;

import genesis_logic.AdvancedMouseListener.MouseButton;
import genesis_logic.AdvancedMouseListener.MouseButtonEventScale;
import genesis_logic.AdvancedMouseListener.MouseButtonEventType;
import genesis_logic.AdvancedMouseListener.MousePositionEventType;
import genesis_util.GenesisHandlerType;
import genesis_util.Handled;
import genesis_util.Handler;
import genesis_util.HandlerRelay;
import genesis_util.HandlerType;
import genesis_util.StateOperator;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class handles the informing of mouselsteners. It does not actively find 
 * any new information though which must be done in the subclasses.
 *
 * @author Mikko Hilpinen.
 * @since 28.12.2012.
 */
public abstract class AbstractMouseListenerHandler extends Handler implements Actor
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private Point2D.Double currentMousePosition;
	private boolean ldown, rdown, lpressed, rpressed, lreleased, rreleased;
	
	private ArrayList<AdvancedMouseListener> entered;
	private ArrayList<AdvancedMouseListener> over;
	private ArrayList<AdvancedMouseListener> exited;
	
	private AdvancedMouseEvent lastevent;
	private double lasteventduration;
	
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
		super(autodeath, actorhandler);
		
		// Initializes attributes
		initialize();
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
		// Informs the objects
		this.lastevent = AdvancedMouseEvent.OTHER;
		this.lasteventduration = steps;
		
		handleObjects();
		
		// Refreshes memory
		if (this.entered.size() > 0)
		{
			this.over.addAll(this.entered);
			this.entered.clear();
		}
		
		this.exited.clear();
		this.lpressed = false;
		this.rpressed = false;
		this.lreleased = false;
		this.rreleased = false;
	}
	
	@Override
	protected boolean handleObject(Handled h)
	{
		// Informs the object about the mouse's position
		// Or button status
		AdvancedMouseListener l = (AdvancedMouseListener) h;
		
		// Checks if informing is needed
		if (!l.getListensToMouseEventsOperator().getState())
			return true;
		
		// Mouse position update
		if (this.lastevent == AdvancedMouseEvent.MOVE)
		{
			// Updates mouse-enter and mouse-leave
			if (l.listensMouseEnterExit())
			{		
				// Checks if entered
				if (!this.over.contains(l) && !this.entered.contains(l) 
						&& l.listensPosition(getMousePosition()))
				{
					this.entered.add(l);
					return true;
				}

				// Checks if exited
				if (this.over.contains(l) && !this.exited.contains(l) && 
						!l.listensPosition(getMousePosition()))
				{
					this.over.remove(l);
					this.exited.add(l);
				}
			}
			// Informs the listener about the move-event as well
			l.onMouseMove(getMousePosition());
		}
		else if (this.lastevent == AdvancedMouseEvent.OTHER)
		{
			// Only if the object cares about mouse movement
			if (l.listensMouseEnterExit())
			{
				// Mouseover
				if (this.over.contains(l))
					l.onMousePositionEvent(MousePositionEventType.OVER, 
							getMousePosition(), this.lasteventduration);
				// Exiting
				else if (this.exited.contains(l))
					l.onMousePositionEvent(MousePositionEventType.EXIT, 
							getMousePosition(), this.lasteventduration);
				// Entering
				else if (this.entered.contains(l))
					l.onMousePositionEvent(MousePositionEventType.ENTER, 
							getMousePosition(), this.lasteventduration);
			}
			
			// Informs about mouse buttons (if the listener is interested)
			if (l.getCurrentButtonScaleOfInterest().equals(
					MouseButtonEventScale.GLOBAL) || 
					(l.getCurrentButtonScaleOfInterest().equals(
					MouseButtonEventScale.LOCAL) && 
					l.listensPosition(getMousePosition())))
			{
				if (leftIsDown())
					l.onMouseButtonEvent(MouseButton.LEFT, 
							MouseButtonEventType.DOWN, getMousePosition(), 
							this.lasteventduration);
				if (rightIsDown())
					l.onMouseButtonEvent(MouseButton.RIGHT, 
							MouseButtonEventType.DOWN, getMousePosition(), 
							this.lasteventduration);
				if (this.lpressed)
					l.onMouseButtonEvent(MouseButton.LEFT, 
							MouseButtonEventType.PRESSED, getMousePosition(), 
							this.lasteventduration);
				if (this.rpressed)
					l.onMouseButtonEvent(MouseButton.RIGHT, 
							MouseButtonEventType.PRESSED, getMousePosition(), 
							this.lasteventduration);
				if (this.lreleased)
					l.onMouseButtonEvent(MouseButton.LEFT, 
							MouseButtonEventType.RELEASED, getMousePosition(), 
							this.lasteventduration);
				if (this.rreleased)
					l.onMouseButtonEvent(MouseButton.RIGHT, 
							MouseButtonEventType.RELEASED, getMousePosition(), 
							this.lasteventduration);
			}
		}
		
		return true;
	}
	
	
	// GETTERS & SETTERS	----------------------------------------------
	
	/**
	 * @return The current position of the mouse
	 */
	public Point2D.Double getMousePosition()
	{
		return this.currentMousePosition;
	}
	
	/**
	 * @return Is the left mouse button currently down
	 */
	public boolean leftIsDown()
	{
		return this.ldown;
	}
	
	/**
	 * @return Is the right mouse button currently down
	 */
	public boolean rightIsDown()
	{
		return this.rdown;
	}
	
	/**
	 * Informs the object about the mouse's current position
	 *
	 * @param newMousePosition the new mouse position to be set
	 */
	public void setMousePosition(Point2D.Double newMousePosition)
	{		
		if (!getMousePosition().equals(newMousePosition))
		{		
			this.currentMousePosition = (Point2D.Double) newMousePosition.clone();
			
			// Informs the objects
			this.lastevent = AdvancedMouseEvent.MOVE;
			handleObjects();
		}
	}
	
	/**
	 * Informs the object about the mouse's left button's status
	 *
	 * @param leftmousedown Is the mouse's left button down
	 */
	public void setLeftMouseDown(boolean leftmousedown)
	{
		if (this.ldown != leftmousedown)
		{
			this.ldown = leftmousedown;
			
			if (leftmousedown)
				this.lpressed = true;
			else
				this.lreleased = true;
		}
	}
	
	/**
	 * Informs the object about the mouse's right button's status
	 *
	 * @param rightmousedown Is the mouse's right button down
	 */
	public void setRightMouseDown(boolean rightmousedown)
	{
		if (this.rdown != rightmousedown)
		{
			this.rdown = rightmousedown;
			
			if (rightmousedown)
				this.rpressed = true;
			else
				this.rreleased = true;
		}
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Adds a new listener to the informed listeners
	 *
	 * @param m The MouseListener added
	 */
	public void addMouseListener(AdvancedMouseListener m)
	{
		addHandled(m);
	}
	
	private void initialize()
	{
		// Initializes attributes
		this.currentMousePosition = new Point2D.Double(0, 0);
		this.entered = new ArrayList<AdvancedMouseListener>();
		this.over = new ArrayList<AdvancedMouseListener>();
		this.exited = new ArrayList<AdvancedMouseListener>();
		this.lpressed = false;
		this.rpressed = false;
		this.lreleased = false;
		this.rreleased = false;
		this.lastevent = AdvancedMouseEvent.OTHER;
		this.lasteventduration = 0;
		
		this.isActiveOperator = new AnyHandledListensMouseOperator(false);
	}
	
	
	// ENUMERATIONS	------------------------------------------------------
	
	private enum AdvancedMouseEvent
	{
		MOVE, OTHER;
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
		protected void changeHandledState(Handled h, boolean newState)
		{
			((AdvancedMouseListener) h).getListensToMouseEventsOperator().setState(newState);
		}

		@Override
		protected boolean getHandledState(Handled h)
		{
			return ((AdvancedMouseListener) h).getListensToMouseEventsOperator().getState();
		}
	}
}
