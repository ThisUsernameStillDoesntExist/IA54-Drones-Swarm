package dronesSwarmSimulation.physics;

import dronesSwarmSimulation.physics.collisions.colliders.Collider;
import dronesSwarmSimulation.utilities.Vect3;

//put position, speed and size in the collider only ?
public abstract class WorldObject {
	
	protected Collider collider;//colliding box (sphere or cuboid)
	protected Vect3 position;//center of the bounding box
	protected Vect3 speed;
	protected Vect3 size;//for a sphere this is the diameter	
	protected WorldObjectCharacteristics charact;
	protected double totalDistanceTravelled;//from beginning of simulation

	public WorldObject() {
		position=new Vect3();
		speed=new Vect3();
		size=new Vect3();
		collider=createSpecificCollider();
		charact=new WorldObjectCharacteristics();
		resetDistanceTravelled();
	}
	
	/**
	 * 
	 * @param position
	 * @param speed
	 * @param size
	 */
	public WorldObject(Vect3 position, Vect3 speed, Vect3 size, Collider c, WorldObjectCharacteristics wot) {
		this.position = position;
		this.speed = speed;
		this.size = size;
		this.collider=c;
		this.charact=wot;
		resetDistanceTravelled();
	}
	
	//we want a deep copy
	public WorldObject(WorldObject w) {
		
		position=new Vect3(w.position);
		speed=new Vect3(w.speed);
		size=new Vect3(w.size);
		collider=w.collider.copy();
		this.charact=w.charact;
		this.totalDistanceTravelled=w.totalDistanceTravelled;
	}
	
	//
	///**
	// * returns a new collider (sphere or cuboid) specific to this class.
	// * @return
	// */
	//protected abstract Collider getSpecificCollider();
	
	/**
	 * checks if a collision occurs, and applies necessary actions if this is the case (typically change position of both worldobjects)
	 * <br><br>
	 * Caution ! This affects both the calling WorldObject and the one in parameter, in order to compute only one intersection for two objects
	 * @param w
	 * @return true if a collision occured and was processed
	 */
	public abstract boolean collideWith(WorldObject w);
	
	/**
	 * Updates speed and position of the drone according to time.
	 * Also update the state of the drone (battery, etc).
	 * @param time
	 */
	public void updateMe(double time)
	{
		todoOnUpdate(time);
		updateSpeed(time);
		
		Vect3 dspeed=speed.getMultipliedBy(time);
		
		Vect3 newpos=position.getAdded(dspeed);//position+=speed*time
		
		totalDistanceTravelled+=position.dist(newpos);
		
		position=newpos;
		
		this.collider.setSpeed(dspeed);//set the last position variation, to be used for collision processing
	}
	
	/**
	 * updates speed according to computed acceleration
	 * @param time
	 */
	private void updateSpeed(double time)
	{	
		//so dirty without operator overloading...
		Vect3 specificAcceleration = getSpecificAcceleration();
		Vect3 dragAcceleration = speed.getMultipliedBy(speed.norm() * charact.getAirDrag() / getTotalWeight()); ////drag proportional to squared speed
		Vect3 linearFrictionAcceleration = speed.getMultipliedBy(charact.getFrictionDrag() / getTotalWeight()); //drag proportional to speed
		Vect3 acceleration = specificAcceleration.getAdded(Constants.Gravity).substract(dragAcceleration).substract(linearFrictionAcceleration);
		
		speed.add(acceleration.getMultipliedBy(time));
	}
	
	/**
	 * Should return the specific object acceleration (for example, thrust or propeller force for a drone).
	 * Return the zero-vector if the object has no specific acceleration.
	 * This method is used only in the updatespeed super method.
	 * @return
	 */
	protected abstract Vect3 getSpecificAcceleration();
	
	/**
	 * do all necessary actions on each frame update.
	 * this method is called before getSpecificAcceleration
	 */
	protected abstract void todoOnUpdate(double time);
	
	
	/**for deep copy
	 * 
	 * @return
	 */
	public abstract WorldObject copy();
	
	
	//getters/setters
	public Vect3 getPosition() {
		return position;
	}
	
	//position is not meant to be used outside of this class family, position should be updated by updateSpeed() and collideWith() only
	public void setPosition(Vect3 position) {
		this.position = position;
	}

	
	public Vect3 getSpeed() {
		return speed;
	}
	
	public void setSpeed(Vect3 speed) {
		this.speed = speed;
	}

	public Vect3 getSize() {
		return size;
	}
	
	/**
	 * can be overridden if the object is carrying a payload
	 * @return
	 */
	public double getTotalWeight()
	{
		return charact.dryWeight;
	}
	
	/**
	 * create a specific collider for this object (sphere or cuboid)
	 * @return
	 */
	protected abstract Collider createSpecificCollider();
	
	/**
	 * compute and returns specific collider
	 * @return
	 */
	public Collider getCollider()
	{
		updateCollider();
		return this.collider;
	}
	
	public void resetDistanceTravelled()
	{
		totalDistanceTravelled=0;
	}
	
	/**
	 * should be called at each frame
	 */
	protected void updateCollider()
	{
		if(this.collider!=null)
		{
			this.collider.setCenter(this.position);
		}
	}

}
