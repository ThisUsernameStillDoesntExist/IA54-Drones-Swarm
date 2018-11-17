package dronesSwarmSimulation;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

public class DroneBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("SwarmOfDrones-IA54");
		
		//-------------------------------Creation and limitation of the Screen(Scene) space(Infinite coordinates system) -----------------------------------------------\\
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 150, 100);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
				new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 150, 100));
		
		//-------------------------------Creation of the Agents setup on the Screen-----------------------------------------------\\
		
		int nombreDrone = 5;
		int charge = 200;

		/*Define the number of DeliverDrone */
		for(int i = 0; i < nombreDrone; i++){
			context.add(new DeliverDrone(space, grid,charge));
		} 
		// package
		// creation of the packages 
		int nombrepack = 5;
		/*Define the number of Package*/
		for (int i = 0; i < nombrepack ; i++) {
			context.add(new Package(space, grid));
		}
	
		// creation of the City/Street 
		context = setUpTheCity(context, grid, space);
		
		//-------------------------------- Positioning of the Agents on the grid system ( Finite system )---------------\\
		// counter to allow the package/drone to take the location of just one building at time
		//int nbBuildingToDeliver = 0; 
		for( Object obj : context )
		{
			NdPoint pt = space.getLocation ( obj );
			grid.moveTo(obj,(int)pt.getX(),(int)pt.getY());

		 }
		
		return context;
	}
	
	private Context<Object> setUpTheCity( Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space)
	{
		// creation of the Buildings, DockSations and Trees
				CoordinatedInitialisationUtils coordinatedInitialisationUtils = new CoordinatedInitialisationUtils();
				
				// 17 Buildings
				for (int i = 0; i < 17; i++) {
					Building b = new Building(space, grid);
					context.add(b);
					NdPoint coordinated = coordinatedInitialisationUtils.getBuildingCoordinatedAt(i);
					space.moveTo(b, coordinated.getX(), coordinated.getY());
				}
				
				// 27 Trees
				for (int i = 0; i < 27; i++) {
					Tree t = new Tree(space, grid);
					context.add(t);
					NdPoint coordinated = coordinatedInitialisationUtils.getTreeCoordinatedAt(i);
					space.moveTo(t, coordinated.getX(), coordinated.getY());
				}
				Parameters params = RunEnvironment.getInstance().getParameters();
				int dockstation = 8;
				if(dockstation > 8) {
					dockstation = 8;
				}
				// 8 Dockstations
				for (int i = 0; i < dockstation; i++) {
					DockStation d = new DockStation(space, grid);
					context.add(d);
					NdPoint coordinated = coordinatedInitialisationUtils.getDockstationCoordinatedAt(i);
					space.moveTo(d, coordinated.getX(), coordinated.getY());
				}
				
				return context;
				
	}
}
