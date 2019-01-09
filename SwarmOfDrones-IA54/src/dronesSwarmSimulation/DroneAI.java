package dronesSwarmSimulation;

import dronesSwarmSimulation.utilities.Vect3;

/**
 * Implements thinking process of the drone (which results in target coordinates and an action to achieve)
 * In other words, the DroneAI get percepted information, builds concepts from it, take a decision, and then translate this decision in effective simple steps of action.
 * TODO : improve with BDI model
 * @author Francis
 *
 */
public class DroneAI {
	
	protected Drone attachedDrone;
	protected Vect3 targetPosition;
	protected double throttleLimit;// max limit of throttle for energy saving, should be set by AI if necessary to use less battery
	
	public DroneAI(Drone attacheddrone)
	{
		this.attachedDrone=attacheddrone;
		this.throttleLimit=1;
	}
	
	
	public Vect3 getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Vect3 targetPosition) {
		if(targetPosition==null && attachedDrone!=null)
		{
			targetPosition=new Vect3(attachedDrone.getPosition());
		}
		this.targetPosition = targetPosition;
	}


	/**
	 * decision method for the drone
	 * -take in input information : tasks, environment, messages from other drones...
	 * -make a decision following rules
	 * -output a propeller direction with a motorthrottle (also output messages for other agents, and basic actions like take package or plug to station)
	 * @param time
	 */
	public void decide(double time) {
		// TODO Auto-generated method stub
		if(targetPosition==null)
		{
			return;
		}
		
		Vect3 tarpos=new Vect3(targetPosition);
		Vect3 propdir=getComputedPropellerDirection(tarpos);
		
		propdir.Normalize();
		
		attachedDrone.setPropellerDirection(propdir);
		
		//motor at full power
		attachedDrone.setMotorThrottle(1);
		
		
	}


	private Vect3 getComputedPropellerDirection(Vect3 tarpos) {
		
		//TODO : add safety coef to avoid full stop too early
		
		Vect3 targetdir=tarpos.getSubstracted(attachedDrone.getPosition());
		Vect3 actualspeed=attachedDrone.getSpeed().copy();//=new Vect3(attachedDrone.getSpeed())
		Vect3 propdir=targetdir;
		
		double cosdirs=targetdir.getAngleCosSafe(actualspeed);
		double speedTowardTarget=cosdirs*actualspeed.norm();//amount of speed directed toward target
		
		if(speedTowardTarget<=0)
		{
			return propdir;
		}
		
		//optimal braking distance
		double brakingdist=speedTowardTarget*speedTowardTarget/attachedDrone.getMaxAccelerationAtThrottle(throttleLimit);//when the drone is at a smaller distance than brakingdist from the target, he should start to brake
		
		double distToTarget=targetdir.norm();//distance between drone and targetposition
		
		if(distToTarget<brakingdist)
		{
			//slow down
			propdir=actualspeed.getReversed();
			
			if(propdir.norm()==0)
			{
				System.out.println("   ERROR : PropDir is zero.");
			}
		}
		
		return propdir;
	}
	
	

}
