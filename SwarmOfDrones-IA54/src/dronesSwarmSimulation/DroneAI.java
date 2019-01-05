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
	
	public DroneAI(Drone attacheddrone)
	{
		this.attachedDrone=attacheddrone;
	}
	
	
	public Vect3 getPropellerDirection()
	{
		return targetPosition.getSubstracted(attachedDrone.getPosition());
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
	
	

}
