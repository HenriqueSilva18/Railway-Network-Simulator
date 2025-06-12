# US22 - Load Scenario

## 4. Tests

**Test 1:** Check that it is not possible to load a scenario with invalid file.

	@Test(expected = IllegalArgumentException.class)
		public void ensureValidFile() {
		MapRepository repository = new MapRepository();
		repository.deserializeScenario("invalid_file.scenario");
	}

**Test 2:** Check that scenario creation from deserialized data is valid.

	@Test
		public void ensureValidScenarioCreation() {
		ScenarioData data = new ScenarioData(
			"TestScenario",
			new Date(),
			new Date(),
			1000.0,
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
		Scenario scenario = Scenario.createScenario(data);
		assertNotNull(scenario);
		assertEquals("TestScenario", scenario.getNameID());
		assertEquals(1000.0, scenario.getInitialBudget());
	}


## 5. Construction (Implementation)

### Class ScenarioController

```java
public List<String> loadScenario() {
    return mapRepository.getAvailableMaps();
}

public List<String> getScenariosForMap(String selectedMap) {
    return mapRepository.getScenariosForMap(selectedMap);
}

public Scenario loadSelectedScenario(String file) {
    ScenarioData data = mapRepository.deserializeScenario(file);
    return Scenario.createScenario(data);
}
```

### Class Scenario

```java
public static Scenario createScenario(ScenarioData data) {
    // Implementation to create a new Scenario instance from deserialized data
    return new Scenario(
        data.getNameID(),
        data.getStartDate(),
        data.getEndDate(),
        data.getInitialBudget(),
        data.getLocomotives(),
        data.getIndustries(),
        data.getStations(),
        data.getCities()
    );
}
```

### Class MapRepository

```java
public List<String> getAvailableMaps() {
    // Implementation to get list of available map files
    return FileUtils.listFilesInDirectory("maps/");
}

public List<String> getScenariosForMap(String selectedMap) {
    // Implementation to get list of scenarios for a specific map
    return FileUtils.listFilesInDirectory("scenarios/" + selectedMap + "/");
}

public ScenarioData deserializeScenario(String file) {
    try {
        // Implementation to read and deserialize scenario data from file
        String serializedData = FileUtils.readFromFile("scenarios/" + file);
        return SerializationUtils.deserialize(serializedData, ScenarioData.class);
    } catch (IOException e) {
        throw new IllegalArgumentException("Invalid scenario file: " + file);
    }
}
```

## 6. Integration and Demo 

* A new option in the Editor menu allows loading previously saved scenarios.
* The system first displays a list of available maps.
* After map selection:
  - The system shows available scenarios for the selected map
  - After scenario selection:
    - The system deserializes the scenario data
    - Creates a new Scenario instance with all elements
    - Displays the loaded scenario with:
      - Basic details (name, dates, budget)
      - Locomotive configurations
      - Industry settings
      - Station configurations
      - City details
* For demo purposes, different saved scenarios can be loaded to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The deserialization process ensures that all scenario data is correctly restored from storage.
* The load operation includes validation to ensure data integrity.
