package utopia.genesis.event;

import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates millisconds and calls all actors when a certain number 
 * of milliseconds has passed. All of the actors should be under the command of 
 * this object. This object doesn't stop functioning by itself if it runs out 
 * of actors.
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
	
	private int callinterval;
	private double maxStepsPerCall;
	private long nextupdatemillis, lastactmillis;
	private List<PerformanceMonitor> monitors;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * This creates a new stephandler. Actors are informed 
	 * when a certain number of milliseconds has passed. Actors can be 
	 * added using addActor method.
	 * @param maxActionsPerSecond How many actions per second rate the handler is trying to 
	 * achieve. Use 0 or negative if you want unbound speed.
	 * @param minActionsPerSecond How many actions per second the handler supports at minimum. 
	 * Some physics related functions may break if too many steps are simulated at once.
	 */
	public StepHandler(int maxActionsPerSecond, double minActionsPerSecond)
	{	
		// Initializes attributes
		if (maxActionsPerSecond <= 0)
			this.callinterval = 0;
		else
			this.callinterval = 1000 / maxActionsPerSecond;
		
		this.maxStepsPerCall = (1000 / minActionsPerSecond) / STEPLENGTH;
		this.nextupdatemillis = 0;
		this.lastactmillis = System.currentTimeMillis();
		this.monitors = new ArrayList<>();
	}
	
	/**
	 * This creates a new stephandler in a separate thread and starts it right away.
	 * @param maxActionsPerSecond How many actions per second rate the handler is trying to 
	 * achieve. Use 0 or negative if you want unbound speed.
	 * @param minActionsPerSecond How many actions per second the handler supports at minimum. 
	 * Some physics related functions may break if too many steps are simulated at once.
	 * @return The stepHandler that was started
	 */
	public static StepHandler createAndStartStepHandler(int maxActionsPerSecond, 
			double minActionsPerSecond)
	{
		StepHandler handler = new StepHandler(maxActionsPerSecond, minActionsPerSecond);
		new Thread(handler).start();
		return handler;
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------

	@Override
	public void run()
	{
		// Starts counting steps and does it until the object is killed
		while (!getIsDeadStateOperator().getState())
			update();
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * @return Starts the stepHandler so that it will be updated periodically
	 */
	public Thread start()
	{
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}
	
	/**
	 * @param millis A number of milliseconds
	 * @return The amount of milliseconds in steps
	 */
	public static double millisToSteps(double millis)
	{
		return millis / STEPLENGTH;
	}
	
	/**
	 * @param steps A number of steps
	 * @return The number of steps in milliseconds
	 */
	public static double stepsToMillis(double steps)
	{
		return steps * STEPLENGTH;
	}
	
	/**
	 * Adds a performance monitor to the informed monitors
	 * @param monitor The performance monitor that will be informed
	 */
	private void addPerformanceMonitor(PerformanceMonitor monitor)
	{
		this.monitors.add(monitor);
	}
	
	// This method updates the actors when needed
	private synchronized void update()
	{
		// Remembers the time
		this.nextupdatemillis = System.currentTimeMillis() + this.callinterval;
		
		// Calculates the step length that is informed for the objects
		long thisActStartedAt = System.currentTimeMillis();
		double steps = (thisActStartedAt - this.lastactmillis) / 
				(double) STEPLENGTH;
		
		// Occasional historical error: when put to 120 fps, the program works with 60. 
		// It can't go higher either... 
		// Except randomly after the software has been accelerated once and 
		// the computer has "awaken"
		//System.out.println(thisActStartedAt - this.lastactmillis);
		
		// Sometimes the true amount of steps can't be informed and a 
		// different number is given instead (physics don't like there 
		// being too many steps at once)
		if (steps > this.maxStepsPerCall)
			steps = this.maxStepsPerCall;
		
		act(steps);
		
		// Updates the stepmillis
		this.lastactmillis = thisActStartedAt;
		
		// Informs the monitors
		for (PerformanceMonitor monitor : this.monitors)
		{
			monitor.updateOperationTime(System.currentTimeMillis() - this.lastactmillis, steps);
		}
		
		// If there is time, the thread will wait until another step is needed
		long waitMillis = this.nextupdatemillis - System.currentTimeMillis();
		if (waitMillis > 0)
		{
			while (true)
			{
				try
				{
					wait(waitMillis);
					break;
				}
				catch (InterruptedException exception)
				{
					waitMillis = this.nextupdatemillis - System.currentTimeMillis();
					if (waitMillis <= 0)
						break;
				}
			}
		}
	}
	
	
	// SUBCLASSES	--------------------------------
	
	/**
	 * This class monitors the performance of the stepHandler. It keeps track of how many 
	 * milliseconds were used in processing.
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
			{
				this.stepHandler.callinterval -= 1;
			}
			// If it got over 70%, slows it down (if possible)
			else if (performance > 70 && this.stepHandler.callinterval < this.maxInterval)
				this.stepHandler.callinterval += 1;
		}
	}
}
