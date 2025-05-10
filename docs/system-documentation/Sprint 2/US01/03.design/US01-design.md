# US01 - Create a Map

## 3. Design

### 3.1. Rationale

| Interaction ID | Question: Which class is responsible for... | Answer | Justification (with patterns) |
|:--------------|:-------------------------------------------|:-------|:------------------------------|
| Step 1 | ... interacting with the actor? | CreateMapUI | Pure Fabrication: there is no reason to assign this responsibility to any existing class in the Domain Model. |
| | ... coordinating the US? | CreateMapController | Controller |
| | ... knowing the user using the system? | UserSession | IE: cf. A&A component documentation. |
| | | Editor | IE: knows its own data |
| | | Map | IE: knows its own data |
| Step 2 | ... validating map name? | Map | IE: contains the naming rules for maps. |
| Step 3 | ... validating map dimensions? | Size | IE: contains the dimension validation rules. |
| Step 4 | ... creating the map object? | Editor | Creator: Editor creates Maps. |
| | ... creating the size object? | Map | Creator: Map contains Size. |
| Step 5 | ... persisting the created map? | MapRepository | IE: maintains all maps. |
| Step 6 | ... informing operation success? | CreateMapUI | IE: is responsible for user interactions. |

### 3.2. Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* Editor
* Map
* Size

Other software classes (i.e. Pure Fabrication) identified:

* CreateMapUI
* CreateMapController
* Repositories
* MapRepository
* ScenarioRepository
* ApplicationSession
* UserSession

## 3.3. Sequence Diagram (SD)

### Full Diagram

![Sequence Diagram - Full](svg/US01-SD-full.svg)

### Partial Diagrams

**Request Map Creation Form**

![Sequence Diagram - Partial - Request Form](svg/US01-SD-partial-request-form.svg)

**Validate Map Data**

![Sequence Diagram - Partial - Validate Data](svg/US01-SD-partial-validate-data.svg)

**Create and Save Map**

![Sequence Diagram - Partial - Create Map](svg/US01-SD-partial-create-map.svg)

## 3.4. Class Diagram (CD)

![US01-CD](svg/US01-CD.svg)