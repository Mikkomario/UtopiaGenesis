package genesis_video;

import genesis_event.ActorHandler;
import genesis_event.KeyListenerHandler;
import genesis_event.MainKeyListenerHandler;
import genesis_event.MainMouseListenerHandler;
import genesis_event.MouseListenerHandler;
import genesis_event.StepHandler;
import genesis_util.Vector3D;
import genesis_video.MainPanel.ScreenSplit;
import utopia.inception.handling.HandlerRelay;

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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GameWindow is the main frame of the program in which all the drawing is done. 
 * The window should hold at least one gamepanel.
 * @author Unto Solala & Mikko Hilpinen
 * @since 8.8.2013
 * @see GamePanel
 */
public class GameWindow extends JFrame
{	
	// TODO: Separate the controller properties of this class (handlers) into a separate entity
	// The view elements (handling main panel, should remain here)
	
	// ATTRIBUTES ---------------------------------------------------------
	
	private Vector3D dimensions, scaling, leftTopPaddings;
	
	private MainMouseListenerHandler mainmousehandler;
	private MainKeyListenerHandler mainKeyHandler;
	private ScreenDrawer screendrawer;
	private HandlerRelay handlerRelay;
	private StepHandler stepHandler;
	
	private ArrayList<JPanel> paddings;
	private MainPanel mainPanel;
	
	/**
	 * The height of the border at the top of the window (if there is one)
	 */
	private static final int BORDERHEIGHT = 32; // TODO: Remove and use insets instead
	private static final long serialVersionUID = -7682965360963042160L;
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new window frame with given width and height.
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
	 */
	public GameWindow(Vector3D dimensions, String title, boolean hastoolbar, 
			int maxfpslimit, int minimumsupportedfps, ScreenSplit split)
	{
		initialize(dimensions, title, hastoolbar, maxfpslimit, minimumsupportedfps, split);
	}
	
	/**
	 * Creates a new window
	 * @param dimensions The size of the window
	 * @param title The title shown in the window
	 * @param hasToolbar Does the window have a tool bar
	 * @param maxFpsLimit How many frames / actions per second the program takes at maximum 
	 * performance. The higher the value, the higher the CPU-usage. At least 60 is recommended. 
	 * (> 0)
	 * @param minimumSupportedFps What is the smallest possible amount of frames / actions per 
	 * second the program supports without slowing the simulation down. A low value has better 
	 * usability at the cost of stability (> 0).
	 */
	public GameWindow(Vector3D dimensions, String title, boolean hasToolbar, int maxFpsLimit, 
			int minimumSupportedFps)
	{
		initialize(dimensions, title, hasToolbar, maxFpsLimit, minimumSupportedFps, 
				ScreenSplit.HORIZONTAL);
	}
	
	
	// GETTERS & SETTERS	-------------------------
	
	/**
	 * @return The main panel that holds all the game panels
	 */
	public MainPanel getMainPanel()
	{
		return this.mainPanel;
	}
	
