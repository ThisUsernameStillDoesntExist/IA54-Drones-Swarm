package dronesSwarmSimulation;

import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.NdPoint;

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
	public static int nbChargingStations=0;
	public static double frameTime=0;
	public static boolean swarmActivated=false;
	public static int nbDeliveryDrones=0;
	public static int initialCharge=0;
	public static int nbPackages=0;
	public static String warehouseName=null;
	public static int nbDockstations=0;
	public static NdPoint wareHousePostion =  null;
	public static Vect3 initDronePosition = new Vect3(0,0,100);
	public static int nbOfBuildings = 17;
	public static int nbOfTrees = 27;
	
	/**
	 * should be called on each simulation init
	 */
	public static void initParamsFromRepast()
	{
		Parameters RP=RunEnvironment.getInstance().getParameters();		
		nbChargingStations=RP.getInteger("nbChargingStations");
		frameTime=RP.getDouble("frametime");
		swarmActivated=RP.getBoolean("swarm");
		nbDeliveryDrones=RP.getInteger("deliver_drone");
		initialCharge=RP.getInteger("charge");
		nbPackages=RP.getInteger("package");
		warehouseName=RP.getString("warehousenames");
		nbDockstations=RP.getInteger("dockstation");
		
		String[]  whPositions =  RP.getString("warehouspositon").split(",");
		if( whPositions.length < 3)
		{
			wareHousePostion =  new NdPoint(107,130,100);
		}
		else {
			wareHousePostion =  new NdPoint(Integer.parseInt(whPositions[0]),Integer.parseInt(whPositions[0]),Integer.parseInt(whPositions[0]));
		}
		
	}
	
	private GlobalParameters()//simulate static class
	{
		
	}
}
