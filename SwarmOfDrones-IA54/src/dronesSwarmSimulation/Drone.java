package dronesSwarmSimulation;

import java.util.ArrayList;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.physics.collisions.CollisionTools;
import dronesSwarmSimulation.physics.collisions.colliders.Collider;
import dronesSwarmSimulation.physics.collisions.colliders.Sphere;
import dronesSwarmSimulation.utilities.UtilityFunctions;
import dronesSwarmSimulation.utilities.Vect3;
import dronesSwarmSimulation.physics.Constants;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/*
 * this is the Base class that describe the general behavior and attributes 
 * of the two types of drones existing in our simulation software 
 * 
 */
public class Drone extends WorldObject {

	private static int TotalNbOfDrones = 0;// used to generate unique drone id

	protected int id;
	private int charge;
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	protected Package task;
	protected DockStation dock;
	protected boolean charging;
	protected ArrayList<DockStation> docks;
	protected CentralController centralController;
	protected DroneAI brain;

	protected double batteryLevel;// W.h=Joules/3600
	protected double payload;// Kg
	protected double motorThrottle;// %
	protected Vect3 propellerDirection;// the z axis vector in the drone local frame (pointing upwards, represent motor
										// force) - should be updated by the AI
	private DockStation pluggedStation;// null if not connected to any station for recharging

	// TODO : create fully parameterized constructor

	// All drone to be create and set on the scene( visual system ) need to receive
	// the space and grid
	public Drone() {
		this(null, null, 0);
	}

	public Drone(ContinuousSpace<Object> space, Grid<Object> grid, int charge) {
		super();
		this.id=getNewId();
		this.space = space;
		this.grid = grid;
		this.charge = charge;
		this.charging = false;
		this.charact = new DroneCharacteristics();
		this.brain = new DroneAI(this);
		this.batteryLevel = getCharacteristics().getBatteryCapacity();
		setPropellerDirection(new Vect3(0, 0, 0));
	}

	protected void doTask() {
		// Derived classes will implement this method
	}

