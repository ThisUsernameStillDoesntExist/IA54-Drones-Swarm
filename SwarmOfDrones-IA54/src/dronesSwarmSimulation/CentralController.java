package dronesSwarmSimulation;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dronesSwarmSimulation.physics.PhysicsEngine;
import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.utilities.UtilityFunctions;
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
	private ArrayList<Drone> lisOfDrones;
	private ArrayList<WareHouse> lisOfWareHouses;
	private ArrayList<Priority> lisOfPriority;
	private ArrayList<WorldObject> lisOfWorldObject;
	private int   RF = 1; // 1KM de distance
	private Statistics stats;
	private PhysicsEngine SIMENGINE;//dont't mess with that, TODO : put this somewhere else, improve architecture

	private Context<Object> context;
	
	
	public CentralController(ContinuousSpace<Object> space, Grid<Object> grid,Context<Object> context) {
		this.space = space;
		this.grid = grid;		
		this.context = context;
		lisOfPackage = new ArrayList<Package>();
		lisOfBuilding = new ArrayList<Building>();
		lisOfDrones = new  ArrayList<Drone>();
		//lisOfPackageNotDelivered = new ArrayList<Package>();
		lisOfDockStation = new ArrayList<DockStation>();
		lisOfWareHouses=new ArrayList<WareHouse>();
		lisOfPriority = new ArrayList<Priority>();
		lisOfPriority.add(Priority.IMMEDIATE);
		lisOfPriority.add(Priority.LATER);
		lisOfWorldObject=new ArrayList<WorldObject>();
	}

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
		for(Drone d : lisOfDrones)
		{
			d.getBrain().getHld().setLisOfDockStation(lisOfDockStation);
			d.getBrain().getHld().setLisOfDrones(lisOfDrones);
			d.getBrain().getHld().setCentralController(this); // set the central controller to later extract information from
		}
		
		//Randomly ordered of building and package, to no be all the same task at the same building
		//SimUtilities.shuffle(lisOfPackage, RandomHelper.getUniform());
		//SimUtilities.shuffle(lisOfBuilding, RandomHelper.getUniform());

		//placePackages(); WareHouse now they know how to place their packages 
		assignPackageToWareHouse(lisOfPackage);
		
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
					NdPoint buildingLocation = space.getLocation(lisOfBuilding.get(countBuinding));
					// Give the the destination of the package, using the building's location
					lisOfPackage.get(i).setDestinationCoord(UtilityFunctions.NdPointToVect3(buildingLocation));
					countBuinding++;
					//System.out.println("building : " + buildingLocation.getX() + " ; " + buildingLocation.getY() +" ; ");
				}
	}

	/**
	 * Spawn packages on the scene
	 */
	private void spawnPackages() {
			for(Package p : lisOfPackage)
			{
				// Arrange the priority randomly
				/* decomment this line, if you ramndonmly behavior of priority packages
				 * 
				 * SimUtilities.shuffle(lisOfPriority, RandomHelper.getUniform());
				*/
				 
				// assign priority to the packages
				p.setPriority(lisOfPriority.get(0));
				// Add the package to the liste
			}
	}
	
	/*
	 * 
	 *  Funtion tha assign the packages of specific company/ WareHouse
	 *  Every PAckage Must have a identification of what Company/WareHouse he belongs to
	 */
	
	private void assignPackageToWareHouse(ArrayList<Package> lisOfPackage)
	{
		
		// search for a where house
		for(Package pa : lisOfPackage)
		{
			// randomly reorder the list to pick one warehouse
			/*
			 * decoment this line if you want tha warehouse take package randomly
			 * SimUtilities.shuffle(lisOfWareHouses, RandomHelper.getUniform());
			 * 
			 */
			
			/* pick the first position and assign one drone on their list
			 * the warehouse on the first position might change if there is more than one warehouse
			*/
			WareHouse wh = lisOfWareHouses.get(0);
			// insert the name of the ware house on the package
			pa.setWareHouseName(wh.getName());
			// add the package on the warehouse list
			wh.getLisOfPackage().add(pa);
			
		}
			//no else if, so an object could belong to more than one list : expected behavior ?
			// it will not happen 
		// after assign each package to their warehouse, we need to call place package on each warehouse
		
		for(WareHouse wh : lisOfWareHouses)
		{
			// call place packages 
			wh.placePackages();
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
			
			
			if(obj instanceof DockStation )
			{
				lisOfDockStation.add((DockStation)obj);
			}
			/*
			if(obj instanceof Package )
			{
				lisOfPackage.add((Package)obj);
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
			*/
			if(obj instanceof WareHouse )
			{
				lisOfWareHouses.add((WareHouse)obj);
			}
			
			if(obj instanceof WorldObject)
			{
				lisOfWorldObject.add((WorldObject) obj);
			}
		}
	}
	
	private void createStatistics()
	{
		stats=new Statistics(lisOfDrones.size(), this);
	}
	
	
	
	void assignTask(ArrayList<Package> lisOfPackage ,ArrayList<Drone> lisOfDrones)
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
			// reinitialize assign package to the first drone on the list until the las drone
			if(countDrones == lisOfDrones.size())
			{
				countDrones =0;
			}
			
			lisOfDrones.get(countDrones).getBrain().getHld().getTasks().add(p);
			lisOfDrones.get(countDrones).getBrain().getHld().setTasksNotDelivered(new LinkedList<Package>());
			countDrones++; // next drone
		}
		
		for(Drone d : lisOfDrones)
		{
			System.out.println("Mys tasks are " + d.getBrain().getHld().getTasks().size());
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

	public ArrayList<Drone> getLisOfDrones() {
		return lisOfDrones;
	}
	

	public ArrayList<WareHouse> getLisOfWareHouses() {
		return lisOfWareHouses;
	}

	public void setLisOfWareHouses(ArrayList<WareHouse> lisOfWareHouses) {
		this.lisOfWareHouses = lisOfWareHouses;
	}

	public void setLisOfDrones(ArrayList<Drone> lisOfDrones) {
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

	public void createSIMENGINE() {
		if(lisOfWorldObject.size()==0) System.out.println("SIMENGINE : No object to simulate");
		
		this.SIMENGINE=new PhysicsEngine(space, grid, lisOfWorldObject);
		this.context.add(this.SIMENGINE);
	}

	
	

	

	

	
}
