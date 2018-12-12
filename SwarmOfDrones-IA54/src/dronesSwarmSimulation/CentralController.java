package dronesSwarmSimulation;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

/* 
 * This class serve as a controller or Central Intelligence on the simulation
   He register all the building and package created on the scene, and randomly 
 	attach each package per building
 */


public class CentralController
{
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private ArrayList<Package> lisOfPackage;
	private ArrayList<Package> lisOfPackageNotDelivered;
	private int  nbOfPackageDelivered=0;
	private ArrayList<Building> lisOfBuilding;
	private ArrayList<DockStation> lisOfDockStation;
	private ArrayList<DeliverDrone> lisOfDrones;
	private ArrayList<Priority> lisOfPriority;
	private int   RF = 1; // 1KM de distance
	
	
	private Context<Object> context;
	
	public CentralController(ContinuousSpace<Object> space, Grid<Object> grid,Context<Object> context) {
		this.space = space;
		this.grid = grid;		
		this.context = context;
		lisOfPackage = new ArrayList<Package>();
		lisOfBuilding = new ArrayList<Building>();
		lisOfDrones = new  ArrayList<DeliverDrone>();
		lisOfPackageNotDelivered = new ArrayList<Package>();
		lisOfDockStation = new ArrayList<DockStation>();
		lisOfPriority = new ArrayList<Priority>();
		lisOfPriority.add(Priority.IMMEDIATE);
		lisOfPriority.add(Priority.LATER);
	}

	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "nbTaskNotDeliveredEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void taskNotDeliveredEvent()
	{
		//chercher les package non delivré, et les mettres dans une liste
		System.out.println("Queue not delivered changed");
		//Chercher les drones without task to do
		 for(DeliverDrone d : lisOfDrones)
		{ 
					if(d.getTasksNotDelivered().size() > 0 && d.getTasks().size() <=0)
					{
						
						for( Package p: d.getTasksNotDelivered())
						{
							if(!lisOfPackageNotDelivered.contains(p))
								lisOfPackageNotDelivered.add(p);
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
		int nbPackage = 0;
		for( Package p : lisOfPackage)
		{
			if(p.getIsDelivered()==false)
				nbPackage++;
			else
				nbOfPackageDelivered++;
		}
		
		if(nbPackage > 0)
		{
			System.out.println("Task are not finished yet, More " + nbPackage + " Packages to be delivered");
			ArrayList<DeliverDrone> newlisOfDrones = new  ArrayList<DeliverDrone>();
			//System.out.println(newlisOfPackage.size() + "Drones no livré");
			
			 for(DeliverDrone d : lisOfDrones)
				{ 
							if(d.getTasksNotDelivered().size() > 0 && d.getTasks().size() <=0)
							{
								newlisOfDrones.add(d);
								for( Package p: d.getTasksNotDelivered())
								{
									if(!lisOfPackageNotDelivered.contains(p))
										lisOfPackageNotDelivered.add(p);
								}
							}
					
				}
			
			//assign Task not done to drones without task to do
			if(newlisOfDrones.size() > 0)
			{
				System.out.println("New list of drones = " + newlisOfDrones.size());
				assignTask(lisOfPackageNotDelivered,newlisOfDrones);
			}
			lisOfPackageNotDelivered = new ArrayList<Package>();
		}	
		else
		{
			System.out.println("All task Are finished, Stop The simularion");
		}
		
	}
	
	
	
	public void registerTask()
	{
		// Create lists 
		for(Object obj : this.context)
		{
			// Create lists of package on the scene
			if(obj instanceof Package )
			{
				Package p = (Package)obj;
				// Arrange the priority randomly
				SimUtilities.shuffle(lisOfPriority, RandomHelper.getUniform());
				// assign priority to the packages
				p.setPriority(lisOfPriority.get(0));
				// Add the package to the liste
				lisOfPackage.add(p);
			}
			
			if(obj instanceof DockStation )
			{
				lisOfDockStation.add((DockStation)obj);
			}
			
			// Create lists of Buildings on the scene
			if(obj instanceof Building )
			{
				lisOfBuilding.add((Building)obj);
			}
			if(obj instanceof DeliverDrone )
			{
				lisOfDrones.add((DeliverDrone)obj);
			}

		}
		
		// Give list of DockStation to All Drones
		for(DeliverDrone d : lisOfDrones)
		{
			d.setLisOfDockStation(lisOfDockStation);
		}
		
		// Randomly ordered of building and package, to no be all the same task at the same building
		SimUtilities.shuffle(lisOfPackage, RandomHelper.getUniform());
		SimUtilities.shuffle(lisOfBuilding, RandomHelper.getUniform());

		
		
		// Package Recuperate  the location of buildings
		
		int countBuinding = 0;
		for(int i=0 ; i< lisOfPackage.size(); i++)
		{	
			// if whe have give each package to one building, and still have package without location to deliver
			// then we reinitiate the distributition of the left's package, this mean there will be a building with one or more 
			// package to be delivered
			if( countBuinding == lisOfBuilding.size())
			{
				countBuinding = 0;
			}
			
			// Get the location of a building
			GridPoint buildingLocation = grid.getLocation(lisOfBuilding.get(countBuinding));
			// Give the the destination of the package, using the building's location
			lisOfPackage.get(i).setDestinationCoord(buildingLocation);
			countBuinding++;
		}
		// divide the number of package to be distributed on each drone
		assignTask(lisOfPackage,lisOfDrones);
	}
	
	void assignTask(ArrayList<Package> lisOfPackage ,ArrayList<DeliverDrone> lisOfDrones)
	{
		// Bug one package assigned to more tha 1 Drone
			//Solution : Free the list the two queue of Package and give the new one
		int numberOfDrone = lisOfDrones.size();
		int numberOfPackage = lisOfPackage.size();
		int numberOfPackagePerDrone = numberOfPackage/numberOfDrone;
		// Give to All DeliverDrone on the scene the list of Package available,	
		int fromIndex = 0;
		int toIndex = numberOfPackagePerDrone  ;
		//System.out.println("from = " + fromIndex + " To = " + toIndex);
		/*DeliverDrone dd = new DeliverDrone();
		for(DeliverDrone d : lisOfDrones)
		{
				dd= d;
				d.setTasks(new LinkedList<Package>(lisOfPackage.subList(fromIndex, toIndex)));
				fromIndex = toIndex ;
				toIndex = fromIndex + numberOfPackagePerDrone;
				d.setTasksNotDelivered(new LinkedList<Package>());
	
		} 
		// if the number of packages are odd
		if(lisOfPackage.size()%2 != 0)
		{
			dd.getTasks().add(lisOfPackage.get(lisOfPackage.size()-1));
		}  */
		int countDrones = 0;
		for(int i=0 ; i < lisOfPackage.size(); i++ )
		{
			Package p = lisOfPackage.get(i);
			// reinitialize assign package to the first drone opn the list until the las drone
			if(countDrones == lisOfDrones.size())
			{
				countDrones =0;
			}
			
			lisOfDrones.get(countDrones).getTasks().add(p);
			lisOfDrones.get(countDrones).setTasksNotDelivered(new LinkedList<Package>());
			countDrones++; // next drone
		}
		
		for(DeliverDrone d : lisOfDrones)
		{
			System.out.println("Mys tasks are " + d.getTasks().size());
		}
		
	}
	
	// This section is the getters and setters of the private field of the class
	

	public int getNbOfPackageDelivered() {
		return nbOfPackageDelivered;
	}

	public void setNbOfPackageDelivered(int nbOfPackageDelivered) {
		this.nbOfPackageDelivered = nbOfPackageDelivered;
	}

	public Context<Object> getContext() {
		return context;
	}

	public void setContext(Context<Object> context) {
		this.context = context;
	}
	
	public ArrayList<Package> getLisOfPackage() {
		return lisOfPackage;
	}

	public void setLisOfPackage(ArrayList<Package> lisOfPackage) {
		this.lisOfPackage = lisOfPackage;
	}

	public ArrayList<Building> getLisOfBuilding() {
		return lisOfBuilding;
	}

	public void setLisOfBuilding(ArrayList<Building> lisOfBuilding) {
		this.lisOfBuilding = lisOfBuilding;
	}
	
	
	
	public ArrayList<DockStation> getLisOfDockStation() {
		return lisOfDockStation;
	}

	public void setLisOfDockStation(ArrayList<DockStation> lisOfDockStation) {
		this.lisOfDockStation = lisOfDockStation;
	}

	public int getRF() {
		return RF;
	}

	

	

	
}
