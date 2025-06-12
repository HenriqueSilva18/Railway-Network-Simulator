# US05 - Build a Station

## 4. Tests

**Test 1:** Check that it is not possible to create an instance of the Station class with null values.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		Station instance = new Station(null, null, null, null);
	}

**Test 2:** Check that it is not possible to create a station at an occupied position.

	@Test(expected = IllegalArgumentException.class)
		public void ensurePositionNotOccupied() {
		Position position = new Position(10, 10);
		position.setOccupied(true);
		Station instance = new Station(StationType.STATION, position, buildingDTO, "Station1");
	}


## 5. Construction (Implementation)

### Class StationBuildingController

```java
public List<StationTypeDTO> getStationTypes() {
    Repositories repositories = Repositories.getInstance();
    StationTypeRepository stationTypeRepository = repositories.getStationTypeRepository();
    List<StationType> stationTypes = stationTypeRepository.getListOfStationTypes();
    return StationTypeMapper.toDTO(stationTypes);
}

public List<BuildingDTO> getAvailableBuildings() {
    BuildingRepository buildingRepository = Repositories.getInstance().getBuildingRepository();
    List<Building> buildings = buildingRepository.getListOfBuildings();
    return BuildingMapper.toDTO(buildings);
}

public String getClosestCity(Position position) {
    List<City> cities = Repositories.getInstance().getMapRepository().getListOfCities();
    City closestCity = null;
    double minDistance = Double.MAX_VALUE;
    
    for (City city : cities) {
        double distance = calculateDistance(position, city.getPosition());
        if (distance < minDistance) {
            minDistance = distance;
            closestCity = city;
        }
    }
    
    return closestCity != null ? closestCity.getName() : null;
}

public boolean buildStation(StationDTO stationDTO) {
    MapRepository mapRepository = Repositories.getInstance().getMapRepository();
    return mapRepository.createStation(stationDTO);
}
```

### Class StationMapper

```java
public Station toModel(StationDTO stationDTO) {
    StationType stationType = stationDTO.getStationType();
    Position position = stationDTO.getPosition();
    BuildingDTO buildingDTO = stationDTO.getDefaultBuilding();
    String proposedName = stationDTO.getProposedName();
    
    return new Station(stationType, position, buildingDTO, proposedName);
}
```

### Class Stations

```java
public boolean createStation(StationDTO stationDTO) {
    Station station = StationMapper.toModel(stationDTO);
    return addStation(station);
}

private boolean addStation(Station station) {
    if (validateStation(station)) {
        station.occupyPosition();
        stations.add(station);
        return true;
    }
    return false;
}
```

## 6. Integration and Demo 

* A new option in the UI allows players to build stations on the map.
* The system provides a list of available station types (DEPOT, STATION, TERMINAL).
* For STATION type, the system requests center point selection (NE, SE, NW, SW).
* The system automatically suggests a station name based on the closest city.
* The system displays station cost and economic radius before placement.
* The station placement is validated to ensure it doesn't overlap with existing stations.
* For demo purposes, stations can be placed at different locations to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, Repository, and Domain layers.
* The use of DTOs helps in transferring data between layers while maintaining encapsulation.
* The validation of station placement is handled at the domain level to ensure data integrity.
