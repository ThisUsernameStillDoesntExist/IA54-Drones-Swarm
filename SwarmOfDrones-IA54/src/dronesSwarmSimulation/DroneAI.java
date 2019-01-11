package dronesSwarmSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.ivy.core.report.DownloadReport;

import dronesSwarmSimulation.utilities.UtilityFunctions;
import dronesSwarmSimulation.utilities.Vect3;

/**
 * Implements thinking process of the drone (which results in target coordinates and an action to achieve)
 * In other words, the DroneAI get percepted information, builds concepts from it, take a decision, and then translate this decision in effective simple steps of action.
 * TODO : improve with BDI model
 * @author Francis
 *
 */
public class DroneAI {
	
	protected DroneAIprops charact;
	protected Drone attachedDrone;
	protected Vect3 targetPosition;
	protected double throttleLimit;// max limit of throttle for energy saving, should be set by AI if necessary to use less battery
	protected HighLevelDecision hld;
	
	
	
	public DroneAI(Drone attacheddrone)
	{
		this.attachedDrone=attacheddrone;
		this.charact=new DroneAIprops();
		this.throttleLimit=1;
		
		initDecisionVars();
	}
	
	
	private void initDecisionVars() {

		hld=new HighLevelDecision();
		
		hld.dejaTrouvePackage = false;
		hld.hasTask = false;
		hld.tasks = new LinkedList<Package>();
		hld.tasksNotDelivered = new LinkedList<Package>();
		hld.tasksDelivered = new  ArrayList<Package>();
		hld.lisOfDockStation = new ArrayList<DockStation>();
		
	}


