package genesis_test;

import genesis_event.GenesisHandlerType;
import genesis_event.HandlerRelay;
import genesis_event.KeyEvent;
import genesis_event.KeyEvent.KeyEventType;
import genesis_event.KeyListener;
import genesis_event.EventSelector;
import genesis_event.SimpleHandled;
import genesis_util.StateOperator;

/**
 * KeyTester prints the keys that are being pressed
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class KeyTester extends SimpleHandled implements KeyListener
{
	// ATTRIBUTES	-----------------------------------------
	
	private EventSelector<KeyEvent> selector;
	private HandlerRelay handlers;
	
	
	// CONSTRUCTOR	-----------------------------------------
	
	/**
	 * Creates a new keyTester that has access to the given handlers
	 * @param handlers The handlers that will handle this object
	 */
	public KeyTester(HandlerRelay handlers)
	{
		super(handlers);
		
		// Listens to key presses
		this.selector = KeyEvent.createEventTypeSelector(KeyEventType.PRESSED);
		getHandlingOperators().setShouldBeHandledOperator(GenesisHandlerType.KEYHANDLER, 
				new StateOperator(true, false));
	}
	
	
	// IMPLEMENTED METHODS	------------------------------

	@Override
	public void onKeyEvent(KeyEvent event)
	{
		// Prints the key and does something special with number keys
		System.out.println("Pressed: " + event.getKeyChar() + " (" + event.getKey() + ")");
		
		switch (event.getKeyChar())
		{
			case '1':
				this.handlers.setHandlingState(GenesisHandlerType.MOUSEHANDLER, false); break;
			case '2':
				this.handlers.setHandlingState(GenesisHandlerType.MOUSEHANDLER, true); break;
			case '3':
				this.handlers.getHandler(
						GenesisHandlerType.KEYHANDLER).getIsDeadStateOperator().setState(true);
				break;
			case '5':
				this.handlers.getHandler(
						GenesisHandlerType.MOUSEHANDLER).getIsDeadStateOperator().setState(true);
				break;
		}
	}

	@Override
	public EventSelector<KeyEvent> getKeyEventSelector()
	{
		return this.selector;
	}
}
