package utopia.genesis.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import utopia.genesis.event.Drawable;
import utopia.genesis.event.StepHandler;
import utopia.genesis.util.DepthConstants;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.PanelMouseListenerHandler;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.util.SimpleHandled;

/**
 * This class tests the splitscreen
 * @author Mikko Hilpinen
 * @since 1.4.2016
 */
class SplitScreenTest
{
	// MAIN METHOD	---------------------
	
	public static void main(String[] args)
	{
		int screens = 3;
		Color[] colors = {Color.DARK_GRAY, Color.LIGHT_GRAY, Color.PINK, Color.GRAY};
		
		GameWindow window = new GameWindow(new Dimension(1360, 768), "Split screen test", 
				false, true, ScreenSplit.HORIZONTAL);
		StepHandler stepHandler = new StepHandler(120, 10);
		
		// Creates multiple game panels & environments
		for (int i = 0; i < screens; i++)
		{
			GamePanel panel = new GamePanel(new Vector3D(1360, 768), ScalingPolicy.PROJECT, 60);
			panel.setBackground(colors[i]);
			window.addGamePanel(panel);
			
			PanelMouseListenerHandler mouseHandler = new PanelMouseListenerHandler(panel, false);
			stepHandler.add(mouseHandler);
			HandlerRelay handlers = new HandlerRelay();
			handlers.addHandler(stepHandler, panel.getDrawer(), mouseHandler);
			
			handlers.add(new TestBox());
			handlers.add(new MousePositionDrawer());
		}
		
		// Starts the game
		stepHandler.start();
	}
	
	
	// NESTED CLASSES	-----------------
	
	private static class TestBox extends SimpleHandled implements Drawable
	{
		@Override
		public void drawSelf(Graphics2D g2d)
		{
			g2d.setColor(Color.RED);
			g2d.drawRect(32, 32, 64, 64);
		}

		@Override
		public int getDepth()
		{
			return DepthConstants.NORMAL;
		}
	}
}
