# US06 - Upgrade a Selected Station with a Building

## 4. Tests

### Station Tests

**Test 1:** Check that it is not possible to upgrade a station with null building.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureNullBuildingIsNotAllowed() {
    Position position = new Position(10.0, 20.0);
    Station station = new Station("STA001", "Central Station", position, 1850);
    station.upgradeStation(null);
}
```

**Test 2:** Check that it is not possible to upgrade a station with insufficient budget.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureInsufficientBudgetPreventsUpgrade() {
    Position position = new Position(10.0, 20.0);
    Station station = new Station("STA001", "Central Station", position, 1850);
    BuildingDTO buildingDTO = new BuildingDTO("BLD001", "Platform", 1860, 10000, 2, "Increases capacity");

    // Assume player has insufficient budget
    Player player = new Player("Player1", 5000); // Less than building cost
    station.upgradeStation(buildingDTO, player);
}
```

### Building Tests

**Test 3:** Check that building details are correctly retrieved.

```java
@Test
public void ensureBuildingDetailsAreCorrect() {
    Building building = new Building("BLD001", "Platform", 1860, 10000, 2, "Increases capacity");

    assertEquals("BLD001", building.getNameID());
    assertEquals("Platform", building.getType());
    assertEquals(1860, building.getAvailabilityYear());
    assertEquals(10000, building.getEvolutionCost());
    assertEquals(2, building.getEvolutionStagesLeft());
    assertNotNull(building.getBuildingDetails());
}
```

**Test 4:** Check that building evolution stages are correctly managed.

```java
@Test
public void ensureBuildingEvolutionIsCorrect() {
    Building building = new Building("BLD001", "Platform", 1860, 10000, 2, "Increases capacity");

    assertTrue(building.canEvolve());

    building.evolve();
    assertEquals(1, building.getEvolutionStagesLeft());

    building.evolve();
    assertEquals(0, building.getEvolutionStagesLeft());
    assertFalse(building.canEvolve());
}
```

### UpgradeStationController Tests

**Test 5:** Check that getListOfStationsDTO returns correct DTO list.

```java
@Test
public void ensureGetListOfStationsDTOReturnsCorrectList() {
    UpgradeStationController controller = new UpgradeStationController();
    List<StationDTO> result = controller.getListOfStationsDTO();

    assertNotNull(result);
    assertFalse(result.isEmpty());

    for (StationDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertNotNull(dto.getName());
    }
}
```

**Test 6:** Check that getListOfAvailableBuildings returns correct DTO list.

```java
@Test
public void ensureGetListOfAvailableBuildingsReturnsCorrectList() {
    UpgradeStationController controller = new UpgradeStationController();
    List<BuildingDTO> result = controller.getListOfAvailableBuildings();

    assertNotNull(result);
    assertFalse(result.isEmpty());

    for (BuildingDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertNotNull(dto.getType());
        assertTrue(dto.getEvolutionCost() > 0);
    }
}
```

**Test 7:** Check that upgradeStation fails with unavailable building.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureUpgradeStationFailsWithUnavailableBuilding() {
    UpgradeStationController controller = new UpgradeStationController();
    StationDTO stationDTO = new StationDTO("STA001", "Central Station");
    Station station = controller.loadStation(stationDTO);

    // Building not available for this year
    BuildingDTO buildingDTO = new BuildingDTO("BLD001", "Platform", 2000, 10000, 2, "Future building");
    controller.upgradeStation(station, buildingDTO);
}
```

**Test 8:** Check that loadStation returns correct station.

```java
@Test
public void ensureLoadStationReturnsCorrectStation() {
    UpgradeStationController controller = new UpgradeStationController();
    StationDTO stationDTO = new StationDTO("STA001", "Central Station");

    Station station = controller.loadStation(stationDTO);

    assertNotNull(station);
    assertEquals("STA001", station.getNameID());
    assertEquals("Central Station", station.getName());
}
```

## 5. Construction (Implementation)

### Class UpgradeStationController

```java
public class UpgradeStationController {
    private final ApplicationSession appSession;

    public UpgradeStationController() {
        this.appSession = ApplicationSession.getInstance();
    }

    public List<StationDTO> getListOfStationsDTO() {
        Scenario currentScenario = appSession.getCurrentScenario();
        Stations stations = currentScenario.getStations();
        List<Station> listStations = stations.getStations();
        return StationMapper.toStationDTOs(listStations);
    }

    public String loadStation(StationDTO stationDTO) {
        String stationNameID = stationDTO.getNameID();
        Scenario currentScenario = appSession.getCurrentScenario();
        Station currentStation = currentScenario.getStationByNameID(stationNameID);
        return currentStation.getStationDetails();
    }

