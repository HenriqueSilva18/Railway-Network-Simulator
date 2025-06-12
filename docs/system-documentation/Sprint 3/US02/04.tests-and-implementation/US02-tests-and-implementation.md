# US02 - Add an Industry

## 4. Tests

### Industry Tests

**Test 1:** Check that it is not possible to create an Industry with null values.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureNullIsNotAllowed() {
    Position position = new Position(10.0, 20.0);
    Industry instance = new Industry(null, null, position, 0, null, null);
}
```

**Test 2:** Check that it is not possible to create an Industry with invalid availability year.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureValidAvailabilityYear() {
    Position position = new Position(10.0, 20.0);
    List<String> suppliedCargo = Arrays.asList("Coal");
    List<String> demandedCargo = Arrays.asList("Steel");
    
    Industry instance = new Industry("IND001", "Mining", position, -1, 
        suppliedCargo, demandedCargo);
}
```

### Position Tests

**Test 3:** Check that position availability is correctly managed.

```java
@Test
public void ensurePositionAvailabilityIsCorrect() {
    Position position = new Position(10.0, 20.0);
    
    assertTrue(position.isAvailable());
    
    position.setOccupied();
    
    assertFalse(position.isAvailable());
}
```

**Test 4:** Check that position coordinates are correctly set.

```java
@Test
public void ensurePositionCoordinatesAreCorrect() {
    double x = 15.5;
    double y = 25.3;
    Position position = new Position(x, y);
    
    assertEquals(x, position.getX(), 0.001);
    assertEquals(y, position.getY(), 0.001);
}
```

### AddIndustryController Tests

**Test 5:** Check that getListOfMaps returns correct DTO list.

```java
@Test
public void ensureGetListOfMapsReturnsCorrectList() {
    AddIndustryController controller = new AddIndustryController();
    List<MapDTO> result = controller.getListOfMaps();
    
    assertNotNull(result);
    assertFalse(result.isEmpty());

    for (MapDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertTrue(dto.getScale() > 0);
    }
}
```

**Test 6:** Check that getListOfIndustrySectors returns correct DTO list.

```java
@Test
public void ensureGetListOfIndustrySectorsReturnsCorrectList() {
    AddIndustryController controller = new AddIndustryController();
    List<IndustrySectorDTO> result = controller.getListOfIndustrySectors();
    
    assertNotNull(result);
    assertFalse(result.isEmpty());

    for (IndustrySectorDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertNotNull(dto.getType());
    }
}
```

**Test 7:** Check that addIndustry fails with occupied position.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureAddIndustryFailsWithOccupiedPosition() {
    AddIndustryController controller = new AddIndustryController();
    MapDTO mapDTO = new MapDTO("TestMap", 1.0);
    
    // First industry
    IndustryDTO industryDTO1 = new IndustryDTO("IND001", "Mining", 10.0, 20.0, 
        1850, Arrays.asList("Coal"), Arrays.asList());
    controller.createAndAddIndustry(mapDTO, industryDTO1);
    
    // Second industry at same position - should fail
    IndustryDTO industryDTO2 = new IndustryDTO("IND002", "Steel", 10.0, 20.0, 
        1860, Arrays.asList("Steel"), Arrays.asList("Coal"));
    controller.createAndAddIndustry(mapDTO, industryDTO2);
}
```

**Test 8:** Check that loadMap returns correct map layout.

```java
@Test
public void ensureLoadMapReturnsCorrectLayout() {
    AddIndustryController controller = new AddIndustryController();
    MapDTO mapDTO = new MapDTO("TestMap", 1.0);
    
    String mapLayout = controller.loadMap(mapDTO);
    
    assertNotNull(mapLayout);
    assertFalse(mapLayout.isEmpty());
}
```

## 5. Construction (Implementation)

### Class AddIndustryController

```java
public class AddIndustryController {
    private final Repositories repositories;
    private final ApplicationSession applicationSession;
    
    public AddIndustryController() {
        this.repositories = Repositories.getInstance();
        this.applicationSession = ApplicationSession.getInstance();
    }
    
    public List<MapDTO> getListOfMaps() {
        MapRepository mapRepository = repositories.getMapRepository();
        List<Map> maps = mapRepository.getListOfMaps();
        return MapMapper.toDTO(maps);
    }
    
    public String loadMap(MapDTO mapDTO) {
        MapRepository mapRepository = repositories.getMapRepository();
        String mapId = mapDTO.getNameID();
        Map currentMap = mapRepository.getMapById(mapId);
        return currentMap.getMapLayout();
    }
    
    public List<IndustrySectorDTO> getListOfIndustrySectors() {
        IndustrySectorRepository sectorRepository = repositories.getIndustrySectorRepository();
        List<IndustrySector> sectors = sectorRepository.getListOfIndustrySectors();
        return IndustrySectorMapper.toDTO(sectors);
    }
    
