package dronesSwarmSimulation;

import dronesSwarmSimulation.physics.Constants;
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
	private double droneAvgSpeed;//=distance travelled per s
	private double droneCurrentSpeed;//mean speed of all drones
	private double totalTimeElapsed;
	private double currentElectricityConsumption;//current power output of all drones
	private double totalElectricityConsumption;//since the beginning
	
	
	
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
		totalTimeElapsed=0;
		currentElectricityConsumption=0;
		totalElectricityConsumption=0;
	}


	/**
	 * update for one frame, should be called once for one update of every drone
	 * @param time
	 */
	@ScheduledMethod(start = 1, interval = 1, priority=1, shuffle=false)//priority should not be the same than drones, in order to guarantee the update of statistics after every drone has updated
	public void update(/*double time*/)
	{
		currentTickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		//totalTimeElapsed=currentTickCount*GlobalParameters.frameTime;
		totalTimeElapsed+=GlobalParameters.frameTime;//take in account a possible change of frametime
		
		updateStandardStats();
		updateStatsPerDrone();
		
		System.out.println("Statistics updated");
	}

	private void updateStandardStats() {

		
		
	}


	/**
	 * update variables that depends on each and every drone
	 */
	private void updateStatsPerDrone() {
		
		//update total distance, mean current speed, current elec cons
		double tdt=0;
		double mcs=0;
		double cec=0;
		for(Drone d : centralController.getLisOfDrones())
		{
			tdt+=d.getTotalDistanceTravelled();
			mcs+=d.getSpeed().norm();
			cec+=d.getMotorConsumption();
		}
		
		
		currentElectricityConsumption=cec;
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
		
		if(totalTimeElapsed!=0)
		{
			droneAvgSpeed=distanceTravelledPerDrone/totalTimeElapsed;
		}
		else
		{
			droneAvgSpeed=0;
		}
		
		totalElectricityConsumption+=currentElectricityConsumption*GlobalParameters.frameTime*Constants.WsToWh;
		
		
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

	public double getTotalTimeElapsed() {
		return totalTimeElapsed;
	}


	public double getCurrentElectricityConsumption() {
		return currentElectricityConsumption;
	}


	public double getTotalElectricityConsumption() {
		return totalElectricityConsumption;
	}
	
	
	
	
	

}
