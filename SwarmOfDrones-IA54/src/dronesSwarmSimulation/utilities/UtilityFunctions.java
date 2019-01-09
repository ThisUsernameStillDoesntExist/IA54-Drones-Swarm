package dronesSwarmSimulation.utilities;

import repast.simphony.space.continuous.NdPoint;

public class UtilityFunctions {
	
	/**
	 * Damn java that doesn't even have the fucking simplest math function.
	 * 
	 * I'm so angry that this function will not even check for bad arguments, ha !
	 * @param n
	 * @param decimals
	 * @return
	 */
	public static double round(double n, int decimals) {
		double power = Math.pow(10, decimals);
		return Math.round(n * power) / power;
	}
	
	public static Vect3 NdPointToVect3(NdPoint ndp)
	{
		//temp checking, can be removed when space is 3D
		if(ndp.dimensionCount()==2) return new Vect3(ndp.getX(), ndp.getY(), 0);		
		
		return new Vect3(ndp.getX(), ndp.getY(), ndp.getZ());
	}

}