	/**
	 * call all the sub-functions needed to update the drone state
	 */
	@Override
	protected void todoOnUpdate(double time) {


		if (isPluggedToStation()) {
			rechargeBattery(time);
		}

		dischargeBattery(time);

		decide(time);
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	// this are just getter and setter
	public ContinuousSpace<Object> getSpace() {
		return space;
	}

	public void setSpace(ContinuousSpace<Object> space) {
		this.space = space;
	}

	public Grid<Object> getGrid() {
		return grid;
	}

	public void setGrid(Grid<Object> grid) {
		this.grid = grid;
	}

	protected DockStation getDock() {
		return dock;
	}

	protected void setDock(DockStation dock) {
		this.dock = dock;

	}

	public ArrayList<DockStation> getDocks() {
		return docks;
	}

	public void setDocks(ArrayList<DockStation> docks) {
		this.docks = docks;
	}

	private int getNewId() {
		TotalNbOfDrones++;
		return TotalNbOfDrones;
	}

	// these are methods that all drone have, they describe general behavior or
	// action
	// that a drone can present or do.
	public void communicate() {
		/* Communication between 2 or more drone or with other smart things */
	}

	public void negociate() {
		/* negotiate between 2 or more drones */
	}

	public void move(NdPoint pt) {
		/* code for movement of drone */

	}

	public NdPoint findDockStation() {
		// Derived classes will implement this method
		return new NdPoint();
	}

	public void run() {
		/* different step of drone */
	}

	public GridPoint getGridPosition() {
		/* return the drone's position */
		return new GridPoint();
	}

	public CentralController getCentralController() {
		return centralController;
	}

	public void setCentralController(CentralController centralController) {
		this.centralController = centralController;
	}

	@Override
	public boolean collideWith(WorldObject w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WorldObject copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collider createSpecificCollider() {
		
		return new Sphere();
	}

	public int getId() {
		return id;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public double getBatteryLevelRelative() {
		return batteryLevel / thisCharacteristics().getBatteryCapacity();
	}

	public double getPayload() {
		return payload;
	}

	public void setMotorThrottle(double motorThrottle) {

		this.motorThrottle = CollisionTools.limit(motorThrottle, 0, 1);
	}

	public double getMotorThrottle() {
		return motorThrottle;
	}

	public Vect3 getPropellerDirection() {
		return propellerDirection;
	}

	public void setPropellerDirection(Vect3 propellerDirection) {

		if (propellerDirection == null) {
			propellerDirection = new Vect3(0, 0, 0);
		}

		this.propellerDirection = propellerDirection;
	}

	// here we take in account extra payload for the computation of acceleration
	@Override
	public double getTotalWeight() {
		return charact.getDryWeight() + payload;
	}

	public double getMotorOutputPower() {
		return getMotorConsumption() * thisCharacteristics().getMotorEfficiency();
	} // W

	public double getMotorConsumption() {
		return batteryState() * motorThrottle * thisCharacteristics().getMotorMaxConsumption();
	} // W

	/**
	 * should be used by physics update call only
	 */
	protected Vect3 getSpecificAcceleration() {
		
		//adjustPropellerDirection();

		Vect3 propellerAcceleration = propellerDirection.getNormalized()
				.multiplyBy(getMaxAccelerationAtThrottle(this.motorThrottle));

		return propellerAcceleration;
	}
	
	/**
	 * return the acceleration norm ((generated by the propeller) for a given throttle (theoretical, battery ignored)
	 * @param tht
	 * @return
	 */
	protected double getMaxAccelerationAtThrottle(double tht)
	{
		//tht=CollisionTools.limit(tht, 0, 1);//throttle in % between 0,1
		
		double motorcons= tht * thisCharacteristics().getMotorMaxConsumption();
		double outputpower=motorcons*thisCharacteristics().getMotorEfficiency();
		
		return thisCharacteristics().getPropellerLift() * outputpower / getTotalWeight();
		
	}

	public DroneCharacteristics getCharacteristics() {
		return thisCharacteristics();
	}

	public DroneAI getBrain() {
		return brain;
	}

	public void setBrain(DroneAI brain) {
		this.brain = brain;
	}

	private void adjustPropellerDirection() {
		Vect3 propDir = new Vect3(propellerDirection);
		if (propDir.getZ() <= 0) {
			System.out.println("Invalid propellerDirection. Did nothing.");
			// motorThrottle=0;//we could stop motors if drone is flipped over

			// in this case leaning angle is exceeded and the drone can go in any unwanted
			// direction, but physics will still work as expected
			// this can be dangerous if the control module doesn't take this into account
			// we have to link propellerDirection from output of control module to input of
			// updateSpeed
			return;
		}

		Vect3 propDirNormalizedZ = propDir.getMultipliedBy(1 / propDir.getZ());// normalize on z axis
		double maxHspeedNormalized = Math.sin(thisCharacteristics().getMaxLeaningAngle() * Constants.DegToRad);
		propDirNormalizedZ.setZ(0);
		double propDirHspeedNormalized = propDirNormalizedZ.norm();
		if (propDirHspeedNormalized > maxHspeedNormalized) {
			// limit max leaning angle
			double decreaseRatio = maxHspeedNormalized / propDirHspeedNormalized;
			propDir.setX(propDir.getX() * decreaseRatio);
			propDir.setY(propDir.getY() * decreaseRatio);
		}

		propellerDirection = propDir;
	}

	private void dischargeBattery(double time) {
		batteryLevel -= getMotorConsumption() * time * Constants.WsToWh;
		if (batteryLevel < 0) {
			batteryLevel = 0;
		}
	}

	public boolean batteryIsEmpty() {
		return batteryLevel == 0;
	}

	// necessary as boolean arithmetic doesn't exist in java
	private double batteryState() {
		if (batteryLevel == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	// TODO low battery state

	private void rechargeBattery(double time) {
		batteryLevel += thisCharacteristics().getBatteryRechargingRate() * time * Constants.WsToWh;
		if (batteryLevel > thisCharacteristics().getBatteryCapacity()) {
			batteryLevel = thisCharacteristics().getBatteryCapacity();
		}
	}

	/*
	 * public void plugToStation(Station st) { this.unplugFromStation();
	 * 
	 * this.pluggedStation=st;
	 * 
	 * if(!st.isPluggedToDrone(this)) { st.plugToDrone(this); } }
	 * 
	 * public void unplugFromStation() { if(this.pluggedStation!=null) {
	 * this.pluggedStation.unplugFromDrone(); }
	 * 
	 * this.pluggedStation=null; }
	 */

	public boolean isPluggedToStation() {
		return this.pluggedStation != null;
	}

	/**
	 * Perform actions according to environment and drone parameters This is what
	 * makes our system multi-agent
	 */
	public void decide(double time) {
		
		brain.decide(time);

	}

	protected DroneCharacteristics thisCharacteristics() {
		return (DroneCharacteristics) this.charact;
	}
	
	
	/**
	 * true if the drone arrived to another object
	 * @param w
	 * @return
	 */
	protected boolean hasArrived(WorldObject w)
	{
		return this.isOver(w);

		//System.out.println("Arrivé au Building");
	}
	
	/**
	 * true if the drone has arrived to this point
	 * @param ndp
	 * @return
	 */
	protected boolean hasArrived(NdPoint ndp)
	{
		Vect3 p=UtilityFunctions.NdPointToVect3(ndp);
		return this.isInRange(p);
	}

}
