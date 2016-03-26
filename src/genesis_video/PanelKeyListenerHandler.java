package genesis_video;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import genesis_event.MainKeyListenerHandler;

/**
 * This listener handler receives its events from the awt keyboard events
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 */
public class PanelKeyListenerHandler extends MainKeyListenerHandler
{
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new listener handler. The events are originated from key events received 
	 * by the contianer
	 * @param panel The panel whose events the listeners will receive
	 */
	public PanelKeyListenerHandler(Container panel)
	{
		panel.addKeyListener(new KeyEventReceiver());
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