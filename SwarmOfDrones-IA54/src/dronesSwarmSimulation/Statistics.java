package dronesSwarmSimulation;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Computes and provides variables and indicators of drone simulation.
 * Used for performance assessment and chart data gathering.
 * @author Francis
 *
 */
public class Statistics {
	
	private int totalNbDrones;
	private double totalDistanceTravelledByDrones;
	private CentralController centralController;
	
	public Statistics(int nbdrones, CentralController ct)
	{
		totalNbDrones=nbdrones;
		centralController=ct;
	}
	
	/**
	 * update for one frame, should be called once for one update of every drone
	 * @param time
	 */
	@ScheduledMethod(start = 1, interval = 1, priority=3, shuffle=false)
	public void update(/*double time*/)
	{
		//update vars with sum of drones values
		System.out.println("BIM:"+centralController.getLisOfDrones().get(0).getPosition().toStringLen(30, 3));
	}

}
