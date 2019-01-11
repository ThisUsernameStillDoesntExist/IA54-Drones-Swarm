package dronesSwarmSimulation;

/**
 * stores the characteristics of the droneAI, such as decision coefficients and pathfinding constants
 * 
 * @author Francis
 *
 */
public class DroneAIprops {
	//TODO : create different sets of values according to type of AI
	
	public double perpendicalComponentToDirectionDampingFactor=0.5;//how much the drone should reduce the component of its speed that is perpendicular to the target direction
	public double directionCorrectionFactor=0.5;//higher=more correction
	
}
