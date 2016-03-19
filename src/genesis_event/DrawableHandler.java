package genesis_event;

import genesis_util.DepthConstants;
import utopia.inception.handling.Handler;
import utopia.inception.handling.HandlerType;

import java.awt.Graphics2D;
import java.util.Comparator;

/**
 * The object from this class will draw multiple drawables, calling their 
 * drawSelf-methods and removing them when necessary
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class DrawableHandler extends Handler<Drawable> implements Drawable
{
	// ATTRIBUTES	------------------------------------------------------
	
	private int depth, lastDrawableDepth;
	private boolean usesDepth;
	private Graphics2D lastg2d;
	private boolean needsSorting, usesSubDrawers;
	private SubDrawer[] subDrawers;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new drawableHandler. Drawables must be added later manually.
	 * @param usesDepth Will the handler draw the objects in a depth-specific order
	 * @param depth How 'deep' the objects in this handler are drawn
	 * @param depthSortLayers In how many sections the depthSorting is done. 
	 * For handlers that contain objects that have small or no depth changes, 
	 * a larger number like 5-6 is excellent. For handlers that contain objects 
	 * that have large depth changes a smaller number 1-3 is better. If the 
	 * handler doesn't use depth this doesn't matter.
	 * @see DepthConstants
	 */
	public DrawableHandler(boolean usesDepth, int depth, int depthSortLayers)
	{
		// Initializes attributes
		initialize(usesDepth, depth, depthSortLayers);
	}
	
	/**
	 * Creates a new DrawableHandler that doesn't use depth
	 */
	public DrawableHandler()
	{
		// Initializes attributes
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
		
		handleObjects(true);
	}
	
	@Override
	public int getDepth()
	{
		return this.depth;
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
	public void add(Drawable d)
	{
		// Checks if depth causes additional issues
		if (this.usesDepth && !(d instanceof SubDrawer))
		{
			// If there are subDrawers, checks to which this drawable should 
			// be added to
			if (this.usesSubDrawers)
			{	
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
	
	
	// OTHER METHODS	---------------------------------------------------
	
	private void initialize(boolean usesDepth, int depth, int depthSortLayers)
	{
		// Initializes attributes
		this.depth = depth;
		this.usesDepth = usesDepth;
		this.lastg2d = null;
		this.needsSorting = false;
		this.lastDrawableDepth = DepthConstants.BOTTOM;
		
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
			super(true, minDepth, 1);
			
			// Initializes attributes
			this.minDepth = minDepth;
			this.maxDepth = maxDepth;
			this.superHandler = superhandler;
			
			// This is an exception to the "no adding yourself to a handler" rule since 
			// the reference is saved anyway
			this.superHandler.add(this);
		}
		
		
		// IMPLEMENTED METHODS	----------------------------------------
		
		@Override
		protected boolean handleObject(Drawable d)
		{
			// Also checks if the object is out of the depth range
			if (!depthIsWithinRange(d.getDepth()))
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