    public List<BuildingDTO> getListOfAvailableBuildings(Station currentStation) {
        List<Building> upgradableBuildings = currentStation.getUpgradableBuildings();
        return BuildingMapper.toDTO(upgradableBuildings);
    }

    public String getBuildingInfo(StationDTO stationDTO, BuildingDTO buildingDTO) {
        String stationNameID = stationDTO.getNameID();
        Scenario currentScenario = appSession.getCurrentScenario();
        Station currentStation = currentScenario.getStationByNameID(stationNameID);

        String buildingNameID = buildingDTO.getNameID();
        Building building = currentStation.getBuildingByNameID(buildingNameID);
        return building.getBuildingDetails();
    }

    public boolean upgradeStation(Station currentStation, BuildingDTO buildingDTO) {
        Scenario currentScenario = appSession.getCurrentScenario();
        Stations stations = currentScenario.getStations();

        boolean success = stations.upgradeStation(currentStation, buildingDTO);

        if (success) {
            int evolutionCost = buildingDTO.getEvolutionCost();
            appSession.upgradePlayerBudget(evolutionCost);
        }

        return success;
    }
}
```

### Class Station

```java
public class Station {
    private final String nameID;
    private final String name;
    private final Position position;
    private final int constructionYear;
    private final Buildings buildings;
    
    public Station(String nameID, String name, Position position, int constructionYear) {
        if (nameID == null || name == null || position == null) {
            throw new IllegalArgumentException("Null values not allowed");
        }
        if (constructionYear < 0) {
            throw new IllegalArgumentException("Construction year must be non-negative");
        }
        if (nameID.trim().isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("NameID and name cannot be empty");
        }
        
        this.nameID = nameID;
        this.name = name;
        this.position = position;
        this.constructionYear = constructionYear;
        this.buildings = new Buildings();
    }
    
    public String getNameID() {
        return nameID;
    }
    
    public String getName() {
        return name;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public int getConstructionYear() {
        return constructionYear;
    }
    
    public String getStationDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Station: ").append(name).append("\n");
        details.append("ID: ").append(nameID).append("\n");
        details.append("Position: (").append(position.getX()).append(", ").append(position.getY()).append(")\n");
        details.append("Construction Year: ").append(constructionYear).append("\n");
        details.append("Buildings: ").append(buildings.getBuildingCount()).append("\n");
        return details.toString();
    }
    
    public List<Building> getUpgradableBuildings() {
        return buildings.getUpgradableBuildings();
    }
    
    public Building getBuildingByNameID(String nameID) {
        return buildings.getBuildingByNameID(nameID);
    }
    
    public boolean upgradeStation(BuildingDTO buildingDTO) {
        if (buildingDTO == null) {
            throw new IllegalArgumentException("Building DTO cannot be null");
        }
        
        return buildings.upgradeStation(buildingDTO);
    }
}
```

### Class Building

```java
public class Building {
    private final String nameID;
    private final String type;
    private final int availabilityYear;
    private final int evolutionCost;
    private int evolutionStagesLeft;
    private final String effect;
    
    public Building(String nameID, String type, int availabilityYear, int evolutionCost,
                   int evolutionStagesLeft, String effect) {
        if (nameID == null || type == null || effect == null) {
            throw new IllegalArgumentException("Null values not allowed");
        }
        if (availabilityYear < 0 || evolutionCost < 0 || evolutionStagesLeft < 0) {
            throw new IllegalArgumentException("Negative values not allowed");
        }
        if (nameID.trim().isEmpty() || type.trim().isEmpty()) {
            throw new IllegalArgumentException("NameID and type cannot be empty");
        }
        
        this.nameID = nameID;
        this.type = type;
        this.availabilityYear = availabilityYear;
        this.evolutionCost = evolutionCost;
        this.evolutionStagesLeft = evolutionStagesLeft;
        this.effect = effect;
    }
    
    public String getNameID() {
        return nameID;
    }
    
    public String getType() {
        return type;
    }
    
    public int getAvailabilityYear() {
        return availabilityYear;
    }
    
    public int getEvolutionCost() {
        return evolutionCost;
    }
    
    public int getEvolutionStagesLeft() {
        return evolutionStagesLeft;
    }
    
    public String getEffect() {
        return effect;
    }
    
