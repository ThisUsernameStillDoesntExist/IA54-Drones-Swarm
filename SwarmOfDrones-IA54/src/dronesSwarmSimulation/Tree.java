/**
 * 
 */
package dronesSwarmSimulation;

import dronesSwarmSimulation.physics.WorldObject;
import dronesSwarmSimulation.physics.collisions.colliders.Collider;
import dronesSwarmSimulation.utilities.Vect3;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/**
 * 
 * @author adilson
 * 
 *This class the trees that are on the environment
 *
 */
public class Tree extends WorldObject {

	protected ContinuousSpace<Object> space;
	protected Grid<Object> grid;
	/**
	 * @param space
	 * @param grid
	 */
	public Tree(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	@Override
	public boolean collideWith(WorldObject w) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected Vect3 getSpecificAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void todoOnUpdate(double time) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public WorldObject copy() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected Collider createSpecificCollider() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void todoOnAfterUpdate(double time) {
		// TODO Auto-generated method stub
		
	}
	
	
}
