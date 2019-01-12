package dronesSwarmSimulation;
/**
 * 
 * @author adilson Francis
 * @class DeliverDrone
 * The concrete class that implement the inteligence of a delivery drone
 */
public class DeliveryDroneAI extends DroneAI {

	public DeliveryDroneAI(Drone attacheddrone) {
		super(attacheddrone);
		
	}
	
	@Override
	protected void initHLDModule() {

		hld=new HighLevelDecisionDeliveryDrone(attachedDrone, this);
	}

}