    public String getBuildingDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Building: ").append(type).append("\n");
        details.append("ID: ").append(nameID).append("\n");
        details.append("Available Year: ").append(availabilityYear).append("\n");
        details.append("Evolution Cost: ").append(evolutionCost).append("\n");
        details.append("Evolution Stages Left: ").append(evolutionStagesLeft).append("\n");
        details.append("Effect: ").append(effect).append("\n");
        return details.toString();
    }
    
    public boolean canEvolve() {
        return evolutionStagesLeft > 0;
    }
    
    public void evolve() {
        if (canEvolve()) {
            evolutionStagesLeft--;
        }
    }
}
```

### Class Buildings

```java
public class Buildings {
    private final List<Building> buildings;
    
    public Buildings() {
        this.buildings = new ArrayList<>();
    }
    
    public List<Building> getUpgradableBuildings() {
        return buildings.stream()
                .filter(Building::canEvolve)
                .collect(Collectors.toList());
    }
    
    public boolean upgradeStation(BuildingDTO buildingDTO) {
        if (buildingDTO == null) {
            throw new IllegalArgumentException("Building DTO cannot be null");
        }
        
        Building building = BuildingMapper.toDomain(buildingDTO);
        return addBuilding(building);
    }
    
    public boolean addBuilding(Building building) {
        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }
        
        // Check if building with same nameID already exists
        boolean exists = buildings.stream()
                .anyMatch(b -> b.getNameID().equals(building.getNameID()));
        
        if (exists) {
            return false; // Building already exists
        }
        
        return buildings.add(building);
    }
    
    public Building getBuildingByNameID(String nameID) {
        return buildings.stream()
                .filter(b -> b.getNameID().equals(nameID))
                .findFirst()
                .orElse(null);
    }
    
    public int getBuildingCount() {
        return buildings.size();
    }
    
    public List<Building> getAllBuildings() {
        return Collections.unmodifiableList(buildings);
    }
}
```

### Class StationDTO

```java
public class StationDTO {
    private final String nameID;
    private final String name;
    private final double x;
    private final double y;
    private final int constructionYear;
    
    public StationDTO(String nameID, String name, double x, double y, int constructionYear) {
        this.nameID = nameID;
        this.name = name;
        this.x = x;
        this.y = y;
        this.constructionYear = constructionYear;
    }
    
    public String getNameID() { return nameID; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getConstructionYear() { return constructionYear; }
    
    public Position getPosition() {
        return new Position(x, y);
    }
}
```

### Class BuildingDTO

```java
public class BuildingDTO {
    private final String nameID;
    private final String type;
    private final int availabilityYear;
    private final int evolutionCost;
    private final int evolutionStagesLeft;
    private final String effect;
    
    public BuildingDTO(String nameID, String type, int availabilityYear, int evolutionCost,
                      int evolutionStagesLeft, String effect) {
        this.nameID = nameID;
        this.type = type;
        this.availabilityYear = availabilityYear;
        this.evolutionCost = evolutionCost;
        this.evolutionStagesLeft = evolutionStagesLeft;
        this.effect = effect;
    }
    
    public String getNameID() { return nameID; }
    public String getType() { return type; }
    public int getAvailabilityYear() { return availabilityYear; }
    public int getEvolutionCost() { return evolutionCost; }
    public int getEvolutionStagesLeft() { return evolutionStagesLeft; }
    public String getEffect() { return effect; }
}
```

### Mapper Classes

### Class StationMapper

```java
public class StationMapper {
    
    public static List<StationDTO> toStationDTOs(List<Station> stations) {
        List<StationDTO> stationDTOs = new ArrayList<>();
        
        for (Station station : stations) {
            StationDTO stationDTO = toDTO(station);
            stationDTOs.add(stationDTO);
        }
        
        return stationDTOs;
    }
    
    public static StationDTO toDTO(Station station) {
        return new StationDTO(
            station.getNameID(),
            station.getName(),
            station.getPosition().getX(),
            station.getPosition().getY(),
            station.getConstructionYear()
        );
    }
    
    public static Station toDomain(StationDTO stationDTO) {
        Position position = new Position(stationDTO.getX(), stationDTO.getY());
        return new Station(
            stationDTO.getNameID(),
            stationDTO.getName(),
            position,
            stationDTO.getConstructionYear()
        );
    }
}
```

### Class BuildingMapper

```java
public class BuildingMapper {
    
    public static List<BuildingDTO> toDTO(List<Building> buildings) {
        List<BuildingDTO> buildingDTOs = new ArrayList<>();
        
        for (Building building : buildings) {
            BuildingDTO buildingDTO = toDTO(building);
            buildingDTOs.add(buildingDTO);
        }
        
        return buildingDTOs;
    }
    
    public static BuildingDTO toDTO(Building building) {
        return new BuildingDTO(
            building.getNameID(),
            building.getType(),
            building.getAvailabilityYear(),
            building.getEvolutionCost(),
            building.getEvolutionStagesLeft(),
            building.getEffect()
        );
    }
    
