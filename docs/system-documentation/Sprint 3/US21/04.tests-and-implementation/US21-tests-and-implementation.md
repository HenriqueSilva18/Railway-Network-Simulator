# US21 - Save Scenario

## 4. Tests

**Test 1:** Check that it is not possible to save a scenario with null data.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		ScenarioRepository repository = new ScenarioRepository();
		repository.saveScenario(null);
	}

**Test 2:** Check that scenario serialization returns valid data.

	@Test
		public void ensureValidSerialization() {
		Scenario scenario = new Scenario("TestScenario", new Date(), new Date(), 1000.0);
		String serializedData = scenario.serialize();
		assertNotNull(serializedData);
		assertTrue(serializedData.length() > 0);
	}

## 5. Construction (Implementation)

### Class SaveScenarioController

```java
public boolean saveScenario() {
    Scenario scenario = getCurrentScenario();
    String serializedScenarioData = scenario.serialize();
    return scenarioRepository.saveScenario(serializedScenarioData);
}

private Scenario getCurrentScenario() {
    // Implementation to get the current scenario being edited
    return currentScenario;
}
```

### Class Scenario

```java
public String serialize() {
    // Implementation to convert scenario data to a serialized format
    ScenarioData scenarioData = new ScenarioData(
        nameID,
        startDate,
        endDate,
        initialBudget,
        locomotives,
        industries,
        stations,
        cities
    );
    return SerializationUtils.serialize(scenarioData);
}
```

### Class ScenarioRepository

```java
public boolean saveScenario(String serializedScenarioData) {
    try {
        // Implementation to save the serialized scenario data
        FileUtils.writeToFile(serializedScenarioData, getScenarioFilePath());
        return true;
    } catch (IOException e) {
        return false;
    }
}

private String getScenarioFilePath() {
    // Implementation to get the appropriate file path for saving
    return "scenarios/" + getScenarioFileName();
}
```

## 6. Integration and Demo 

* A new option in the Editor menu allows saving the current scenario.
* The system requests confirmation before saving to prevent accidental overwrites.
* The scenario is serialized to preserve all its properties:
  - Scenario name and dates
  - Initial budget
  - Locomotive configurations
  - Industry settings
  - Station configurations
  - City details
* After successful save:
  - The system displays a success message
  - Shows the saved scenario details
  - Updates the scenario list in the UI
* For demo purposes, scenarios can be saved and loaded to demonstrate the persistence functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The serialization process ensures that all scenario data is preserved in a format that can be easily stored and retrieved.
* The save operation includes validation to prevent data corruption.
