# US15 - Create a simulator

## 1. Requirements Engineering

### 1.1. User Story Description

As a Product Owner, I want to create a simulator that generates cargoes at current stations, automatically, considering the cities and industries that the railway network serves.

### 1.2. Customer Specifications and Clarifications 

**From the specifications document:**

> The simulator will enable the manager (or in the playful perspective, the player) to create a railway network composed of stations that serve cities and industries, lines connecting those stations, and trains that transport cargo between stations, according to a route defined by the manager. It is crucial to highlight that cities and industries only generate or transform cargo if they have a station; when the cargo is generated, it is then available to be collected at the station.

> In the simulator, the player/user acquires trains within the available budget and can put the train into service on a specific route.

> In addition to creating the dynamic elements already described, the player/user can play or pause the simulator. During the simulator’s operation, the evolution of events should be displayed, namely the trains’ journeys (start and end).

> The RailRoad Tycoon (II and later) game/simulator is particularly complete, covering the areas of gameplay, economy and engineering. Keeping in mind that this game is an inspiration for the current project, there are many topics covered by the game that will not be focused on in the project, in particular:
> - Collision detection
> - Timetable generation/management
> - Calculation of train (de)accelerations
> - Editing of line details (graphical mode), connections will be topological

**From the client clarifications:**

> **Question:** Should cargo generation dynamically update as the railway network expands or changes?
>
> **Answer:** Yes; the generation is done for the industries and house blocks served by the stations present in the network.

> **Question:** Will the user have customization options for cargo generation rules, or will it be fully automated?
>
> **Answer:** Just in the edition of the scenario.

> **Question:** How should cargo stockpiling be managed at stations to prevent excessive accumulation?
>
> **Answer:** A maximum number can be considered per cargo type (e.g. 30).

> **Question:** There is a limit to cargo storage?
>
> **Answer:** 24.

> **Question:** How should cargo generation be done? At fixed time intervals or based on specific events (train arrival)?
>
> **Answer:** Accordingly to the frequency defined for the industry and house blocks by the station (the distribution along the year can be fixed or random).

> **Question:** Should the simulator run in real-time or in set intervals?
>
> **Answer:** Not in real-time.

> **Question:** Can users manually adjust cargo generation rates?
>
> **Answer:** Maybe not the player but generation should be configurable (maybe in a config file).

> **Question:** Should generated cargo be influenced by train schedules?
>
> **Answer:** There are no train schedules!

### 1.3. Acceptance Criteria

* **AC1:** This simulator should provide options for start/pause.

### 1.4. Found out Dependencies

* There is a dependency on "US01 - Create a map" as there must be at least one map created for the simulation to run.
* There is a dependency on "US02 - Add an industry" as industries are needed to generate cargo at stations.
* There is a dependency on "US03 - Add a city" as cities generate passenger and mail cargo at stations.
* There is a dependency on "US04 - Create a scenario" as scenarios define the restrictions for the simulation.
* There is a dependency on "US05 - Build station" as stations are required to serve cities and industries for cargo generation.
* There is a dependency on "US08 - Build a railway line" as railway lines are needed to connect stations.
* There is a dependency on "US09 - Buy a locomotive" as locomotives are needed to form trains for cargo transport.
* There is a dependency on "US10 - Assign a train to a route" as routes are needed for trains to transport cargo between stations.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
    * n/a
* Selected data:
    * an operation

**Output Data:**

* (In)Success of the operation
* Simulation results
* Simulation report

### 1.6. System Sequence Diagram (SSD)

![US12-SSD](svg/US15-SSD.svg)

### 1.7 Other Relevant Remarks

**(i) special requirements:**
- n/a

**(ii) data and/or technology variations:**
- Scenario restrictions will affect the simulation.

**(iii) how often this US is held:**
- The Product Owner will develop the simulator initially and maintain it throughout the project.
- The Simulator will be used by players during gameplay.