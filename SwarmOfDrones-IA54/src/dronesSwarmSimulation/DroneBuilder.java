package dronesSwarmSimulation;

import com.jcraft.jsch.jce.Random;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.utilities.UtilityFunctions;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.PointTranslator;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.BouncyBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

public class DroneBuilder implements ContextBuilder<Object> {
/**
 * This method serve as the setup function of the simulation, all the object main to the resuts, all the parameters are recuperated here 
 * @author adilson
 * @return Context containing all the object on the simulation to be run by the system
 */
	@Override
	public Context build(Context<Object> context) {
		//Context of the current simulation
		context.setId("SwarmOfDrones-IA54");
		// initialize the global parameters that will be used for the simulation'(
		GlobalParameters.initParamsFromRepast();
		
		//-------------------------------Creation and limitation of the Screen(Scene) and  representation of the environment grid, space(Infinite coordinates system) -----------------------------------------------\\
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		
		Vect3 spacedims=GlobalParameters.spaceDimensions;
		PointTranslator ptranslator=new repast.simphony.space.continuous.InfiniteBorders<>();
		ptranslator.init(new Dimensions(spacedims.getX(), spacedims.getY(), spacedims.getZ()));
		// creation of the continuos space to reprersent the space where drones will move, to get real results
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), ptranslator, spacedims.getX(), spacedims.getY(), spacedims.getZ());
		// creation of another space of type Grid, to help us do neighborhood between agents of thge same type
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
				new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, (int)(spacedims.getX()), (int)(spacedims.getY())));
		
		//-------------------------------Creation of the Agents setup on the Screen-----------------------------------------------\\
		CentralController cc = new CentralController(space, grid, context);
		// get an object representing  the parameters set on the user interface
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		
		int nombreDrone = GlobalParameters.nbDeliveryDrones;
		int charge = GlobalParameters.initialCharge;

		/*Define the number of DeliverDrone  */
		for(int i = 0; i < nombreDrone; i++){
			double rg=1;
			if(GlobalParameters.differentInitBatteryLevels)
			{
				rg=0.5;
			}
			double randbatterylevel=(1-rg)*(1-Math.random())+rg;
			DeliverDrone d = new DeliverDrone(space, grid, GlobalParameters.initDronePosition, randbatterylevel);
			context.add(d);
			cc.getLisOfDrones().add(d);
		} 
		// package
		
		// creation of the packages 
		int nombrepack = GlobalParameters.nbPackages;
		/*Define the number of Package */
		for (int i = 0; i < nombrepack ; i++) {
			Package p = new Package(space, grid);
			context.add(p);
			cc.getLisOfPackage().add(p);
		}
		// creation of warehouse
		// get de warehouse name parameter
		String wareHouseName = GlobalParameters.warehouseName;
		
		WareHouse wareHouse = new WareHouse( space, grid, wareHouseName);
		context.add(wareHouse);
		NdPoint pointWareHouse = GlobalParameters.wareHousePostion;
		space.moveTo( wareHouse, pointWareHouse.getX(), pointWareHouse.getY(), pointWareHouse.getZ());
		// creation of the City/Street 
		context = setUpTheCity(context, grid, space,cc);
		
		//-------------------------------- Positioning of the Agents on the grid system ( Finite system )---------------\\
		// counter to allow the package/drone to take the location of just one building at time
		// put all the object on the grid to do neighborhood
		for( Object obj : context )
		{
			NdPoint pt = space.getLocation ( obj );
			
			grid.moveTo(obj,(int)pt.getX(),(int)pt.getY());

		 }
		
		// register all the object on the context to do mesure and statistcs, and assign task to drones
		cc.registerTask();
		context.add(cc);
		context.add(cc.getStats());//to allow scheduled calls on the refreshing method of statistics, and to retrieve values from it
		
		fromRepastSpaceToWorldObjectPosition(context, space);//copy the space randomseeded positions into objects positions
		
		cc.createSIMENGINE();//create physics engine
		
		return context;
	}
	
	/**
	 * this function transform all the the object on the context to objectwith caracteristiscs like in a real word ( 3D word)
	 * @return
	 */
	private void fromRepastSpaceToWorldObjectPosition(Context<Object> context, ContinuousSpace<Object> space) {

		for(Object o : context.getObjects(WorldObject.class))
		{
			if(o instanceof WorldObject)
			{
				NdPoint p=space.getLocation(o);
				//((WorldObject)o).setPosition(new Vect3(p.getX(), p.getY(), p.getZ()));
				((WorldObject)o).setPosition(new Vect3(p.getX(), p.getY(), 100));//test
			}
		}
	}
	
	/**
	 * This method serve as the initializer os the concrete environment, with buildings, trees station etc
	 * 
	 * @param context the current context of the simulation with all objects
	 * @param grid 
	 * @param space
	 * @param cc
	 * @return
	 */
	private Context<Object> setUpTheCity( Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space, CentralController cc )
	{
		// creation of the Buildings, DockSations and Trees
				CoordinatedInitialisationUtils coordinatedInitialisationUtils = new CoordinatedInitialisationUtils();
				
				// 17 Buildings
				for (int i = 0; i <GlobalParameters.nbOfBuildings; i++) {
					Building b = new Building(space, grid);
					context.add(b);
					NdPoint coordinated = coordinatedInitialisationUtils.getBuildingCoordinatedAt(i);
					//b.setPosition(UtilityFunctions.NdPointToVect3(coordinated));
					space.moveTo(b, coordinated.getX(), coordinated.getY(), coordinated.getZ());
					cc.getLisOfBuilding().add(b);
				}
				
				// 27 Trees
				for (int i = 0; i <GlobalParameters.nbOfTrees; i++) {
					Tree t = new Tree(space, grid);
					context.add(t);
					NdPoint coordinated = coordinatedInitialisationUtils.getTreeCoordinatedAt(i);
					//t.setPosition(UtilityFunctions.NdPointToVect3(coordinated));
					space.moveTo(t, coordinated.getX(), coordinated.getY(), coordinated.getZ());
				}
				Parameters params = RunEnvironment.getInstance().getParameters();
				int dockstation =GlobalParameters.nbDockstations;
				if(dockstation > 8) {
					dockstation = 8;
				}
				// 8 Dockstations
				for (int i = 0; i < dockstation; i++) {
					DockStation d = new DockStation(space, grid);
					context.add(d);
					NdPoint coordinated = coordinatedInitialisationUtils.getDockstationCoordinatedAt(i);
					//d.setPosition(UtilityFunctions.NdPointToVect3(coordinated));
					space.moveTo(d, coordinated.getX()+50, coordinated.getY(), coordinated.getZ());
				}
				
				return context;
				
	}
}
 