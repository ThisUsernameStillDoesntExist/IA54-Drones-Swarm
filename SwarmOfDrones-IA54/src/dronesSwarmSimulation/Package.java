package dronesSwarmSimulation;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.physics.collisions.colliders.Collider;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;



/*
 * 
 * This class represent the package or the product to be deliver in one exact point, which
 * can be a building
 */
public class Package extends WorldObject {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	//priority 
	private Priority priority ;
	private Vect3 destinationCoord; // destination to deliver the package
	private boolean isTaken;
	private boolean isDelivered;
	private String wareHouseName;
	public Package(ContinuousSpace<Object> space, Grid<Object> grid ) {
		this.space = space;
		this.grid = grid;
		this.isTaken = false;
		isDelivered = false;
		
	}

	//new method
	// this function is used to help the package to be deliver following   the drone
	// and it receives the drone charged to carry the package, so that can it get the drone's position and follow him.
	public void move(Drone drone)
	{
		NdPoint dronePos = space.getLocation(drone);
		space.moveTo(this, dronePos.getX(), dronePos.getY(), dronePos.getZ());
		grid.moveTo(this, (int) (dronePos.getX() + 0.5f), (int) (dronePos.getY() + 0.5f));

	}

	
	public Priority getPriority() {
		return priority;
	}
	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public boolean getIsDelivered() {
		return isDelivered;
	}
	public void setIsDelivered(boolean isDelivered) {
		this.isDelivered = isDelivered;
	}
	// getters and setters of the private fields
	public boolean isTaken() {
		return isTaken;
	}
	public void setTaken(boolean isTaken) {
		this.isTaken = isTaken;
	}
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
	public Vect3 getDestinationCoord() {
		return destinationCoord;
	}

	public void setDestinationCoord(Vect3 destinationCoord) {
		this.destinationCoord = destinationCoord;
	}

	@Override
	public boolean collideWith(WorldObject w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Vect3 getSpecificAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void todoOnUpdate(double time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WorldObject copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collider createSpecificCollider() {
		// TODO Auto-generated method stub
		return null;
	}


}
