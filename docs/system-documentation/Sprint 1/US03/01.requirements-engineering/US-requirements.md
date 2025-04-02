# US003 - Add a City

## 1. Requirements Engineering

### 1.1. User Story Description

As an Editor, I want to add a city in a position XY of the selected map, with a name and a positive number of house blocks.

### 1.2. Customer Specifications and Clarifications 

**From the specifications document:**

> A city bears a name, a location (with XY coordinates on the map), and a set of blocks that represent housing. In the simulator’s context, cities generate and consume passengers and mail, but they also consume final products (e.g., food, textiles, cars).

**From the client clarifications:**

> **Question:** Is it possible for a city to be destroyed by a natural disaster or a war? If so, is it necessary to create a mechanism to remove the city from the map and create a new one to transfer the active elements from the first one(people, mail, ...)?
> > **Answer:** A city can be removed in edit mode, but not in simulation mode.

> **Question:** Is there a minimum or maximum number of house blocks that can be assigned?
> > **Answer:** The number needs to be a positive on; there is no maximum, it's up to the editor to decide.

> **Question:** According to User Story 3, can the city name contain spaces?
> > **Answer:** Yes, it can, "Torres Novas" for example.

> **Question:** According to User Story 3, can the house blocks be assigned manually (by the editor) or automatically (randomly around the city position)?
> > **Answer:** However, it is already clarified in the statement.

> **Question:** According to User Story 3, should the city name be unique within the same map?
> > **Answer:** It can be repeated but it can become confusing for players, the editor should be alerted to the situation.

> **Question:** Should it be considered an acceptance criterion that the map must be visually updated to show the city that was previously added to the respective map and its house blocks?
>> **Answer:** There are no specifications for user interfaces yet.


### 1.3. Acceptance Criteria

AC1: A city name cannot have special characters or digits.

AC2: The house blocks can be assigned manually or automatically (randomly around the city tag position)

### 1.4. Found out Dependencies

* There is a dependency on "US001 - Add a Map" as there must be at least one map to add a city.

### 1.5 Input and Output Data

**Input Data:**
* Typed data:
    * a name
    * a position Xaxys
    * a position Yaxys
    * a number of house blocks

* Selected data:
    * a map
    * decision on how to assign the house blocks (manually or automatically)
    * position for assigning the house blocks (if manually)

**Output Data:**

    * Success or insuccess of the operation

    * List of existing cities in the selected map

    * Details of the city that was added (name, position, number of house blocks, map)

### 1.6. System Sequence Diagram (SSD)

_Insert here a SSD depicting the envisioned Actor-System interactions and throughout which data is inputted and outputted to fulfill the requirement. All interactions must be numbered._

![US003-SSD](svg/base.svg)

### 1.7 Other Relevant Remarks

_Use this section to capture other relevant information that is related with this US such as:  
&nbsp; &nbsp; (i) special requirements;  
&nbsp; &nbsp; (ii) data and/or technology variations;  
&nbsp; &nbsp; (iii) how often this US is held._