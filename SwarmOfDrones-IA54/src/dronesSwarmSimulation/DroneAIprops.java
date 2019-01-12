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
	
	public double batteryBeginChargeRelativeThreshold=0.3;//under this threshold, the drone will search for a dockstation
	public double batteryEndChargeRelativeThreshold=0.9;//above this threshold, the drone will disconnect from a dockstation
	
	public double packagePriorityDecisionFactor=2;//the weight of the package priority in the dockstation negotiation
	public double hasTaskDecisionFactor=2;
}
