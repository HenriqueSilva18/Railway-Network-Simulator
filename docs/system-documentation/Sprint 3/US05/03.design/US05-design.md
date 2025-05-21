
# US05 - Build a Station

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...          | Answer                    | Justification (with patterns)                                                           |
|:---------------|:-----------------------------------------------------|:--------------------------|:----------------------------------------------------------------------------------------|
| Step 1  		     | ... interacting with the actor?                      | StationBuildingUI         | Pure Fabrication: no reason to assign this responsibility to any existing domain class. |
|                | ... coordinating the US?                             | StationBuildingController | Controller pattern: coordinates the use case.                                           |
|                | ... showing available maps?                          | MapRepository             | Information Expert: knows all available maps.                                           |
| Step 2  		     | ... recording the selected map?                      | StationBuildingUI         | IE: UI is responsible for keeping track of user selections.                             |
| Step 3  		     | ... loading and displaying the map?                  | MapRepository             | IE: has access to the map data.                                                         |
|                |                                                      | Map                       | IE: contains its own data to be displayed.                                              |
| Step 4  		     | ... processing the request to build a station?       | StationBuildingController | Controller: coordinates actions between UI and domain.                                  |
| Step 5  		     | ... knowing the available station types?             | StationTypeRepository     | IE: contains all station type information.                                              |
| Step 6  		     | ... recording the selected station type?             | StationBuildingUI         | IE: UI is responsible for keeping track of user selections.                             |
| Step 7  		     | ... validating if position is valid on map?          | Map                       | IE: knows its own structure and valid positions.                                        |
|                |                                                      | Position                  | IE: knows its own state (occupied or not).                                              |
| Step 8  		     | ... determining if center point selection is needed? | StationType               | IE: knows its own properties and requirements.                                          |
| Step 9  		     | ... recording the selected center point?             | StationBuildingUI         | IE: UI is responsible for keeping track of user selections.                             |
| Step 10  		    | ... determining the closest city?                    | Map                       | IE: knows all cities and their positions.                                               |
|                | ... calculating station cost and economic radius?    | StationType               | IE: contains cost and radius information.                                               |
|                | ... generating a preview of station placement?       | Map                       | IE: can determine impact of station placement.                                          |
| Step 11  		    | ... confirming station placement?                    | StationBuildingController | Controller: coordinates the action.                                                     |
|                | ... deducting cost from player's budget?             | Player                    | IE: manages its own budget.                                                             |
|                | ... creating the station instance?                   | Map                       | Creator: contains stations.                                                             |
|                | ... updating the map with new station?               | Map                       | IE: knows its own structure and manages station placement.                              |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Player
* Map
* City
* Position
* Station
* StationType

Other software classes (i.e. Pure Fabrication) identified:

* StationBuildingUI
* StationBuildingController
* MapRepository
* StationTypeRepository

## 3.2. Sequence Diagram (SD)

![US05-SD](svg/US05-SD.svg)

## 3.3. Class Diagram (CD)

![US05-CD](svg/US05-CD.svg)
