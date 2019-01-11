# IA54-Drones-Swarm
Multi-agent drones simulation using Java+Repast, aiming at reproducing drones interactions for delivering packages in a smart city.

TODO : Adilson

1. If the package is not delivered(because of charge), the drone need to notify the others Drones  to search for other available drone come to delivery the package in question
    1.1 Drones need to seachr for the nearest station to charge they battery  ---> OK Done

2- Electrical formula for the discharge of the drone battery

3- Graphic, time/package delivered ---> OK Done
4- Implement the HelperDrone behavior.
  -- The HelperDrones will be helpful when the DeliverDrones let package on the street because of charge, So the DeliverDrone notify others drones and the HelperDrones will look for package let on the street to control until the DeliverDrone come to search for it.
5- all packages spawn in warehouse
6- each random seed -> generate unique config
7- all drones spawn in same place

TODO : Francis
- implement decision making methods in the drone brain (droneAI)
- pathfinding, trajectory optimisation for drones
- maybe ignore collisions, and set a boolean when a drone is landed, to stop its motors and consumption
- clean everything obsolete (including physicsengine class parts)
- optimize performances (try to remove costly synchronize) 
- resolve charge conflict with several drones at dockstation (communicate)


TODO :
- factorize code a lot and move redundant things into subfunctions
- remove unnecessary getters/setters at the end
- every hardcoded value should be put in an attribute of a special parameters class (this allow to change every value, and factorize code -> all parameters values can be found in the same class)
- link charge to real drone battery and support plug to station


Technical notes :
- We don't check for null references, so be careful and provide new objects in constructors
