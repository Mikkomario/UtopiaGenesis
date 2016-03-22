package genesis_video;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import genesis_util.Vector3D;

import javax.swing.JPanel;

import genesis_event.ActorHandler;
import genesis_event.KeyListenerHandler;
import genesis_event.MainKeyListenerHandler;

/**
 * MainPanel is the panel that draws game content. a MainPanel can hold multiple GamePanels
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class MainPanel extends JPanel implements ComponentListener
{
	// ATTRIBUTES	------------------------------------
	
	private static final long serialVersionUID = 20842862831771371L;

	private List<GamePanel> gamePanels;
	private ScreenSplit split;
	
	private KeyListenerHandler keyHandler;
	private ActorHandler actorHandler = new ActorHandler();
	// TODO: Move some of the game window functionality here (keyboard, drawing on action)
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new panel with the given resolution
	 * @param size The panels initial size
	 * @param split How the screen is split between multiple gamePanels
	 */
	public MainPanel(Dimension size, ScreenSplit split)
	{
		// Initializes attributes
		this.gamePanels = new ArrayList<>();
		this.split = split;
		
		PanelKeyHandler keyHandler = new PanelKeyHandler();
		this.keyHandler = keyHandler;
		addKeyListener(keyHandler);
		
		// Formats the screen
		setSize(size);
		setVisible(true);
		setLayout(null);
	}
	
	
	// IMPLEMENTED METHODS	---------------------
	
	@Override
	public void componentResized(ComponentEvent e)
	{
		updatePanelBounds();
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		// Ignored
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		// Ignored
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		// Ignored
	}
	
	
	// GETTERS & SETTERS	----------------------------
	
	/**
	 * @return The game panels held in this panel. The returned list is a copy and changes 
	 * made to it won't affect the original
	 */
	public List<GamePanel> getGamePanels()
	{
		return new ArrayList<>(this.gamePanels);
	}
	
	/**
	 * @return How many game panels are currently held in this panel
	 */
	public int getGamePanelAmount()
	{
		return this.gamePanels.size();
	}
	
	
	// OTHER METHODS	--------------------------------
	
	/**
	 * Adds a new GamePanel
	 * @param panel The panel that will be shown
	 */
	public void addGamePanel(GamePanel panel)
	{
		if (panel != null && !this.gamePanels.contains(panel) && this.gamePanels.size() < 4)
		{
			this.gamePanels.add(panel);
			updatePanelBounds();
			
			// Updates the panel's handler information, if necessary
			if (!panel.getHandlerRelay().containsHandlerOfType(this.keyHandler.getHandlerType()))
				panel.getHandlerRelay().addHandler(this.keyHandler);
			if (!panel.getHandlerRelay().containsHandlerOfType(this.actorHandler.getHandlerType()))
				panel.getHandlerRelay().addHandler(this.actorHandler);
		}
	}
	
	/**
	 * Creates and adds a new GamePanel to the screen.
	 * @return The panel that was just added
	 */
	/*
	public GamePanel addGamePanel()
	{
		if (getGamePanelAmount() >= 4)
			return null;
		
		GamePanel newPanel = new GamePanel(getSize());
		addGamePanel(newPanel);
		
		return newPanel;
	}*/
	
	/**
	 * Removes a new GamePanel from the panels
	 * @param panel The panel that will be removed
	 */
	public void removeGamePanel(GamePanel panel)
	{
		if (panel != null && this.gamePanels.contains(panel))
		{
			this.gamePanels.remove(panel);
			updatePanelBounds();
		}
	}
	
	private void updatePanelBounds()
	{
		// Updates the GamePanel sizes
		for (int i = 0; i < this.gamePanels.size(); i++)
		{
			Vector3D panelSize = new Vector3D(getWidth(), getHeight());
			
			if (getGamePanelAmount() > 1)
			{
				if (this.split == ScreenSplit.VERTICAL || getGamePanelAmount() == 4 || 
						(getGamePanelAmount() == 3 && i > 0))
					panelSize = panelSize.dividedBy(2, 1, 1);
				if (this.split == ScreenSplit.HORIZONTAL || getGamePanelAmount() == 4 || 
						(getGamePanelAmount() == 3 && i > 0))
					panelSize = panelSize.dividedBy(1, 2, 1);
			}
			
			this.gamePanels.get(i).setSize(panelSize.toDimension());
		}
		
		updatePanelPositions();
	}
	
	private void updatePanelPositions()
	{
		// Repositions each panel
		for (int i = 0; i < getGamePanelAmount(); i++)
		{
			int x = 0;
			int y = 0;
			
			// On 4 panel split screen, each panel is ordered similarly
			if (getGamePanelAmount() == 4)
			{
				x = i % 2;
				y = i / 2;
			}
			// The first panel is always at (0, 0)
			else if (i > 0)
			{
				// The 2nd and 3rd panel share the bottom half on horizontal split
				if (this.split == ScreenSplit.HORIZONTAL)
				{
					y = 1;
					if (i == 2)
						x = 1;
				}
				// On vertical split, the right side is shared
				else
				{
					x = 1;
					if (i == 2)
						y = 1;
				}
			}
			
			this.gamePanels.get(i).setLocation(x * getWidth() / 2, y * getHeight() / 2);
		}
	}
	
	
	// ENUMERATIONS	------------------------------
	
	/**
	 * ScreenSplit determines how a screen is split between multiple panels
	 * 
	 * @author Mikko Hilpinen
	 * @since 18.11.2014
	 */
	public static enum ScreenSplit
	{
		/**
		 * The panels are placed a top of each other
		 */
		HORIZONTAL,
		/**
		 * The panels are placed next to each other
		 */
		VERTICAL;
	}
	
	
	// NESTED CLASSES	---------------
	
	/**
	 * This listener handler receives its events from the awt keyboard events
	 * @author Unto Solala & Mikko Hilpinen
	 * @since 8.8.2013
	 */
	private static class PanelKeyHandler extends MainKeyListenerHandler implements KeyListener
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
