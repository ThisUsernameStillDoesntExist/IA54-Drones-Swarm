# IA54-Drones-Swarm
Multi-agent drones simulation using Java+Repast, aiming at reproducing drones interactions for delivering packages in a smart city.

TODO : Adilson

1. If the package is not delivered(because of charge), the drone need to notify the others Drones  to search for other available drone come to delivery the package in question
    1.1 Drones need to seachr for the nearest station to charge they battery  ---> OK Done

2- Electrical formula for the discharge of the drone battery

3- Graphic, time/package delivered ---> OK Done
4- Implement the HelperDrone behavior.
  -- The HelperDrones will be helpful when the DeliverDrones let package on the street because of charge, So the DeliverDrone notify others drones and the HelperDrones will look for package let on the street to control until the DeliverDrone come to search for it.
7- all drones spawn in same place

TODO : Francis
- clean everything obsolete (including physicsengine class parts)
- optimize performances (try to remove costly synchronize) 
- resolve charge conflict with several drones at dockstation (communicate)

TODO :
- factorize code a lot and move redundant things into subfunctions
- every hardcoded value should be put in an attribute of a special parameters class (this allow to change every value, and factorize code -> all parameters values can be found in the same class)


Technical notes :
- We don't check for null references, so be careful and provide new objects in constructors
