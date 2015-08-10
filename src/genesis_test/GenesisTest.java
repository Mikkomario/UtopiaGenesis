package genesis_test;

import java.awt.Color;

import genesis_event.Handled;
import genesis_event.HandlerRelay;
import genesis_event.StepHandler;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

/**
 * MouseListenerTest tests the capabilities of AdvancedMouseListener and related features
 * 
 * @author Mikko Hilpinen
 * @since 20.11.2014
 */
public class GenesisTest
{
	// CONSTRUCTOR	----------------------------------------------
	
	private GenesisTest()
	{
		// The interface is static
	}

	
	// MAIN METHOD	---------------------------------------------
	
	/**
	 * Starts the test
	 * @param args
	 */
	public static void main(String[] args)
	{
		GameWindow window = new GameWindow(new Vector3D(800, 600), "Test", true, 120, 20);
		GamePanel panel = window.getMainPanel().addGamePanel();
		panel.setBackground(Color.BLACK);
		
		// Uses mouseHandler and DrawableHandler by default (drawer is available after the 
		// panel has been created)
		HandlerRelay handlers = HandlerRelay.createDefaultHandlerRelay(window, panel);
		
		Handled drawer = new MousePositionDrawer(handlers);
		new KeyTester(handlers, drawer);
		
		// Creates a performance monitor as well
		new TextPerformanceMonitor(1000, window.getStepHandler());
		new StepHandler.PerformanceAccelerator(100, window.getStepHandler());
	}
}
