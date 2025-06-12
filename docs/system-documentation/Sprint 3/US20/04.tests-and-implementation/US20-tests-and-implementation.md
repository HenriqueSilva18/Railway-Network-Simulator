# US20 - Load a Map

## 4. Tests
 
**Test 1:** Check that it is not possible to load a map with invalid file.

	@Test(expected = IllegalArgumentException.class)
		public void ensureValidFile() {
		MapRepository repository = new MapRepository();
		repository.deserializeMap("invalid_file.map");
	}

**Test 2:** Check that map creation from deserialized data is valid.

	@Test
		public void ensureValidMapCreation() {
		MapData data = new MapData("TestMap", new Size(100, 100), 1.0);
		Map map = Map.createMap(data);
		assertNotNull(map);
		assertEquals("TestMap", map.getNameID());
		assertEquals(100, map.getSize().getWidth());
		assertEquals(100, map.getSize().getHeight());
		assertEquals(1.0, map.getScale());
	}


## 5. Construction (Implementation)

### Class MapController

```java
public List<String> loadMap() {
    return mapRepository.getSavedMaps();
}

public Map loadSelectedMap(String file) {
    MapData data = mapRepository.deserializeMap(file);
    return Map.createMap(data);
}
```

### Class Map

```java
public static Map createMap(MapData data) {
    // Implementation to create a new Map instance from deserialized data
    return new Map(
        data.getNameID(),
        data.getSize(),
        data.getScale(),
        data.getStations(),
        data.getRoutes()
    );
}
```

### Class MapRepository

```java
public List<String> getSavedMaps() {
    // Implementation to get list of available map files
    return FileUtils.listFilesInDirectory("maps/");
}

public MapData deserializeMap(String file) {
    try {
        // Implementation to read and deserialize map data from file
        String serializedData = FileUtils.readFromFile("maps/" + file);
        return SerializationUtils.deserialize(serializedData, MapData.class);
    } catch (IOException e) {
        throw new IllegalArgumentException("Invalid map file: " + file);
    }
}
```

## 6. Integration and Demo 

* A new option in the Editor menu allows loading previously saved maps.
* The system displays a list of available saved maps.
* After map selection:
  - The system deserializes the map data
  - Creates a new Map instance with all elements
  - Displays the loaded map with:
    - All stations and their details
    - All routes and their configurations
    - Map properties (name, scale, dimensions)
* For demo purposes, different saved maps can be loaded to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The deserialization process ensures that all map data is correctly restored from storage.
* The load operation includes validation to ensure data integrity.