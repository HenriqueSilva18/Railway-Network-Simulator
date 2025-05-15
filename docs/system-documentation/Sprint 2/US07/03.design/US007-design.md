# US007 - List stations and view their details

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...                                 | Answer                 | Justification (with patterns)                                         |
|:--------------:|:-----------------------------------------------------------------------------|:------------------------|:----------------------------------------------------------------------|
| Step 1         | initiating the request to list available maps?                              | `ListStationUI`        | UI layer starts the use case                                          |
| Step 2         | coordinating the request logic for available maps?                          | `ListStationController`| Applies Controller pattern, mediates between UI and model             |
| Step 3         | maintaining the current session and editor user?                            | `ApplicationSession`   | Singleton holding current user session                                |
| Step 4         | providing access to map data?                                                | `MapRepository`        | Responsible for persistence                |
| Step 5         | returning cities of a map?                                                   | `Map`                  | Aggregates cities                |
| Step 6         | returning stations for a city?                                               | `City`                 | Responsible for its stations                    |
| Step 7         | retrieving a station's basic details?                                        | `Station`              | Information Expert over its own attributes                            |
| Step 8         | retrieving the list of buildings in a station?                              | `Station`              | Aggregates buildings                              |
| Step 9         | retrieving the demanded and supplied cargo for a station?                   | `Station`              | Knows its cargo needs and supplies                                    |
| Step 10        | presenting data back to the player?                                          | `ListStationUI`        | Interface responsibility                           |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* `Map`
* `City`
* `Station`
* `Building`
* `Cargo`

Other software classes (i.e. Pure Fabrication) identified:

* `ListStationUI`
* `ListStationController`
* `MapRepository`
* `ApplicationSession`
* `UserSession`

## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

![US07-SD](svg/US07-SD-split.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US07-CD](svg/US07-CD.svg)
