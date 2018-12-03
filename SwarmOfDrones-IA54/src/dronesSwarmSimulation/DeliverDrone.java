package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class DeliverDrone extends Drone {
	private boolean dejaTrouvePackage ;
	private boolean hasTask;
	private Package task;
	private Queue<Package> tasks;
	private Queue<Package> tasksNotDelivered;
	private int nbTaskNotDelivered = 0;
	private int index = 0;
	private int charge ;

	
	public DeliverDrone(ContinuousSpace<Object> space, Grid<Object> grid, int charge) {
		super(space, grid, charge);
	    dejaTrouvePackage = false;
	    this.hasTask = false;
	    this.charge = charge;
	    tasksNotDelivered = new LinkedList<Package>();

	}
	
	// method that implement the functional behavior of the drone
	// it is called each 1 second
	@Override
	@ScheduledMethod(start = 1, interval = 2)
	public void doTask()
	{
		
		if(charge>0)
		{	
			//System.out.println("nombre de drones = " + tasks.size());
		
		
			if(hasTask && !dejaTrouvePackage)
			{
				
					findPackage(grid.getLocation(task));
					charge--;
				
			}
			else
			{	

				if(!tasks.isEmpty() && !hasTask)
				{
					task = tasks.remove();
					hasTask = true;
					System.out.println("nouveau package");
				}
			}
			
			if(dejaTrouvePackage)
			{

				if(hasArrived(task.getDestinationCoord()))
				{
					// communiquer la centrale pour dire que plus un package delivré
					// trouver un autre package
					hasTask = false;
					dejaTrouvePackage = false;

				}
				else
				{
					
						move(task.getDestinationCoord());
						this.getTask().move(this);
						charge--;
					
				}
			}
		}
		else
		{
			// notify the central
			if(hasTask)
			{
				tasksNotDelivered.add(task);
				nbTaskNotDelivered++;
				task.setIsDelivered(false);
				System.out.println("Triger l'evenement package");
				task = null;
				hasTask=false;
				dejaTrouvePackage = false;
			}
			
		}
	
	}
	// method that move the Drone to a desired location on the scene(screen), we just need to give in the location
	// This method is used to move the to the building where the package will be delivered
	@Override
	public void move(GridPoint pt)
	{
		if (!pt.equals(grid.getLocation(this )) ) {
			
			
				//turn(pt);
				NdPoint  myPoint = space.getLocation(this);
				
				NdPoint  otherPoint = new  NdPoint(pt.getX(), pt.getY ());
				
				double  angle = SpatialMath.calcAngleFor2DMovement(space ,
				
				myPoint , otherPoint );
				
				space.moveByVector(this , 1, angle , 0);
				
				myPoint = space.getLocation(this);
				
				grid.moveTo(this , (int)myPoint.getX(), (int)myPoint.getY ());
			
		}

	}
	private boolean hasArrived(GridPoint pt)
	{
		GridPoint actualLocation = grid.getLocation(this);
		double distance = Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
		if(distance <= 1 && distance >=0)	{
			System.out.println("Arrivé au Building");
			return true;
		}
		
		return false;
	}
	
	
	// method that move the Drone to a desired location on the scene(screen), it used to 
	// find the package that has been assigned to him, to be delivered
	
	public void findPackage(GridPoint pt)
	{
		
		if(!pt.equals(grid.getLocation(this)))
		{
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(),pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement (space,myPoint , otherPoint );
			space.moveByVector(this, 1, angle,0);
			myPoint = space.getLocation(this);
			grid.moveTo ( this ,( int )myPoint.getX (), ( int )myPoint.getY ());
			
			// if the drone has found the package, the we change the state of the variable
			// so that the drone stop looking for the package and start looking for the building
			/*if((int)myPoint.getX() == (int)otherPoint.getX() && (int)myPoint.getY() == (int)otherPoint.getY())
			{
				dejaTrouvePackage = true;
			} */
			
		}
		else
		{
			GridPoint actualLocation = grid.getLocation(this);
			double distance = Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
			if(distance < 1)	{
				System.out.println("Arrivé au Package");
				dejaTrouvePackage = true;
			}
		}
	}
	@Override
	public void findDockStation(GridPoint pt)
	{
		// TODO : 
			// -Drone need to fin dthe nearet Dockstation -- Optimization
		if(!pt.equals(grid.getLocation(this)) )
		{
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(),pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement (space,myPoint , otherPoint );
			space.moveByVector(this, 1, angle,0);
			myPoint = space.getLocation(this);
			grid.moveTo ( this ,( int )myPoint.getX (), ( int )myPoint.getY ());
			
			// if the drone has found the package, the we change the state of the variable
			// so that the drone stop looking for the package and start looking for the building
		}
	}

	
	public Queue<Package> getTasksNotDelivered() {
		return tasksNotDelivered;
	}

	public void setTasksNotDelivered(Queue<Package> tasksNotDelivered) {
		this.tasksNotDelivered = tasksNotDelivered;
	}

	public Queue<Package> getTasks() {
		return tasks;
	}

	public void setTasks(Queue<Package> tasks) {
		this.tasks = tasks;
	}
	
	public boolean hasTask() {
		return hasTask;
	}

	public void setHasTask(boolean hasTask) {
		this.hasTask = hasTask;
	}

	public boolean isDejaTrouvePackage() {
		return dejaTrouvePackage;
	}

	public void setDejaTrouvePackage(boolean dejaTrouvePackage) {
		this.dejaTrouvePackage = dejaTrouvePackage;
	}

	
	// method to get the task assigned to the drone
	
	// method to assign a task on the drone
	public void setTask(Package task) {
		this.task = task;
		this.dejaTrouvePackage = false;
	}
	
	public Package getTask() {
		return task;
	}
	

	@Override
	public ContinuousSpace<Object> getSpace() {
		return space;
	}

	@Override
	public void setSpace(ContinuousSpace<Object> space) {
		this.space = space;
	}

	@Override
	public Grid<Object> getGrid() {
		return grid;
	}

	@Override
	public void setGrid(Grid<Object> grid) {
		this.grid = grid;
	}

	@Override
	public void communicate() {
		/*Communication between 2 or more drone or with other smart things */
	}
	
	@Override
	public void negociate() {
		/*negotiate between 2 or more drones */
	}
	
	
	
	@Override
	public void run() {
		/*different step of drone*/
	}
	
	@Override
	public GridPoint  getPosition() {
		/*return drone position*/
		return new GridPoint();
	}

}
