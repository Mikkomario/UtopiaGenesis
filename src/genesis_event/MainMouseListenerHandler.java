package genesis_event;

import genesis_event.MouseEvent.MouseButton;
import genesis_util.Vector3D;

import java.awt.event.MouseEvent;

/**
 * This class takes input straight from the gamewindow and informs all 
 * mouselisteners 'below' it. There should be only one 
 * mainmouselistenerhandler created and / or used at a time
 * @author Mikko Hilpinen.
 * @since 29.12.2012.
 * @see genesis_video.GameWindow
 */
public class MainMouseListenerHandler extends AbstractMouseListenerHandler
{	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Informs the handler about the mouse's current position and button status
	 * 
	 * @param mousePosition The mouse's current position
	 * @param mousePressed Is a mouse button pressed
	 * @param mouseButton Which mouse button is pressed
	 */
	public void setMouseStatus(Vector3D mousePosition, 
			boolean mousePressed, int mouseButton)
	{
		setMousePosition(mousePosition);
		
		if (mousePressed)
		{
			MouseButton button = MouseButton.NONE;
			
			switch (mouseButton)
			{
				case MouseEvent.BUTTON1: button = MouseButton.LEFT; break;
				case MouseEvent.BUTTON2: button = MouseButton.MIDDLE; break;
				case MouseEvent.BUTTON3: button = MouseButton.RIGHT; break;
			}
			
			setButtonState(button, true);
		}
		else
		{
			for (MouseButton button : MouseButton.values())
			{
				if (button != MouseButton.NONE)
					setButtonState(button, false);
			}
		}
	}
}
