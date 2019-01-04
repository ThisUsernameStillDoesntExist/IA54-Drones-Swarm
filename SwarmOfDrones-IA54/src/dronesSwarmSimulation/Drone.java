package dronesSwarmSimulation;

import java.util.ArrayList;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.physics.collisions.colliders.Collider;
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
	private int charge;
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	protected Package task;
	protected DockStation dock;
	protected boolean charging;
	protected ArrayList<DockStation> docks;
	protected CentralController centralController;
	
	// All drone to be create and set on the scene( visual system ) need to receive the space and grid
	public Drone()
	{
		
	}
	public Drone(ContinuousSpace<Object> space, Grid<Object> grid, int charge) {
		this.space = space;
		this.grid = grid;
		this.charge = charge;
		this.charging = false;
	}
	
	protected void doTask()
	{
		// Derived classes will implement this method
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
	
	// these are methods that all drone have, they describe general behavior or action
	// that a drone can present or do.
	public void communicate() {
		/*Communication between 2 or more drone or with other smart things */
	}
	
	public void negociate() {
		/*negotiate between 2 or more drones */
	}
	
	public void move(GridPoint pt) {
		/*code for movement of drone*/
		
	}
	
	public GridPoint findDockStation()
	{
		// Derived classes will implement this method
		return new GridPoint();
	}
	public void run() {
		/*different step of drone*/
	}
	
	public GridPoint getGridPosition() {
		/*return the drone's position*/
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
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
