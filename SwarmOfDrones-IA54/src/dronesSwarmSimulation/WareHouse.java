package dronesSwarmSimulation;

import java.util.ArrayList;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

/**
 * 
 * @author adilson
 * 
 * this class represent the company where a package and drone belongs to
 * TODO : some part of the code need to be ajusted to work well when on the parameter window the user want more than one ware house
 */

public class WareHouse {
	private String name; 
	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	private ArrayList<Package> lisOfPackage;
	
	
	
	public  WareHouse(ContinuousSpace<Object> space, Grid<Object> grid, String name)
	{
		this.space = space;
		this.grid = grid;
		this.name = name;
		lisOfPackage = new ArrayList<Package>();
	}
	
	/**
	 * This method is used to calculate where the ware house is situated and to place all packages belonging to this ware house close to her
	 * @return 
	 */
	public void placePackages()
	{
		// get the location of the warehouse
		NdPoint pointWareHouse  = space.getLocation(this);//Only one warehouse for the moment
		
		//put all the packages on the warehouse and deslocate by 15 inch
		 int pos = -15;
		for(Package pa : lisOfPackage)
		{
			if(pos == 50)
			{
				pos = -15;
			}
			// process to put all package close to his warehouse
			space.moveTo(pa, pointWareHouse.getX()+pos, pointWareHouse.getY()-7, pointWareHouse.getZ());
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
