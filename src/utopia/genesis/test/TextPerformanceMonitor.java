package utopia.genesis.test;

import utopia.genesis.event.StepHandler;
import utopia.genesis.event.StepHandler.PerformanceMonitor;

/**
 * This class prints the performance data to the system output
 * 
 * @author Mikko Hilpinen
 * @since 11.12.2014
 */
public class TextPerformanceMonitor extends PerformanceMonitor
{
	// CONSTRUCTOR	--------------------------
	
	/**
	 * Creates a new monitor
	 * @param updateInterval How often the performance status is printed
	 * @param stepHandler The stepHandler that informs this object about performance
	 */
	public TextPerformanceMonitor(long updateInterval, StepHandler stepHandler)
	{
		super(updateInterval, stepHandler);
	}
	
	
	// IMPLEMENTED METHODS	-------------------

	@Override
	protected void updatePerformanceStatus(double lastCalculationMillis,
			double stepsPerCall)
	{
		//System.out.println(lastCalculationMillis);
		System.out.println(getIntervalPerformance(lastCalculationMillis) + 
				" %  --- A.StepLenght: " + stepsPerCall);
	}
}
