package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import repast.simphony.space.continuous.NdPoint;

/**
 * 
 *
 */
public class CoordinatedInitialisationUtils {
	
	//create point lists for the buildings
	private List<NdPoint> buildingPoints;
	
	//create point lists for the trees
	private List<NdPoint> treePoints;
	
	//create point lists for the dockstation
	private List<NdPoint> dockstationPoints;
	
	/**
	 * initialize the point lists
	 */
	public CoordinatedInitialisationUtils() {
		intializeBuildingPoints();
		intializeTreePoints();
		intializeDockstationPoints();
	}
	
	// points for trees
	private void intializeTreePoints() {
		
		double z=100;
		
		treePoints = new ArrayList<>();
		treePoints.add(new NdPoint(49,12,z));
		treePoints.add(new NdPoint(42,10,z));
		treePoints.add(new NdPoint(45,18,z));
		treePoints.add(new NdPoint(50,88,z));
		treePoints.add(new NdPoint(55,83,z));
		treePoints.add(new NdPoint(10,60,z));
		treePoints.add(new NdPoint(13,50,z));
		treePoints.add(new NdPoint(17,56,z));
		treePoints.add(new NdPoint(71,46,z));
		treePoints.add(new NdPoint(71,54,z));
		treePoints.add(new NdPoint(79,46,z));
		treePoints.add(new NdPoint(79,54,z));
		treePoints.add(new NdPoint(125,33,z));
		treePoints.add(new NdPoint(118,34,z));
		treePoints.add(new NdPoint(111,33,z));
		treePoints.add(new NdPoint(104,34,z));
		treePoints.add(new NdPoint(127,18,z));
		treePoints.add(new NdPoint(122,18,z));
		treePoints.add(new NdPoint(115,18,z));
		treePoints.add(new NdPoint(108,19,z));
		treePoints.add(new NdPoint(101,18,z));
		treePoints.add(new NdPoint(134,85,z));
		treePoints.add(new NdPoint(138,85,z));
		treePoints.add(new NdPoint(142,87,z));
		treePoints.add(new NdPoint(110,90,z));
		treePoints.add(new NdPoint(115,87,z));
		treePoints.add(new NdPoint(120,92,z));	
		
	}
	
	// points for buildings
	private void intializeBuildingPoints() {
		
		double z=100;
		
		buildingPoints = new ArrayList<>();
		buildingPoints.add(new NdPoint(25,12,z));
		buildingPoints.add(new NdPoint(32,25,z));
		buildingPoints.add(new NdPoint(45,35,z));
		buildingPoints.add(new NdPoint(15,30,z));
		buildingPoints.add(new NdPoint(15,75,z));
		buildingPoints.add(new NdPoint(30,68,z));
		buildingPoints.add(new NdPoint(45,77,z));
		buildingPoints.add(new NdPoint(37,88,z));
		buildingPoints.add(new NdPoint(22,88,z));
		buildingPoints.add(new NdPoint(135,15,z));
		buildingPoints.add(new NdPoint(135,25,z));
		buildingPoints.add(new NdPoint(135,35,z));
		buildingPoints.add(new NdPoint(139,80,z));
		buildingPoints.add(new NdPoint(120,75,z));
		buildingPoints.add(new NdPoint(105,85,z));
		buildingPoints.add(new NdPoint(105,70,z));
		buildingPoints.add(new NdPoint(136,95,z));
		
	}
	//points for dockstation
	private void intializeDockstationPoints() {
		
		double z=100;
		
		dockstationPoints = new ArrayList<>();
		dockstationPoints.add(new NdPoint(25,12,z));
		dockstationPoints.add(new NdPoint(45,35,z));
		dockstationPoints.add(new NdPoint(45,77,z));
		dockstationPoints.add(new NdPoint(135,35,z));
		dockstationPoints.add(new NdPoint(120,75,z));
		dockstationPoints.add(new NdPoint(15,75,z));
		dockstationPoints.add(new NdPoint(136,95,z));
		dockstationPoints.add(new NdPoint(37,88,z));
		
	}
	
	// get the point of building
	public NdPoint getBuildingCoordinatedAt(int i) {
		if(i>=0 && i<buildingPoints.size()) {
			return buildingPoints.get(i);
		} else {
			return null;
		}
	}
	
	// get the point of tree
	public NdPoint getTreeCoordinatedAt(int i) {
		if(i>=0 && i<treePoints.size()) {
			return treePoints.get(i);
		} else {
			return null;
		}
	}
	
	
	// get the point of dockstation
	public NdPoint getDockstationCoordinatedAt(int i) {
		if(i>=0 && i<dockstationPoints.size()) {
			return dockstationPoints.get(i);
		} else {
			return null;
		}
	}
	
}
