
package dronesSwarmSimulation.physics;

import dronesSwarmSimulation.utilities.Vect3;

public final class Constants {
	
	public static final double DegToRad=Math.PI/180;
	public static final double WsToWh=1.0/3600; //watt per second to watt per hour
	public static final Vect3 Gravity=new Vect3(0,0,-9.81);// m/s²
	
	public static final double MeterToPixel=50;//1 meter in physicsEngine = ? pixels on screen, with a zoom of 1.0    
    
    
}
