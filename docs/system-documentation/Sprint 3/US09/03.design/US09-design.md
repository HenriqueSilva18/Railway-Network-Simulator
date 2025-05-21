# US09 - Buy a Locomotive

## 3. Design

### 3.1. Rationale

| Interaction ID | Question: Which class is responsible for...        | Answer                  | Justification (with patterns)                                                                                                     |
|:---------------|:--------------------------------------------------|:-----------------------|:--------------------------------------------------------------------------------------------------------------------------------|
| Step 1         | ... interacting with the actor?                    | MapUI                  | Pure Fabrication: there is no reason to assign this responsibility to any existing class in the Domain Model.                     |
|                | ... coordinating the map operations?               | MapController          | Controller                                                                                                                        |
|                | ... getting available maps?                        | MapRepository          | IE: knows/has access to all maps.                                                                                                 |
| Step 2         | ... showing available maps?                        | MapUI                  | IE: is responsible for user interactions.                                                                                         |
| Step 3         | ... saving the selected map?                       | MapUI                  | IE: is responsible for keeping the selected map.                                                                                  |
|                | ... getting map scenarios?                         | MapRepository          | IE: knows/has access to all scenarios for a map.                                                                                  |
| Step 4         | ... showing available scenarios?                   | MapUI                  | IE: is responsible for user interactions.                                                                                         |
| Step 5         | ... saving the selected scenario?                  | MapUI                  | IE: is responsible for keeping the selected scenario.                                                                             |
| Step 6         | ... loading the selected map?                      | MapController          | Controller: coordinates the loading of map data.                                                                                  |
|                | ... getting map layout?                            | Map                    | IE: knows its own layout for a given scenario.                                                                                    |
| Step 7         | ... displaying the map?                           | MapUI                  | IE: is responsible for user interactions.                                                                                         |
| Step 8         | ... interacting with the actor?                    | BuyLocomotiveUI        | Pure Fabrication: there is no reason to assign this responsibility to any existing class in the Domain Model.                     |
|                | ... coordinating the US?                           | BuyLocomotiveController| Controller                                                                                                                        |
|                | ... knowing the user using the system?             | UserSession            | IE: cf. A&A component documentation.                                                                                              |
|                | ... getting current scenario and date?             | ApplicationSession     | IE: knows the current game state including scenario and date.                                                                     |
|                | ... getting available locomotives?                  | Scenario               | IE: knows its available locomotives for a given date.                                                                             |
| Step 9         | ... showing available locomotives?                  | BuyLocomotiveUI        | IE: is responsible for user interactions.                                                                                         |
| Step 10        | ... saving the selected locomotive?                | BuyLocomotiveUI        | IE: is responsible for keeping the selected locomotive.                                                                           |
|                | ... getting locomotive details?                    | LocomotiveRepository   | IE: knows/has access to all locomotives.                                                                                          |
| Step 11        | ... showing locomotive details?                    | BuyLocomotiveUI        | IE: is responsible for user interactions.                                                                                         |
| Step 12        | ... getting player information?                    | PlayerRepository       | IE: knows/has access to all players.                                                                                              |
|                | ... validating purchase possibility?               | Player                 | IE: knows its own budget and can validate if purchase is possible.                                                                |
|                | ... processing the purchase?                       | Player                 | Creator (Rule 1): in the DM Player owns Locomotives.                                                                              |
|                | ... updating locomotive ownership?                 | Locomotive             | IE: knows its own data including owner.                                                                                           |
|                | ... informing operation success?                   | BuyLocomotiveUI        | IE: is responsible for user interactions.                                                                                         |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are: 

* Player
* Locomotive
* Scenario
* Map

Other software classes (i.e. Pure Fabrication) identified: 

* MapUI
* MapController
* BuyLocomotiveUI  
* BuyLocomotiveController
* Repositories
* MapRepository
* LocomotiveRepository
* PlayerRepository
* ScenarioRepository
* ApplicationSession
* UserSession

## 3.2. Sequence Diagram (SD)

### Full Diagram

This diagram shows the full sequence of interactions between the classes involved in the realization of this user story.

![Sequence Diagram - Full](svg/US09-SD-full.svg)

### Split Diagrams

The following diagram shows the same sequence of interactions between the classes involved in the realization of this user story, but it is split in partial diagrams to better illustrate the interactions between the classes.

It uses Interaction Occurrence (a.k.a. Interaction Use).

![Sequence Diagram - split](svg/US09-SD-split.svg)

**Get Available Maps**

![Sequence Diagram - Partial - Get Available Maps](svg/US09-SD-partial-get-available-maps.svg)

**Get Map Scenarios**

![Sequence Diagram - Partial - Get Map Scenarios](svg/US09-SD-partial-get-map-scenarios.svg)

**Load Map**

![Sequence Diagram - Partial - Load Map](svg/US09-SD-partial-load-map.svg)

**Get Current Scenario and Date**

![Sequence Diagram - Partial - Get Current Scenario and Date](svg/US09-SD-partial-get-current-scenario-and-date.svg)

**Get Available Locomotives**

![Sequence Diagram - Partial - Get Available Locomotives](svg/US09-SD-partial-get-available-locomotives.svg)

**Get Locomotive**

![Sequence Diagram - Partial - Get Locomotive](svg/US09-SD-partial-get-locomotive.svg)

**Get Player From Session**

![Sequence Diagram - Partial - Get Player From Session](svg/US09-SD-partial-get-player-from-session.svg)

**Purchase Locomotive**

![Sequence Diagram - Partial - Purchase Locomotive](svg/US09-SD-partial-purchase-locomotive.svg)

## 3.3. Class Diagram (CD)

![Class Diagram](svg/US09-CD.svg)
