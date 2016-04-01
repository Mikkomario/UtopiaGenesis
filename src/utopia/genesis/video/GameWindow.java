package utopia.genesis.video;

import utopia.genesis.util.Vector3D;
import utopia.genesis.video.SplitPanel.ScreenSplit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GameWindow is the main frame of the program. The window contains a single main panel plus 
 * possible margins when scaled.
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see GamePanel
 */
public class GameWindow extends JFrame implements ComponentListener
{
	// ATTRIBUTES ---------------------------------------------------------
	
	private ArrayList<JPanel> paddings;
	private SplitPanel mainPanel;
	private Vector3D originalPanelSize;
	private boolean usesPaddings;
	
	private static final long serialVersionUID = -7682965360963042160L;
	
	
	// CONSTRUCTOR ----------------------------------
	
	/**
	 * Creates a new window frame with given width and height.
	 * @param size The size of the window contents. If borders are used, the actual size of the 
	 * window may be slightly larger
	 * @param title The title displayed in the window's border
	 * @param borderless Should the window be borderless
	 * @param usePaddings Should the window display paddings when aspect ratio changes
	 * @param split How the screen is split between multiple panels
	 */
	public GameWindow(Dimension size, String title, boolean borderless, boolean usePaddings, 
			ScreenSplit split)
	{
		// initialises the layout information
		if (borderless)
			setUndecorated(true);
		
		setTitle(title);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		//setResizable(false);
		getContentPane().setBackground(Color.BLACK);
		setVisible(true);
		
		// Adds the insets to the window size
		Insets insets = getInsets();
		setSize(size.width + insets.left + insets.right, size.height + insets.top + insets.bottom);
		
		// Initializes attributes
		this.usesPaddings = usePaddings;
		this.paddings = new ArrayList<>();
		this.mainPanel = new SplitPanel(size, split);
		this.originalPanelSize = new Vector3D(size);
		
		// Adds the panel
		add(this.mainPanel, BorderLayout.CENTER);
		
		addComponentListener(this);
	}
	
	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	public void componentResized(ComponentEvent e)
	{
		// May add paddings
		if (this.usesPaddings)
		{
			// Resets the paddings
			removePaddings();
			
			Insets insets = getInsets();
			Vector3D actualSize = new Vector3D(getWidth() - insets.left - insets.right, 
					getHeight() - insets.top - insets.bottom);
			Vector3D requiredScaling = actualSize.dividedBy(this.originalPanelSize);
			
			// Calculates the main panel end size. If padding is required adds that
			Vector3D mainPanelEndSize;
			if (requiredScaling.getFirst() > requiredScaling.getSecond())
			{
				mainPanelEndSize = this.originalPanelSize.times(requiredScaling.getSecond());
				// Adds padding to left and right
				Dimension paddingSize = actualSize.minus(new Vector3D(mainPanelEndSize.getFirst(), 
						0)).dividedBy(new Vector3D(2, 1, 1)).toDimension();
				addPadding(paddingSize, BorderLayout.WEST);
				addPadding(paddingSize, BorderLayout.EAST);
			}
			else if (requiredScaling.getSecond() > requiredScaling.getFirst())
			{
				mainPanelEndSize = this.originalPanelSize.times(requiredScaling.getFirst());
				// Adds padding to top and bottom
				Dimension paddingSize = actualSize.minus(new Vector3D(0, 
						mainPanelEndSize.getSecond())).dividedBy(new Vector3D(1, 2, 1)).toDimension();
				addPadding(paddingSize, BorderLayout.NORTH);
				addPadding(paddingSize, BorderLayout.SOUTH);
			}
			else
				mainPanelEndSize = this.originalPanelSize.times(requiredScaling);
			
			this.mainPanel.setSize(mainPanelEndSize.toDimension());
		}
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
	
	
	// GETTERS & SETTERS	-------------------------
	
	/**
	 * @return The main panel that holds all the game panels
	 */
	public SplitPanel getMainPanel()
	{
		return this.mainPanel;
	}
	
	
	// OTHER METHODS	 -----------------------------
	
	/**
	 * Adds a new game panel to the panels displayed on this window
	 * @param panel The panel that will be displayed on the window
	 */
	public void addGamePanel(GamePanel panel)
	{
		getMainPanel().addGamePanel(panel);
	}
	
	/**
	 * Makes the window fill the whole screen without borders
	 * @param showTaskBar Should some area be left for the task bar
	 */
	public void setFullScreen(boolean showTaskBar)
	{
		Dimension targetSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = 0, y = 0;
		if (showTaskBar)
		{
			Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
			targetSize = new Dimension(targetSize.width - screenInsets.left - screenInsets.right, 
					targetSize.height - screenInsets.top - screenInsets.bottom);
			x = screenInsets.left;
			y = screenInsets.top;
		}
		
		setBounds(x, y, targetSize.width, targetSize.height);
	}
	
	private void addPadding(Dimension size, String direction)
	{
		JPanel padding = new JPanel();
		padding.setSize(size);
		padding.setPreferredSize(size);
		padding.setMaximumSize(size);
		padding.setMinimumSize(size);
		padding.setOpaque(true);
		padding.setVisible(true);
		padding.setBackground(Color.BLACK);
		add(padding, direction);
		this.paddings.add(padding);
	}
	
	private void removePaddings()
	{
		for (int i = 0; i < this.paddings.size(); i++)
		{
			remove(this.paddings.get(i));
		}
		this.paddings.clear();
	}
}
