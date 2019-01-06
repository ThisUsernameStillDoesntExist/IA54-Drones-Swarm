# IA54-Drones-Swarm
Multi-agent drones simulation using Java+Repast, aiming at reproducing drones interactions for delivering packages in a smart city.

TODO : Adilson
1.When the drone delivered a package, the drones need to notify the package, and the package need to nofify the central intelligence
    1.1 The Central intelligence(CentralController) need to keep count of the package that were delivered, and time to , make the graphic curve
2. If the package is not delivered(because of charge), the drone need to notify the central intelligence to search for other available drone come to delivery the package in question
    2.1 Drones need to seachr fo rthe nearest station to charge they battery

3- elctrical formula for the discharge of the drone battery

4- Graphic, time/package delivered

TODO : Francis
- integrate physics engine in repast continuous space (synchronization)
- implement decision making methods in the drone brain (droneAI)
- pathfinding, trajectory optimisation for drones
- maybe ignore collisions, and set a boolean when a drone is landed, to stop its motors and consumption
- factorize code in the drone class

- It would be better and more consistent to work only with continuous space, at least to check whether a drone has arrived to a particular location 


Technical notes :
- We don't check for null references, so be careful and provide new objects in constructors
