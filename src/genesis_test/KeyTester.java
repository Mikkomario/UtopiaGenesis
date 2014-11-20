package genesis_test;

import genesis_event.AdvancedKeyEvent;
import genesis_event.AdvancedKeyEvent.KeyEventType;
import genesis_event.AdvancedKeyListener;
import genesis_event.EventSelector;
import genesis_event.KeyListenerHandler;
import genesis_event.MouseListenerHandler;
import genesis_util.LatchStateOperator;
import genesis_util.StateOperator;

/**
 * KeyTester prints the keys that are being pressed
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class KeyTester implements AdvancedKeyListener
{
	// ATTRIBUTES	-----------------------------------------
	
	private EventSelector<AdvancedKeyEvent> selector;
	private StateOperator isDeadOperator, isActiveOperator;
	private MouseListenerHandler mouseHandler;
	private KeyListenerHandler keyHandler;
	
	
	// CONSTRUCTOR	-----------------------------------------
	
	/**
	 * Creates a new keyTester that has access to the given handlers
	 * @param keyHandler The key listener handler that informs the tester about key events
	 * @param mouseHandler The mouse handler that is affected by this tester
	 */
	public KeyTester(KeyListenerHandler keyHandler, MouseListenerHandler mouseHandler)
	{
		// Initializes attributes
		this.isDeadOperator = new LatchStateOperator(false);
		this.isActiveOperator = new StateOperator(true, true);
		this.mouseHandler = mouseHandler;
		this.keyHandler = keyHandler;
		
		// Listens to key presses
		this.selector = AdvancedKeyEvent.createEventTypeSelector(KeyEventType.PRESSED);
		
		if (keyHandler != null)
			keyHandler.add(this);
	}
	
	
	// IMPLEMENTED METHODS	------------------------------

	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}

	@Override
	public void onKeyEvent(AdvancedKeyEvent event)
	{
		// Prints the key and does something special with number keys
		System.out.println("Pressed: " + event.getKeyChar());
		
		switch (event.getKeyChar())
		{
			case '1': this.mouseHandler.getListensToMouseEventsOperator().setState(false); break;
			case '2': this.mouseHandler.getListensToMouseEventsOperator().setState(true); break;
			case '3': this.keyHandler.getIsDeadStateOperator().setState(true); break;
			case '4': this.keyHandler.getListensToKeyEventsOperator().setState(false); break;
			case '5': this.mouseHandler.getIsDeadStateOperator().setState(true); break;
		}
	}

	@Override
	public EventSelector<AdvancedKeyEvent> getKeyEventSelector()
	{
		return this.selector;
	}

	@Override
	public StateOperator getListensToKeyEventsOperator()
	{
		return this.isActiveOperator;
	}

}