	public Vect3 getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Vect3 targetPosition) {
		if(targetPosition==null && attachedDrone!=null)
		{
			targetPosition=new Vect3(attachedDrone.getPosition());
		}
		this.targetPosition = targetPosition;
	}


	/**
	 * decision method for the drone
	 * -take in input information : tasks, environment, messages from other drones...
	 * -make a decision following rules
	 * -output a propeller direction with a motorthrottle (also output messages for other agents, and basic actions like take package or plug to station)
	 * @param time
	 */
	public void decide(double time) {
		// TODO Auto-generated method stub
		if(targetPosition==null)
		{
			return;
		}
		
		Vect3 tarpos=new Vect3(targetPosition);
		
		DroneCommandValues dcv=getComputedCommandValues(tarpos);
		
		dcv.getPropellerDirection().Normalize();
		
		attachedDrone.setPropellerDirection(dcv.getPropellerDirection());
		attachedDrone.setMotorThrottle(dcv.getMotorThrottle());
		
		
	}
	
	/**
	 * get a coef multiplicating dirvector such that ||upvector+coef*dirvector||=1
	 * if upvector has already a norm >=1, return 0
	 * @param upvector
	 * @param dirvector
	 * @return
	 */
	private double getComplementNormCoef(Vect3 upvector, Vect3 dirvector)
	{
		double uvSQnorm=upvector.squaredNorm();
		double dvSQnorm=dirvector.squaredNorm();
		if(uvSQnorm>=1 || dvSQnorm==0)
		{
			return 0;
		}
		
		double dotprod=upvector.dotProduct(dirvector);
		
		double discriminant=4*dotprod*dotprod-4*dvSQnorm*(uvSQnorm-1);
		double res=(-2*dotprod+Math.sqrt(discriminant))/(2*dvSQnorm);
		
		return res;
	}

	private double getComputedThrottle(Vect3 tarpos)
	{
		//motor at full power
		return 1;
	}

	private DroneCommandValues getComputedCommandValues(Vect3 tarpos) {
		
		//TODO : add safety coef to avoid full stop too early
		
		DroneCommandValues DCV=new DroneCommandValues();
		DCV.setMotorThrottle(getComputedThrottle(tarpos));
		
		Vect3 targetdir=tarpos.getSubstracted(attachedDrone.getPosition());
		Vect3 propdir;
		
		DCV.setPropellerDirection(targetdir);
		
		double tht=attachedDrone.getCounterGravityThrottle();
		
		adjustDirection(DCV, targetdir, tarpos);
		
		anticipateBraking(DCV, targetdir, throttleLimit-tht);//modify dcv.propellerdirection
		Vect3 modtargetdir=DCV.getPropellerDirection();
		
		Vect3 throttleup=new Vect3(0,0,1);
		
		throttleup.multiplyBy(tht);//to maintain vertical speed (if speed=0, =maintain altitude)
		Vect3 tardirN=modtargetdir.getNormalized();
		if(tardirN.squaredNorm()==0)
		{
			DCV.setMotorThrottle(tht);//final direction is vertical
		}
		
		double dcoef=getComplementNormCoef(throttleup, tardirN);
		Vect3 adjustedtardir=tardirN.getMultipliedBy(dcoef);
				
		propdir=throttleup.getAdded(adjustedtardir);
		
		DCV.setPropellerDirection(propdir);
		
		return DCV;
		
	}
	
	/**
	 * reduce error in speed when targeting a position, to avoid drone turning around objective
	 * works like a servitude system
	 * Only 2D, should be improved with vector math
	 * @param dcv
	 * @param targetdir
	 * @param availableBrakingThrottle
	 */
	private void adjustDirection(DroneCommandValues dcv, Vect3 targetdir, Vect3 tarpos) {
		
		targetdir=targetdir.copy();//in case we change this vector, we don't touch the original
		
		
		Vect3 tardirN=targetdir.getNormalized();
		Vect3 speed=attachedDrone.getSpeed().copy();
		Vect3 speedN=speed.getNormalized();
		
		/*
		Vect3 normalSpeedN=new Vect3(-speedN.getY(), speedN.getX(), 0);
		double orthogonalComponentOfSpeed=normalSpeedN.dotProduct(tardirN);
		*/
		Vect3 colinearComponentOfSpeed=speed.getMultipliedBy(speedN.dotProduct(tardirN));
		
		//substract speed to target pos to get propeller dir
		Vect3 orthogonalComponentOfSpeed=speed.getSubstracted(colinearComponentOfSpeed);
		Vect3 newtarpos=tarpos.getSubstracted(orthogonalComponentOfSpeed.getMultipliedBy(this.charact.directionCorrectionFactor));
		Vect3 newtardir=newtarpos.getSubstracted(attachedDrone.getPosition());
		
		dcv.setPropellerDirection(newtardir);
	}

	/**
	 * brake if necessary to approach target position with a reasonable speed
	 * modify propeller direction (and potentially motorthrottle) in dcv
	 * @param dcv
	 * @param targetdir
	 */
	private void anticipateBraking(DroneCommandValues dcv, Vect3 targetdir, double availableBrakingThrottle) {

		
		targetdir=targetdir.copy();//work with a duplicate vector, this is necessary due to the lack of structs in java
		Vect3 actualspeed=attachedDrone.getSpeed().copy();//=new Vect3(attachedDrone.getSpeed())
		double cosdirs=targetdir.getAngleCosSafe(actualspeed);
		double speedTowardTarget=cosdirs*actualspeed.norm();//amount of speed directed toward target
		
		if(speedTowardTarget<=0)//going in the opposite direction, no braking needed
		{
			return;
		}
		
		//optimal braking distance
		double brakingdist=speedTowardTarget*speedTowardTarget/attachedDrone.getMaxAccelerationAtThrottle(availableBrakingThrottle);//when the drone is at a smaller distance than brakingdist from the target, he should start to brake
		
		double distToTarget=targetdir.norm();//distance between drone and targetposition
		
		if(distToTarget<brakingdist)
		{
			//slow down
			Vect3 propdir=UtilityFunctions.getReversedOnXYPlane(actualspeed);
			System.out.print(" Braking");
			
			dcv.setPropellerDirection(propdir);
			
			if(propdir.norm()==0)
			{
				System.out.println("   ERROR : PropDir is zero.");
			}
		}
		
		return;
		
	}
	
	
	
	

}
