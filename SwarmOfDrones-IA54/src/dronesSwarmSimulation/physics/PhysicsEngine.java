package dronesSwarmSimulation.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dronesSwarmSimulation.DeliverDrone;
import dronesSwarmSimulation.Drone;
import dronesSwarmSimulation.GlobalParameters;
import dronesSwarmSimulation.physics.collisions.CollisionSortElement;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class PhysicsEngine {
	
	private List<WorldObject> wolist;
	ContinuousSpace<Object> space;
	Grid<Object> grid;
	
	
	public PhysicsEngine(ContinuousSpace<Object> space, Grid<Object> grid, List<WorldObject> l) {
		this.space=space;
		this.grid=grid;
		wolist=l;
	}
	
	/**
	 * called once per frame to refresh the whole simulation environment
	 */
	@ScheduledMethod(start = 1, interval = 1, priority=2)
	public void repastSimulationRefreshMethod()
	{
		//this test method show how to update a drone position using new physics.
		
		//retrieve the time that we will provide to the update drone function		
		double frametime=GlobalParameters.frameTime;
		//double tickdelay=RunEnvironment.getInstance().getScheduleTickDelay();
		double time=frametime;
		
		updateWorld(time);
		
	}
	
	
	public void updateWorld(double time)
	{
		
		for (WorldObject w : wolist) {
			
			w.updateMe(time);
			
			Vect3 newpos=w.getPosition();
			
			space.moveTo(w, newpos.getX(), newpos.getY(), newpos.getZ());//3D //update the drone position in the repast continuous space
			
			NdPoint newDronePoint = space.getLocation(w);
			
			grid.moveTo(this , (int)newDronePoint.getX(), (int)newDronePoint.getY ());//update the grid
		}
	}
	

}
