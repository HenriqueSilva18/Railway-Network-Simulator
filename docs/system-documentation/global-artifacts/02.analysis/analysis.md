# OO Analysis

The construction process of the domain model is based on the client specifications, especially the nouns (for _concepts_) and verbs (for _relations_) used.

## Rationale to identify domain conceptual classes

To identify domain conceptual classes, start by making a list of candidate conceptual classes inspired by the list of categories suggested in the book "Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development".

### _Conceptual Class Category List_

**Business Transactions**

* Scenario (game session)
* Simulator (game simulation)
* Route (transportation route)

---

**Transaction Line Items**

* Cargo (goods being transported)
* RailwayLine (connection between stations)

---

**Product/Service related to a Transaction or Transaction Line Item**

* Station (service point with cost)
* Building (station upgrades)
* Locomotive (transport service with acquisition/maintenance costs)
* Carriage (transport capacity)

---

**Transaction Records**

* Report (simulation results from Simulator)
* Train (transport unit record)

---

**Roles of People or Organizations**

* Player (game participant with budget)
* Editor (content creator)

---

**Places**

* Map (game world)
* City (population center)
* Industry (production facility)
* Position (spatial location)
* Size (map dimensions)

---

**Noteworthy Events**

* Simulation run
* Route completion
* Financial transactions (purchases/constructions)

---

**Physical Objects**

* Train
* Locomotive
* Carriage
* Station
* Building
* RailwayLine
* Position

---

**Descriptions of Things**

* Scenario (game rules and restrictions)
* Map (world layout)
* Cargo (goods specifications)
* Industry (production specs)

---

**Catalogs**

* Available locomotives
* Available buildings
* Available industries
* Available scenarios

---

**Containers**

* Station (for cargo and buildings)
* Train (with carriages)
* Map (contains cities, industries, positions)
* Route (with stations and railway lines)

---

**Elements of Containers**

* Carriage (part of train)
* Building (part of station)
* City (part of map)
* Industry (part of map)
* Station (part of map)
* Position (element of map)

---

**Organizations**

* (Not explicitly modeled)

---

**Other External/Collaborating Systems**

* Simulator (external simulation engine)

---

**Records of finance, work, contracts, legal matters**

* Player budget
* Station construction costs
* RailwayLine construction costs
* Locomotive acquisition/maintenance costs
* Building evolution cost

---

**Financial Instruments**

* Player budget
* Station cost
* RailwayLine cost
* Locomotive acquisitionPrice
* Locomotive maintenancePrice
* Building evolutionCost

---

**Documents mentioned/used to perform some work**

* Simulation report

---

## Rationale to identify associations between conceptual classes

An association is a relationship between instances of objects that indicates a relevant connection and that is worth remembering, or it is derivable from the List of Common Associations:

- **_A_** is physically or logically part of **_B_**
- **_A_** is physically or logically contained in/on **_B_**
- **_A_** is a description for **_B_**
- **_A_** is known/logged/recorded/reported/captured in **_B_**
- **_A_** uses or manages or owns **_B_**
- **_A_** is related to a transaction (item) of **_B_**

| Concept (A) | Association       | Concept (B) |
|-------------|-------------------|-------------|
| Building    | evolves into      | Building    |
| Carriage    | carries           | Cargo       |
| City        | produces/consumes | Cargo       |
| City        | located at        | Position    |
| Editor      | creates           | Map         |
| Editor      | creates           | Scenario    |
| Industry    | produces/consumes | Cargo       |
| Industry    | located at        | Position    |
| Locomotive  | configured in     | Scenario    |
| Map         | contains          | City        |
| Map         | contains          | Industry    |
| Map         | contains          | Position    |
| Map         | has               | Size        |
| Player      | builds            | RailwayLine |
| Player      | builds            | Station     |
| Player      | buys              | Locomotive  |
| Player      | defines           | Route       |
| Player      | plays             | Scenario    |
| RailwayLine | connects          | Station     |
| Route       | contains          | RailwayLine |
| Route       | defined by        | Player      |
| Route       | includes          | Station     |
| Route       | manages           | Cargo       |
| Scenario    | configures        | Industry    |
| Scenario    | configures        | Locomotive  |
| Scenario    | configures        | Station     |
| Scenario    | configures        | City        |
| Scenario    | uses              | Map         |
| Scenario    | runs in           | Simulator   |
| Simulator   | generates         | Cargo       |
| Station     | serves            | City        |
| Station     | serves            | Industry    |
| Station     | stores            | Cargo       |
| Station     | upgraded with     | Building    |
| Station     | located at        | Position    |
| Train       | assigned to       | Route       |
| Train       | composed of       | Carriage    |
| Train       | powered by        | Locomotive  |
| Rout        | defined by        | Player      |

## Domain Model

![Domain Model](svg/DomainModel.svg)
