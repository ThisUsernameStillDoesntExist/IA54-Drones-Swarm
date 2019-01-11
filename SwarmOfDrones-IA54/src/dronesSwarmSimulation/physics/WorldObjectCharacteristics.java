package dronesSwarmSimulation.physics;

import dronesSwarmSimulation.utilities.Vect3;

public class WorldObjectCharacteristics {

	

	protected double airDrag;// Kg/m
	protected double frictionDrag;// Kg/s
	protected Vect3 size;// m [Width*Length*Height]  , for a sphere this is the diameter
	protected double radius;// m
	protected double dryWeight;// Kg

	public WorldObjectCharacteristics() {
		this(WorldObjectType.Default);
	}

	public WorldObjectCharacteristics(WorldObjectType wot) {
		setCharacteristics(wot);
	}

	//to be overriden
	protected void setCharacteristics(WorldObjectType wot) {

		if(wot==WorldObjectType.Package)
		{
			airDrag=0;
			frictionDrag=0;
			radius=0.5;
			dryWeight=0.02;//20grams
		}
		else//default
		{
			airDrag=0;
			frictionDrag=0;
			radius=1;
			dryWeight=1;
		}
		
		setSizeFromRadius();
	}
	
	//to simplify things
	protected void setSizeFromRadius()
	{
		size=new Vect3(radius,radius,radius);
	}
	

	public Vect3 getSize() {
		return size;
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
