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
	
	protected Drone thisDrone;//attached drone body
	protected DroneAI thisAI;//attached drone AI
	protected int id;
	protected boolean dejaTrouvePackage ;
	protected boolean hasTask;
	protected Package task;
	protected ArrayList<DockStation> lisOfDockStation;
	protected ArrayList<Drone> lisOfDrones;
	protected Queue<Package> tasks;
	protected Queue<Package> tasksNotDelivered;
	protected int nbOfDroppedPackages;
	protected  ArrayList<Package> tasksDelivered;
	protected int index = 0;
	protected DockStation targetDockStation;//where we want to recharge, null when no need to recharge
	//protected int charge=400;//temporary, remove this
	
	protected CentralController centralController;
	public static int idcontrol = 0;
	
	protected int nbTaskNotDeliveredEvent = 0;
	protected boolean finishedWorkEvent = false;
	protected boolean searchingForStation = false;
	
	
	public HighLevelDecision(Drone dronebody, DroneAI dai) {
		
		//this.centralController=cc;
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

	
	public void doTask()
	{
		DockStation nearestDockPos = findDockStation(); 
		
		if(thisDrone.isPluggedToStation())
		{
			if(thisDrone.getBatteryLevelRelative() > thisAI.getCharact().batteryEndChargeRelativeThreshold)
			{
				// after, we have to unplug from the station
				thisDrone.unplugFromStation();
				
				if(tasks.size() > 0)//if remaining tasks to do
				{
					thisDrone.takeOff();//motors starts and movement is possible
				}
				
			}
			else
			{
				return;//do nothing but charge
			}			
			
		}
		else if(thisDrone.getBatteryLevelRelative() >  thisAI.getCharact().batteryBeginChargeRelativeThreshold /*|| nearestDockPos==null*/)
		{	
			searchingForStation=false;
			if(hasTask && !dejaTrouvePackage)
			{
					if(hasArrived(task.getPosition()))
					{
						dejaTrouvePackage = true;
						thisDrone.pickPackage(task);
					}
					else
					{
						orderMoveDecision(task.getPosition());
						//charge--;
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
					UtilityFunctions.printConsoleLn("nouveau package, priority = " + task.getPriority());
				}
				
			}
			
			if(dejaTrouvePackage)
			{

				if(hasArrived(task.getDestinationCoord()))
				{
					thisDrone.dropPackage();
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
				//UtilityFunctions.printConsoleLn("Triger l'evenement package");
				task = null;
				hasTask=false;
				dejaTrouvePackage = false;
				nbOfDroppedPackages++;
			}
			
			searchingForStation=true;
			// find Dockstation to charge
			//Get the nearest dockstation position
			
			// if the has arrived at the dockstation, charge the drone
			if(nearestDockPos!=null)
			{
				if(hasArrived(nearestDockPos.getPosition()))
				{
					
					thisDrone.land();//motor stopped and battery do not discharge
					// charge my battery
					thisDrone.plugToStation(nearestDockPos);
					
					
					//UtilityFunctions.printConsoleLn("Pluged and unpluged");
				}
				else
				{
					// if not arrived at the dockstation, continue looking for the dockstation
					orderMoveDecision(nearestDockPos.getPosition());
				}	
			}
			
		}
		
		if(tasks.size() <=0)
		{
			// notify all drones that I have finished with my tasks
			finishedWorkEvent = true;
			UtilityFunctions.printConsoleLn("Fire event task finished");
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
		
		UtilityFunctions.printConsole("Drone "+thisDrone.getId());
		
		Vect3 newDronePos=this.getPosition();
		
		space.moveTo(this, newDronePos.getX(), newDronePos.getY(), newDronePos.getZ());//3D //update the drone position in the repast continuous space
		
		NdPoint newDronePoint = space.getLocation(this);
		
		//updates the grid
		grid.moveTo(this , (int)newDronePoint.getX(), (int)newDronePoint.getY ());
		

		UtilityFunctions.printConsole(" targetpos : "+targetpoint);
		UtilityFunctions.printConsole(" actualpos : "+newDronePos.toStringLen(30, 3));
		UtilityFunctions.printConsole(" batterylevel : "+thisDrone.getBatteryLevelRelative());
		UtilityFunctions.printConsoleLn("");
		
		
	

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
			//UtilityFunctions.printConsoleLn("Arrivé au Building");
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
			//	UtilityFunctions.printConsoleLn("Arrivé au Package");
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
					//UtilityFunctions.printConsoleLn("Distance " + distance);
				}
			}
		}
		
		//return nearestPos;
		return dock;
	}*/

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
				//UtilityFunctions.printConsoleLn("Distance " + distance);
			}
		}
		
		return closestPackage;
		
		
	}
	
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
		boolean swarm =GlobalParameters.swarmActivated;
		// test if all package are in  mode isDelivered=true
		if(swarm)
		{
			//chercher les package non delivr�, et les mettres dans une liste
			UtilityFunctions.printConsoleLn("Queue not delivered changed");
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
	
	
	@Watch(watcheeClassName = "dronesSwarmSimulation.HighLevelDecision",
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
			UtilityFunctions.printConsoleLn("Queue not delivered changed");
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

		//UtilityFunctions.printConsoleLn("Arrivé au Building");
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
