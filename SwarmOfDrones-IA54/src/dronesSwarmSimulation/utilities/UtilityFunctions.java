package dronesSwarmSimulation.utilities;

import dronesSwarmSimulation.GlobalParameters;
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
	
	/**
	 * reverse only x and y
	 * @param v
	 * @return
	 */
	public static Vect3 getReversedOnXYPlane(Vect3 v)
	{
		return new Vect3(-v.getX(),-v.getY(),v.getZ());
	}
	
	/**
	 * print to console if a variable is set
	 * @param text
	 * @return
	 */
	public static void printConsole(String text)
	{
		if(GlobalParameters.consoleDisplay)
		{
			System.out.print(text);
		}
	}
	
	/**
	 * print to console if a variable is set
	 * @param text
	 * @return
	 */
	public static void printConsoleLn(String text)
	{
		if(GlobalParameters.consoleDisplay)
		{
			System.out.println(text);
		}
	}

}
