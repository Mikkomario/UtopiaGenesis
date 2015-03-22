package genesis_video;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import genesis_util.Vector3D;

import javax.swing.JPanel;

/**
 * MainPanel is the panel that draws all the game content. MainPanel can hold multiple separate 
 * GamePanels
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class MainPanel extends JPanel
{
	// ATTRIBUTES	------------------------------------
	
	private static final long serialVersionUID = 20842862831771371L;

	private Vector3D dimensions, scaling;
	private List<GamePanel> gamePanels;
	private ScreenSplit split;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new panel with the given sizes
	 * 
	 * @param dimensions The panels original size
	 * @param split How the screen is split between multiple gamePanels
	 */
	public MainPanel(Vector3D dimensions, ScreenSplit split)
	{
		// Initializes attributes
		this.dimensions = dimensions;
		this.scaling = Vector3D.identityVector();
		this.gamePanels = new ArrayList<GamePanel>();
		this.split = split;
		
		// Formats the screen
		setVisible(true);
		setLayout(new GridBagLayout());
		
		updatePanelPositions();
		updateSizes();
	}
	
	
	// GETTERS & SETTERS	----------------------------
	
	/**
	 * @return How many GamePanels are currently drawn in this panel
	 */
	public int getGamePanelAmount()
	{
		return this.gamePanels.size();
	}
	
	/**
	 * Returns a gamePanel from the main panel
	 * 
	 * @param index The index of the GamePanel, starting from 0
	 * @return A gamePanel from this panel or null if no panel with that index exists
	 */
	public GamePanel getGamePanel(int index)
	{
		if (index < 0 || index >= this.gamePanels.size())
			return null;
			
		return this.gamePanels.get(index);
	}
	
	
	// OTHER METHODS	--------------------------------
	
	/**
	 * Adds a new GamePanel
	 * @param panel The panel that will be shown
	 */
	public void addGamePanel(GamePanel panel)
	{
		if (panel != null && !this.gamePanels.contains(panel) && getGamePanelAmount() < 4)
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
		
		GamePanel newPanel = new GamePanel(this.dimensions);
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
	 * Changes the panel's scaling
	 * @param newScale The new scaling the panel receives
	 */
	public void setScale(Vector3D newScale)
	{
		this.scaling = newScale;
		updateSizes();
	}
	
	/**
	 * Scales the panel's dimensions
	 * @param scaling How much the dimensions are scaled
	 */
	public void scale(Vector3D scaling)
	{
		setScale(this.scaling.times(scaling));
	}
	
	private void updateSizes()
	{
		// Calculates the main panel size
		Vector3D newSizes = this.dimensions.times(this.scaling);
		setSize(newSizes.toDimension());
		
		// Updates the GamePanel sizes
		for (int i = 0; i < this.gamePanels.size(); i++)
		{
			Vector3D panelSize = new Vector3D(newSizes);
			
			if (getGamePanelAmount() > 1)
			{
				if (this.split == ScreenSplit.VERTICAL || getGamePanelAmount() == 4 || 
						(getGamePanelAmount() == 3 && i > 0))
					panelSize = new Vector3D(panelSize.getFirst() * 0.5, panelSize.getSecond());
				if (this.split == ScreenSplit.HORIZONTAL || getGamePanelAmount() == 4 || 
						(getGamePanelAmount() == 3 && i > 0))
					panelSize = new Vector3D(panelSize.getFirst(), panelSize.getSecond() * 0.5);
			}
			
			getGamePanel(i).setSizes(panelSize);
		}
	}
	
	private void updatePanelPositions()
	{
		// Removes the old panels
		for (GamePanel panel : this.gamePanels)
		{
			remove(panel);
		}
		// Adds the panels again
		for (int i = 0; i < getGamePanelAmount(); i++)
		{
			GridBagConstraints c = new GridBagConstraints();
			
			if (getGamePanelAmount() == 1)
			{
				c.gridx = 0;
				c.gridy = 0;
			}
			else if (getGamePanelAmount() == 2)
			{
				if (this.split == ScreenSplit.HORIZONTAL)
				{
					c.gridx = 0;
					c.gridy = i;
				}
				else
				{
					c.gridx = i;
					c.gridy = 0;
				}
			}
			else if (getGamePanelAmount() == 3)
			{
				if (this.split == ScreenSplit.HORIZONTAL)
				{
					if (i == 0)
					{
						c.gridx = 0;
						c.gridy = 0;
						c.gridwidth = 2;
					}
					else
					{
						c.gridx = i - 1;
						c.gridy = 1;
					}
				}
				else
				{
					if (i == 0)
					{
						c.gridx = 0;
						c.gridy = 0;
						c.gridheight = 2;
					}
					else
					{
						c.gridx = 1;
						c.gridy = i - 1;
					}
				}
			}
			else
			{
				c.gridx = i % 2;
				c.gridy = i / 2;
			}
			
			add(getGamePanel(i), c);
		}
	}
	
	
	// ENUMERATIONS	------------------------------
	
	/**
	 * ScreenSplit determines how a screen is split between multiple panels
	 * 
	 * @author Mikko Hilpinen
	 * @since 18.11.2014
	 */
	public enum ScreenSplit
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
