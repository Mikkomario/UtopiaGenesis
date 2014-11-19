package genesis_graphic;

import genesis_graphic.MainPanel.ScreenSplit;
import genesis_logic.ActorHandler;
import genesis_logic.KeyListenerHandler;
import genesis_logic.MainKeyListenerHandler;
import genesis_logic.MainMouseListenerHandler;
import genesis_logic.MouseListenerHandler;
import genesis_logic.StepHandler;
import genesis_util.HandlerRelay;
import genesis_util.Vector2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GameWindow is the main frame of the program in which all the drawing is done. 
 * The window should hold at least one gamepanel.
 * 
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see GamePanel
 */
public class GameWindow extends JFrame
{	
	// ATTRIBUTES ---------------------------------------------------------
	
	private Vector2D dimensions, scaling, leftTopPaddings;
	
	private MainMouseListenerHandler mainmousehandler;
	private MainKeyListenerHandler mainKeyHandler;
	private ScreenDrawer screendrawer;
	private HandlerRelay handlerRelay;
	
	private ArrayList<JPanel> paddings;
	private MainPanel mainPanel;
	
	/**
	 * The height of the border at the top of the window (if there is one)
	 */
	private static final int BORDERHEIGHT = 32;
	private static final long serialVersionUID = -7682965360963042160L;
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new window frame with given width and height.
	 * 
	 * @param dimensions The size of the window (in pixels)
	 * @param title The title shown in the window's border
	 * @param hastoolbar Should the window have an toolbar (usually false if 
	 * fullscreen is used)
	 * @param maxfpslimit What is the maximum amount of frames / actions per second. 
	 * The larger fpslimit, the higher CPU-usage. At least 60 fps is recommended. (> 0)
	 * @param minimumsupportedfps What is the smallest possible amount of 
	 * frames / actions per second the program supports so that the physics 
	 * are adjusted to keep the game speed fast enough. The program will start 
	 * to slow down if the fps drops below this value so keeping it low increases 
	 * usability. The program's physics may not support very low framerates 
	 * though. (> 0)
	 * @param split How the screen is split between multiple panels
	 * @param optimizeAps Should Aps (actions per second) optimization be 
	 * activated. The optimization tries to increase / decrease the Aps to the 
	 * optimal value. Usually this is unnecessary but may counter the 
	 * computer's attempts to limit the Aps
	 */
	public GameWindow(Vector2D dimensions, String title, boolean hastoolbar, 
			int maxfpslimit, int minimumsupportedfps, ScreenSplit split, boolean optimizeAps)
	{
		// Sets the decorations off if needed
		if (!hastoolbar)
			setUndecorated(true);
		
		// Initializes attributes
		this.dimensions = dimensions;
		this.scaling = Vector2D.identityVector();
		this.paddings = new ArrayList<JPanel>();
		this.leftTopPaddings = new Vector2D(0, 0);
		this.mainPanel = new MainPanel(this.dimensions, split);
		
		this.setTitle(title);
		
		// Takes the toolbar into account with height calculations
		if (hastoolbar)
			this.dimensions = this.dimensions.plus(new Vector2D(0, BORDERHEIGHT));
		
		//Let's format our window
		this.formatWindow();
		
		// Adds listener(s) to the window
		this.mainPanel.addMouseListener(new BasicMouseListener());
		addKeyListener(new BasicKeyListener());
		
		// Creates and initializes important handlers
		StepHandler stepHandler = new StepHandler(1000 / maxfpslimit, 
				(int) Math.round((1000.0 / minimumsupportedfps) / 
				StepHandler.STEPLENGTH), this, optimizeAps);
		
		// And the screen drawer
		this.screendrawer = new ScreenDrawer(this);
		
		ActorHandler listenerActorHandler = new ActorHandler(false, stepHandler);
		this.mainKeyHandler = new MainKeyListenerHandler(listenerActorHandler);
		this.mainmousehandler = new MainMouseListenerHandler(listenerActorHandler);
		
		KeyListenerHandler keyHandler = new KeyListenerHandler(false, this.mainKeyHandler);
		MouseListenerHandler mouseHandler = new MouseListenerHandler(false, 
				listenerActorHandler, null);
		
		this.mainmousehandler.add(mouseHandler);
		
		this.handlerRelay.addHandler(stepHandler);
		this.handlerRelay.addHandler(keyHandler);
		this.handlerRelay.addHandler(mouseHandler);
		
		// Starts the game
		new Thread(stepHandler).start();
		new Thread(this.screendrawer).start();
	}
	
	
	// GETTERS & SETTERS	-------------------------
	
	/**
	 * @return The main panel that holds all the game panels
	 */
	public MainPanel getMainPanel()
	{
		return this.mainPanel;
	}
	
	
	// OTHER METHODS	 ---------------------------------------------------
	
	private void formatWindow()
	{
		//Let's set our window's layout
		this.setLayout(new BorderLayout());
		//Let's make sure our window closes properly
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Let's set our window's size
		this.setSize(this.dimensions.toDimension());
		// Also sets other stats
		setResizable(false);
		getContentPane().setBackground(Color.BLACK);
		//And make it visible
		this.setVisible(true);
	}
	
	/**
	 * Updates mouse's position in the game
	 */
	public void callMousePositionUpdate()
	{
		// Throws exceptions from time to time so nullcheck is needed
		if (MouseInfo.getPointerInfo() == null)
			return;
		
		Point mousePointOnScreen = MouseInfo.getPointerInfo().getLocation();
		
		this.mainmousehandler.setMousePosition(
				getMousePositionOnGamePanels(new Vector2D(mousePointOnScreen)));
	}
	
