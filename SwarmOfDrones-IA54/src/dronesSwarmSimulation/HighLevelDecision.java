package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.utilities.UtilityFunctions;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * regroup vars and process used in high level decision making, as opposed to propeller and
 * motor management
 * the essential multi-agent part is here, with communication and task management
 * 
 * 
 * @author Francis
 *
 */
public class HighLevelDecision {
	
	//TODO : maybe refactor this, and create separate modules, this is a fast adaptation

	/*public boolean dejaTrouvePackage;
	public boolean hasTask;
	public Package task;
	public ArrayList<DockStation> lisOfDockStation;
	public Queue<Package> tasks;
	public Queue<Package> tasksNotDelivered;
	public ArrayList<Package> tasksDelivered;
	public int nbTaskNotDeliveredEvent = 0;
	public int index = 0;
	public boolean finishedWorkEvent = false;
	public static int idcontrol = 0;*/
	
	private Drone thisDrone;//attached drone body
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
	private CentralController centralController;
	public static int idcontrol = 0;
	
	public HighLevelDecision(Drone dronebody) {
		
		//this.centralController=cc;
		this.thisDrone=dronebody;
		
	    dejaTrouvePackage = false;
	    this.hasTask = false;
	    this.charge = charge;
	    tasks = new LinkedList<Package>();
	    tasksNotDelivered = new LinkedList<Package>();
	    tasksDelivered = new  ArrayList<Package>();
	    lisOfDockStation = new ArrayList<DockStation>(); 
	    
	    
	}

	
	public void doTask()
	{
	
		if(charge >= 10)
		{	

			if(hasTask && !dejaTrouvePackage)
			{
					if(hasArrived(task.getPosition()))
					{
						dejaTrouvePackage = true;
					}
					else
					{
						orderMoveDecision(task.getPosition());
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
						orderMoveDecision(task.getDestinationCoord());
						//TODO : Do it with position instead of space and grid
						this.getTask().move(thisDrone);
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
			DockStation nearestDockPos = findDockStation(); 
			// if the has arrived at the dockstation, charge the drone
			if(hasArrived(nearestDockPos.getPosition()))
			{
				
				charge = 400;
			}
			else
			{
				// if not arrived at the dockstation, continue looking for the dockstation
				orderMoveDecision(nearestDockPos.getPosition());
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
	//test method
	// method that move the Drone to a desired location on the scene(screen), we just need to give in the location
	// This method is used to move the to the building where the package will be delivered
	//@Override
	public void move(NdPoint pt)
	{
		//this test method show how to update a drone position using new physics.
			
		//retrieve the time that we will provide to the update drone function		
		double frametime=GlobalParameters.frameTime;
		//double tickdelay=RunEnvironment.getInstance().getScheduleTickDelay();
		double time=frametime;//*tickdelay/1000.0;
		
		NdPoint  targetpoint = new  NdPoint(pt.getX(), pt.getY (), 100);//arbitrary z
		
		System.out.print("Drone "+thisDrone.getId());
		
		Vect3 newDronePos=this.getPosition();
		
		space.moveTo(this, newDronePos.getX(), newDronePos.getY(), newDronePos.getZ());//3D //update the drone position in the repast continuous space
		
		NdPoint newDronePoint = space.getLocation(this);
		
		//updates the grid
		grid.moveTo(this , (int)newDronePoint.getX(), (int)newDronePoint.getY ());
		

		System.out.print(" targetpos : "+targetpoint);
		System.out.print(" actualpos : "+newDronePos.toStringLen(30, 3));
		System.out.print(" batterylevel : "+thisDrone.getBatteryLevelRelative());
		System.out.println("");
		
		
	

	}*/
	
	
	/**
	 * decide to move to a position
	 * @param pt
	 */
	public void orderMoveDecision(Vect3 pt)
	{
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
	
	
	public DockStation findDockStation()
	{

		double nearest=Double.MAX_VALUE;
		Vect3 nearestPos = new Vect3();
		DockStation dock = lisOfDockStation.get(0);
		Vect3 actualLocation = thisDrone.getPosition();
		double distance;
		
		for(DockStation ds : lisOfDockStation )
		{ 
			
			Vect3 pt=ds.getPosition();
			distance =  actualLocation.dist(pt);
			
			if(!ds.isBusy())
			{
				//TODO : use dedicated method for that, factorize
				if(nearest > distance )
				{
					nearest = distance;
					nearestPos = pt;
					dock = ds;
					//System.out.println("Distance " + distance);
				}
			}
		}
		
		//return nearestPos;
		return dock;
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
		Vect3 nearestPos = new Vect3();
		Vect3 actualLocation = this.thisDrone.getPosition();
		double distance;
		Package closestPackage = null;
		for(Package pc : listTask )
		{
			Vect3 pt = pc.getPosition();
			distance =  actualLocation.dist(pt);
			
			//TODO : use dedicated method for that
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
	


	public void communicate() {
		/*Communication between 2 or more drone or with other smart things */
	}
	
	public void negociate() {
		/*negotiate between 2 or more drones */
	}
	
	public CentralController getCentralController() {
		return centralController;
	}
	
	/**
	 * true if the drone arrived to another object
	 * @param w
	 * @return
	 */
	protected boolean hasArrived(WorldObject w)
	{
		return thisDrone.isOver(w);

		//System.out.println("Arrivé au Building");
	}
	
	/**
	 * true if the drone has arrived to this point
	 * @param ndp
	 * @return
	 */
	protected boolean hasArrived(Vect3 p)
	{
		return thisDrone.isInRange(p);
	}


	public void setCentralController(CentralController centralController) {
		this.centralController = centralController;
	}
	
	
	

}
