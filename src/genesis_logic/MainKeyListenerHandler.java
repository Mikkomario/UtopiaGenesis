package genesis_logic;

import genesis_logic.AdvancedKeyEvent.ContentType;
import genesis_logic.AdvancedKeyEvent.KeyEventType;
import genesis_util.StateOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class unites the actor and keyListening interfaces so that keyevents are 
 * only called once in a step. The handler informs all its listeners about the 
 * events
 *
 * @author Mikko Hilpinen.
 * @since 2.12.2012.
 */
public class MainKeyListenerHandler extends KeyListenerHandler implements Actor
{
	// ATTRIBUTES	------------------------------------------------------
	
	private Map<KeyEventType, Map<ContentType, List<Integer>>> keyStates;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Simply creates a new KeyListenerHandler. Keylistenerhandler does not 
	 * die automatically so it must be killed with the kill method. Also, 
	 * listeners must be added manually later.
	 * 
	 * @param actorhandler The handler that will handle this handler (optional)
	 */
	public MainKeyListenerHandler(ActorHandler actorhandler)
	{
		super(false);
		
		// Initializes the attributes
		initialize();
		
		if (actorhandler != null)
			actorhandler.add(this);
	}
	
	/**
	 * Creates a new empty MainKeyListenerHandler
	 */
	public MainKeyListenerHandler()
	{
		super(false);
		
		initialize();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	public StateOperator getIsActiveStateOperator()
	{
		return getListensToKeyEventsOperator();
	}
	
	@Override
	public void act(double steps)
	{	
		// Informs the objects
		handleObjects(new MainKeyOperator(steps));
		
		// Negates some of the changes (pressed & released)
		for (KeyEventType eventType : this.keyStates.keySet())
		{
			if (eventType != KeyEventType.DOWN)
			{
				for (ContentType contentType : this.keyStates.get(eventType).keySet())
				{
					this.keyStates.get(eventType).get(contentType).clear();
				}
			}
		}
	}
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * This method should be called at each keyPressed -event
	 *
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
		
		// Checks whether the key was just pressed instead of being already down
		if (!this.keyStates.get(KeyEventType.DOWN).get(contentType).contains(content))
		{
			// If so, marks the key as pressed
			if (!this.keyStates.get(KeyEventType.PRESSED).get(contentType).contains(content))
				this.keyStates.get(KeyEventType.PRESSED).get(contentType).add(content);
		
			// And sets the key down
			this.keyStates.get(KeyEventType.DOWN).get(contentType).add(content);
		}
	}
	
	/**
	 * This method should be called at each keyReleased -event
	 *
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
		
		// Marks the key as released
		if (!this.keyStates.get(KeyEventType.RELEASED).get(contentType).contains(content))
			this.keyStates.get(KeyEventType.RELEASED).get(contentType).add(content);
		
		// Sets the key up (= not down)
		List<Integer> keysDownList = this.keyStates.get(KeyEventType.DOWN).get(contentType);
		if (keysDownList.contains(content))
			keysDownList.remove(keysDownList.indexOf(content));
	}
	
	private void initialize()
	{
		// Initializes the attributes
		this.keyStates = new HashMap<KeyEventType, Map<ContentType, List<Integer>>>();
		for (KeyEventType keyEvent : KeyEventType.values())
		{
			this.keyStates.put(keyEvent, new HashMap<ContentType, List<Integer>>());
			for (ContentType contentType : ContentType.values())
			{
				this.keyStates.get(keyEvent).put(contentType, new ArrayList<Integer>());
			}
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
		protected boolean handleObject(AdvancedKeyListener listener)
		{
			// Informs the object about the current event(s)
			// Only informs active objects
			if (!listener.getListensToKeyEventsOperator().getState())
				return true;
			
			for (KeyEventType eventType : MainKeyListenerHandler.this.keyStates.keySet())
			{
				for (ContentType contentType : 
						MainKeyListenerHandler.this.keyStates.get(eventType).keySet())
				{
					List<Integer> keys = 
							MainKeyListenerHandler.this.keyStates.get(eventType).get(contentType);
					
					for (int i = 0; i < keys.size(); i++)
					{
						informListenerAboutKeyEvent(listener, 
								new AdvancedKeyEvent(keys.get(i), eventType, contentType, 
								this.eventDuration));
					}
				}
			}
			
			return true;
		}	
	}
}
