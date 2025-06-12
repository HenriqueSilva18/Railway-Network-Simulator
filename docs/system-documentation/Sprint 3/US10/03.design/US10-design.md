# US10 - Assign a train to a route

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for... | Answer                | Justification (with patterns)                                                        |
|:---------------|:--------------------------------------------|:----------------------|:-------------------------------------------------------------------------------------|
| Step 1  		     | ...interacting with the user?               | AssignTrainUI         | Pure Fabrication: there is no reason to assign this responsibility to a domain class |
| Step 2  		     | ...coordinating the US?                     | AssignTrainController | Controller: coordinates the use case                                                 |
| Step 3  		     | ...getting available routes?                | RouteRepository       | Pure Fabrication: responsible for route data access                                  |
| Step 4   		    | ...getting route details?                   | RouteMapper           | Mapper: converts domain objects to DTOs                                              |
| Step 5  		     | ...getting route points?                    | PointOfRouteMapper    | Mapper: converts domain objects to DTOs                                              |
| Step 6  		     | ...setting cargo mode?                      | PointOfRoute          | Information Expert: owns its data                                                    |
| Step 7  		     | ...getting available trains?                | TrainRepository       | Pure Fabrication: responsible for train data access                                  |
| Step 8  		     | ...getting train details?                   | TrainMapper           | Mapper: converts domain objects to DTOs                                              |
| Step 9  		     | ...assigning train to route?                | Route                 | Information Expert: owns the relationship with trains                                |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Route
* PointOfRoute
* Train
* CargoMode

Other software classes (i.e. Pure Fabrication) identified:

* AssignTrainUI
* AssignTrainController
* RouteRepository
* TrainRepository
* Routes
* Trains
* RouteDTO
* RouteDetailsDTO
* TrainDTO
* TrainDetailsDTO
* PointOfRouteDTO
* TrainRouteAssignmentDTO
* RouteMapper
* TrainMapper
* PointOfRouteMapper

## 3.2. Sequence Diagram (SD)

![US10-SD](svg/US10-SD-split.svg)


## 3.3. Class Diagram (CD)

![US10-CD](svg/US10-CD.svg)
between classes follow a clean architecture pattern, with clear separation of concerns and proper data flow between layers.