package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.utilities.UtilityFunctions;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.parameter.Parameters;
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
	private int id;
	private boolean dejaTrouvePackage ;
	private boolean hasTask;
	private Package task;
	private ArrayList<DockStation> lisOfDockStation;
	private Queue<Package> tasks;
	private Queue<Package> tasksNotDelivered;
	private  ArrayList<Package> tasksDelivered;
	private int nbTaskNotDeliveredEvent = 0;
	private int index = 0;
	private int charge ;
	private boolean finishedWorkEvent = false;
	public static int idcontrol = 0;
	
	public DeliverDrone(ContinuousSpace<Object> space, Grid<Object> grid, int charge) {
		super(space, grid, charge);
	    dejaTrouvePackage = false;
	    this.hasTask = false;
	    this.charge = charge;
	    tasks = new LinkedList<Package>();
	    tasksNotDelivered = new LinkedList<Package>();
	    tasksDelivered = new  ArrayList<Package>();
	    lisOfDockStation = new ArrayList<DockStation>();
	    
	}
	public DeliverDrone() {super();};
	// method that implement the functional behavior of the drone
	// it is called each 1 second
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority=10)
	public void doTask()
	{
		
		
		
		
		/*
		if(true)
		{
			return;
		}*/
		
		if(charge>10)
		{	

			if(hasTask && !dejaTrouvePackage)
			{
					if(hasArrived(space.getLocation(task)))
					{
						dejaTrouvePackage = true;
					}
					else
					{
						move(space.getLocation(task));
						charge--;
					}
			}
			else
			{	

				if(!tasks.isEmpty() && !hasTask)
				{
					//task = tasks.remove();
					task = getNewTask();
					task.setTaken(true);
					hasTask = true;
					System.out.println("nouveau package, priority = " + task.getPriority());
				}
			}
			
			if(dejaTrouvePackage)
			{

				if(hasArrived(task.getDestinationCoord()))
				{
					// communiquer la centrale pour dire que plus un package delivré
					task.setIsDelivered(true);
					// add to list of task delivered
					tasksDelivered.add(task);
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
			// notify the All Drones that exists task not delivered because of charge
			if(hasTask)
			{
				tasksNotDelivered.add(task);
				nbTaskNotDeliveredEvent++;
				task.setIsDelivered(false);
				task.setTaken(false);
				//System.out.println("Triger l'evenement package");
				task = null;
				hasTask=false;
				dejaTrouvePackage = false;
			}
			// find Dockstation to charge
			//Get the nearest dockstation position
			NdPoint nearestDockPos = findDockStation(); 
			// if the has arrived at the dockstation, charge the drone
			if(hasArrived(nearestDockPos))
			{
				charge = 400;
			}
			else
			{
				// if not arrived at the dockstation, continue looking for the dockstation
				move(nearestDockPos);
			}	
		}
		
		if(tasks.size() <=0)
		{
			// notify all drones that I have finished with my tasks
			finishedWorkEvent = true;
			System.out.println("Fire event task finished");
		}
		// Test if all the task are done
			// Fire the finishedWorkEvent to the task controller
		// When is that all the task are done for a particular drone
		
	
	}
	
	/*
	// method that move the Drone to a desired location on the scene(screen), we just need to give in the location
	// This method is used to move the to the building where the package will be delivered
	@Override
	public void move(GridPoint pt)
	{
		if (!pt.equals(grid.getLocation(this )) ) {
			
				//turn(pt);
				NdPoint  myPoint = space.getLocation(this);
				
				NdPoint  otherPoint = new  NdPoint(pt.getX(), pt.getY ());
				
				double  angle = SpatialMath.calcAngleFor2DMovement(space ,myPoint , otherPoint );
				
				space.moveByVector(this , 1, angle , 0);
				
				myPoint = space.getLocation(this);
				
				grid.moveTo(this , (int)myPoint.getX(), (int)myPoint.getY ());
			
		}

	}*/
	
	//test method
	// method that move the Drone to a desired location on the scene(screen), we just need to give in the location
	// This method is used to move the to the building where the package will be delivered
	//@Override
	public void move(NdPoint pt)
	{
		//this test method show how to update a drone position using new physics.
		
		System.out.print("Drone "+this.getId());
			
		//retrieve the time that we will provide to the update drone function		
		double frametime=GlobalParameters.frameTime;
		//double tickdelay=RunEnvironment.getInstance().getScheduleTickDelay();
		double time=frametime;//*tickdelay/1000.0;
		
		NdPoint  targetpoint = new  NdPoint(pt.getX(), pt.getY (), 100);//arbitrary z
		System.out.print(" targetpos : "+targetpoint);
		
		Vect3 tarp=UtilityFunctions.NdPointToVect3(targetpoint);
		//gives the target position to the drone brain (the brain will be improved to find the best path, but for the moment it only computes a direction)
		this.getBrain().setTargetPosition(tarp);
		
		this.updateMe(time);//make the drone think/decide and update the drone state (battery level, speed...) and position
		
		Vect3 newDronePos=this.getPosition();
		System.out.print(" actualpos : "+newDronePos.toStringLen(30, 3));
		
		space.moveTo(this, newDronePos.getX(), newDronePos.getY(), newDronePos.getZ());//3D //update the drone position in the repast continuous space
		
		NdPoint newDronePoint = space.getLocation(this);
		
		//updates the grid
		grid.moveTo(this , (int)newDronePoint.getX(), (int)newDronePoint.getY ());
		
		System.out.print(" batterylevel : "+this.getBatteryLevelRelative());
		System.out.println("");
		
	

	}
	

	
	/*
	private boolean hasArrived(GridPoint pt)
	{
		GridPoint actualLocation = grid.getLocation(this);
		double distance = Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
		if(distance <= 2 && distance >=0)	{
			//System.out.println("Arrivé au Building");
			return true;
		}
		
		return false;
	}*/
	
	/*
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
			if((int)myPoint.getX() == (int)otherPoint.getX() && (int)myPoint.getY() == (int)otherPoint.getY())
			{
				dejaTrouvePackage = true;
			} 
			
		}
		else
		{
			//GridPoint actualLocation = grid.getLocation(this);
			//double distance = Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
			//if(distance < 1)	{
			//	System.out.println("Arrivé au Package");
			//	dejaTrouvePackage = true;
			//}
		}
	}*/
	
	
	@Override
	public NdPoint findDockStation()
	{

		double nearest=Double.MAX_VALUE;
		NdPoint nearestPos = new NdPoint();
		NdPoint actualLocation = space.getLocation(this);
		double distance;
		
		for(DockStation ds : lisOfDockStation )
		{
			NdPoint pt = space.getLocation(ds);
			distance =  Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
			
			if(nearest > distance )
			{
				nearest = distance;
				nearestPos = pt;
				//System.out.println("Distance " + distance);
			}
		}
		
		return nearestPos;
	}

	public Package getNewTask()
	{
		Package p = closeEstPackage(tasks);
		// return the package with highest priority and most close to him, if there is one
		for(Package highPriorityPackage : getListHighPrioriotyPackage(tasks))
		{
			// update the closest
			p = closeEstPackage(getListHighPrioriotyPackage(tasks));
			// if this High Priority package is the closest one them, take this one, or look for another close one
			if(highPriorityPackage == p )
			{
				tasks.remove(highPriorityPackage);
				return highPriorityPackage;
			}
		}
		
		// if the are no package with High priority them the drone must pick the package 
		// that is more close to him to deliver.
		
		p = closeEstPackage(tasks);
		tasks.remove(p);
		
		return  p;
		//return tasks.remove();
	}

	Queue<Package> getListHighPrioriotyPackage(Queue<Package> listTask)
	{
		Queue<Package>  results = new LinkedList<Package>();
		for(Package p : tasks)
		{
			if((p.getPriority() == Priority.IMMEDIATE) )
			{
				results.add(p);
			}
		}
		return results;
	}
	// funcion to search for the closest package
	Package closeEstPackage(Queue<Package> listTask )
	{
		double nearest=1000.00;
		GridPoint nearestPos = new GridPoint();
		GridPoint actualLocation = grid.getLocation(this);
		double distance;
		Package closestPackage = null;
		for(Package pc : listTask )
		{
			GridPoint pt = grid.getLocation(pc);
			distance =  Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
			
			if(nearest > distance )
			{
				nearest = distance;
				nearestPos = pt;
				closestPackage = pc;
				//System.out.println("Distance " + distance);
			}
		}
		
		return closestPackage;
		
		
	}
	/**
	 * triggered when a package has been dropped during a delivery
	 */
	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "nbTaskNotDeliveredEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskNotDeliveredEvent()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		boolean swarm =GlobalParameters.swarmActivated;
		// test if all package are in  mode isDelivered=true
		if(swarm)
		{
			//chercher les package non delivr�, et les mettres dans une liste
			System.out.println("Queue not delivered changed");
			CentralController companyInfo = this.getCentralController();
			ArrayList<Package> lisOfPackage = companyInfo.getLisOfPackage();
			//Chercher les drones without task to do
			synchronized(this)
			{
						if(getTasksNotDelivered().size() > 0 && getTasks().size() <=0)
						{
							
							for( Package p: lisOfPackage)
							{
								//!tasksNotDelivered.contains(p) &&
								if( !p.isTaken() && !p.getIsDelivered() && !tasks.contains(p))
								{
									p.setTaken(true);
									tasks.add(p);
								}
							}
						}
				
			}
		}
		else
		{
			synchronized(this)
			{
				tasks.addAll(getTasksNotDelivered());
				tasksNotDelivered.clear();
			}
		}
	}
	
	
	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "finishedWorkEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskFinishedEvent()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		boolean swarm =GlobalParameters.swarmActivated;
		// test if all package are in  mode isDelivered=true
		if(swarm)
		{
			System.out.println("Queue not delivered changed");
			CentralController companyInfo = this.getCentralController();
			ArrayList<Package> lisOfPackage = companyInfo.getLisOfPackage();
			//Chercher les drones without task to do
			synchronized(this)
			{
						if(getTasksNotDelivered().size() > 0 && getTasks().size() <=0)
						{
							
							for( Package p: lisOfPackage)
							{
								//!tasksNotDelivered.contains(p) &&
								if( !p.isTaken() && !p.getIsDelivered() && !tasks.contains(p))
								{
									p.setTaken(true);
									tasks.add(p);
								}
							}
						}	
			}
		}
		else
		{
			synchronized(this)
			{
				// When sawrm is not activated, all packages in the taskNotDelivered Queue must be put to the 
				// Queue tasks, and removed from the  taskNotDelivered.
				tasks.addAll(getTasksNotDelivered());
				tasksNotDelivered.clear();
			}
		}
			
	}
	
	
	public ArrayList<Package> getTasksDelivered() {
		return tasksDelivered;
	}

	public void setTasksDelivered(ArrayList<Package> tasksDelivered) {
		this.tasksDelivered = tasksDelivered;
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
	
	
	public ArrayList<DockStation> getLisOfDockStation() {
		return lisOfDockStation;
	}

	public void setLisOfDockStation(ArrayList<DockStation> lisOfDockStation) {
		this.lisOfDockStation = lisOfDockStation;
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
	public GridPoint getGridPosition() {
		/*return drone position*/
		return new GridPoint();
	}

}
