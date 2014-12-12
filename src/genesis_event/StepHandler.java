package genesis_event;

import java.util.ArrayList;
import java.util.List;

import genesis_video.GameWindow;

/**
 * This class calculates millisconds and calls all actors when a certain number 
 * of milliseconds has passed. All of the actors should be under the command of 
 * this object. This object doesn't stop functioning by itself if it runs out 
 * of actors.<p>
 *
 * @author Mikko Hilpinen.
 * @since 29.11.2012.
 */
public class StepHandler extends ActorHandler implements Runnable
{
	// ATTRIBUTES	-------------------------------------------------------
	
	/**
	 * How long does a single step take in milliseconds.
	 */
	public static final int STEPLENGTH = 15;
	
	private int callinterval, maxstepspercall;
	private long nextupdatemillis, lastactmillis;
	private boolean running;
	private GameWindow window;
	private List<PerformanceMonitor> monitors;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * This creates a new stephandler. Actors are informed 
	 * when a certain number of milliseconds has passed. Actors can be 
	 * added using addActor method.
	 * 
	 * @param callInterval How many milliseconds will there at least be between 
	 * update calls? This also defines the maximum frame rate / action rate 
	 * for the program. No more than 20 milliseconds is advised. All computers 
	 * may be unable to update the program in less than 10 milliseconds though. 
	 * (>0)
	 * @param maxStepsPerCall How many steps can be "skipped" or simulated 
	 * during a single call. Normally there is only one step or less for each 
	 * call, but if the program can't run fast enough more steps are simulated 
	 * for each call. The larger the value, the more unstable the program can 
	 * become under heavy CPU-usage, but the better the game keeps from slowing 
	 * down. The adviced value is from 2 to 3 but it can be 
	 * different depending on the nature of the software. (> 0)
	 * @param window The which which created the stepHandler
	 */
	public StepHandler(int callInterval, int maxStepsPerCall, 
			GameWindow window)
	{
		super(false); // Stephandler doesn't have a superhandler
		
		// Initializes attributes
		this.callinterval = callInterval;
		this.maxstepspercall = maxStepsPerCall;
		this.nextupdatemillis = 0;
		this.lastactmillis = System.currentTimeMillis();
		this.running = false;
		this.window = window;
		this.monitors = new ArrayList<>();
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------

	@Override
	public void run()
	{
		this.running = true;
		
		// Starts counting steps and does it until the object is killed
		while (this.running)
			update();
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Adds a performance monitor to the informed monitors
	 * @param monitor The performance monitor that will be informed
	 */
	private void addPerformanceMonitor(PerformanceMonitor monitor)
	{
		this.monitors.add(monitor);
	}
	
	/**
	 * Stops the stephandler from functioning anymore
	 */
	public void stop()
	{
		this.running = false;
	}
	
	// This method updates the actors and the window when needed
	private void update()
	{
		// Remembers the time
		this.nextupdatemillis = System.currentTimeMillis() + this.callinterval;
		
		// Calls all actors
		if (!getIsDeadStateOperator().getState())
		{
			// Calculates the step length that is informed for the objects
			long thisActStartedAt = System.currentTimeMillis();
			double steps = (thisActStartedAt - this.lastactmillis) / 
					(double) STEPLENGTH;
			
			// TODO: The when put to 120 fps, the program works with 60. It can't go higher 
			// either... Except randomly after the software has been accelerated once and 
			// the computer has "awaken"
			//System.out.println(thisActStartedAt - this.lastactmillis);
			
			// Sometimes the true amount of steps can't be informed and a 
			// different number is given instead (physics don't like there 
			// being too many steps at once)
			if (steps > this.maxstepspercall)
				steps = this.maxstepspercall;
			
			act(steps);
			
			// Updates the game according to the changes
			this.window.callScreenUpdate();
			this.window.callMousePositionUpdate();
			
			// Updates the stepmillis
			//this.lastactmillis = System.currentTimeMillis();
			this.lastactmillis = thisActStartedAt;
			
			// Informs the monitors
			for (PerformanceMonitor monitor : this.monitors)
			{
				monitor.updateOperationTime(System.currentTimeMillis() - this.lastactmillis, steps);
			}
		}
		// Stops running if dies
		else
			stop();
		
		// If there is time, the thread will wait until another step is needed
		if (System.currentTimeMillis() < this.nextupdatemillis)
		{
			synchronized (this)
			{
				try
				{
					// Apparently this can become negative under very 
					// rare circumstances (added the second check, hope it helps)
					if (System.currentTimeMillis() < this.nextupdatemillis)
						wait(this.nextupdatemillis - System.currentTimeMillis());
				}
				catch (InterruptedException exception)
				{
					System.err.println("StepHandler's stepdelay was " +
							"interupted unexpectedly");
					exception.printStackTrace();
				}
			}
		}
	}
	
	
	// SUBCLASSES	--------------------------------
	
	/**
	 * This class monitors the performance of the stepHandler. It keeps track of how many 
	 * milliseconds were used in processing.
	 * 
	 * @author Mikko Hilpinen
	 * @since 11.12.2014
	 */
	public abstract static class PerformanceMonitor
	{
		// ATTRIBUTES	----------------------------
		
		private long nextUpdateMillis, updateInterval;
		private double stepAmount, calculationMillis;
		private int actCalls;
		
		
		// CONSTRUCTOR	----------------------------
		
		/**
		 * Creates a new object to monitor performance
		 * @param updateInterval How often the subclass is informed of the monitor status 
		 * (in milliseconds)
		 * @param stepHandler The stepHandler that will inform this monitor about performance 
		 * times
		 */
		public PerformanceMonitor(long updateInterval, StepHandler stepHandler)
		{
			// Initializes atributes
			this.calculationMillis = 0;
			this.nextUpdateMillis = System.currentTimeMillis() + updateInterval;
			this.updateInterval = updateInterval;
			this.stepAmount = 0;
			this.actCalls = 0;
			
			if (stepHandler != null)
				stepHandler.addPerformanceMonitor(this);
		}
		
		
		// ABSTRACT METHODS	-------------------------
		
		/**
		 * This method is called upon certain intervals to inform the subclass
		 * @param lastCalculationMillis How many milliseconds were spent in calculations during the 
		 * last interval
		 * @param stepsPerCall How many steps each act event took care of, in average
		 */
		protected abstract void updatePerformanceStatus(double lastCalculationMillis, 
				double stepsPerCall);
		
		
		// OTHER METHODS	-------------------------
		
		private void updateOperationTime(double operationMillis, double steps)
		{
			this.calculationMillis += operationMillis;
			this.stepAmount += steps;
			this.actCalls += 1;
			
			if (System.currentTimeMillis() >= this.nextUpdateMillis)
			{
				this.nextUpdateMillis = System.currentTimeMillis() + this.updateInterval;
				updatePerformanceStatus(this.calculationMillis, this.stepAmount / this.actCalls);
				this.calculationMillis = 0;
				this.stepAmount = 0;
				this.actCalls = 0;
			}
		}
		
		/**
		 * Calculates, how many percent of the performance capacity was used during an interval
		 * @param calculationMillis How many milliseconds were spent calculating stuff during 
		 * the interval
		 * @return How large a portion of the maximum performance was in use [0, 100]
		 */
		protected int getIntervalPerformance(double calculationMillis)
		{
			return (int) (100 * calculationMillis / this.updateInterval);
		}
	}
	
	/**
	 * Performance accelrator tries to make the program run as smoothly as possible by 
	 * monitoring and adjusting computation time.
	 * 
	 * @author Mikko Hilpinen
	 * @since 12.12.2014
	 */
	public static class PerformanceAccelerator extends PerformanceMonitor
	{
		// ATTRIBUTES	--------------------------
		
		private StepHandler stepHandler;
		private int maxInterval;
		
		
		// CONSTRUCTOR	--------------------------
		
		/**
		 * Creates a new accelrator that will modify the given stepHandler
		 * @param updateInterval How often modifications are made (in milliseconds)
		 * @param stepHandler The stepHandler that is monitored and adjusted
		 */
		public PerformanceAccelerator(long updateInterval,
				StepHandler stepHandler)
		{
			super(updateInterval, stepHandler);
			
			// Initializes attributes
			this.stepHandler = stepHandler;
			this.maxInterval = this.stepHandler.callinterval;
		}
		
		
		// IMPLEMENTED METHODS	------------------

		@Override
		protected void updatePerformanceStatus(double lastCalculationMillis,
				double stepsPerCall)
		{
			int performance = getIntervalPerformance(lastCalculationMillis);
			
			// If the time usage was under 30%, accelerates
			if (performance < 30 && this.stepHandler.callinterval > 1)
				this.stepHandler.callinterval -= 1;
			// If it got over 70%, slows it down (if possible)
			else if (performance > 70 && this.stepHandler.callinterval < this.maxInterval)
				this.stepHandler.callinterval += 1;
		}
	}
}