	/**
	 * This method should be called when the screen needs redrawing
	 */
	public void callScreenUpdate()
	{
		// Updates the screen drawer
		this.screendrawer.callUpdate();
	}
	
	/**
	 * @return The handlerRelay this window uses
	 */
	public HandlerRelay getHandlerRelay()
	{
		return this.handlerRelay;
	}
	
	/**
	 * Scales the window to fill the given size. Panels should already be 
	 * added to the window or they won't be scaled. The resolution of the 
	 * window stays the same.
	 * 
	 * @param newDimensions The new size of the window
	 * @param keepaspectratio Should the ratio between x- and yscaling stay 
	 * the same through the process
	 * @param allowpadding Should the screen get the given size even if 
	 * aspect ratio is kept (will cause empty areas to appear on the screen)
	 */
	public void scaleToSize(Vector2D newDimensions, boolean keepaspectratio, 
			boolean allowpadding)
	{
		// Removes old padding
		removePaddings();
		// Remembers the former dimensions
		Vector2D lastDimensions = this.dimensions;
		// Calculates the needed scaling
		Vector2D scale = newDimensions.dividedBy(lastDimensions);

		// Changes the window's size if it doesn't need any more fixing
		if (!keepaspectratio || allowpadding)
			setSize(newDimensions.toDimension());
		// The program may need to update the scaling so the ratio stays the same
		if (keepaspectratio)
		{
			double smallerScale = Math.min(scale.getFirst(), scale.getSecond());
			scale = new Vector2D(smallerScale, smallerScale);
			Vector2D newSizes = lastDimensions.times(scale);
			
			// Changes the window's size accordingly
			if (!allowpadding)
				setSize(newSizes.toDimension());
			// Or adds padding
			else
			{
				// If new width is not the same as the intended, adds vertical 
				// padding
				if (newSizes.getFirst() < newDimensions.getFirst())
				{
					this.leftTopPaddings = new Vector2D((newDimensions.getFirst() - 
							newSizes.getFirst()) / 2, 0);

					addPadding(new Vector2D(this.leftTopPaddings.getFirst(), 
							newDimensions.getSecond()), BorderLayout.WEST);
					addPadding(new Vector2D(this.leftTopPaddings.getFirst(), 
							newDimensions.getSecond()), BorderLayout.EAST);
				}
				else if (newSizes.getSecond() < newDimensions.getSecond())
				{
					this.leftTopPaddings = new Vector2D(0, (newDimensions.getSecond() - 
							newSizes.getSecond()) / 2);

					addPadding(new Vector2D(newDimensions.getFirst(), 
							this.leftTopPaddings.getSecond()), BorderLayout.NORTH);
					addPadding(new Vector2D(newDimensions.getFirst(), 
							this.leftTopPaddings.getSecond()), BorderLayout.SOUTH);
				}
			}
		}
		// Scales the panels
		this.mainPanel.scale(scale);
		
		// Updates scale values
		this.scaling = this.scaling.times(scale);
	}
	
	/**
	 * Sets the window's scaling back to 1
	 */
	public void resetScaling()
	{
		// Changes the panels' scaling
		this.mainPanel.setScale(new Vector2D(1, 1));
		
		// Changes the window's size
		setSize(this.dimensions.toDimension());
		// Resets the scale values
		this.scaling = Vector2D.identityVector();
		// Removes the padding
		removePaddings();
	}
	
	/**
	 * Makes the window fill the whole screen without borders
	 * @param keepaspectratio Should the ratio between x- and yscaling stay 
	 * the same through the process
	 */
	public void setFullScreen(boolean keepaspectratio)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		scaleToSize(new Vector2D(screenSize.getWidth(), screenSize.getHeight()), 
				keepaspectratio, true);
	}
	
	private void addPadding(Vector2D dimensions, String direction)
	{
		//System.out.println("Adds padding");
		JPanel padding = new JPanel();
		Dimension size = dimensions.toDimension();
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
		this.leftTopPaddings = new Vector2D(0, 0);
	}
	
	// Adds padding, screen position, scaling & borders to mouse position calculation
	private Vector2D getMousePositionOnGamePanels(Vector2D mousePositionOnScreen)
	{
		return mousePositionOnScreen.minus(this.leftTopPaddings).minus(
				new Vector2D(getInsets().left, getInsets().top)).dividedBy(
				this.scaling).minus(new Vector2D(getX(), getY()));
	}
	
	// Takes (only) screen scaling into account in coordinate calculations
	private Vector2D getScaledPoint(Vector2D p)
	{
		return p.dividedBy(this.scaling);
	}
	
	
	// SUBCLASSES	----------------------------------------------------
	
	/**
	 * Main window's helper class, which listens to what the mouse does.
	 * 
	 * @author Unto Solala.
	 * @since 8.8.2013
	 */
	private class BasicMouseListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// Not needed
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			Vector2D mousePosition = new Vector2D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), true, e.getButton());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Vector2D mousePosition = new Vector2D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), false, e.getButton());
		}
	}
	
	/**
	 * Main window's helper class, which listens to what the keyboard does.
	 * 
	 * @author Unto Solala.
	 * @since 8.8.2013
	 */
	private class BasicKeyListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent ke)
		{
			GameWindow.this.mainKeyHandler.onKeyPressed(ke.getKeyChar(), 
					ke.getKeyCode(), ke.getKeyChar() == KeyEvent.CHAR_UNDEFINED);
		}

		@Override
		public void keyReleased(KeyEvent ke)
		{
			GameWindow.this.mainKeyHandler.onKeyReleased(ke.getKeyChar(), 
					ke.getKeyCode(), ke.getKeyChar() == KeyEvent.CHAR_UNDEFINED);
		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
			// Not needed
		}
	}
}
