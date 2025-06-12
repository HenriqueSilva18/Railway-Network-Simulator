# US20 - Load a Map

## 1. Requirements Engineering

### 1.1. User Story Description

- As an Editor, I want to Load a Map from a file previously saved.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The Map should be loaded from a previously saved file.
 
**From the client clarifications:**

> **Question:** Se a nossa aplicação permite o armazenamento dos mapas para utilização futura, usando persistência através de serialização. Nestes mapas já estão também persistidos uma lista de cenários correspondentes. E conforme crio um novo cenário para o determinado mapa este é bem guardado e pode também ser utilizado em situações futuras. 
> Posso dar como feita a US20 e US21 perante estes critérios?
> > **Answer:** Um mapa é mapa.
Um cenário é um cenário.
O primeiro guarda industrias, cidades, etc
O segundo guarda comportamento dos portos, intervalo temporal na qual decorrerá o cenário, ...
Um cenário utiliza um mapa.
Um mapa pode ser utilizado por múltiplos mapas.
Logo, quando guarda uma mapa não guarda um cenário e vice-versa. 

### 1.3. Acceptance Criteria

* **AC1:** The system must load a previously saved map with all its attributes (nameID, scale, size) and associated entities (cities, industries, positions) from a serialized file.
* **AC2:** The system must display the list of available saved maps for selection.

### 1.4. Found out Dependencies

* There is a dependency on **"US19 - Save a Map"** since a map must be saved before it can be loaded.
 
### 1.5 Input and Output Data

**Input Data:**
* Selected data:
     * Map file to be loaded from the list of saved maps

* Typed data:
    * None (all data comes from the selected map file)
 
**Output Data:**
* List of available saved maps
* (In)Success of the operation
* Loaded map with all its elements (cities, industries, railways)
* Map details (nameID, scale, size)

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US20-SSD.svg)

**_Other alternatives might exist._**

### 1.7 Other Relevant Remarks

* The load operation uses deserialization as specified in the client clarifications.
* The loaded map should be immediately available for editing after successful loading.