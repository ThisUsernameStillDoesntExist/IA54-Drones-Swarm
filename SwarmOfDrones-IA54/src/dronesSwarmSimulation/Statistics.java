package dronesSwarmSimulation;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

/**
 * Computes and provides variables and indicators of drone simulation.
 * Used for performance assessment and chart data gathering.
 * @author Francis
 *
 */
public class Statistics {
	
	private int totalNbDrones;
	private CentralController centralController;
	private double currentTickCount;
	
	//declare dynamic variables here :
	private double totalDistanceTravelledByDrones;
	private double distanceTravelledPerDrone;
	private double droneAvgSpeed;//=distance travelled per count
	private double droneCurrentSpeed;//mean speed of all drones
	
	
	
	public Statistics(int nbdrones, CentralController ct)
	{
		totalNbDrones=nbdrones;
		centralController=ct;
		currentTickCount=0;
		
		initStatsVars();
	}
	
	
	private void initStatsVars() {
		//explicit init of vars here
		totalDistanceTravelledByDrones=0;
		distanceTravelledPerDrone=0;
		droneAvgSpeed=0;
		droneCurrentSpeed=0;
	}


	/**
	 * update for one frame, should be called once for one update of every drone
	 * @param time
	 */
	@ScheduledMethod(start = 1, interval = 1, priority=1, shuffle=false)//priority should not be the same than drones, in order to guarantee the update of statistics after every drone has updated
	public void update(/*double time*/)
	{
		currentTickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		
		updateStatsPerDrone();
		
		System.out.println("Statistics updated");
	}

	/**
	 * update variables that depends on each and every drone
	 */
	private void updateStatsPerDrone() {
		
		//update total distance, mean current speed
		double tdt=0;
		double mcs=0;
		for(Drone d : centralController.getLisOfDrones())
		{
			tdt+=d.getTotalDistanceTravelled();
			mcs+=d.getSpeed().norm();
		}
		
		
		totalDistanceTravelledByDrones=tdt;
		if(centralController.getLisOfDrones().size()>0)
		{
			distanceTravelledPerDrone=totalDistanceTravelledByDrones/centralController.getLisOfDrones().size();
			droneCurrentSpeed=mcs/centralController.getLisOfDrones().size();
		}
		else
		{
			distanceTravelledPerDrone=0;
			droneCurrentSpeed=0;
		}
		
		if(currentTickCount!=0)
		{
			droneAvgSpeed=distanceTravelledPerDrone/currentTickCount;
		}
		else
		{
			droneAvgSpeed=0;
		}
		
		
	}


	public double getTotalDistanceTravelledByDrones() {
		return totalDistanceTravelledByDrones;
	}


	public double getDistanceTravelledPerDrone() {
		return distanceTravelledPerDrone;
	}


	public double getDroneAvgSpeed() {
		return droneAvgSpeed;
	}
	
	public double getDroneCurrentSpeed() {
		return droneCurrentSpeed;
	}
	
	

}
