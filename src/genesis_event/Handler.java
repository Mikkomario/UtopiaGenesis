package genesis_event;

import genesis_util.HandlingStateOperatorRelay;
import genesis_util.StateOperator;
import genesis_util.StateOperatorListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handlers specialize in handling certain types of objects. Each handler can 
 * inform its subobjects and can be handled itself.
 *
 * @author Mikko Hilpinen.
 * @param <T> The type of the handled held in this handler
 * @since 8.12.2012.
 */
public abstract class Handler<T extends Handled> implements Handled, StateOperatorListener
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private static final int ADD = 1;
	private static final int REMOVE = 2;
	private static final int CLEAR = 3;
	
	private Map<HandlingOperation, List<T>> operationLists;
	private StateOperator isDeadOperator;
	private boolean started, autoDeath;
	private HandlingStateOperatorRelay handlingOperators;
	
	private Map<HandlingOperation, ReentrantLock> locks;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new Handler and adds it as a handled to the applicable handlers in the given 
	 * relay.
	 * @param autoDeath Will the handler die once it becomes empty again
	 * @param superHandlers A HandlerRelay that holds the Handlers that handle this handler (optional)
	 */
	public Handler(boolean autoDeath, HandlerRelay superHandlers)
	{
		// Initializes attributes
		initialize(autoDeath);
		
		if (superHandlers != null)
			superHandlers.addHandled(this);
	}
	
	/**
	 * Creates a new Handler that won't be handled by any handler.
	 * 
	 * @param autoDeath Will the handler die once it becomes empty again
	 */
	public Handler(boolean autoDeath)
	{
		initialize(autoDeath);
	}
	
	
	// ABSTRACT METHODS	---------------------------------------------------
	
	/**
	 * @return Which kind of HandlerType this handler represents
	 */
	public abstract HandlerType getHandlerType();
	
	/**
	 * Many handlers are supposed to do something to the handled objects. 
	 * That something should be done in this method. The method is called as 
	 * a part of the handleObjects method.
	 *
	 * @param h The handled that may need handling
	 * @return Should object handling be continued (true) or skipped for the 
	 * remaining handleds (false)
	 */
	protected abstract boolean handleObject(T h);
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		// Initializes the operator only if necessary
		if (this.isDeadOperator == null)
		{
			if (this.autoDeath)
				this.isDeadOperator = new HandledDependentAutodeathOperator();
			else
				this.isDeadOperator = new IndependentDeathOperator();
			this.isDeadOperator.getListenerHandler().add(this);
		}
		return this.isDeadOperator;
	}
	
	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		// If the Handler was killed, clears the remaining operation lists
		if (newState && source == this.isDeadOperator)
		{
			for (HandlingOperation operation : HandlingOperation.values())
			{
				modifyOperationList(operation, CLEAR, null);
			}
		}
	}
	
	@Override
	public HandlingStateOperatorRelay getHandlingOperators()
	{
		return this.handlingOperators;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * @return Is the handler currently empty
	 */
	public boolean isEmpty()
	{
		return getHandledNumber() == 0;
	}
	
	/**
	 * @return The stateOperator that defines whether the objects in this handler should 
	 * be handled
	 */
	public StateOperator getHandlingOperator()
	{
		return getHandlingOperators().getShouldBeHandledOperator(getHandlerType());
	}
	
	/**
	 * Takes Handleds from another handler and moves them to this handler instead.
	 * 
	 * @param other The handler from which the Handleds are moved from. 
	 * Must be of the same HandlerType with this handler.
	 */
	public void transferHandledsFrom(Handler<T> other)
	{
		// Transfers the handleds
		List<T> handledsToBeTransferred = new ArrayList<T>();
		handledsToBeTransferred.addAll(other.operationLists.get(HandlingOperation.HANDLE));
		handledsToBeTransferred.addAll(other.operationLists.get(HandlingOperation.ADD));
		
		for (T h : handledsToBeTransferred)
		{
			add(h);
			other.removeHandled(h);
		}
		
		handledsToBeTransferred.clear();
	}
	
	/**
	 * Goes through all the handleds and calls the operator's handleObject() 
	 * -method for the objects
	 * @param operator The operation done for each handled. Null if the default 
	 * handleObject(Handled) should be used
	 * @param checkHandlingState If this is true, the object's handling state affects whether 
	 * the {@link #handleObject(Handled)} will be called for that object. If false, the method 
	 * will be called for each object in the handler
	 * @see #handleObject(Handled)
	 * @see HandlingOperator
	 */
	protected void handleObjects(HandlingOperator operator, boolean checkHandlingState)
	{	
		updateStatus();
		
		// Goes through all the handleds
		boolean handlingskipped = false;
		this.locks.get(HandlingOperation.HANDLE).lock();

		try
		{
			Iterator<T> iterator = this.operationLists.get(HandlingOperation.HANDLE).iterator();
			
			while (iterator.hasNext())
			{
				/* TODO: Concurrent modification exception on line on the next line?
				if (this.killed)
					break;
				*/
				T h = iterator.next();
				
				if (!h.getIsDeadStateOperator().getState())
				{	
					// Doesn't handle objects after handleobjects has returned 
					// false. Continues through the cycle though to remove dead 
					// handleds
					// The object's state also defines whether it will be handled at all
					if (!handlingskipped && !checkHandlingState || (h.getHandlingOperators() 
							!= null && h.getHandlingOperators().getShouldBeHandledOperator(
							getHandlerType()).getState()))
					{
						if (operator == null)
						{
							if (!handleObject(h))
								handlingskipped = true;
						}
						else if (!operator.handleObject(h))
							handlingskipped = true;
					}
				}
				else
					removeHandled(h);
			}
		}
		finally { this.locks.get(HandlingOperation.HANDLE).unlock(); }
		
		updateStatus();
	}
	
	/**
	 * Goes through all the handleds and calls handleObject -method for those 
	 * objects
	 * @param checkHandlingState If this is true, the object's handling state affects whether 
	 * the {@link #handleObject(Handled)} will be called for that object. If false, the method 
	 * will be called for each object in the handler
	 * @see #handleObject(Handled)
	 * @see #handleObjects(HandlingOperator, boolean)
	 */
	protected void handleObjects(boolean checkHandlingState)
	{
		handleObjects(null, checkHandlingState);
	}
	
	/**
	 * Adds a new object to the handled objects
	 *
	 * @param h The object to be handled
	 */
	public void add(T h)
	{	
		// TODO: Delay add for all objects so that they can initialize themselves first
		
		// Performs necessary checks
		if (h != null && h != this && !this.operationLists.get(
				HandlingOperation.HANDLE).contains(h) && 
				!this.operationLists.get(HandlingOperation.ADD).contains(h))
		{
			// Adds the handled to the queue
			modifyOperationList(HandlingOperation.ADD, ADD, h);
			this.started = true;
		}
	}
	
	/**
	 * Removes a handled from the group of handled objects
	 *
	 * @param h The handled object to be removed
	 */
	public void removeHandled(Handled h)
	{
		if (h != null && !this.operationLists.get(HandlingOperation.REMOVE).contains(h) && 
				this.operationLists.get(HandlingOperation.HANDLE).contains(h))
			modifyOperationList(HandlingOperation.REMOVE, ADD, h);
	}
	
	/**
	 * Removes all the handleds from the handler
	 */
	public void removeAllHandleds()
	{
		this.locks.get(HandlingOperation.HANDLE).lock();
		try
		{
			Iterator<T> iter = this.operationLists.get(HandlingOperation.HANDLE).iterator();
			
			while (iter.hasNext())
			{
				removeHandled(iter.next());
			}
		}
		finally { this.locks.get(HandlingOperation.HANDLE).unlock(); }
		
		// Also cancels the adding of new handleds
		modifyOperationList(HandlingOperation.ADD, CLEAR, null);
	}
	
	/**
	 * @return How many objects is the handler currently taking care of
	 */
	public int getHandledNumber()
	{
		return this.operationLists.get(HandlingOperation.HANDLE).size() + 
				this.operationLists.get(HandlingOperation.ADD).size();
	}
	
	/**
	 * Checks if the handler contains the given object
	 * @param h The handled that may reside in the handler
	 * @return Is the given handled in this handler
	 */
	public boolean containsHandled(Handled h)
	{
		return this.operationLists.get(HandlingOperation.HANDLE).contains(h) || 
				this.operationLists.get(HandlingOperation.ADD).contains(h);
	}
	
	/**
	 * Adds a handled to this Handler. This only works if the handled is of type allowed 
	 * by the Handler's handlerType.
	 * 
	 * @param h The handled that may be added to the Handler
	 * @throws IllegalArgumentException If the Handled is not of the correct type
	 */
	@SuppressWarnings("unchecked")
	protected void volatileAdd(Handled h) throws IllegalArgumentException
	{
		// Checks the type
		if (getHandlerType().getSupportedHandledClass().isInstance(h))
			add((T) h);
		else
			throw new IllegalArgumentException("Handled " + h + 
					" ins't allowed in this handler");
	}
	
	/**
	 * Updates the handler list by adding new members and removing old ones. 
	 * This method should not be called during an iteration but is useful before 
	 * testing the handler status.<br>
	 * Status is automatically updated each time the handleds in the handler 
	 * are handled.
	 * 
	 * @see #handleObjects(boolean)
	 */
	protected void updateStatus()
	{
		// Adds the new handleds (if possible)
		addNewHandleds();
		// Removes the removed handleds (if possible)
		clearRemovedHandleds();
	}
	
	/**
	 * Sorts the list of handleds using the given comparator
	 *
	 * @param c The comparator used to sort the handleds
	 */
	protected void sortHandleds(Comparator<T> c)
	{
		Collections.sort(this.operationLists.get(HandlingOperation.HANDLE), c);
	}
	
	// This should be called at the end of the iteration
	private void clearRemovedHandleds()
	{
		if (this.operationLists.get(HandlingOperation.REMOVE).isEmpty())
			return;
		
		this.locks.get(HandlingOperation.REMOVE).lock();
		try
		{
			// Removes all removed handleds from handleds or added or 
			// inserted handleds
			for (Handled h : this.operationLists.get(HandlingOperation.REMOVE))
			{
				if (this.operationLists.get(HandlingOperation.HANDLE).contains(h))
					modifyOperationList(HandlingOperation.HANDLE, REMOVE, h);
			}
			
			// Empties the removing list
			modifyOperationList(HandlingOperation.REMOVE, CLEAR, null);
		}
		finally { this.locks.get(HandlingOperation.REMOVE).unlock(); }
	}
	
	private void addNewHandleds()
	{
		// If the handler has no handleds to be added, does nothing
		if (this.operationLists.get(HandlingOperation.ADD).isEmpty())
			return;
		
		this.locks.get(HandlingOperation.ADD).lock();
		try
		{
			// Adds all handleds from the addlist to the handleds
			for (Handled h : this.operationLists.get(HandlingOperation.ADD))
			{
				modifyOperationList(HandlingOperation.HANDLE, ADD, h);
			}
			
			// Clears the addlist
			modifyOperationList(HandlingOperation.ADD, CLEAR, null);
		}
		finally { this.locks.get(HandlingOperation.ADD).unlock(); }
	}
	
	@SuppressWarnings("unchecked")
	private void modifyOperationList(HandlingOperation targetOperation, int job, Handled target)
	{
		// Locks the correct lock
		Handler.this.locks.get(targetOperation).lock();
		
		try
		{
			List<T> targetList = Handler.this.operationLists.get(targetOperation);
			
			switch (job)
			{
				case ADD: targetList.add((T) target); break;
				case REMOVE: targetList.remove(target); break;
				case CLEAR: targetList.clear(); break;
			}
		}
		finally
		{
			Handler.this.locks.get(targetOperation).unlock();
		}	
	}
	
	
	private void initialize(boolean autoDeath)
	{
		this.operationLists = new HashMap<>();
		for (HandlingOperation operation : HandlingOperation.values())
		{
			if (operation == HandlingOperation.HANDLE)
				this.operationLists.put(operation, new LinkedList<T>());
			else
				this.operationLists.put(operation, new ArrayList<T>());
		}
		
		this.started = false;
		this.locks = new HashMap<HandlingOperation, ReentrantLock>();
		this.locks.put(HandlingOperation.HANDLE, new ReentrantLock());
		this.locks.put(HandlingOperation.ADD, new ReentrantLock());
		this.locks.put(HandlingOperation.REMOVE, new ReentrantLock());
		
		// Handlers can't use stateOperators that use handlers. StackOverFlow
		this.autoDeath = autoDeath;
		this.isDeadOperator = null;
		this.handlingOperators = new HandlingStateOperatorRelay(new StateOperator(true, 
				false));
		getHandlingOperators().setShouldBeHandledOperator(getHandlerType(), 
				new ForAnyHandledShouldBeHandledOperator());
	}
	
	
	// ENUMERATIONS	------------------------------------------------------
	
	private enum HandlingOperation
	{
		HANDLE, ADD, REMOVE;
	}
	
	
	// SUBCLASSES	-------------------------------------------------------
	
	/**
	 * HandlingOperator is a function object that does a specific operation 
	 * for a single handled. The subclasses of this class will define the 
	 * nature of the operation.<br>
	 * HandlingOperators are used in handleObjects() -method and are usually 
	 * used with multiple handleds in succession.
	 *
	 * @author Mikko Hilpinen.
	 * @since 19.10.2013.
	 */
	protected abstract class HandlingOperator
	{
		// ABSTRACT METHODS	---------------------------------------------
		
		/**
		 * In this method the operator affects the handled in some way.
		 *
		 * @param h The handled that needs to be done something with
		 * @return Should the operation be done for the remaining handleds as well
		 */
		protected abstract boolean handleObject(T h);
	}
	
	private abstract class IterativeStateOperator extends StateOperator
	{
		// CONSTRUCTOR	--------------------------------------
				
		public IterativeStateOperator(boolean mutable)
		{
			super(false, mutable);
		}
		
		// ABSTRACT METHODS	----------------------------------
		
		/**
		 * Returns the stateOperator of the given handled
		 * @param h The handled in question
		 * @return The handled's stateOperator
		 */
		protected abstract StateOperator getHandledStateOperator(T h);
		
		
		// IMPLEMENTED METHODS	------------------------------
		
		@Override
		public void setState(boolean newState)
		{
			// Tries to change the state of all the handleds
			handleObjects(new StateAdjustMentOperator(newState), false);
			super.setState(newState);
		}
		
		
		// SUBCLASSES	-----------------------------------------
		
		private class StateAdjustMentOperator extends HandlingOperator
		{
			// ATTRIBUTES	-------------------------------------
			
			private boolean newState;
			
			
			// CONSTRUCTOR	-------------------------------------
			
			public StateAdjustMentOperator(boolean newState)
			{
				// Initializes attributes
				this.newState = newState;
			}
			
			
			// IMPLEMENTED METHODS	-----------------------------
			
			@Override
			protected boolean handleObject(T h)
			{
				getHandledStateOperator(h).setState(this.newState);
				return true;
			}	
		}
		
		protected class StateCheckOperator extends HandlingOperator
		{
			// ATTRIBUTES	------------------------------------
			
			private boolean found, searchedState;
			
			
			// CONSTRUCTOR	------------------------------------
			
			public StateCheckOperator(boolean searchedState)
			{
				// Initializes attributes
				this.found = false;
				this.searchedState = searchedState;
			}
			
			
			// IMPLEMENTED METHODS	----------------------------
			
			@Override
			protected boolean handleObject(T h)
			{
				StateOperator stateOperator = getHandledStateOperator(h);
				if (stateOperator != null && stateOperator.getState() == this.searchedState)
				{
					this.found = true;
					return false;
				}
				else
					return true;
			}
			
			
			// OTHER METHODS	--------------------------------
			
			public boolean getState()
			{
				return this.found;
			}
		}
	}
	
	/**
	 * This StateOperator affects and checks the state of all the Handleds kept in this 
	 * Handler. There must be only one handled with true state in order for the operator's 
	 * state to be true. The class is abstract since only the subclasses know the methods of 
	 * using handled states.
	 * 
	 * @author Mikko Hilpinen
	 * @since 17.11.2014
	 */
	protected abstract class ForAnyHandledsOperator extends IterativeStateOperator
	{
		// CONSTRUCTOR	--------------------------------------
		
		/**
		 * Creates a new StateOperator.
		 * 
		 * @param mutable can the state of the handleds be modified by external sources
		 */
		public ForAnyHandledsOperator(boolean mutable)
		{
			super(mutable);
		}
		
		
		// IMPLEMENTED METHODS	------------------------------
		
		@Override
		public boolean getState()
		{
			// The operator's state depends on the state of the handleds
			StateCheckOperator operator = new StateCheckOperator(true);
			handleObjects(operator, false);
			return operator.getState();
		}
	}
	
	/**
	 * This StateOperator affects and checks the state of all the Handleds kept in this 
	 * Handler. All the handlers' states must be true in order for the operator's state 
	 * to be true. The class is abstract since only the subclasses know the methods of 
	 * using handled states.
	 * 
	 * @author Mikko Hilpinen
	 * @since 16.11.2014
	 */
	protected abstract class ForAllHandledsOperator extends IterativeStateOperator
	{
		// CONSTRUCTOR	--------------------------------------
		
		/**
		 * Creates a new StateOperator.
		 * 
		 * @param mutable can the state of the handleds be modified by external sources
		 */
		public ForAllHandledsOperator(boolean mutable)
		{
			super(mutable);
		}
		
		
		// IMPLEMENTED METHODS	------------------------------
		
		@Override
		public boolean getState()
		{
			// The operator's state depends on the state of the handleds
			StateCheckOperator operator = new StateCheckOperator(false);
			handleObjects(operator, false);
			return !operator.getState();
		}
	}
	
	private class ForAnyHandledShouldBeHandledOperator extends ForAnyHandledsOperator
	{
		// CONSTRUCTOR	------------------
		
		public ForAnyHandledShouldBeHandledOperator()
		{
			super(true);
		}
		
		
		// IMPLEMENTED METHODS	----------
		
		@Override
		protected StateOperator getHandledStateOperator(T h)
		{
			return h.getHandlingOperators().getShouldBeHandledOperator(getHandlerType());
		}
	}
	
	private class ForAllHandledsIsDeadOperator extends ForAllHandledsOperator
	{
		// CONSTRUCTOR	----------------------------------------
		
		public ForAllHandledsIsDeadOperator()
		{
			super(true);
		}
		
		
		// IMPLEMENTED METHODS	--------------------------------

		@Override
		protected StateOperator getHandledStateOperator(T h)
		{
			return h.getIsDeadStateOperator();
		}
	}
	
	private class HandledDependentAutodeathOperator extends StateOperator
	{
		// ATTRIBUTES	------------------------------------
		
		private StateOperator allHandledsAreDeadOperator;
		
		
		// CONSTRUCTOR	------------------------------------
		
		public HandledDependentAutodeathOperator()
		{
			super(false, true);
			
			// Initializes attributes
			this.allHandledsAreDeadOperator = new ForAllHandledsIsDeadOperator();
		}
		
		
		// IMPLEMENTED METHODS	----------------------------
		
		@Override
		public boolean getState()
		{
			if (!Handler.this.started)
				return super.getState();
			else
				return super.getState() || this.allHandledsAreDeadOperator.getState();
		}
		
		@Override
		public void setState(boolean newState)
		{
			super.setState(newState);
			// Also sets the state for the handleds
			this.allHandledsAreDeadOperator.setState(newState);
		}
	}
	
	// Handleds are killed on death, but own state isn't dependent on handleds
	private class IndependentDeathOperator extends IterativeStateOperator
	{
		// CONSTRUCTOR	--------------------------------------
		
		public IndependentDeathOperator()
		{
			super(true);
		}
		
		
		// IMPLEMENTED METHODS	------------------------------

		@Override
		protected StateOperator getHandledStateOperator(T h)
		{
			return h.getIsDeadStateOperator();
		}
	}
}
