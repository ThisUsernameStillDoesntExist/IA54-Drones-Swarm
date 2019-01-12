package dronesSwarmSimulation;

public class DeliveryDroneAI extends DroneAI {

	public DeliveryDroneAI(Drone attacheddrone) {
		super(attacheddrone);
		
	}
	
	@Override
	protected void initHLDModule() {

		hld=new HighLevelDecisionDeliveryDrone(attachedDrone, this);
	}

}
