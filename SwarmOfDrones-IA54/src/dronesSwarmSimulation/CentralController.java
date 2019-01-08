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
import repast.simphony.space.continuous.NdPoint;
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
	private ArrayList<WareHouse> lisOfWareHouses;
	private ArrayList<Priority> lisOfPriority;
	private int   RF = 1; // 1KM de distance
	private Statistics stats;

	private Context<Object> context;
	
	
	public CentralController(ContinuousSpace<Object> space, Grid<Object> grid,Context<Object> context) {
		this.space = space;
		this.grid = grid;		
		this.context = context;
		lisOfPackage = new ArrayList<Package>();
		lisOfBuilding = new ArrayList<Building>();
		lisOfDrones = new  ArrayList<DeliverDrone>();
		//lisOfPackageNotDelivered = new ArrayList<Package>();
		lisOfDockStation = new ArrayList<DockStation>();
		lisOfWareHouses=new ArrayList<WareHouse>();
		lisOfPriority = new ArrayList<Priority>();
		lisOfPriority.add(Priority.IMMEDIATE);
		lisOfPriority.add(Priority.LATER);
	}

	// event to gather information of drones that have finished work or not
	/*
	@Watch(watcheeClassName = "dronesSwarmSimulation.DeliverDrone",
			watcheeFieldNames = "finishedWorkEvent",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void log()
	{
		// test if all package are in  Delivered
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
			// Log some informations to the console
			System.out.println("Task are not finished yet, More " + nbPackage + " Packages to be delivered");
			// to keep track of drones that have finished their work
			ArrayList<DeliverDrone> newlisOfDrones = new  ArrayList<DeliverDrone>();
	
			//control of drones without task to do
			if(newlisOfDrones.size() > 0)
			{
				System.out.println("New list of drones = " + newlisOfDrones.size());
			}
			lisOfPackageNotDelivered = new ArrayList<Package>();
		}	
		else
		{
			System.out.println("All task Are finished, Stop The simularion");
		}
		
	}  */
	
	
	/**
	 * distribute packages, dockstations, and assign tasks to drones
	 */
	public void registerTask()
	{
		// Create lists 
		populateLists(this.context);
		
		createStatistics();
		
		spawnPackages();
		
		// Give list of DockStation to All Drones
		for(DeliverDrone d : lisOfDrones)
		{
			d.setLisOfDockStation(lisOfDockStation);
			d.setCentralController(this); // set the company to later extract information from
		}
		
		// Randomly ordered of building and package, to no be all the same task at the same building
		//SimUtilities.shuffle(lisOfPackage, RandomHelper.getUniform());
		//SimUtilities.shuffle(lisOfBuilding, RandomHelper.getUniform());

		placePackages();
		
		assignPackagesToBuildings();
		
		// divide the number of package to be distributed on each drone
		assignTask(lisOfPackage,lisOfDrones);
	}
	
	private void assignPackagesToBuildings() {
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
	}

	/**
	 * Spawn packages on the scene
	 */
	private void spawnPackages() {
			for(Package p : lisOfPackage)
			{
				// Arrange the priority randomly
				SimUtilities.shuffle(lisOfPriority, RandomHelper.getUniform());
				// assign priority to the packages
				p.setPriority(lisOfPriority.get(0));
				// Add the package to the liste

			}
	}

	/**
	 * add context objects to the right list according to their type
	 * this allow easier retrieving of context objects by type
	 */
	private void populateLists(Context<Object> context)
	{
		for(Object obj : context)
		{
			//no else if, so an object could belong to more than one list : expected behavior ?
			
			if(obj instanceof Package )
			{
				lisOfPackage.add((Package)obj);
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
			
			if(obj instanceof WareHouse )
			{
				lisOfWareHouses.add((WareHouse)obj);
			}
		}
	}
	
	private void createStatistics()
	{
		stats=new Statistics(lisOfDrones.size(), this);
	}
	
	/**
	 * place packages in the warehouse
	 */
	private void placePackages()
	{
		NdPoint pointWareHouse  = space.getLocation(lisOfWareHouses.get(0));//Only one warehouse for the moment
		
		//put all the packages on the warehouse
		int pos = -50;
		for(Package pa : lisOfPackage)
		{
			if(pos == 0)
			{
				pos = -50;
			}
			space.moveTo(pa, (int)pointWareHouse.getX()+pos,(int)pointWareHouse.getY()-7);
			NdPoint pt = space.getLocation(pa);
			grid.moveTo(pa,(int)pt.getX(),(int)pt.getY());
			pos = pos +5;
		}
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
	
	
	
	public ArrayList<Package> getLisOfPackageNotDelivered() {
		return lisOfPackageNotDelivered;
	}

	public void setLisOfPackageNotDelivered(ArrayList<Package> lisOfPackageNotDelivered) {
		this.lisOfPackageNotDelivered = lisOfPackageNotDelivered;
	}

	public ArrayList<DeliverDrone> getLisOfDrones() {
		return lisOfDrones;
	}

	public void setLisOfDrones(ArrayList<DeliverDrone> lisOfDrones) {
		this.lisOfDrones = lisOfDrones;
	}

	public ArrayList<Priority> getLisOfPriority() {
		return lisOfPriority;
	}

	public void setLisOfPriority(ArrayList<Priority> lisOfPriority) {
		this.lisOfPriority = lisOfPriority;
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

	public Statistics getStats() {
		return stats;
	}
	
	

	

	

	
}