    public IndustryDTO addIndustry(String nameID, double x, double y, 
                                  IndustrySectorDTO sector, int availabilityYear,
                                  List<String> suppliedCargo, List<String> demandedCargo) {
        return new IndustryDTO(nameID, sector.getType(), x, y, availabilityYear, 
                              suppliedCargo, demandedCargo);
    }
    
    public boolean createAndAddIndustry(MapDTO mapDTO, IndustryDTO industryDTO) {
        MapRepository mapRepository = repositories.getMapRepository();
        String mapId = mapDTO.getNameID();
        Map currentMap = mapRepository.getMapById(mapId);
        
        return currentMap.createAndAddIndustry(industryDTO);
    }
}
```

### Class Industry

```java
public class Industry {
    private final String nameID;
    private final String type;
    private final Position position;
    private final int availabilityYear;
    private final List<String> suppliedCargo;
    private final List<String> demandedCargo;
    
    public Industry(String nameID, String type, Position position, int availabilityYear,
                   List<String> suppliedCargo, List<String> demandedCargo) {
        if (nameID == null || type == null || position == null || 
            suppliedCargo == null || demandedCargo == null) {
            throw new IllegalArgumentException("Null values not allowed");
        }
        if (availabilityYear < 0) {
            throw new IllegalArgumentException("Availability year must be non-negative");
        }
        if (nameID.trim().isEmpty() || type.trim().isEmpty()) {
            throw new IllegalArgumentException("NameID and type cannot be empty");
        }
        
        this.nameID = nameID;
        this.type = type;
        this.position = position;
        this.availabilityYear = availabilityYear;
        this.suppliedCargo = new ArrayList<>(suppliedCargo);
        this.demandedCargo = new ArrayList<>(demandedCargo);
    }
    
    public String getNameID() {
        return nameID;
    }
    
    public String getType() {
        return type;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public int getAvailabilityYear() {
        return availabilityYear;
    }
    
    public List<String> getSuppliedCargo() {
        return Collections.unmodifiableList(suppliedCargo);
    }
    
    public List<String> getDemandedCargo() {
        return Collections.unmodifiableList(demandedCargo);
    }
}
```

### Class Position

```java
public class Position {
    private final double x;
    private final double y;
    private boolean occupied;
    
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public boolean isAvailable() {
        return !occupied;
    }
    
    public void setOccupied() {
        this.occupied = true;
    }
    
    public void setAvailable() {
        this.occupied = false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Position position = (Position) obj;
        return Double.compare(position.x, x) == 0 && 
               Double.compare(position.y, y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
```

### Class Map (createAndAddIndustry method)

```java
public boolean createAndAddIndustry(IndustryDTO industryDTO) {
    // Check if position is available
    if (!positions.isPositionAvailable(industryDTO.getX(), industryDTO.getY())) {
        throw new IllegalArgumentException("Position is already occupied");
    }
    
    // Add industry to the map
    boolean success = industries.addIndustry(industryDTO);
    
    if (success) {
        // Mark position as occupied
        positions.setOccupied(industryDTO.getX(), industryDTO.getY());
    }
    
    return success;
}
```

### Class IndustryDTO

```java
public class IndustryDTO {
    private final String nameID;
    private final String type;
    private final double x;
    private final double y;
    private final int availabilityYear;
    private final List<String> suppliedCargo;
    private final List<String> demandedCargo;
    
    public IndustryDTO(String nameID, String type, double x, double y, 
                      int availabilityYear, List<String> suppliedCargo, 
                      List<String> demandedCargo) {
        this.nameID = nameID;
        this.type = type;
        this.x = x;
        this.y = y;
        this.availabilityYear = availabilityYear;
        this.suppliedCargo = new ArrayList<>(suppliedCargo);
        this.demandedCargo = new ArrayList<>(demandedCargo);
    }
    
    public String getNameID() { return nameID; }
    public String getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getAvailabilityYear() { return availabilityYear; }
    public List<String> getSuppliedCargo() { return Collections.unmodifiableList(suppliedCargo); }
    public List<String> getDemandedCargo() { return Collections.unmodifiableList(demandedCargo); }
    
    public Position getPosition() {
        return new Position(x, y);
    }
}
```

## 6. Integration and Demo

* A new option was added to the Editor menu for adding industries to maps.
* The add industry functionality integrates with:
    - Map management (loading and displaying maps)
    - Industry sector management (selecting industry types)
    - Position management (validating and occupying positions)
    - Industry collection management (adding new industries to maps)
* For demo purposes, some maps and industry sectors are pre-configured in the system.
* The system validates position availability before allowing industry placement.

## 7. Observations

* The implementation follows a clean separation of concerns:
    - UI layer (AddIndustryUI)
    - Controller layer (AddIndustryController)
    - Domain layer (Industry, Position, IndustrySector, Map)
    - Data access layer (MapRepository, IndustrySectorRepository)
* The implementation uses several design patterns:
    - Repository pattern for data access
    - DTO pattern for data transfer
    - Mapper pattern for object conversion
    - Controller pattern for coordination
* Position validation ensures no two industries can occupy the same location.
* The Industry class enforces business rules through validation in the constructor.
* The system maintains referential integrity between industries and their positions on the map.