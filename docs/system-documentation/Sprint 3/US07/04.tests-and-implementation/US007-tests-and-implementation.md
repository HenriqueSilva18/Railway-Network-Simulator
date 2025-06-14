# US007 - List Stations & View Details

## 4. Tests

To validate the correct implementation of the feature that lists all stations and displays their detailed information, the following tests were designed:

### Unit Tests

**Test 1:** Ensure the service returns a non-null list of `StationDTO`.

```java
@Test
void ensureListIsNotNull() {
    Station s = new Station("ST01", position, 50, 3);

    List<StationDTO> result = controller.listAllStations();
    assertNotNull(result);
}
```

**Test 2:** Ensure station details are correctly mapped into the DTO.

```java
@Test
void ensureDetailsAreMappedCorrectly() {
    Station s = new Station("ST01", position, 50, 3);
    when(repository.getAllStations()).thenReturn(List.of(s));

    List<StationDTO> result = service.listAllStations();
    StationDTO dto = result.get(0);

    assertEquals("ST01", dto.getNameID());
    assertEquals(50, dto.getStorageCapacity());
    assertEquals(3, dto.getBuildingSlots());
}
```

**Test 3:** Ensure an empty list is returned if there are no stations.

```java
@Test
void ensureEmptyListWhenNoStations() {
    List<StationDTO> result = service.listAllStations();
    assertTrue(result.isEmpty());
}
```

## 5. Construction (Implementation)

```java
public class ListStationController {

    private final MapRepository repository;

    public StationController(StationService service) {
        this.service = service;
    }

    public List<StationDTO> listAllStations() {
        return repository.listAllStations();
    }
    
    public void getStationDetails(List<StationDTO> stations, String stationId) {
        for (StationDTO station : stations) {
            if (station.getNameID().equals(stationId)) {
                return station;
            }
        }
        
        String details = station.getDetails();
        List<Building> buildings = station.getBuildings();
        List<Cargo> cargoDemanded = station.getDemandedCargo();
        List<Cargo> cargoSupplied = station.getSuppliedCargo();
        
        compileStationDetails(details, buildings, cargoDemanded, cargoSupplied);l
        
        
        
        throw new IllegalArgumentException("Station with ID " + stationId + " not found.");
        
    }
    
    public void compileStationDetails(String details, List<Building> buildings, 
                                       List<Cargo> cargoDemanded, List<Cargo> cargoSupplied) {
        System.out.println("Station Details: " + details);
        System.out.println("Buildings: " + buildings.toString());
        System.out.println("Cargo Demanded: " + cargoDemanded.toString());
        System.out.println("Cargo Supplied: " + cargoSupplied.toString());
    }
}
```

```java
public class Station{
    private String nameID;
    private Position position;
    private int storageCapacity;
    private int buildingSlots;
    private List<Building> buildings;
    private List<Cargo> suppliedCargo;
    private List<Cargo> demandedCargo;

    public Station(String nameID, Position position, int storageCapacity, int buildingSlots) {
        this.nameID = nameID;
        this.position = position;
        this.storageCapacity = storageCapacity;
        this.buildingSlots = buildingSlots;
        this.buildings = new ArrayList<>();
        this.suppliedCargo = new ArrayList<>();
        this.demandedCargo = new ArrayList<>();
    }
    
    public String getNameID() {
        return nameID;
    }
    public Position getPosition() {
        return position;
    }
    public int getStorageCapacity() {
        return storageCapacity;
    }
    public int getBuildingSlots() {
        return buildingSlots;
    }
    public List<Building> getBuildings() {
        return buildings;
    }
    public List<Cargo> getSuppliedCargo() {
        return suppliedCargo;
    }
    public List<Cargo> getDemandedCargo() {
        return demandedCargo;
    }
    

    public String getDetails() {
        return "Station Name: " + nameID + ", Type: " + type + ", Storage Capacity: " + storageCapacity +
               ", Building Slots: " + buildingSlots;
    }
}
```

## 6. Integration and Demo

This feature was integrated with the map and game state management modules. Specifically:

- Each station listed belongs to a loaded map, retrieved via the current map context.
- The UI component queries the controller, which load all stations and maps  into DTOs.
- These DTOs are then displayed in a list format, where each station can be expanded to view details such as:
    - Station Name
    - Storage Capacity
    - Number of Building Slots
    - List of Buildings
    - Supplied and Demanded Cargo
    - Served Cities

For demo purposes, a dropdown of maps was used to filter stations by map, and clicking on a station revealed a panel with its full details.

## 7. Observations

This functionality provides essential visibility into the current state of the rail network, especially useful for debugging, planning, or simulation.

**Possible Improvements:**
- Allow filtering by city.

This user story proved to be fundamental for enhancing map analysis and simulation monitoring.