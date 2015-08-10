package genesis_test;

import genesis_event.GenesisHandlerType;
import genesis_event.Handled;
import genesis_event.HandlerRelay;
import genesis_event.KeyEvent;
import genesis_event.KeyEvent.KeyEventType;
import genesis_event.KeyListener;
import genesis_event.EventSelector;
import genesis_util.SimpleHandled;
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
	private Handled target;
	
	
	// CONSTRUCTOR	-----------------------------------------
	
	/**
	 * Creates a new keyTester that has access to the given handlers
	 * @param handlers The handlers that will handle this object
	 * @param target The object that is affected by the tester
	 */
	public KeyTester(HandlerRelay handlers, Handled target)
	{
		super(handlers);
		
		// Listens to key presses
		this.selector = KeyEvent.createEventTypeSelector(KeyEventType.PRESSED);
		getHandlingOperators().setShouldBeHandledOperator(GenesisHandlerType.KEYHANDLER, 
				new StateOperator(true, false));
		this.handlers = handlers;
		this.target = target;
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
			case '4':
				this.target.getHandlingOperators().getShouldBeHandledOperator(
						GenesisHandlerType.MOUSEHANDLER).setState(false);
				break;
			case '5':
				this.target.getHandlingOperators().getShouldBeHandledOperator(
						GenesisHandlerType.MOUSEHANDLER).setState(true);
				break;
			case '6':
				this.handlers.getHandler(GenesisHandlerType.MOUSEHANDLER
						).getIsDeadStateOperator().setState(true);
				break;
		}
	}

	@Override
	public EventSelector<KeyEvent> getKeyEventSelector()
	{
		return this.selector;
	}
}
