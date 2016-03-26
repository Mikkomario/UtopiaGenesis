package genesis_event;

import genesis_event.KeyEvent.ContentType;
import genesis_event.KeyEvent.KeyEventType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class unites the actor and keyListening interfaces so that key events are 
 * only called once in a step. The handler informs all its listeners about the 
 * events
 * @author Mikko Hilpinen.
 * @since 2.12.2012.
 */
public class MainKeyListenerHandler extends KeyListenerHandler implements Actor
{
	// ATTRIBUTES	------------------------------------------------------
	
	private Map<KeyEventType, Map<ContentType, ConcurrentLinkedQueue<Integer>>> keyStates;
	private ReentrantLock lock = new ReentrantLock();
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Simply creates a new KeyListenerHandler. Keylistenerhandler does not 
	 * die automatically so it must be killed with the kill method. Also, 
	 * listeners must be added manually later.
	 */
	public MainKeyListenerHandler()
	{
		// Initializes the attributes
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public void act(double steps)
	{	
		// Informs the objects
		handleObjects(new MainKeyOperator(steps), true);
		
		// Negates some of the changes (pressed & released)
		// Locks the lists during the operation
		lock();
		try
		{
			for (KeyEventType eventType : this.keyStates.keySet())
			{
				if (eventType != KeyEventType.DOWN)
				{
					for (ContentType contentType : this.keyStates.get(eventType).keySet())
					{
						getKeyList(eventType, contentType).clear();
					}
				}
			}
		}
		finally
		{
			unlock();
		}
	}
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * This method should be called at each keyPressed -event
	 * @param key The key that was pressed
	 * @param code The key's keycode
	 * @param coded Does the key use its keycode
	 */
	public void onKeyPressed(char key, int code, boolean coded)
	{
		ContentType contentType = ContentType.KEY;
		int content = key;
		if (coded)
		{
			contentType = ContentType.KEYCODE;
			content = code;
		}
		
		// Locks the lists during the operation
		lock();
		try
		{
			// Checks whether the key was just pressed instead of being already down
			if (!getKeyList(KeyEventType.DOWN, contentType).contains(content))
			{
				// If so, marks the key as pressed
				if (!getKeyList(KeyEventType.PRESSED, contentType).contains(content))
					getKeyList(KeyEventType.PRESSED, contentType).add(content);
			
				// And sets the key down
				getKeyList(KeyEventType.DOWN, contentType).add(content);
			}
		}
		finally
		{
			unlock();
		}
	}
	
	/**
	 * This method should be called at each keyReleased -event
	 * @param key The key that was released
	 * @param code The key's keycode
	 * @param coded Does the key use its keycode
	 */
	public void onKeyReleased(char key, int code, boolean coded)
	{
		ContentType contentType = ContentType.KEY;
		int content = key;
		if (coded)
		{
			contentType = ContentType.KEYCODE;
			content = code;
		}
		
		lock();
		try
		{
			// Marks the key as released
			if (!getKeyList(KeyEventType.RELEASED, contentType).contains(content))
				getKeyList(KeyEventType.RELEASED, contentType).add(content);
			
			// Sets the key up (= not down)
			getKeyList(KeyEventType.DOWN, contentType).remove(content);
		}
		finally
		{
			unlock();
		}
	}
	
	private ConcurrentLinkedQueue<Integer> getKeyList(KeyEventType eventType, ContentType contentType)
	{
		return this.keyStates.get(eventType).get(contentType);
	}
	
	private void lock()
	{
		this.lock.lock();
	}
	
	private void unlock()
	{
		this.lock.unlock();
	}
	
	private void initialize()
	{
		lock();
		try
		{
			// Initializes the attributes
			this.keyStates = new HashMap<>();
			for (KeyEventType keyEvent : KeyEventType.values())
			{
				this.keyStates.put(keyEvent, new HashMap<>());
				for (ContentType contentType : ContentType.values())
				{
					this.keyStates.get(keyEvent).put(contentType, new ConcurrentLinkedQueue<>());
				}
			}
		}
		finally
		{
			unlock();
		}
	}
	
	
	// SUBCLASSES	----------------------------------------------
	
	private class MainKeyOperator extends HandlingOperator
	{
		// ATTRIBUTES	------------------------------------------
		
		private double eventDuration;
		
		
		// CONSTRUCTOR	------------------------------------------
		
		public MainKeyOperator(double eventDuration)
		{
			this.eventDuration = eventDuration;
		}
		
		
		// IMPLEMENTED METHODS	----------------------------------
		
		@Override
		protected boolean handleObject(KeyListener listener)
		{
			// Informs the object about the current event(s)
			for (KeyEventType eventType : MainKeyListenerHandler.this.keyStates.keySet())
			{
				for (ContentType contentType : 
						MainKeyListenerHandler.this.keyStates.get(eventType).keySet())
				{
					ConcurrentLinkedQueue<Integer> keys = getKeyList(eventType, contentType);
					
					// TODO: Null pointer on next line. Make this thread safe
					for (int key : keys)
					{
						informListenerAboutKeyEvent(listener, 
								new KeyEvent(key, eventType, contentType, this.eventDuration));
					}
				}
			}
			
			return true;
		}	
	}
}
