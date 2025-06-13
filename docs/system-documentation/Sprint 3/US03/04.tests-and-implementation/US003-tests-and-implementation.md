# US003 - Add a City to a Map

## 4. Tests

This document outlines the tests and implementation details for the user story US003, which involves adding a city to a map in the system.

### 4.1. Data Validation

**Test 1:** Verify that the city name cannot be null or empty.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureCityNameCannotBeNull() {
    CityDTO dto = new CityDTO(null, new Position(5, 5), 3);
    controller.createCity(dto);
}
```
**Test 2:** Verify if the city is within the boundaries of the map.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureCityIsWithinMapBoundaries() {
    CityDTO dto = new CityDTO("CityX", new Position(999, 999), 2);
    controller.createCity(dto);
}
```
**Test 3:** Must be possible to assign auto blocks to the city.

```java
@Test
public void ensureCityIsAddedWithAutoBlocks() {
    MapDTO mapDTO = new MapDTO("TestMap", 10, 10);
    controller.selectMap(mapDTO);
    
    CityDTO cityDTO = new CityDTO("TestCity", new Position(5, 5), 3);
    List<HouseBlockDTO> blocks = controller.createAutoListHouseBlocksDTO(cityDTO, mapDTO);
    
    assertFalse(blocks.isEmpty());
    assertEquals(3, blocks.size());
    
}
```

**Test 4:** Verify if the city is persisted in the map.

```java
@Test
public void ensureCityIsPersistedInMap() {
    MapDTO mapDTO = new MapDTO("TestMap", 10, 10);
    controller.selectMap(mapDTO);
    
    CityDTO cityDTO = new CityDTO("TestCity", new Position(5, 5), 3);
    controller.createCity(cityDTO);
    
    Map map = mapRepository.getMapByName("TestMap").orElseThrow();
    assertTrue(map.getCities().stream().anyMatch(city -> city.getNameID().equals("TestCity")));
}
```

## 5. Construction (Implementation)

### Class AddCityController

```java
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.HouseBlock;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;

public class AddCityController {
    private final MapRepository mapRepository;


    public AddCityController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();

    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        
        if (!name.matches("^[a-zA-Z0-9]+([_-][a-zA-Z0-9]+)*$")) {
            throw new IllegalArgumentException("City name contains invalid characters or format");
        }

        if (currentMap.getCities().stream().anyMatch(city -> city.getNameID().equals(name))) {
            throw new IllegalArgumentException("City name already exists");
        }

        return name;
    }

    public HouseBlockDTO createHouseBlockDTO(Map map, Position position) {
        if (map == null) {
            throw new IllegalStateException("No map selected");
        }

        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        Map map = mapRepository.getMapByName(map.getNameID())
                .orElseThrow(() -> new IllegalArgumentException("Map not found"));

        map.getCityBoundaries().validatePosition(position);

        map.isPositionOccupied(position);


        HouseBlockDTO houseBlock = new HouseBlockDTO(position, true);
        return houseBlock;
    }

    public List<HouseBlockDTO> createAutoListHouseBlocksDTO(CityDTO cityDTO, MapDTO mapDTO) {
        if (cityDTO == null || mapDTO == null) {
            throw new IllegalArgumentException("City or Map cannot be null");
        }

        Map map = mapRepository.getMapByName(mapDTO.getNameID())
                .orElseThrow(() -> new IllegalArgumentException("Map not found"));


        List<Position> availablePositions = map.getAvailablePositions();
        if (availablePositions.isEmpty()) {
            throw new IllegalStateException("No available positions on the map");
        }

        List<HouseBlockDTO> blocks = new ArrayList<>();
        for (Position position : availablePositions) {
            if (map.isCellEmpty(position.getX(), position.getY())) {
                HouseBlockDTO houseBlock = new HouseBlockDTO(map, position);
                blocks.add(houseBlock);
                map.markPositionAsOccupied(position);
            }
        }

        return blocks;
    }


    public Position validateCoordinates(int x, int y) {
        if (currentMap == null) {
            throw new IllegalStateException("No map selected");
        }

        // Now we return the position even if it's occupied, to allow replacement
        if (x < 0 || x >= currentMap.getSize().getWidth() ||
                y < 0 || y >= currentMap.getSize().getHeight()) {
            throw new IllegalArgumentException("Position (" + x + "," + y + ") is out of bounds");
        }

        return new Position(x, y);
    }


    public City saveCity(MapDTO mapDTO, CityDTO cityDTO) {
        return mapRepository.createCity(mapDTO, cityDTO);
    }
} 
```


## 6. Integration and Demo

* The integration of this functionality required coordination with the map management module and the city creation flow. Specifically:
   - The controller integrates with the MapRepository to retrieve the corresponding map by ID.
   - Once the city object is created, it is added to the Cities collection within the corresponding Map.
   - The house blocks associated with the city are generated automatically (or manually, depending on the mode) and marked as occupied in the Map grid.
   - The system ensures that cities are only created in valid and unoccupied positions within the defined map boundaries.

For demonstration purposes, the feature was tested through a simple UI where users can select a map, input the city name and position, and visualize the blocks allocated to the new city in the map view.


## 7. Observations

* The implementation follows a clean separation of concerns:
    - UI layer (AddCityUI)
    - Controller layer (AddCityController)
    - Domain layer (city, Map, HouseBlock, Position)
    - Data access layer (MapRepository)
* The implementation uses several design patterns:
    - Repository pattern for data access
    - DTO pattern for data transfer
    - Mapper pattern for object conversion
    - Controller pattern for coordination]()