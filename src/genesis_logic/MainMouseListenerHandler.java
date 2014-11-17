package genesis_logic;

import genesis_util.HandlerRelay;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * This class takes input straight from the gamewindow and informs all 
 * mouselisteners 'below' it. There should be only one 
 * mainmouselistenerhandler created and / or used at a time
 *
 * @author Mikko Hilpinen.
 * @since 29.12.2012.
 * @see genesis_graphic.GameWindow
 */
public class MainMouseListenerHandler extends AbstractMouseListenerHandler
{	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new empty mouselistenerhandler. The handler won't die 
	 * automatically
	 * 
	 * @param actorhandler The handler that will handle this handler (optional)
	 */
	public MainMouseListenerHandler(ActorHandler actorhandler)
	{
		super(false, actorhandler);
	}
	
	/**
	 * Creates a new empty MainMouseListenerHandler.
	 * @param superHandlers The HandlerRelay that holds the handlers that handle this handler
	 */
	public MainMouseListenerHandler(HandlerRelay superHandlers)
	{
		super(false, superHandlers);
	}
	
	/**
	 * Creates a new empty MainMouseListenerHandler
	 */
	public MainMouseListenerHandler()
	{
		super(false);
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Informs the handler about the mouse's current position and button status
	 * 
	 * @param mousePosition The mouse's current position
	 * @param mousePressed Is a mouse button pressed
	 * @param mouseButton Which mouse button is pressed
	 */
	public void setMouseStatus(Point2D.Double mousePosition, 
			boolean mousePressed, int mouseButton)
	{
		setMousePosition(mousePosition);
		
		if (mousePressed)
		{
			if (mouseButton == MouseEvent.BUTTON1)
				setLeftMouseDown(true);
			else if (mouseButton == MouseEvent.BUTTON3)
				setRightMouseDown(true);
		}
		else
		{
			setLeftMouseDown(false);
			setRightMouseDown(false);
		}
	}
}
