package dronesSwarmSimulation;

import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

/**
 * Stores and regroup parameters of the simulation.
 * Avoids hardcoded magic constants and allow to modify a lot of parameters in the same place.
 * Repast parameters should also be retrieved in this class.
 * @author Francis
 *
 */
public final class GlobalParameters {

	public static final Vect3 spaceDimensions=new Vect3(200, 150, 400);
	
	//repast parameters
	private static Parameters RP=RunEnvironment.getInstance().getParameters();		
	public static final int nbChargingStations=RP.getInteger("nbChargingStations");
	public static final double frameTime=RP.getDouble("frametime");
	public static final boolean swarmActivated=RP.getBoolean("swarm");
	public static final int nbDeliveryDrones=RP.getInteger("deliver_drone");
	public static final int initialCharge=RP.getInteger("charge");
	public static final int nbPackages=RP.getInteger("package");
	public static final String warehouseName=RP.getString("warehousenames");
	public static final int nbDockstations=RP.getInteger("dockstation");
	
	
	private GlobalParameters()//simulate static class
	{
		
	}
}
