package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	/**
	 * attached Drone physical Body
	 * @param thisDrone
	 */
	protected Drone thisDrone;//attached drone body
	/**
	 * attached Drone Intelligence
	 * @param thisAI
	 */
	protected DroneAI thisAI;//attached drone AI
	/**
	 *unique id of the drone
	 * @param id
	 */
	protected int id;
	/**
	 * state to know when the drone found the package
	 * @param dejaTrouvePackage
	 */
	protected boolean dejaTrouvePackage ;
	/**
	 * state to know  if the drone has a package
	 * @param hasTask
	 */
	protected boolean hasTask;
	/**
	 * the current task that Drone is delivering
	 * @param task
	 */
	protected Package task;
	/**
	 * List of Dockstation and positon on the environnment
	 * @param lisOfDockStation
	 */
	protected ArrayList<DockStation> lisOfDockStation;
	/**
	 * List to put drones in conflic's resolution
	 * @param lisOfDrones
	 */
	protected ArrayList<Drone> lisOfDrones;
	/**
	 * list of task assigned to this drone
	 * @param tasks
	 */
	protected Queue<Package> tasks;
	/**
	 * list to conatins all package not delivered
	 * @param tasksNotDelivered
	 */
	protected Queue<Package> tasksNotDelivered;
	/**
	 * Number of package dropped by the drone used in graph mesure, when he has to charge
	 * @param nbOfDroppedPackages
	 */
	protected int nbOfDroppedPackages;
	/**
	 * list to conatins all delivered packages
	 * @param tasksDelivered
	 */
	protected  ArrayList<Package> tasksDelivered;
	/**
	 * 
	 * @param index
	 */
	protected int index = 0;
	/**
	 * The current dockstation that the drone his charging at 
	 * @param  targetDockStation
	 */
	protected DockStation targetDockStation;
	//protected int charge=400;//temporary, remove this
	/**
	 * object that has information about the environment
	 * @param centralController
	 */
	protected CentralController centralController;
	/**
	 * counter to assign unique id to drones
	 * @param idcontrol
	 */
	public static int idcontrol = 0;
	/**
	 * variable to triggered an event when the drone fail to deliver a package
	 * @param nbTaskNotDeliveredEvent
	 */
	protected int nbTaskNotDeliveredEvent = 0;
	/**
	 *  variable to triggered an event when the drone has finished his task
	 * @param finishedWorkEvent
	 */
	protected boolean finishedWorkEvent = false;
	/**
	 * state to know  if the drone is searching for dosckstaion
	 * @param searchingForStation
	 */
	protected boolean searchingForStation = false;
	
	
	public HighLevelDecision(Drone dronebody, DroneAI dai) {
		
		//Initialize all the property of drone
		
		this.thisDrone=dronebody;
		this.thisAI=dai;
		
	    dejaTrouvePackage = false;
	    this.hasTask = false;
	    tasks = new LinkedList<Package>();
	    tasksNotDelivered = new LinkedList<Package>();
	    tasksDelivered = new  ArrayList<Package>();
	    lisOfDockStation = new ArrayList<DockStation>(); 
	    nbOfDroppedPackages=0;
	    
	    
	}
	
	/**
	 * function that implement the fundamental behavior of a DeliveryDrone
	 * @method  doTask
	 */
	
	public void doTask()
	{
		DockStation nearestDockPos = findDockStation(); 
		// test to know if the drone is plugued in station charging
		if(thisDrone.isPluggedToStation())
		{
			//
			if(thisDrone.getBatteryLevelRelative() > thisAI.getCharact().batteryEndChargeRelativeThreshold)
			{
				// after, we have to unplug from the station
				thisDrone.unplugFromStation();
				thisDrone.takeOff();//motors starts and movement is possible
			}
			else
			{
				return;//do nothing but charge
			}			
			
		}
		else if(thisDrone.getBatteryLevelRelative() >  thisAI.getCharact().batteryBeginChargeRelativeThreshold || nearestDockPos==null)
		{	// if the drone has charge, them do not look for dosckstation
			searchingForStation=false;
			
			if(hasTask && !dejaTrouvePackage)
			{	
				// if found the task 
					if(hasArrived(task.getPosition()))
					{
						//take the package 
						dejaTrouvePackage = true;
						thisDrone.pickPackage(task);
					}
					else
					{
						//go search the task
						orderMoveDecision(task.getPosition());
						//charge--;
					}
			}
			else
			{	
				// if the list of the tasks is not empty and there isn't a current task
				if(!tasks.isEmpty() && !hasTask)
				{
					//task = tasks.remove();
					// get new task from the list 
					task = getNewTask();
					// change the state of the task, so that others drone don't bother with that task
					task.setTaken(true);
					// change thge state, to say that he has a new task
					hasTask = true;
				}
				
			}
			// test if his has found the package
			if(dejaTrouvePackage)
			{

				if(hasArrived(task.getDestinationCoord()))
				{
					/*
					 *  if he has found the building were the package has to be delivered 
					 *  them he drop the package close to the building
					 */
					thisDrone.dropPackage();
					/*
					 *  change the state of the task, to know that the task has been delivered
					 */
					task.setIsDelivered(true);
					// add to list of task delivered
					tasksDelivered.add(task);
					// change the states to find another task
					hasTask = false;
					dejaTrouvePackage = false;	
				}
				else
				{
						orderMoveDecision(task.getDestinationCoord());
						//TODO : Do it with position instead of space and grid
						//this.getTask().move(thisDrone);
						//charge--;
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
				nbOfDroppedPackages++;
			}
			
			searchingForStation=true;
			// find Dockstation to charge
			//Get the nearest dockstation position
			
			// if the has arrived at the dockstation, charge the drone
			
			if(hasArrived(nearestDockPos.getPosition()))
			{
				
				thisDrone.land();//motor stopped and battery do not discharge
				// charge my battery
				thisDrone.plugToStation(nearestDockPos);
				
				
				//System.out.println("Pluged and unpluged");
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
	 * 
	 * negociate with other drones for a dock station, and return true if we have the priority 
	 * @param nearestDockPos
	 * @return
	 */
	protected boolean getNegotiationResultForDockStation(DockStation ds) {
		
		double myvalue=getWishValueForDockStation(ds);
		
		for(Drone d : lisOfDrones)
		{
			if(d.getBrain().getHld().getWishValueForDockStation(ds)>myvalue) return false;
		}

		return true;//my value is superior to all other drones values
	}


	/**
	 * decide to move to a position
	 * @param pt
	 */
	public void orderMoveDecision(Vect3 pt)
	{
		setTargetPosition(pt.copy());
	}
	
	/*
	protected boolean hasArrived(GridPoint pt)
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
	
	
	protected void setTargetPosition(Vect3 tarpos) {
		
		thisDrone.getBrain().setTargetPosition(tarpos);
		
	}


	/**
	 * find the best available dockstation, null if none
	 * @return
	 */
	public DockStation findDockStation()
	{

		List<DockStation> ads=getAvailableDockstations();
		
		Collections.sort(ads, WorldObject.getCompByDistanceFrom(thisDrone.getPosition()));//sorted by distance
		
		for(DockStation ds : ads)//browse through dockstation from nearest to farthest till we gain access to one (free station or successfull negotiation)
		{
			if(getNegotiationResultForDockStation(ds))//successfull negociation
			{
				return ds;
			}
		}

		return null;
	}
	
	protected List<DockStation> getAvailableDockstations()
	{
		List<DockStation> res=new ArrayList<DockStation>();
		
		for(DockStation ds : lisOfDockStation )
		{ 
			if(!ds.isBusy())
			{
				res.add(ds);
			}
		}
		
		return res;
	}
	
	/*
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
	}*/

	/**
	 * find the most important package to be delivered based on priority, distance etc
	 * @return
	 */
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

	/**
	 * get the lists of high priority package if there is one or more
	 * @param Queue<Package> listTask
	 * @return Queue<Package>
	 */
	Queue<Package> getListHighPrioriotyPackage(Queue<Package> listTask)
	{
		Queue<Package>  results = new LinkedList<Package>();
		// find all package with priority immediate
		for(Package p : tasks)
		{
			if((p.getPriority() == Priority.IMMEDIATE) )
			{
				results.add(p);
			}
		}
		return results;
	}
	/**
	 * get the closest package to the current drone 
	 * @param Queue<Package> listTask
	 * @return Queue<Package>
	 */
	Package closeEstPackage(Queue<Package> listTask )
	{
		// we suppose the nearest package is infinite distance
		double nearest=1000.00;
		Vect3 nearestPos = new Vect3();
		// get the actual location os the drone
		Vect3 actualLocation = this.thisDrone.getPosition();
		// variable to locate the shortest current distance
		double distance;
		Package closestPackage = null;
		// find the package with the shortest distance
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
	 * event  triggered  when the drone is looking for DockSation
	 * @param DockStation dst
	 * @return 
	 */
	@Watch(watcheeClassName = "dronesSwarmSimulation.HighLevelDecision",
			watcheeFieldNames = "searchingForStation",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void searchingForStation(DockStation dst)
	{		
		if(!searchingForStation) return;
		
		//TODO
	}
	
	
	/**
	 * triggered when a package has been dropped during a delivery
	 */
	@Watch(watcheeClassName = "dronesSwarmSimulation.HighLevelDecision",
			watcheeFieldNames = "nbTaskNotDeliveredEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskNotDeliveredEvent()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		// recuperate the swarm mode from the parameters
		boolean swarm =GlobalParameters.swarmActivated;
		
		// if the swam mode is activated them an algorithm of collaboration is executed
		if(swarm)
		{
			//chercher les package non delivr�, et les mettres dans une liste
			System.out.println("Queue not delivered changed");
			CentralController companyInfo = this.getCentralController();
			ArrayList<Package> lisOfPackage = companyInfo.getLisOfPackage();
			//search for drones without task to do
			synchronized(this)
			{			
						if(getTasksNotDelivered().size() > 0 && getTasks().size() <=0)
						{
							
							for( Package p: lisOfPackage)
							{
								
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

			/*
			 *  if the swam mode is not  activated the drone has just to take all task not delivered and 
			 *  add to the list of tasks to be delivered
			 */
			synchronized(this)
			{
				tasks.addAll(getTasksNotDelivered());
				tasksNotDelivered.clear();
			}
		}
	}
	
	/**
	 * triggered when a drone has finished all task, to go find others drone that have not finished yet
	 */
	@Watch(watcheeClassName = "dronesSwarmSimulation.HighLevelDecision",
			watcheeFieldNames = "finishedWorkEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskFinishedEvent()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		// recuperate the swarm mode from the parameters
		boolean swarm =GlobalParameters.swarmActivated;
		// if the swam mode is activated them an algorithm of collaboration is executed
		if(swarm)
		{
			System.out.println("Queue not delivered changed");
			CentralController companyInfo = this.getCentralController();
			ArrayList<Package> lisOfPackage = companyInfo.getLisOfPackage();
			//search for drones without task to do
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
		


	public ArrayList<Drone> getLisOfDrones() {
		return lisOfDrones;
	}


	public void setLisOfDrones(ArrayList<Drone> lisOfDrones) {
		this.lisOfDrones = lisOfDrones;
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


	public DockStation getTargetDockStation() {
		return targetDockStation;
	}

	/**
	 * return a value defining the priority of this drone to recharge on this station. higher means more chance of winning the negotiation
	 * @param ds
	 * @return
	 */
	public double getWishValueForDockStation(DockStation ds)
	{
		double distfromstation=thisDrone.getPosition().dist(ds.getPosition());
		double batlevel=thisDrone.getBatteryLevelRelative();
		double weight=distfromstation*batlevel;
		
		double val=10;
		
		if(task!=null)
		{
			val*=thisAI.getCharact().hasTaskDecisionFactor;
		
			if(task.getPriority()==Priority.IMMEDIATE)
			{
				val*=thisAI.getCharact().packagePriorityDecisionFactor;
			}
		}
		
		
		if(weight==0)
		{
			return Double.MAX_VALUE;
		}
		
		return val/weight;
				
	}


	public int getNbOfDroppedPackages() {
		return nbOfDroppedPackages;
	}

	
	

}
