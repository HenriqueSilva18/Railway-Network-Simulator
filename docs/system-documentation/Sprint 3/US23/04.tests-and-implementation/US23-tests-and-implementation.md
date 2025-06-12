# US23 - Save Game Progress

## 4. Tests

**Test 1:** Check that it is not possible to save a game with null state.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		GameRepository repository = new GameRepository();
		repository.saveGame(null);
	}

**Test 2:** Check that game state serialization returns valid data.

	@Test
		public void ensureValidSerialization() {
		Scenario scenario = new Scenario("TestGame");
		GameState gameState = new GameState(
			scenario,
			new Date(),
			1000.0,
			new ArrayList<>(),
			new ArrayList<>()
		);
		String serializedData = scenario.serializeGameState(gameState);
		assertNotNull(serializedData);
		assertTrue(serializedData.length() > 0);
	}


## 5. Construction (Implementation)

### Class SaveGameController

```java
public boolean saveGameProgress() {
    Scenario scenario = getCurrentScenario();
    String serializedState = scenario.serializeGameState(scenario.getCurrentGameState());
    return saveToFile(serializedState);
}

private boolean saveToFile(String serializedState) {
    try {
        GameRepository repository = new GameRepository();
        return repository.saveGame(serializedState);
    } catch (IOException e) {
        return false;
    }
}

private Scenario getCurrentScenario() {
    // Implementation to get the current game scenario
    return currentScenario;
}
```

### Class Scenario

```java
public String serializeGameState(GameState gameState) {
    // Implementation to convert game state to a serialized format
    GameStateData stateData = new GameStateData(
        gameState.getScenario(),
        gameState.getCurrentDate(),
        gameState.getCurrentBudget(),
        gameState.getActiveTrains(),
        gameState.getCompletedDeliveries()
    );
    return SerializationUtils.serialize(stateData);
}
```

### Class GameRepository

```java
public boolean saveGame(String serializedGameData) throws IOException {
    // Implementation to save the serialized game data
    String filePath = getGameFilePath();
    FileUtils.writeToFile(serializedGameData, filePath);
    return true;
}

private String getGameFilePath() {
    // Implementation to get the appropriate file path for saving
    return "games/" + getGameFileName();
}
```

## 6. Integration and Demo 

* A new option in the game menu allows saving the current game progress.
* Before saving:
  - The system displays the current game state
  - Shows current progress (budget, trains, deliveries)
  - Requests save confirmation
* After confirmation:
  - The system serializes the game state
  - Saves the data to a file
  - Displays success message
  - Shows saved game details
* For demo purposes, games can be saved at different progress points to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The serialization process ensures that all game state data is preserved in a format that can be easily stored and retrieved.
* The save operation includes validation to prevent data corruption.
