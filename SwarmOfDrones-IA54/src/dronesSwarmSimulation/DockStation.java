package dronesSwarmSimulation;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class DockStation {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean busy;
	protected Drone pluggedDrone;
	
	public DockStation(ContinuousSpace<Object> space, Grid<Object> grid) {
		super();
		this.space = space;
		this.grid = grid;
		busy = false;
		unplugFromDrone();
	}
	
	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
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
	
	public GridPoint getPositon()
	{
		
		return grid.getLocation(this)	;
	}

	public void unplugFromDrone() {
		
		pluggedDrone=null;
	}

	public void plugToDrone(Drone drone) {
		if(!isPluggedToDrone())
		{
			pluggedDrone=drone;
		}		
		else
		{
			System.out.println("Can't accept this drone, already plugged to another drone.");
		}
	}
	
	public boolean isPluggedToDrone(Drone drone) {
		return pluggedDrone==drone;
	}
	
	public boolean isPluggedToDrone() {
		return pluggedDrone!=null;
	}
}
