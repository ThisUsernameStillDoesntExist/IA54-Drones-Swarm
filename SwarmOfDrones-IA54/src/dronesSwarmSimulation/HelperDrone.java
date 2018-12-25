package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class HelperDrone extends Drone{
	
	
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
	
	public HelperDrone(ContinuousSpace<Object> space, Grid<Object> grid, int charge) {
		super(space, grid, charge);
	    dejaTrouvePackage = false;
	    this.hasTask = false;
	    this.charge = charge;
	    tasks = new LinkedList<Package>();
	    tasksNotDelivered = new LinkedList<Package>();
	    tasksDelivered = new  ArrayList<Package>();
	    lisOfDockStation = new ArrayList<DockStation>();
	    
	}
	public HelperDrone() {};
	// method that implement the functional behavior of the drone
	// it is called each 1 second
	@Override
	@ScheduledMethod(start = 1, interval = 1)
	public void doTask()
	{
		
		if(charge>10)
		{	

			if(hasTask && !dejaTrouvePackage)
			{
					if(hasArrived(grid.getLocation(task)))
					{
						dejaTrouvePackage = true;
					}
					else
					{
						move(grid.getLocation(task));
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
			// notify the central
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
			GridPoint nearestDockPos = findDockStation(); 
			// if the has arrived at the dockstation, charge the drone
			if(hasArrived(nearestDockPos))
			{
				charge = 100;
			}
			else
			{
				// if not arrived at the dockstation, continue looking for the dockstation
				move(nearestDockPos);
			}	
		}
		
		if(tasks.size() <=0)
		{
			finishedWorkEvent = true;
			System.out.println("Fire event task finished");
		}
		// Test if all the task are done
			// Fire the finishedWorkEvent to the task controller
		// When is that all the task are done for a particular drone
	
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
				
				double  angle = SpatialMath.calcAngleFor2DMovement(space ,myPoint , otherPoint );
				
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
			//System.out.println("Arrivé au Building");
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
			if((int)myPoint.getX() == (int)otherPoint.getX() && (int)myPoint.getY() == (int)otherPoint.getY())
			{
				dejaTrouvePackage = true;
			} 
			
		}
		else
		{
			/*GridPoint actualLocation = grid.getLocation(this);
			double distance = Math.hypot(pt.getX()-actualLocation.getX(), pt.getY()-actualLocation.getY());
			if(distance < 1)	{
				System.out.println("Arrivé au Package");
				dejaTrouvePackage = true;
			}*/
		}
	}
	@Override
	public GridPoint findDockStation()
	{

		double nearest=1000.00;
		GridPoint nearestPos = new GridPoint();
		GridPoint actualLocation = grid.getLocation(this);
		double distance;
		
		for(DockStation ds : lisOfDockStation )
		{
			GridPoint pt = grid.getLocation(ds);
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
		
		for(Package p : tasks)
		{
			if(p.getPriority() == Priority.IMMEDIATE)
			{
				tasks.remove(p);
				
				return p;
			}
		}
		
		return tasks.remove();
	}
	
	
	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "nbTaskNotDeliveredEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskNotDeliveredEvent()
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
	
	
	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "finishedWorkEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskFinishedEvent()
	{
		// test if all package are in  mode isDelivered=true
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
	public GridPoint  getPosition() {
		/*return drone position*/
		return new GridPoint();
	}

}
