package genesis_graphic;

import genesis_logic.ActorHandler;
import genesis_logic.KeyListenerHandler;
import genesis_logic.MainKeyListenerHandler;
import genesis_logic.MainMouseListenerHandler;
import genesis_logic.MouseListenerHandler;
import genesis_logic.StepHandler;
import genesis_util.HandlerRelay;

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
import java.awt.geom.Point2D;
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
	
	private int width;
	private int height;
	private double xscale, yscale;
	private int toppaddingheight, leftpaddingwidth;
	
	private MainMouseListenerHandler mainmousehandler;
	private MainKeyListenerHandler mainKeyHandler;
	private ScreenDrawer screendrawer;
	private HandlerRelay handlerRelay;
	
	private ArrayList<GamePanel> panels;
	private ArrayList<JPanel> paddings;
	private JPanel gamepanel;
	
	/**
	 * The height of the border at the top of the window (if there is one)
	 */
	private static final int BORDERHEIGHT = 32;
	private static final long serialVersionUID = -7682965360963042160L;
	
	
	// CONSTRUCTOR ---------------------------------------------------------
	
	/**
	 * Creates a new window frame with given width and height.
	 * 
	 * @param width	Window's width (in pixels).
	 * @param height Window's height (in pixels).
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
	 * @param optimizeAps Should Aps (actions per second) optimization be 
	 * activated. The optimization tries to increase / decrease the Aps to the 
	 * optimal value. Usually this is unnecessary but may counter the 
	 * computer's attempts to limit the Aps
	 */
	public GameWindow(int width, int height, String title, boolean hastoolbar, 
			int maxfpslimit, int minimumsupportedfps, boolean optimizeAps)
	{
		// Sets the decorations off if needed
		if (!hastoolbar)
			setUndecorated(true);
		
		// Initializes attributes
		this.width = width;
		this.height = height;
		this.xscale = 1;
		this.yscale = 1;
		this.panels = new ArrayList<GamePanel>();
		this.paddings = new ArrayList<JPanel>();
		this.toppaddingheight = 0;
		this.leftpaddingwidth = 0;
		
		this.setTitle(title);
		
		// Takes the toolbar into account with height calculations
		if (hastoolbar)
		{
			//System.out.println("Heightens the window");
			this.height += BORDERHEIGHT;
		}
		
		//Let's format our window
		this.formatWindow();
		//And make it visible
		this.setVisible(true);
		
		// Adds listener(s) to the window
		this.gamepanel.addMouseListener(new BasicMouseListener());
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
	
	
	// OTHER METHODS	 ---------------------------------------------------
	
	private void formatWindow()
	{
		//Let's set our window's layout
		this.setLayout(new BorderLayout());
		//Let's make sure our window closes properly
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Let's set our window's size
		this.setSize(this.width, this.height);
		// Also sets other stats
		setResizable(false);
		this.gamepanel = new JPanel();
		this.gamepanel.setVisible(true);
		this.gamepanel.setLayout(new BorderLayout());
		add(this.gamepanel, BorderLayout.CENTER);
		//setLocationRelativeTo(null);
		getContentPane().setBackground(Color.BLACK);
	}
	
	/**
	 * Adds a new GamePanel to the given direction.
	 * 
	 * @param newPanel	The GamePanel you want to add to the window.
	 * @param direction	The direction where you want to place the panel. (For
	 * example Borderlayout.NORTH)
	 * @see BorderLayout
	 */
	public void addGamePanel(GamePanel newPanel, String direction)
	{
		// Checks the arguments
		if (newPanel == null || direction == null)
			return;
		
		this.gamepanel.add(newPanel, direction);
		this.panels.add(newPanel);
	}
	
	/**
	 * Removes a gamepanel from the window
	 *
	 * @param p The panel to be removed
	 * @param killContent Should the objects drawn in the panel be killed
	 */
	public void removePanel(GamePanel p, boolean killContent)
	{
		if (!this.panels.contains(p))
			return;
		remove(p);
		this.panels.remove(p);
		
		// Kills the content of the panel if needed
		if (killContent)
			p.getDrawer().getIsDeadStateOperator().setState(true);
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
				getMousePositionOnGamePanels(mousePointOnScreen));
	}
	
	/**
	 * This method should be called when the screen needs redrawing
	 */
	public void callScreenUpdate()
	{
		// Updates the screen drawer
		this.screendrawer.callUpdate();
		//if (this.screendrawer.isRunning())
		//	this.screendrawer.notify();
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
	 * @param width The new width of the window
	 * @param height The new height of the window
	 * @param keepaspectratio Should the ratio between x- and yscaling stay 
	 * the same through the process
	 * @param allowpadding Should the screen get the given size even if 
	 * aspect ratio is kept (will cause empty areas to appear on the screen)
	 */
	public void scaleToSize(int width, int height, boolean keepaspectratio, 
			boolean allowpadding)
	{
		// Removes old padding
		removePaddings();
		// Remembers the former dimensions
		int lastwidth = getWidth();
		int lastheight = getHeight();
		// Calculates the needed scaling
		double xscale = width / (double) lastwidth;
		double yscale = height / (double) lastheight;
		// Changes the window's size if it doesn't need any more fixing
		if (!keepaspectratio || allowpadding)
			setSize(width, height);
		// The program may need to update the scaling so the ratio stays the same
		if (keepaspectratio)
		{
			xscale = Math.min(xscale, yscale);
			yscale = Math.min(xscale, yscale);
			int newwidth = (int) (lastwidth * xscale);
			int newheight = (int) (lastheight * yscale);
			// Changes the window's size accordingly
			if (!allowpadding)
				setSize(newwidth, newheight);
			// Or adds padding
			else
			{
				// If new width is not the same as the intended, adds vertical 
				// padding
				if (newwidth < width)
				{
					this.leftpaddingwidth = (width - newwidth)/2;
					addPadding(this.leftpaddingwidth, height, BorderLayout.WEST);
					addPadding(this.leftpaddingwidth, height, BorderLayout.EAST);
				}
				else if (newheight < height)
				{
					this.toppaddingheight = (height - newheight)/2;
					addPadding(width, this.toppaddingheight, BorderLayout.NORTH);
					addPadding(width, this.toppaddingheight, BorderLayout.SOUTH);
				}
			}
		}
		// Scales the panels
		for (int i = 0; i < this.panels.size(); i++)
		{
			this.panels.get(i).scale(xscale, yscale);
		}
		// Updates scale values
		this.xscale *= xscale;
		this.yscale *= yscale;
	}
	
	/**
	 * Sets the window's scaling back to 1
	 */
	public void resetScaling()
	{
		// Changes the panels' scaling
		for (int i = 0; i < this.panels.size(); i++)
		{
			this.panels.get(i).setScale(1, 1);
		}
		// Changes the window's size
		setSize(this.width, this.height);
		// Resets the scale values
		this.xscale = 1;
		this.yscale = 1;
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
		// TODO: Set so that the screen ratio remains (if needs to)
		// -> xscale = yscale (min scale) in the panel(s)
		// Problem is that even though the panel might be smaller than the 
		// frame it may want to resize itself (swing "()#(¤ )
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenwidth = screenSize.getWidth();
		double screenheight = screenSize.getHeight();
		
		scaleToSize((int) screenwidth, (int) screenheight, keepaspectratio, 
				true);
	}
	
	private void addPadding(int w, int h, String direction)
	{
		//System.out.println("Adds padding");
		JPanel padding = new JPanel();
		Dimension size = new Dimension(w, h);
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
		this.leftpaddingwidth = 0;
		this.toppaddingheight = 0;
	}
	
	// Adds padding, screen position, scaling & borders to mouse position calculation
	private Point2D.Double getMousePositionOnGamePanels(Point mousePositionOnScreen)
	{
		int mousex = (int) ((mousePositionOnScreen.x - this.leftpaddingwidth - 
				getInsets().left) / this.xscale) - getX();
		int mousey = (int) ((mousePositionOnScreen.y - this.toppaddingheight - 
				getInsets().top) / this.yscale) - getY();
		
		return new Point2D.Double(mousex, mousey);
	}
	
	// Takes (only) screen scaling into account in coordinate calculations
	private Point2D.Double getScaledPoint(Point p)
	{
		return new Point2D.Double(p.getX() / this.xscale, p.getY() / this.yscale);
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
			Point mousePoint = e.getPoint();
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePoint), true, e.getButton());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Point mousePoint = e.getPoint();
			
			// Informs the mouse status (scaling affects the mouse coordinates)
			GameWindow.this.mainmousehandler.setMouseStatus(
					getScaledPoint(mousePoint), false, e.getButton());
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
