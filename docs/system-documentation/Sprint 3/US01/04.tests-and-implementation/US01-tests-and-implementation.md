# US01 - Create a Map

## 4. Tests

**Test 1:** Check that it is not possible to create an instance of the Map class with null values.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		Map instance = new Map(null, null, null);
	}

**Test 2:** Check that it is not possible to create an instance of the Map class with invalid dimensions.

	@Test(expected = IllegalArgumentException.class)
		public void ensureValidDimensions() {
		Size size = new Size(-1, -1);
		Map instance = new Map("Map1", size, 1.0);
	}


## 5. Construction (Implementation)

### Class CreateMapController

```java
public Map createMap(MapDTO mapDTO) {
    Repositories repositories = Repositories.getInstance();
    MapRepository mapRepository = repositories.getMapRepository();
    
    return mapRepository.createMap(mapDTO);
}
```

### Class MapRepository

```java
public Map createMap(MapDTO mapDTO) {
    Maps maps = new Maps();
    return maps.createMap(mapDTO);
}
```

### Class Maps

```java
public Map createMap(MapDTO mapDTO) {
    Map map = MapMapper.toModel(mapDTO);
    addMap(map);
    return map;
}

private void addMap(Map map) {
    validateMap(map);
    maps.add(map);
}
```

## 6. Integration and Demo 

* A new option on the Editor menu options was added to create maps.
* The system allows creating maps with specified name, dimensions, and scale.
* The created map is validated and stored in the system.
* For demo purposes, some sample maps can be created to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, Repository, and Domain layers.
* The use of DTOs helps in transferring data between layers while maintaining encapsulation.
* The validation of map properties is handled at the domain level to ensure data integrity.