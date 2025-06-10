# US04 - Create a Scenario

## 4. Tests

### Scenario Tests

**Test 1:** Check that it is not possible to create a Scenario with null values.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureNullIsNotAllowed() {
    Scenario instance = new Scenario(null, null, null, null, null, null, null);
}
```

**Test 2:** Check that it is not possible to create a Scenario with invalid date range.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureValidDateRange() {
    Date startDate = new Date(2024, 12, 31);
    Date endDate = new Date(2024, 1, 1);
    
    Scenario instance = new Scenario("SCN001", startDate, endDate, 100000.0, 
        new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
}
```

### CreateScenarioController Tests

**Test 3:** Check that getListOfMaps returns correct DTO list.

```java
@Test
public void ensureGetListOfMapsReturnsCorrectList() {
    CreateScenarioController controller = new CreateScenarioController();
    List<MapDTO> result = controller.getListOfMaps();
    
    assertNotNull(result);
    assertFalse(result.isEmpty());
    // Verify each DTO has required fields
    for (MapDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertTrue(dto.getScale() > 0);
    }
}
```

**Test 4:** Check that createScenario fails with invalid map.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureCreateScenarioFailsWithInvalidMap() {
    CreateScenarioController controller = new CreateScenarioController();
    MapDTO invalidMapDTO = new MapDTO("invalid", 0.0);
    ScenarioDTO scenarioDTO = new ScenarioDTO("SCN001", new Date(), new Date(), 100000.0);
    
    controller.createScenario(invalidMapDTO, scenarioDTO);
}
```

## 5. Construction (Implementation)

### Class CreateScenarioController

```java
public class CreateScenarioController {
    private final Repositories repositories;
    
    public CreateScenarioController() {
        this.repositories = Repositories.getInstance();
    }
    
    public List<MapDTO> getListOfMaps() {
        MapRepository mapRepository = repositories.getMapRepository();
        List<Map> maps = mapRepository.getListOfMaps();
        return MapMapper.toDTO(maps);
    }
    
    public List<LocomotiveTypeDTO> getListOfLocomotiveTypes() {
        LocomotiveTypeRepository typeRepository = repositories.getLocomotiveTypeRepository();
        List<LocomotiveType> types = typeRepository.getListOfLocomotiveTypes();
        return LocomotiveTypeMapper.toDTO(types);
    }
    
    public List<IndustryDTO> getListOfMapIndustries(MapDTO mapDTO) {
        MapRepository mapRepository = repositories.getMapRepository();
        List<Industry> industries = mapRepository.getListOfMapIndustries(mapDTO.getNameID());
        return IndustryMapper.toDTO(industries);
    }
    
    public boolean createScenario(MapDTO mapDTO, ScenarioDTO scenarioDTO) {
        MapRepository mapRepository = repositories.getMapRepository();
        
        // Add cities from map to scenario
        List<CityDTO> listCityDTO = mapDTO.getListOfCityDTO();
        scenarioDTO.addCities(listCityDTO);
        
        // Create scenario using map nameID
        String mapNameID = mapDTO.getNameID();
        return mapRepository.createScenario(scenarioDTO, mapNameID);
    }
}
```

### Class Scenario

```java
public class Scenario {
    private final String nameID;
    private final Date startDate;
    private final Date endDate;
    private final double initialBudget;
    private final List<City> cities;
    private final List<Industry> industries;
    private final List<LocomotiveType> locomotiveTypes;
    
    public Scenario(String nameID, Date startDate, Date endDate, double initialBudget,
                   List<City> cities, List<Industry> industries, List<LocomotiveType> locomotiveTypes) {
        if (nameID == null || startDate == null || endDate == null || 
            cities == null || industries == null || locomotiveTypes == null) {
            throw new IllegalArgumentException("Null values not allowed");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (initialBudget <= 0) {
            throw new IllegalArgumentException("Initial budget must be positive");
        }
        
        this.nameID = nameID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialBudget = initialBudget;
        this.cities = new ArrayList<>(cities);
        this.industries = new ArrayList<>(industries);
        this.locomotiveTypes = new ArrayList<>(locomotiveTypes);
    }
    
    public List<LocomotiveType> getLocomotiveTypes() {
        return Collections.unmodifiableList(locomotiveTypes);
    }
}
```

## 6. Integration and Demo

* A new option was added to the Editor menu for creating scenarios.
* The scenario creation functionality integrates with:
  - Map management (selecting and configuring maps)
  - Industry management (configuring available industries)
  - Locomotive type management (selecting available types)
  - City management (transferring cities from map to scenario)
* For demo purposes, some maps and locomotive types are pre-configured in the system.

## 7. Observations

* The implementation follows a clean separation of concerns:
  - UI layer (CreateScenarioUI)
  - Controller layer (CreateScenarioController)
  - Domain layer (Scenario, Map, Industry, LocomotiveType)
  - Data access layer (MapRepository, LocomotiveTypeRepository)
* The implementation uses several design patterns:
  - Repository pattern for data access
  - DTO pattern for data transfer
  - Mapper pattern for object conversion
  - Controller pattern for coordination