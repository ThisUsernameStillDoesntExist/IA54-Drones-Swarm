package dronesSwarmSimulation;

import dronesSwarmSimulation.physics.WorldObjectCharacteristics;
import dronesSwarmSimulation.physics.WorldObjectType;

public class DroneCharacteristics extends WorldObjectCharacteristics {
	
	// maxspeed is now a function of drag, propeller lift and motor power

	/*
	 * We assume a linear charge/discharge model, and a potentially infinite
	 * instantaneous power delivered from the accumulators.
	 * 
	 * Consumption takes only motors into account, and not electronic or
	 * communication components.
	 */
	
	protected double maxLeaningAngle;//degrees, should be positive
	protected double motorEfficiency;//No unit
	protected double motorMaxConsumption;//W
	protected double propellerLift;//s/m
	protected double maxPayload;//Kg
	protected double batteryCapacity;//W.h=Joules/3600
	protected double batteryRechargingRate;//W
	
	public DroneCharacteristics() {
		setCharacteristics(WorldObjectType.StandardDrone);
	}
	
	@Override
	protected void setCharacteristics(WorldObjectType wot) {
		// The max propeller force (=motormaxconso*motorefficiency*propellerlift) should
		// at least be equal to 9.81*dryWeight, otherwise your drone won't even takeoff.
		// If you want to carry a payload of mass m, the recommended max propeller force
		// is roughly 20*(dryweight+m)
		// The maxLeaningAngle should ideally be such that when the drone is leaning at
		// that angle with max throttle, its vertical acceleration is zero.
		// Set a smaller angle if you want safety, and a larger if you want fast
		// horizontal speeds
		propellerLift = 0.04;
		airDrag = 0.004;// airdrag will limit the maxspeed
		frictionDrag=0;

		switch (wot) {
		case MiniDrone:// based on Parrot Mambo
			propellerLift = 0.035;
			airDrag = 0.005;
			maxLeaningAngle = 30;
			radius = 0.09;
			motorEfficiency = 0.85;
			motorMaxConsumption = 25;// for 4 motors
			maxPayload = 0.005;
			dryWeight = 0.065;
			batteryCapacity = 2.2;// around 5 min autonomy at max throttle
			batteryRechargingRate = 4.4;// 30 min charging time
			break;
		case StandardDrone:// based on DJI Spark
			propellerLift = 0.035;
			airDrag = 0.012;
			maxLeaningAngle = 25;
			radius = 0.11;
			motorEfficiency = 0.85;
			motorMaxConsumption = 100;// around 10 min autonomy at max throttle
			maxPayload = 0.03;
			dryWeight = 0.3;
			batteryCapacity = 17;
			batteryRechargingRate = 29;
			break;
		case HelperDrone:// based on DJI S900
			propellerLift = 0.04;
			airDrag = 0.02;
			maxLeaningAngle = 20;
			radius = 0.5;
			motorEfficiency = 0.9;
			motorMaxConsumption = 3000;// for 6 motors
			maxPayload = 4;
			dryWeight = 3.5;
			batteryCapacity = 300;
			batteryRechargingRate = 300;
			break;
		}
	}
	
	public double getMaxLeaningAngle() {
		return maxLeaningAngle;
	}
	
	public double getMotorEfficiency() {
		return motorEfficiency;
	}

	public double getMotorMaxConsumption() {
		return motorMaxConsumption;
	}

	public double getPropellerLift() {
		return propellerLift;
	}

	public double getMaxPayload() {
		return maxPayload;
	}
	
	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	public double getBatteryRechargingRate() {
		return batteryRechargingRate;
	}
	


}
