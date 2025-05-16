# US05 - Build a Station

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for... | Answer                 | Justification (with patterns)                                                        |
|:---------------|:--------------------------------------------|:-----------------------|:-------------------------------------------------------------------------------------|
| Step 1  		     | ...interacting with the user?               | BuildStationUI         | Pure Fabrication: there is no reason to assign this responsibility to a domain class |
| Step 2  		     | ...coordinating the US?                     | BuildStationController | Controller: coordinates the use case                                                 |
| Step 3  		     | ...getting available station types?         | StationTypeRepository  | Information Expert: knows all station types                                          |
| Step 4  		     | ...displaying station type options?         | BuildStationUI         | Pure Fabrication: UI classes present information to users                            |
| Step 5  		     | ...getting map positions?                   | MapRepository          | Information Expert: knows all map information                                        |
| Step 6  		     | ...validating station position?             | Map                    | Information Expert: owns its data and can validate position availability             |              
| Step 7  		     | ...creating the station?                    | Player                 | Creator: Player builds Stations according to the domain model                        |
| Step 8  		     | ...validating the player's budget?          | Player                 | Information Expert: knows its own budget and can validate affordability              |
| Step 9  		     | ...saving the station?                      | StationRepository      | Pure Fabrication: responsible for persistence operations                             |
| Step 10  		    | ...informing operation success?             | BuildStationUI         | Pure Fabrication: UI classes handle user feedback                                    |  

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Player
* Map
* Position
* Station
* StationType
* City
* Industry

Other software classes (i.e. Pure Fabrication) identified:

* BuildStationUI  
* BuildStationController
* StationRepository
* StationTypeRepository
* MapRepository

## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

![US05-SD](svg/US05-SD.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US05-CD](svg/US05-CD.svg)