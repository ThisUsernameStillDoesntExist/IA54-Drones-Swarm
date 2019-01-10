package dronesSwarmSimulation;

import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.environment.RunEnvironment;

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
	public static final int nbChargingStations=RunEnvironment.getInstance().getParameters().getInteger("nbChargingStations");
	
	
	private GlobalParameters()//simulate static class
	{
		
	}
}
