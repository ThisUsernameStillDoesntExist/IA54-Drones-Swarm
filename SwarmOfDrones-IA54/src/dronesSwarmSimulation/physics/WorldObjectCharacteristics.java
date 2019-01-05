package dronesSwarmSimulation.physics;

public class WorldObjectCharacteristics {

	

	protected double airDrag;// Kg/m
	protected double frictionDrag;// Kg/s
	protected double radius;// m
	protected double dryWeight;// Kg

	public WorldObjectCharacteristics() {
		this(WorldObjectType.Default);
	}

	public WorldObjectCharacteristics(WorldObjectType wot) {
		setCharacteristics(wot);
	}

	protected void setCharacteristics(WorldObjectType wot) {
		airDrag=0;
		frictionDrag=0;
		radius=1;
		dryWeight=1;
	}
	


	

	public double getRadius() {
		return radius;
	}

	
	public double getDryWeight() {
		return dryWeight;
	}

	public double getFrictionDrag() {
		return frictionDrag;
	}

	public double getAirDrag() {
		return airDrag;
	}

}
