
public class DroneCharacteristics {
    
    
	//All parameters are useful and changing one of them has a direct incidence on the simulation and on the performance measurement result.

    //maxspeed is a function of drag, propeller lift and motor power
    private double radius;//m 
    private double overallPowerTransmissionEfficiency;//s/m
    private double motorMaxConsumption;//W
    private double maxPayload;//Kg
    private double dryWeight;//Kg  //=empty weight
    private double batteryCapacity;//W.h=Joules/3600
    private double batteryRechargingRate;//W
    private double communicationRange;//m
    private double airDrag;//Kg/m  //air resistance
    //maxheight=communication range or map height bound

    
    public DroneCharacteristics()
    {
        this(DroneType.Standard);
    }

    public DroneCharacteristics(DroneType dt)
    {
        setCharacteristics(dt);
    }

    public void setCharacteristics(DroneType dt)
    {
        //The max propeller force (=motormaxconso*motorefficiency*propellerlift) should at least be equal to 9.81*dryWeight, otherwise your drone won't even takeoff.
        //If you want to carry a payload of mass m, the recommended max propeller force is roughly 20*(dryweight+m)
        //The maxLeaningAngle should ideally be such that when the drone is leaning at that angle with max throttle, its vertical speed is zero.
        //Set a smaller angle if you want safety, and a larger if you want fast horizontal speeds
        
        //EXAMPLE FOR PARAMETERS VALUES
        //based on DJI Spark
        airDrag=0.012;//airdrag will limit the maxspeed
        radius=0.11;
        overallPowerTransmissionEfficiency=0.03;
        motorMaxConsumption=100;//around 10 min autonomy at max throttle
        maxPayload=0.03;
        dryWeight=0.3;
        batteryCapacity=17;
        batteryRechargingRate=29;
        communicationRange=100;
    }
    
    
    /*
     * We assume a linear charge/discharge model, and a potentially infinite instantaneous power delivered from the accumulators.
     * 
     * Consumption takes only motors into account, and not electronic or communication components.
     */
    

    //TODO generate getters / setters
    
    

}

