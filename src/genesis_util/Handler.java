package genesis_util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handlers specialize in handling certain types of objects. Each handler can 
 * inform its subobjects and can be handled itself.
 *
 * @author Mikko Hilpinen.
 * @since 8.12.2012.
 */
public abstract class Handler implements Handled, StateOperatorListener
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private LinkedList<Handled> handleds;
	private ArrayList<Handled> handledstoberemoved, handledstobeadded;
	private boolean disabled; // Has the handler been temporarily disabled
	private StateOperator isDeadOperator;
	private boolean started;
	
	private HashMap<HandlingOperation, ReentrantLock> locks;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new handler that is still empty. Handled objects must be added 
	 * manually later. If autodeath is set on, the handled will be destroyed as 
	 * soon as it becomes empty.
	 *
	 * @param autodeath Will the handler die automatically when it becomes empty
	 * @param superhandler The handler that will handle the object (optional)
	 */
	public Handler(boolean autodeath, Handler superhandler)
	{
		// Initializes attributes
		this.handleds = new LinkedList<Handled>();
		this.handledstobeadded = new ArrayList<Handled>();
		this.handledstoberemoved = new ArrayList<Handled>();
		this.disabled = false;
		this.started = false;
		this.locks = new HashMap<HandlingOperation, ReentrantLock>();
		this.locks.put(HandlingOperation.HANDLE, new ReentrantLock());
		this.locks.put(HandlingOperation.ADD, new ReentrantLock());
		this.locks.put(HandlingOperation.REMOVE, new ReentrantLock());
		
		if (autodeath)
			this.isDeadOperator = new HandledDependentAutodeathOperator();
		else
			this.isDeadOperator = new LatchStateOperator(false);
		this.isDeadOperator.getListenerHandler().addStateListener(this);
		
		// Tries to add itself to the superhandler
		if (superhandler != null)
			superhandler.addHandled(this);
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
	 * @param h The handler that may need handling
	 * @return Should object handling be continued (true) or skipped for the 
	 * remaining handleds (false)
	 */
	protected abstract boolean handleObject(Handled h);
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}
	
	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		// If the Handler was killed, clears the remaining operation lists
		if (newState && source == this.isDeadOperator)
		{
			// Safely clears the handleds
			clearOperationList(HandlingOperation.HANDLE);
			// Safely clears the added handleds
			clearOperationList(HandlingOperation.ADD);
			// And finally clears the removed handleds
			clearOperationList(HandlingOperation.REMOVE);
		}
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Takes Handleds from another handler and moves them to this handler instead.
	 * 
	 * @param other The handler from which the Handleds are moved from. 
	 * Must be of the same HandlerType with this handler.
	 * @throws HandledTypeException If the given handler is not of the same type with 
	 * this handler
	 */
	protected void transferHandledsFrom(Handler other) throws HandledTypeException
	{
		// Checks that the given handler is of the right type
		if (other.getHandlerType() != getHandlerType())
			throw new HandledTypeException("Cannot accept elements from handler of type " + 
					other.getHandlerType());
		
		// Transfers the handleds
		List<Handled> handledsToBeTransferred = new ArrayList<Handled>();
		handledsToBeTransferred.addAll(other.handleds);
		
		for (Handled h : handledsToBeTransferred)
		{
			addHandled(h);
			other.removeHandled(h);
		}
		
		handledsToBeTransferred.clear();
	}
	
	/**
	 * Goes through all the handleds and calls the operator's handleObject() 
	 * -method for the objects
	 * @param operator The operation done for each handled. Null if the default 
	 * handleObject(Handled) should be used
	 * 
	 * @see #handleObject(Handled)
	 * @see HandlingOperator
	 */
	// TODO: Add boolean parameter performUpdate is needed
	protected void handleObjects(HandlingOperator operator)
	{	
		updateStatus();
		
		// Disabled handlers don't handle objects until reactivated
		if (this.disabled)
			return;
		
		// Goes through all the handleds
		boolean handlingskipped = false;
		this.locks.get(HandlingOperation.HANDLE).lock();

		try
		{
			Iterator<Handled> iterator = this.handleds.iterator();
			
			while (iterator.hasNext())
			{
				/*
				if (this.killed)
					break;
				*/
				Handled h = iterator.next();
				
				if (!h.getIsDeadStateOperator().getState())
				{	
					// Doesn't handle objects after handleobjects has returned 
					// false. Continues through the cycle though to remove dead 
					// handleds
					if (!handlingskipped)
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
	 * 
	 * @see #handleObject(Handled)
	 * @see #handleObjects(HandlingOperator)
	 */
	protected void handleObjects()
	{
		handleObjects(null);
	}
	
	/**
	 * @return The iterator of the handled list
	 * @see #handleObjects()
	 * @warning This method is not very safe and should not be used if 
	 * handleObjects() can be used instead
	 */
	protected Iterator<Handled> getIterator()
	{
		return this.handleds.iterator();
	}
	
	/**
	 * Adds a new object to the handled objects
	 *
	 * @param h The object to be handled
	 */
	protected void addHandled(Handled h)
	{
		// Handled must be of the supported class
		if (!getHandlerType().getSupportedHandledClass().isInstance(h))
		{
			System.err.println(getClass().getName() + 
					" does not support given object's class");
			return;
		}
		
		// Performs necessary checks
		if (h != this && !this.handleds.contains(h) && 
				!this.handledstobeadded.contains(h))
		{
			// Adds the handled to the queue
			addToOperationList(HandlingOperation.ADD, h);
			this.started = true;
			//System.out.println(this + " adds a handled to queue (now " + 
			//			this.handledstobeadded.size() + ")");
		}
	}
	
	/**
	 * Removes a handled from the group of handled objects
	 *
	 * @param h The handled object to be removed
	 */
	public void removeHandled(Handled h)
	{
		if (h != null && !this.handledstoberemoved.contains(h) && 
				this.handleds.contains(h))
		{
			addToOperationList(HandlingOperation.REMOVE, h);
		}
	}
	
	/**
	 * Removes all the handleds from the handler
	 */
	public void removeAllHandleds()
	{
		this.locks.get(HandlingOperation.HANDLE).lock();
		try
		{
			Iterator<Handled> iter = getIterator();
			
			while (iter.hasNext())
			{
				removeHandled(iter.next());
			}
		}
		finally { this.locks.get(HandlingOperation.HANDLE).unlock(); }
		
		// Also cancels the adding of new handleds
		clearOperationList(HandlingOperation.ADD);
	}
	
	/**
	 * Temporarily disables the handler. This can be used to block certain 
	 * functions for a while. The disable should be ended with endDisable().
	 * 
	 * @see #endDisable()
	 */
	public void disable()
	{
		this.disabled = true;
	}
	
	/**
	 * Ends a temporary disable put on the handler, making it function normally 
	 * again
	 */
	public void endDisable()
	{
		this.disabled = false;
	}
	
	/**
	 * @return How many objects is the handler currently taking care of
	 */
	protected int getHandledNumber()
	{
		return this.handleds.size();
	}
	
	/**
	 * Prints the amount of handleds the handler currently contains. This 
	 * should be used for testing purposes only.
	 */
	public void printHandledNumber()
	{
		System.out.println(getHandledNumber());
	}
	
	/**
	 * @return The first handled in the list of handleds
	 */
	protected Handled getFirstHandled()
	{
		return this.handleds.getFirst();
	}
	
	/**
	 * Returns a certain handled form the list
	 *
	 * @param index The index from which the handled is taken
	 * @return The handled from the given index or null if no such index exists
	 * @warning Normally it is adviced to use the iterator to go through the 
	 * handleds but if the caller modifies the list during the iteration, this 
	 * method should be used instead
	 * @see #getIterator()
	 * @see #handleObject(Handled)
	 */
	protected Handled getHandled(int index)
	{
		if (index < 0 || index >= getHandledNumber())
			return null;
		return this.handleds.get(index);
	}
	
	/**
	 * Updates the handler list by adding new members and removing old ones. 
	 * This method should not be called during an iteration but is useful before 
	 * testsing the handler status.<br>
	 * Status is automatically updated each time the handleds in the handler 
	 * are handled.
	 * 
	 * @see #handleObjects()
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
	protected void sortHandleds(Comparator<Handled> c)
	{
		Collections.sort(this.handleds, c);
	}
	
	// This should be called at the end of the iteration
	private void clearRemovedHandleds()
	{
		if (this.handledstoberemoved.isEmpty())
			return;
		
		this.locks.get(HandlingOperation.REMOVE).lock();
		try
		{
			// Removes all removed handleds from handleds or added or 
			// inserted handleds
			for (Handled h : this.handledstoberemoved)
			{
				if (this.handleds.contains(h))
					removeFromOperationList(HandlingOperation.HANDLE, h);
			}
			
			// Empties the removing list
			// TODO: One might want to change these into clearoperationList(...)
			this.handledstoberemoved.clear();
		}
		finally { this.locks.get(HandlingOperation.REMOVE).unlock(); }
	}
	
	private void addNewHandleds()
	{
		// If the handler has no handleds to be added, does nothing
		if (this.handledstobeadded.isEmpty())
			return;
		
		this.locks.get(HandlingOperation.ADD).lock();
		try
		{
			// Adds all handleds from the addlist to the handleds
			for (Handled h : this.handledstobeadded)
			{
				addToOperationList(HandlingOperation.HANDLE, h);
			}
			
			// Clears the addlist
			this.handledstobeadded.clear();
		}
		finally { this.locks.get(HandlingOperation.ADD).unlock(); }
	}
	
	// Thread-safely clears a data structure used with the given operation type
	// TODO: Try to figure out a way to make this without copy-paste, though 
	// it might be difficult since there are no function pointers in java
	private void clearOperationList(HandlingOperation o)
	{
		// Checks the argument
		if (o == null)
			return;
		
		// Locks the correct lock
		this.locks.get(o).lock();
		
		try
		{
			switch (o)
			{
				// I really wish I had function pointers in use now...
				case HANDLE: this.handleds.clear(); break;
				case ADD: this.handledstobeadded.clear(); break;
				case REMOVE: this.handledstoberemoved.clear(); break;
			}
		}
		finally
		{
			this.locks.get(o).unlock();
		}
	}
	
	// Thread safely adds an handled to an operation list
	private void addToOperationList(HandlingOperation o, Handled h)
	{
		// Checks the argument
		if (o == null || h == null)
			return;
		
		// Locks the correct lock
		this.locks.get(o).lock();
		
		try
		{
			switch (o)
			{
				case HANDLE: this.handleds.add(h); break;
				case ADD: this.handledstobeadded.add(h); break;
				case REMOVE: this.handledstoberemoved.add(h); break;
			}
		}
		finally
		{
			this.locks.get(o).unlock();
		}
	}
	
	// Thread safely removes an handled from an operation list
	private void removeFromOperationList(HandlingOperation o, Handled h)
	{
		// Checks the argument
		if (o == null || h == null)
			return;
		
		// Locks the correct lock
		this.locks.get(o).lock();
		
		try
		{
			switch (o)
			{
				case HANDLE: this.handleds.remove(h); break;
				case ADD: this.handledstobeadded.remove(h); break;
				case REMOVE: this.handledstoberemoved.remove(h); break;
			}
		}
		finally
		{
			this.locks.get(o).unlock();
		}
	}
	
	
	// ENUMERATIONS	------------------------------------------------------
	
	private enum HandlingOperation
	{
		HANDLE, ADD, REMOVE;
	}
	
	
	// SUBCLASSES	-------------------------------------------------------
	
	/**
	 * HandledTypeExceptions are thrown when unsupported handled classes or handler types 
	 * are used. This can be considered fatal programming errors.
	 * 
	 * @author Mikko Hilpinen
	 * @since 16.11.2014
	 */
	public static class HandledTypeException extends RuntimeException
	{
		private static final long serialVersionUID = 272001470257069786L;

		/**
		 * Creates a new exception with the given message
		 * @param message The message that will be sent along with the exception
		 */
		public HandledTypeException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception without a message
		 */
		public HandledTypeException()
		{
			super();
		}
	}
	
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
		protected abstract boolean handleObject(Handled h);
	}
	
	private abstract class IterativeStateOperator extends StateOperator implements 
			StateOperatorListener
	{
		// ATTRIBUTES	--------------------------------------
		
		private StateOperator isDeadOperator;
				
		
		// CONSTRUCTOR	--------------------------------------
				
		public IterativeStateOperator(boolean mutable)
		{
			super(true, mutable);
			
			// Initializes attributes
			this.isDeadOperator = new StateOperator(false, false);
			getListenerHandler().addStateListener(this);
		}
		
		// ABSTRACT METHODS	----------------------------------
		
		/**
		 * Changes a state of a handled
		 * @param h the handled that will be modified
		 * @param newState The new state the handled should receive
		 */
		protected abstract void changeHandledState(Handled h, boolean newState);
		
		/**
		 * Checks a state of a handled
		 * @param h The handled that will be checked
		 * @return The state of the handled
		 */
		protected abstract boolean getHandledState(Handled h);
		
		
		// IMPLEMENTED METHODS	------------------------------
		
		@Override
		public StateOperator getIsDeadStateOperator()
		{
			return this.isDeadOperator;
		}
		
		@Override
		public void onStateChange(StateOperator source, boolean newState)
		{
			// Tries to change the state of all the handleds
			HandlingOperator operator = new StateAdjustMentOperator(newState);
			handleObjects(operator);
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
			protected boolean handleObject(Handled h)
			{
				changeHandledState(h, this.newState);
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
			protected boolean handleObject(Handled h)
			{
				if (getHandledState(h) == this.searchedState)
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
			handleObjects(operator);
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
			handleObjects(operator);
			return !operator.getState();
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
		protected void changeHandledState(Handled h, boolean newState)
		{
			h.getIsDeadStateOperator().setState(newState);
		}

		@Override
		protected boolean getHandledState(Handled h)
		{
			return h.getIsDeadStateOperator().getState();
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
	}
}
