package genesis_event;

import genesis_util.DepthConstants;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * The object from this class will draw multiple drawables, calling their 
 * drawSelf-methods and removing them when necessary
 *
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class DrawableHandler extends Handler<Drawable> implements Drawable
{	
	// TODO: DrawableHandler has numerous multithreading issues. The objects are not fully 
	// initialized when added to the handler and should not be used right away
	
	// ATTRIBUTES	------------------------------------------------------
	
	private int depth, lastDrawableDepth;
	private boolean usesDepth;
	private Graphics2D lastg2d;
	private boolean needsSorting, usesSubDrawers, subDrawersAreReady;
	private SubDrawer[] subDrawers;
	private Stack<Drawable> drawablesWaitingDepthSorting;
	private List<Drawable> drawablesWaitingToBeAdded;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new drawableHandler. Drawables must be added later manually.
	 *
	 * @param autodeath Will the handler die if it has no living drawables to handle
	 * @param usesDepth Will the handler draw the objects in a depth-specific order
	 * @param depth How 'deep' the objects in this handler are drawn
	 * @param depthSortLayers In how many sections the depthSorting is done. 
	 * For handlers that contain objects that have small or no depth changes, 
	 * a larger number like 5-6 is excellent. For handlers that contain objects 
	 * that have large depth changes a smaller number 1-3 is better. If the 
	 * handler doesn't use depth this doesn't matter.
	 * @param superhandler The drawableHandler that will draw this handler (optional)
	 * @see DepthConstants
	 */
	public DrawableHandler(boolean autodeath, boolean usesDepth, int depth, 
			int depthSortLayers, DrawableHandler superhandler)
	{
		super(autodeath);
		
		// Initializes attributes
		initialize(usesDepth, depth, depthSortLayers);
		
		if (superhandler != null)
			superhandler.add(this);
	}
	
	/**
	 * Creates a new drawablehandler. Drawables must be added later manually.
	 *
	 * @param autoDeath Will the handler die if it has no living drawables to handle
	 * @param usesDepth Will the handler draw the objects in a depth-specific order
	 * @param depth How 'deep' the objects in this handler are drawn
	 * @param depthSortLayers In how many sections the depthSorting is done. 
	 * For handlers that contain objects that have small or no depth changes, 
	 * a larger number like 5-6 is excellent. For handlers that contain objects 
	 * that have large depth changes a smaller number 1-3 is better. If the 
	 * handler doesn't use depth this doesn't matter.
	 * @param superHandlers The HandlerRelay that holds the handlers that will handle this handler
	 * @see DepthConstants
	 */
	public DrawableHandler(boolean autoDeath, boolean usesDepth, int depth, 
			int depthSortLayers, HandlerRelay superHandlers)
	{
		super(autoDeath, superHandlers);
		
		initialize(usesDepth, depth, depthSortLayers);
	}
	
	/**
	 * Creates a new drawablehandler. Drawables must be added later manually.
	 *
	 * @param autoDeath Will the handler die if it has no living drawables to handle
	 * @param usesDepth Will the handler draw the objects in a depth-specific order
	 * @param depth How 'deep' the objects in this handler are drawn
	 * @param depthSortLayers In how many sections the depthSorting is done. 
	 * For handlers that contain objects that have small or no depth changes, 
	 * a larger number like 5-6 is excellent. For handlers that contain objects 
	 * that have large depth changes a smaller number 1-3 is better. If the 
	 * handler doesn't use depth this doesn't matter.
	 */
	public DrawableHandler(boolean autoDeath, boolean usesDepth, int depth, int depthSortLayers)
	{
		super(autoDeath);
		
		initialize(usesDepth, depth, depthSortLayers);
	}
	
	/**
	 * Creates a new DrawableHandler that doesn't use depth
	 * @param autoDeath Will the handler automatically die once it runs out of handleds
	 * @param superHandler The drawableHandler that will handle this handler
	 */
	public DrawableHandler(boolean autoDeath, DrawableHandler superHandler)
	{
		super(autoDeath);
		
		// Initializes attributes
		initialize(false, 0, 1);
		
		if (superHandler != null)
			superHandler.add(this);
	}
	
	/**
	 * Creates a new DrawableHandler that doesn't use depth
	 * @param autoDeath Will the handler automatically die once it runs out of handleds
	 * @param handlers The handlers that will handle this handler
	 */
	public DrawableHandler(boolean autoDeath, HandlerRelay handlers)
	{
		super(autoDeath, handlers);
		
		initialize(false, 0, 1);
	}
	
	/**
	 * Creates a new DrawableHandler that doesn't use depth
	 * @param autoDeath Will the handler automatically die once it runs out of handleds
	 */
	public DrawableHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		initialize(false, 0, 1);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public HandlerType getHandlerType()
	{
		return GenesisHandlerType.DRAWABLEHANDLER;
	}
	
	@Override
	public void drawSelf(Graphics2D g2d)
	{
		// Handleobjects draws the handleds at default
		this.lastg2d = g2d;
		this.lastDrawableDepth = DepthConstants.BOTTOM + 1000;
		
		handleObjects();
	}
	
	@Override
	public int getDepth()
	{
		return this.depth;
	}
	
	@Override
	public void add(Drawable d)
	{
		// Adds the drawables to a separate list first. The list is added only after a 
		// single handling iteration
		this.drawablesWaitingToBeAdded.add(d);
	}
	
	@Override
	protected boolean handleObject(Drawable d)
	{
		// Draws the visible object
		d.drawSelf(this.lastg2d);
		
		// Also checks if the depths are still ok
		if (d.getDepth() > this.lastDrawableDepth)
			this.needsSorting = true;
		this.lastDrawableDepth = d.getDepth();
		
		return true;
	}
	
	@Override
	protected void updateStatus()
	{
		// In addition to normal update, sorts the handling list if needed
		super.updateStatus();
		
		if (this.needsSorting)
		{
			sortHandleds(new DepthSorter());
			this.needsSorting = false;
		}
	}
	
	@Override
	protected void handleObjects(HandlingOperator operator)
	{
		super.handleObjects(operator);
		
		// Drawables are added a bit late
		List<Drawable> add = new ArrayList<>();
		add.addAll(this.drawablesWaitingToBeAdded);
		this.drawablesWaitingToBeAdded.clear();
		
		for (Drawable d : add)
		{
			delayedAdd(d);
		}
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	// The add function in this handler is delayed by one iteration
	private void delayedAdd(Drawable d)
	{
		// Checks if depth causes additional issues
		if (this.usesDepth && !(d instanceof SubDrawer))
		{
			// If there are subDrawers, checks to which this drawable should 
			// be added to
			if (this.usesSubDrawers)
			{
				// If the subDrawers aren't ready yet, simply adds the drawable 
				// to a stack of waiting objects
				if (!this.subDrawersAreReady)
				{
					this.drawablesWaitingDepthSorting.push(d);
					return;
				}
				
				int drawableDepth = d.getDepth();
				boolean spotFound = false;
					
				for (int i = 0; i < this.subDrawers.length; i++)
				{
					if (this.subDrawers[i].depthIsWithinRange(drawableDepth))
					{
						spotFound = true;
						this.subDrawers[i].add(d);
						break;
					}
				}
				
				// For error checking, checks that a spot was actually found
				if (!spotFound)
				{
					System.err.println("DrawableHandler couldn't find a spot "
							+ "for an object with depth " + drawableDepth + 
							", please use depth within depthConstants' range");
					this.needsSorting = true;
					super.add(d);
				}
			}
			// If the handler uses depth sorting but not subDrawers, the 
			// handling list needs to be sorted after this addition
			else
			{
				this.needsSorting = true;
				super.add(d);
			}
		}
		// Otherwise simply adds the handled and is done with it
		else
			super.add(d);
	}
	
	private void initialize(boolean usesDepth, int depth, int depthSortLayers)
	{
		// Initializes attributes
		this.drawablesWaitingDepthSorting = new Stack<Drawable>();
		this.drawablesWaitingToBeAdded = new ArrayList<>();
		this.depth = depth;
		this.usesDepth = usesDepth;
		this.lastg2d = null;
		this.needsSorting = false;
		this.lastDrawableDepth = DepthConstants.BOTTOM;
		this.subDrawersAreReady = false;
		
		// Initializes the subdrawers (if needed)
		if (usesDepth && depthSortLayers > 1)
		{
			this.usesSubDrawers = true;
			
			this.subDrawers = new SubDrawer[depthSortLayers];
			int depthRange = ((DepthConstants.BOTTOM + 100) - 
					(DepthConstants.TOP - 100)) / this.subDrawers.length;
			int lastMaxDepth = DepthConstants.BOTTOM + 100;
			
			for (int i = 0; i < this.subDrawers.length; i++)
			{
				this.subDrawers[i] = new SubDrawer(this, 
						lastMaxDepth - depthRange, lastMaxDepth);
				lastMaxDepth -= depthRange;
			}
			
			this.subDrawersAreReady = true;
		
			// Adds all 1000 drawables that wanted to be added before the 
			// subDrawers could be initialized
			while (this.drawablesWaitingDepthSorting.size() > 0)
				add(this.drawablesWaitingDepthSorting.pop());
		}
		else
		{
			this.subDrawers = null;
			this.usesSubDrawers = false;
		}
	}
	
	
	// SUBCLASSES	------------------------------------------------------
	
	private class DepthSorter implements Comparator<Drawable>
	{
		@Override
		public int compare(Drawable d1, Drawable d2)
		{
			// Drawables with more depth are put to the front of the list
			return d2.getDepth() - d1.getDepth();
		}	
	}
	
	// Subdrawers handle drawables from certain depth ranges. The handleds 
	// are re-added to the superhandler if their depth changes too much
	private static class SubDrawer extends DrawableHandler
	{
		// ATTRIBUTES	------------------------------------------------
		
		private int minDepth, maxDepth;
		private DrawableHandler superHandler;
		
		
		// CONSTRUCTOR	------------------------------------------------
		
		public SubDrawer(DrawableHandler superhandler, int minDepth, int maxDepth)
		{
			super(false, true, minDepth, 1, superhandler);
			
			// Initializes attributes
			this.minDepth = minDepth;
			this.maxDepth = maxDepth;
			this.superHandler = superhandler;
		}
		
		
		// IMPLEMENTED METHODS	----------------------------------------
		
		@Override
		protected boolean handleObject(Drawable d)
		{
			// Also checks if the object is out of the depth range
			if (d.getDepth() < this.minDepth || d.getDepth() > this.maxDepth)
			{
				// Removes the drawable from this depth range and requests a 
				// repositioning
				removeHandled(d);
				this.superHandler.add(d);
			}	
			
			return super.handleObject(d);
		}
		
		// OTHER METHODS	------------------------------------------------
		
		public boolean depthIsWithinRange(int depth)
		{
			return (depth >= this.minDepth && depth <= this.maxDepth);
		}
	}
}
