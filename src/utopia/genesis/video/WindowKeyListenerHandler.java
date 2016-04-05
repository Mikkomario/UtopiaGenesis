package utopia.genesis.video;

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import utopia.genesis.event.ActorHandler;
import utopia.genesis.event.MainKeyListenerHandler;

/**
 * This listener handler receives its events from the awt keyboard events
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 */
public class WindowKeyListenerHandler extends MainKeyListenerHandler
{
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new listener handler. The events are originated from key events received 
	 * by the window. Remember to add the handler to a working actor handler.
	 * @param window The window whose events the listeners will receive
	 */
	public WindowKeyListenerHandler(Window window)
	{
		window.addKeyListener(new KeyEventReceiver());
	}
	
	/**
	 * Creates a new listener handler. The events are originated from the key events received 
	 * by the window. The handler will be added to the provided actor handler.
	 * @param window The window whose events the listeners will receive
	 * @param actorHandler The actor handler that will inform this handler about step events
	 * @return a window key listener handler ready to be used
	 */
	public static WindowKeyListenerHandler createWindowKeyListenerHandler(Window window, 
			ActorHandler actorHandler)
	{
		WindowKeyListenerHandler handler = new WindowKeyListenerHandler(window);
		if (actorHandler != null)
			actorHandler.add(handler);
		return handler;
	}
	
	
	// NESTED CLASSES	--------------
	
	private class KeyEventReceiver implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent ke)
		{
			onKeyPressed(ke.getKeyChar(), ke.getKeyCode(), 
					ke.getKeyChar() == KeyEvent.CHAR_UNDEFINED);
		}

		@Override
		public void keyReleased(KeyEvent ke)
		{
			onKeyReleased(ke.getKeyChar(), ke.getKeyCode(), 
					ke.getKeyChar() == KeyEvent.CHAR_UNDEFINED);
		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
			// Not needed
		}
	}
}