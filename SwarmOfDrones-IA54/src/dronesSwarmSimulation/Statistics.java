package dronesSwarmSimulation;

/**
 * Computes and provides variables and indicators of drone simulation.
 * Used for performance assessment and chart data gathering.
 * @author Francis
 *
 */
public class Statistics {
	
	protected int totalNbDrones;
	protected double totalDistanceTravelledByDrones;
	
	public Statistics(int nbdrones)
	{
		totalNbDrones=nbdrones;
	}
	
	/**
	 * update for one frame, should be called once for one update of every drone
	 * @param time
	 */
	public void update(double time)
	{
		
	}

}
