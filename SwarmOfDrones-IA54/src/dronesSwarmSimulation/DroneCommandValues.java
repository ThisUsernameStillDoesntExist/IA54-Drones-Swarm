package dronesSwarmSimulation;

import dronesSwarmSimulation.physics.collisions.CollisionTools;
import dronesSwarmSimulation.utilities.Vect3;

/**
 * contains the command values computed by the drone AI controlling module
 * @author Francis
 *
 */
public class DroneCommandValues {
	private Vect3 propellerDirection=new Vect3();
	private double motorThrottle=0;
	private boolean commandSuccess=true;
	
	
	/**
	 * get the propeller direction, not necessarily normalized
	 * @return
	 */
	public Vect3 getPropellerDirection() {
		return propellerDirection;
	}
	public void setPropellerDirection(Vect3 propellerDirection) {
		if(propellerDirection==null)
		{
			propellerDirection=new Vect3();
		}
		this.propellerDirection = propellerDirection;
	}
	public double getMotorThrottle() {
		return motorThrottle;
	}
	public void setMotorThrottle(double motorThrottle) {
		
		this.motorThrottle = CollisionTools.limit(motorThrottle, 0, 1);
	}
	public boolean isCommandSuccess() {
		return commandSuccess;
	}
	public void setCommandSuccess(boolean commandSuccess) {
		this.commandSuccess = commandSuccess;
	}
	

}