	/**
	 * @return The stepHandler that informs objects about the passing of time
	 */
	public StepHandler getStepHandler()
	{
		return this.stepHandler;
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
		if (MouseInfo.getPointerInfo() == null)
			return;
		
		Point mousePointOnScreen = MouseInfo.getPointerInfo().getLocation();
		
		this.mainmousehandler.setMousePosition(
				getMousePositionOnGamePanels(new Vector3D(mousePointOnScreen)));
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
	 * @param newDimensions The new size of the window
	 * @param keepaspectratio Should the ratio between x and y -scaling stay 
	 * the same through the process
	 * @param allowpadding Should the screen get the given size even if 
	 * aspect ratio is kept (will cause empty areas to appear on the screen)
	 */
	public void scaleToSize(Vector3D newDimensions, boolean keepaspectratio, 
			boolean allowpadding)
	{
		// Removes old padding
		removePaddings();
		// Remembers the former dimensions
		Vector3D lastDimensions = this.dimensions;
		// Calculates the needed scaling
		Vector3D scale = newDimensions.dividedBy(lastDimensions);

		// Changes the window's size if it doesn't need any more fixing
		if (!keepaspectratio)
			setSize(newDimensions.toDimension());
		// The program may need to update the scaling so the ratio stays the same
		else
		{
			double smallerScale = Math.min(scale.getFirst(), scale.getSecond());
			scale = new Vector3D(smallerScale, smallerScale);
			Vector3D newDimensionsWithAspectRatio = lastDimensions.times(scale);
			
			// Changes the window's size accordingly
			if (!allowpadding)
				setSize(newDimensionsWithAspectRatio.toDimension());
			// Or adds padding
			else
			{
				// If new width is not the same as the intended, adds vertical 
				// padding
				if (newDimensionsWithAspectRatio.getFirst() < newDimensions.getFirst())
				{
					this.leftTopPaddings = new Vector3D((newDimensions.getFirst() - 
							newDimensionsWithAspectRatio.getFirst()) / 2, 0);

					addPadding(new Vector3D(this.leftTopPaddings.getFirst(), 
							newDimensions.getSecond()), BorderLayout.WEST);
					addPadding(new Vector3D(this.leftTopPaddings.getFirst(), 
							newDimensions.getSecond()), BorderLayout.EAST);
				}
				else if (newDimensionsWithAspectRatio.getSecond() < newDimensions.getSecond())
				{
					this.leftTopPaddings = new Vector3D(0, (newDimensions.getSecond() - 
							newDimensionsWithAspectRatio.getSecond()) / 2);

					addPadding(new Vector3D(newDimensions.getFirst(), 
							this.leftTopPaddings.getSecond()), BorderLayout.NORTH);
					addPadding(new Vector3D(newDimensions.getFirst(), 
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
		this.mainPanel.setScale(new Vector3D(1, 1));
		
		// Changes the window's size
		setSize(this.dimensions.toDimension());
		// Resets the scale values
		this.scaling = Vector3D.identityVector();
		// Removes the padding
		removePaddings();
	}
	
	/**
	 * Makes the window fill the whole screen without borders
	 * @param keepaspectratio Should the ratio between x and y -scaling stay 
	 * the same through the process
	 */
	public void setFullScreen(boolean keepaspectratio)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		scaleToSize(new Vector3D(screenSize.getWidth(), screenSize.getHeight()), 
				keepaspectratio, true);
	}
	
	private void initialize(Vector3D dimensions, String title, boolean hastoolbar, 
			int maxfpslimit, int minimumsupportedfps, ScreenSplit split)
	{
		// Sets the decorations off if needed
		if (!hastoolbar)
			setUndecorated(true);
		
		// Initializes attributes
		this.dimensions = dimensions;
		this.scaling = Vector3D.identityVector();
		this.paddings = new ArrayList<JPanel>();
		this.leftTopPaddings = new Vector3D(0, 0);
		this.mainPanel = new MainPanel(this.dimensions, split);
		
		this.setTitle(title);
		
		// Takes the toolbar into account with height calculations
		if (hastoolbar)
			this.dimensions = this.dimensions.plus(new Vector3D(0, BORDERHEIGHT));
		
		//Let's format our window
		this.formatWindow();
		
		// Adds the panel
		add(this.mainPanel, BorderLayout.CENTER);
		
		// Adds listener(s) to the window
		BasicMouseListener basicMouseListener = new BasicMouseListener();
		this.mainPanel.addMouseListener(basicMouseListener);
		this.mainPanel.addMouseWheelListener(basicMouseListener);
		
		addKeyListener(new BasicKeyListener());
		
		// Creates and initializes important handlers
		this.stepHandler = new StepHandler(1000 / maxfpslimit, 
				(int) Math.round((1000.0 / minimumsupportedfps) / 
				StepHandler.STEPLENGTH), this);
		
		// And the screen drawer
		this.screendrawer = new ScreenDrawer(this);
		
		ActorHandler listenerActorHandler = new ActorHandler(false, this.stepHandler);
		this.mainKeyHandler = new MainKeyListenerHandler(listenerActorHandler);
		this.mainmousehandler = new MainMouseListenerHandler(listenerActorHandler);
		
		KeyListenerHandler keyHandler = new KeyListenerHandler(false, this.mainKeyHandler);
		MouseListenerHandler mouseHandler = new MouseListenerHandler(false, 
				listenerActorHandler, null);
		
		this.mainmousehandler.add(mouseHandler);
		
		this.handlerRelay = new HandlerRelay();
		this.handlerRelay.addHandler(this.stepHandler);
		this.handlerRelay.addHandler(keyHandler);
		this.handlerRelay.addHandler(mouseHandler);
		
		// Starts the game
		new Thread(this.stepHandler).start();
		new Thread(this.screendrawer).start();
	}
	
	private void addPadding(Vector3D dimensions, String direction)
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
		this.leftTopPaddings = new Vector3D(0, 0);
	}
	
	// Adds padding, screen position, scaling & borders to mouse position calculation
	private Vector3D getMousePositionOnGamePanels(Vector3D mousePositionOnScreen)
	{
		return mousePositionOnScreen.minus(this.leftTopPaddings).minus(
				new Vector3D(getInsets().left, getInsets().top)).dividedBy(
				this.scaling).minus(new Vector3D(getX(), getY()));
	}
	
	// Takes (only) screen scaling into account in coordinate calculations
	private Vector3D getScaledPoint(Vector3D p)
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
	private class BasicMouseListener implements MouseListener, MouseWheelListener
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
			Vector3D mousePosition = new Vector3D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), true, e.getButton());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Vector3D mousePosition = new Vector3D(e.getPoint());
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePosition), false, e.getButton());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			GameWindow.this.mainmousehandler.setMousePosition(getScaledPoint(
					new Vector3D(e.getPoint())));
			GameWindow.this.mainmousehandler.informMouseWheelTurn(e.getPreciseWheelRotation(), 
					e.getWheelRotation());
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
