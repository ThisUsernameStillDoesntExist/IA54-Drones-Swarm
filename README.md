# IA54-Drones-Swarm
Multi-agent drones simulation using Java+Repast, aiming at reproducing drones interactions for delivering packages in a smart city.

TODO : Adilson

1. If the package is not delivered(because of charge), the drone need to notify the others Drones  to search for other available drone come to delivery the package in question
    1.1 Drones need to seachr for the nearest station to charge they battery  ---> OK Done

2- Electrical formula for the discharge of the drone battery

3- Graphic, time/package delivered ---> OK Done
4- Implement the HelperDrone behavior.
  -- The HelperDrones will be helpful when the DeliverDrones let package on the street because of charge, So the DeliverDrone notify others drones and the HelperDrones will look for package let on the street to control until the DeliverDrone come to search for it.
    

TODO : Francis
- integrate physics engine in repast continuous space (synchronization)
- implement decision making methods in the drone brain (droneAI)
- pathfinding, trajectory optimisation for drones
- maybe ignore collisions, and set a boolean when a drone is landed, to stop its motors and consumption
- factorize code in the drone class

- It would be better and more consistent to work only with continuous space, at least to check whether a drone has arrived to a particular location 


Technical notes :
- We don't check for null references, so be careful and provide new objects in constructors