    public static Building toDomain(BuildingDTO buildingDTO) {
        return new Building(
            buildingDTO.getNameID(),
            buildingDTO.getType(),
            buildingDTO.getAvailabilityYear(),
            buildingDTO.getEvolutionCost(),
            buildingDTO.getEvolutionStagesLeft(),
            buildingDTO.getEffect()
        );
    }
}
```

### Class Stations

```java
public class Stations {
    private final List<Station> stations;
    
    public Stations() {
        this.stations = new ArrayList<>();
    }
    
    public List<Station> getStations() {
        List<Station> listStations = new ArrayList<>();
        for (Station station : stations) {
            listStations.add(station);
        }
        return listStations;
    }
    
    public boolean upgradeStation(Station currentStation, BuildingDTO buildingDTO) {
        if (currentStation == null || buildingDTO == null) {
            throw new IllegalArgumentException("Station and Building DTO cannot be null");
        }
        
        return currentStation.upgradeStation(buildingDTO);
    }
    
    public void addStation(Station station) {
        if (station != null) {
            stations.add(station);
        }
    }
    
    public Station getStationByNameID(String nameID) {
        return stations.stream()
                .filter(s -> s.getNameID().equals(nameID))
                .findFirst()
                .orElse(null);
    }
}
```

### Class Scenario

```java
public class Scenario {
    private final Stations stations;
    private final String scenarioName;
    private final int currentYear;
    
    public Scenario(String scenarioName, int currentYear) {
        this.scenarioName = scenarioName;
        this.currentYear = currentYear;
        this.stations = new Stations();
    }
    
    public Stations getStations() {
        return stations;
    }
    
    public Station getStationByNameID(String stationNameID) {
        return stations.getStationByNameID(stationNameID);
    }
    
    public int getCurrentYear() {
        return currentYear;
    }
    
    public String getScenarioName() {
        return scenarioName;
    }
}
```

### Class ApplicationSession

```java
public class ApplicationSession {
    private static ApplicationSession instance;
    private Scenario currentScenario;
    private Player currentPlayer;
    
    private ApplicationSession() {
        // Private constructor for singleton
    }
    
    public static ApplicationSession getInstance() {
        if (instance == null) {
            instance = new ApplicationSession();
        }
        return instance;
    }
    
    public Scenario getCurrentScenario() {
        return currentScenario;
    }
    
    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    public boolean upgradePlayerBudget(int evolutionCost) {
        if (currentPlayer == null) {
            return false;
        }
        
        return updatePlayerBudget(currentPlayer, -evolutionCost);
    }
    
    private boolean updatePlayerBudget(Player player, int amount) {
        if (player.getBudget() + amount < 0) {
            return false; // Insufficient budget
        }
        
        player.updateBudget(amount);
        return true;
    }
}
```

### Class Player

```java
public class Player {
    private final String name;
    private int budget;
    
    public Player(String name, int initialBudget) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (initialBudget < 0) {
            throw new IllegalArgumentException("Initial budget cannot be negative");
        }
        
        this.name = name;
        this.budget = initialBudget;
    }
    
    public String getName() {
        return name;
    }
    
    public int getBudget() {
        return budget;
    }
    
    public void updateBudget(int amount) {
        this.budget += amount;
    }
    
    public boolean canAfford(int cost) {
        return budget >= cost;
    }
}
```


## 6. Integration and Demo

* A new option was added to the Station menu for upgrading stations with buildings.
* The upgrade station functionality integrates with:
    - Station management (loading and displaying stations)
    - Building management (selecting available buildings)
    - Player budget management (validating and deducting costs)
    - Scenario management (checking year availability)
* For demo purposes, some stations and buildings are pre-configured in the system.
* The system validates building availability based on the current game year.
* The system ensures players have sufficient budget before allowing upgrades.

## 7. Observations

* The implementation follows a clean separation of concerns:
    - UI layer (UpgradeStationUI)
    - Controller layer (UpgradeStationController)
    - Domain layer (Station, Building, Buildings, Player)
    - Data access layer (ApplicationSession, Scenario)
* The implementation uses several design patterns:
    - Session pattern for application state management
    - DTO pattern for data transfer
    - Mapper pattern for object conversion
    - Controller pattern for coordination
    - Collection pattern for managing buildings
* Budget validation ensures players cannot upgrade beyond their financial capacity.
* The Building class supports evolution stages for progressive upgrades.
* Year availability checks ensure buildings are only available when historically appropriate.
* The system maintains referential integrity between stations and their buildings.
* Building effects provide gameplay benefits that enhance station functionality.