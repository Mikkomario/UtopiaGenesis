package genesis_test;

import java.awt.Color;

import genesis_event.HandlerRelay;
import genesis_event.KeyListenerHandler;
import genesis_event.MouseListenerHandler;
import genesis_util.Vector2D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;
import genesis_video.MainPanel.ScreenSplit;

/**
 * MouseListenerTest tests the capabilities of AdvancedMouseListener and related features
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class GenesisTest
{
	// ATTRIBUTES	----------------------------------------------
	
	private GameWindow window;
	private HandlerRelay handlers;
	
	
	// CONSTRUCTOR	----------------------------------------------
	
	/**
	 * Creates a new test and opens the window
	 */
	public GenesisTest()
	{
		this.window = new GameWindow(new Vector2D(800, 600), "Test", true, 120, 20, 
				ScreenSplit.HORIZONTAL, false);
		
		// Uses mouseHandler and DrawableHandler by default (drawer is available after the 
		// panel has been created)
		this.handlers = new HandlerRelay();
	}
	
	
	// OTHER METHODS	------------------------------------------
	
	/**
	 * Starts the test
	 */
	public void start()
	{
		GamePanel newPanel = this.window.getMainPanel().addGamePanel();
		newPanel.setBackground(Color.BLACK);
		//GamePanel panel2 = this.window.getMainPanel().addGamePanel();
		//this.window.getMainPanel().addGamePanel();
		
		MouseListenerHandler mouseHandler = new MouseListenerHandler(true, this.window.getHandlerRelay());
		this.handlers.addHandler(mouseHandler);
		KeyListenerHandler keyHandler = new KeyListenerHandler(true, this.window.getHandlerRelay());
		this.handlers.addHandler(keyHandler);
		
		this.handlers.addHandler(newPanel.getDrawer());
		
		
		new MousePositionDrawer(this.handlers);
		new KeyTester(keyHandler, mouseHandler);
	}

	
	// MAIN METHOD	---------------------------------------------
	
	/**
	 * Starts the test
	 * @param args
	 */
	public static void main(String[] args)
	{
		new GenesisTest().start();
	}
}