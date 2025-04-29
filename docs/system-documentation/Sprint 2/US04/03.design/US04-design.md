# US04 - Create a Scenario

## 3. Design

### 3.1. Rationale

| Interaction ID | Question: Which class is responsible for...         | Answer                   | Justification (with patterns) |
|:---------------|:----------------------------------------------------|:-------------------------|:------------------------------|
| Step 1  	      | ... interacting with the actor?	                    | CreateScenarioUI         | Pure Fabrication              |
|                | ... coordinating the US?                            | CreateScenarioController | Controller                    |
| Step 2  	      | ... requesting data?                                | CreateScenarioUI         |                               |
| Step 3  	      | ... saving the inputted data? 		                    | CreateScenarioUI         |                               |
|                | ... validating selected data ? (local validation)   |                          |                               |
| Step 4  	      | ... knowing all existing industries to show?							 | Repositories             |                               |
|                |                                                     | IndustryRepository       |                               |
| Step 5  	      | ... saving the selected industries? 							         | CreateScenarioUI         |                               |
| Step 6  	      | 							                                             |                          |                               |              
| Step 7  	      | 							                                             |                          |                               |
| Step 8  	      | 							                                             |                          |                               |
| Step 9  	      | 							                                             |                          |                               |
| Step 10        | 							                                             |                          |                               |  

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Class1
* Class2
* Class3

Other software classes (i.e. Pure Fabrication) identified:

* xxxxUI  
* xxxxController

## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

![USXXX-SD](svg/USXXX-SD.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![USXXX-CD](svg/USXXX-CD.svg)