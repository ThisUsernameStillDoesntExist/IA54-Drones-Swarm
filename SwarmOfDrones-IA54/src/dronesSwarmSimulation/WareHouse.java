package dronesSwarmSimulation;

import java.util.ArrayList;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

/// WareHouse
public class WareHouse {
	private String name; 
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private ArrayList<Package> lisOfPackage;
	
	
	// need method to insert the packages and others functionalities to drone come to search for packages
	public  WareHouse(ContinuousSpace<Object> space, Grid<Object> grid, String name)
	{
		this.space = space;
		this.grid = grid;
		this.name = name;
		lisOfPackage = new ArrayList<Package>();
	}
	
	
	public void placePackages()
	{
		NdPoint pointWareHouse  = space.getLocation(this);//Only one warehouse for the moment
		
		//put all the packages on the warehouse
		 int pos = -15;
		for(Package pa : lisOfPackage)
		{
			if(pos == 50)
			{
				pos = -15;
			}
			space.moveTo(pa, (int)pointWareHouse.getX()+pos,(int)pointWareHouse.getY()-7);
			NdPoint pt = space.getLocation(pa);
			grid.moveTo(pa,(int)pt.getX(),(int)pt.getY());
			pos = pos +5;
		}
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ContinuousSpace<Object> getSpace() {
		return space;
	}

	public void setSpace(ContinuousSpace<Object> space) {
		this.space = space;
	}

	public Grid<Object> getGrid() {
		return grid;
	}

	public void setGrid(Grid<Object> grid) {
		this.grid = grid;
	}

	public ArrayList<Package> getLisOfPackage() {
		return lisOfPackage;
	}

	public void setLisOfPackage(ArrayList<Package> lisOfPackage) {
		this.lisOfPackage = lisOfPackage;
	}
	
	
}
