# OO Analysis

The construction process of the domain model is based on the client specifications, especially the nouns (for _concepts_) and verbs (for _relations_) used.

## Rationale to identify domain conceptual classes

To identify domain conceptual classes, start by making a list of candidate conceptual classes inspired by the list of categories suggested in the book "Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development".

### _Conceptual Class Category List_

**Business Transactions**

* Purchase
* Construction

---

**Transaction Line Items**

* RailwayLine purchase
* Train purchase

---

**Product/Service related to a Transaction or Transaction Line Item**

* Cargo transport
* Passenger transport

---

**Transaction Records**

* Created maps
* Created scenarios

---  

**Roles of People or Organizations**

* User
* Editor
* Player
* ProductOwner

---

**Places**

* Map
* City
* Station
* Port

---

**Noteworthy Events**

* Scenario restrictions
* Resource generation

---

**Physical Objects**

* Train
* Carriage
* Locomotive
* RailwayLine
* Building
* Industry
* Cargo
* Resource
* Product

---

**Descriptions of Things**

* Position (xAxis, yAxis)
* ResourceType
* ProductType
* ServiceType
* TrackType
* LocomotiveType
* StationType
* BuildingCategory

---

**Catalogs**

* List of locomotive types
* List of track types
* List of resource types
* List of product types
* List of service types
* List of building categories

---

**Containers**

* Map (contains industries and cities)
* Train (contains locomotive and carriages)
* Station (stores cargo and includes buildings)
* Route (includes stations and cargo pickups)

---

**Elements of Containers**

* Industry within a Map
* City within a Map
* Cargo within a Station
* Carriages within a Train
* Station buildings within a Station

---

**Organizations**

* Railway network management

---

**Other External/Collaborating Systems**

* Simulator

---

**Records of finance, work, contracts, legal matters**

* Budget management for players
* Maintenance costs for locomotives
* Construction costs for stations and railway lines

---

**Financial Instruments**

* Train purchase
* Railway line construction

---

**Documents mentioned/used to perform some work**

* Scenarios

---

## Rationale to identify associations between conceptual classes

An association is a relationship between instances of objects that indicates a relevant connection and that is worth remembering, or it is derivable from the List of Common Associations:

- **_A_** is physically or logically part of **_B_**
- **_A_** is physically or logically contained in/on **_B_**
- **_A_** is a description for **_B_**
- **_A_** is known/logged/recorded/reported/captured in **_B_**
- **_A_** uses or manages or owns **_B_**
- **_A_** is related to a transaction (item) of **_B_**

| Concept (A)            |  Association               |  Concept (B) |
|-----------------------|---------------------------|--------------|
| Editor               | creates                    | Map          |
| Editor               | creates                    | Scenario     |
| Scenario            | uses                       | Map          |
| Map                 | contains                   | Industry     |
| Map                 | contains                   | City         |
| Player              | builds                     | Station      |
| Player              | builds                     | RailwayLine  |
| Player              | buys                       | Train        |
| Player              | creates                    | Route        |
| Station            | upgrades with              | Building     |
| Station            | serves                      | City         |
| Station            | serves                      | Industry     |
| Station            | stores                      | Cargo        |
| RailwayLine        | connects                    | Station      |
| Train              | has                         | Locomotive   |
| Train              | has                         | Carriage     |
| Train              | assigned to                 | Route        |
| Train              | transports                  | Cargo        |
| Route              | includes                    | StationCargoPickup |
| StationCargoPickup | at                          | Station      |
| StationCargoPickup | specifies                   | Cargo        |
| Station            | included in                 | Route        |
| Scenario           | restricts                   | Locomotive   |
| Scenario           | configures                  | Port        |
| PrimarySectorIndustry | generates                | Resource     |
| City               | generates                   | ServiceType  |
| TransformingIndustry | consumes                  | Resource     |
| TransformingIndustry | produces                  | Product      |
| City               | consumes                    | Product      |
| City               | consumes                    | ServiceType  |
| Port               | imports/exports            | Resource     |
| Port               | imports/exports            | Product      |
| Port               | transforms                  | Resource     |
| Port               | produces                    | Product      |
| ProductOwner       | manages                     | Simulator    |
| Station            | has                         | StationType  |
| RailwayLine        | is                          | TrackType    |
| Locomotive        | is                          | LocomotiveType  |
| StationBuildingSlot | contains                   | Building     |
| StationBuildingSlot | is of category             | BuildingCategory  |

## Domain Model

**Do NOT forget to identify concept attributes too.**

**Insert below the Domain Model Diagram in a SVG format**

![Domain Model](svg/DomainModel.svg)

