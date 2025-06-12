# US19 - Save Map

## 4. Tests

**Test 1:** Check that it is not possible to save a map with null data.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		MapRepository repository = new MapRepository();
		repository.saveMap(null);
	}

**Test 2:** Check that map serialization returns valid data.

	@Test
		public void ensureValidSerialization() {
		Map map = new Map("TestMap", new Size(100, 100), 1.0);
		String serializedData = map.serialize();
		assertNotNull(serializedData);
		assertTrue(serializedData.length() > 0);
	}



## 5. Construction (Implementation)

### Class SaveMapController

```java
public boolean saveMap() {
    Map map = getCurrentMap();
    String serializedMapData = map.serialize();
    return mapRepository.saveMap(serializedMapData);
}

private Map getCurrentMap() {
    // Implementation to get the current map being edited
    return currentMap;
}
```

### Class Map

```java
public String serialize() {
    // Implementation to convert map data to a serialized format
    // This could be JSON, XML, or any other serialization format
    MapData mapData = new MapData(name, size, scale, stations, routes);
    return SerializationUtils.serialize(mapData);
}
```

### Class MapRepository

```java
public boolean saveMap(String serializedMapData) {
    try {
        // Implementation to save the serialized map data
        // This could be to a file, database, or other storage
        FileUtils.writeToFile(serializedMapData, getMapFilePath());
        return true;
    } catch (IOException e) {
        return false;
    }
}

private String getMapFilePath() {
    // Implementation to get the appropriate file path for saving
    return "maps/" + getMapFileName();
}
```

## 6. Integration and Demo 

* A new option in the Editor menu allows saving the current map.
* The system requests confirmation before saving to prevent accidental overwrites.
* The map is serialized to preserve all its properties:
  - Map name and dimensions
  - Scale
  - Station locations and details
  - Route information
* After successful save:
  - The system displays a success message
  - Shows the saved map details
  - Updates the map list in the UI
* For demo purposes, maps can be saved and loaded to demonstrate the persistence functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The serialization process ensures that all map data is preserved in a format that can be easily stored and retrieved.
* The save operation includes validation to prevent data corruption.
 