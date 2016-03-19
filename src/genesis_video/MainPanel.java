package genesis_video;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import genesis_util.Vector3D;

import javax.swing.JPanel;

/**
 * MainPanel is the panel that draws game content. a MainPanel can hold multiple GamePanels
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class MainPanel extends JPanel
{
	// ATTRIBUTES	------------------------------------
	
	private static final long serialVersionUID = 20842862831771371L;

	private Vector3D scaling;
	private List<GamePanel> gamePanels;
	private ScreenSplit split;
	
	// TODO: Move some of the game window functionality here (mouse, keyboard, drawing on action)
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new panel with the given resolution
	 * @param dimensions The panels initial size
	 * @param split How the screen is split between multiple gamePanels
	 */
	public MainPanel(Dimension dimensions, ScreenSplit split)
	{
		// Initializes attributes
		this.scaling = Vector3D.identityVector();
		this.gamePanels = new ArrayList<>();
		this.split = split;
		
		// Formats the screen
		setVisible(true);
		setLayout(null);
		
		updatePanelPositions();
		setSize(dimensions);
	}
	
	
	// IMPLEMENTED METHODS	---------------------
	
	@Override
	public void setSize(Dimension size)
	{
		super.setSize(size);
		updateSizes();
		updatePanelPositions();
	}
	
	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		updateSizes();
		updatePanelPositions();
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
			updatePanelPositions();
			updateSizes();
		}
	}
	
	/**
	 * Creates and adds a new GamePanel to the screen.
	 * @return The panel that was just added
	 */
	public GamePanel addGamePanel()
	{
		if (getGamePanelAmount() >= 4)
			return null;
		
		GamePanel newPanel = new GamePanel(getSize());
		addGamePanel(newPanel);
		
		return newPanel;
	}
	
	/**
	 * Removes a new GamePanel from the panels
	 * @param panel The panel that will be removed
	 */
	public void removeGamePanel(GamePanel panel)
	{
		if (panel != null && this.gamePanels.contains(panel))
		{
			this.gamePanels.remove(panel);
			updatePanelPositions();
			updateSizes();
		}
	}
	
	/**
	 * Changes the panel's scaling. This won't change the panel's resolution, just the in-game 
	 * size
	 * @param newScale The new scaling the panel receives
	 */
	public void setScale(Vector3D newScale)
	{
		this.scaling = newScale;
		updateSizes();
	}
	
	/**
	 * Scales the panel's in-game size. Doesn't affect the panel's resolution
	 * @param scaling How much the dimensions are scaled
	 */
	public void scale(Vector3D scaling)
	{
		setScale(this.scaling.times(scaling));
	}
	
	/**
	 * Changes the panel's resolution, maintaining the in-game size
	 * @param newSize The new resolution of the panel
	 */
	public void scaleToFill(Vector3D newSize)
	{
		Vector3D originalSize = new Vector3D(getWidth(), getHeight());
		Vector3D scaling = newSize.dividedBy(originalSize);
		
		scale(scaling);
		setSize(newSize.toDimension());
	}
	
	private void updateSizes()
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
}
