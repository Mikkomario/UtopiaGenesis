package utopia.genesis.video;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import utopia.genesis.util.Vector3D;

/**
 * A split panel can handle multiple game panels, splitting the screen when required
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class SplitPanel extends JPanel implements ComponentListener
{
	// ATTRIBUTES	------------------------------------
	
	private static final long serialVersionUID = 20842862831771371L;

	private List<GamePanel> gamePanels;
	private ScreenSplit split;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new panel with the given resolution
	 * @param size The panels initial size
	 * @param split How the screen is split between multiple gamePanels
	 */
	public SplitPanel(Dimension size, ScreenSplit split)
	{
		// Initializes attributes
		this.gamePanels = new ArrayList<>();
		this.split = split;
		
		// Formats the screen
		setSize(size);
		setVisible(true);
		setLayout(null);
		
		addComponentListener(this);
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
			add(panel);
			this.gamePanels.add(panel);
			updatePanelBounds();
		}
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
}
