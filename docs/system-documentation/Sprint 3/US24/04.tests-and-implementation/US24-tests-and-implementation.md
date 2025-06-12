# US24 - Load Game Progress

## 4. Tests

**Test 1:** Check that it is not possible to load a game with invalid file.

	@Test(expected = IllegalArgumentException.class)
		public void ensureValidFile() {
		GameRepository repository = new GameRepository();
		repository.loadGame("invalid_file.game");
	}

**Test 2:** Check that game state deserialization returns valid data.

	@Test
		public void ensureValidDeserialization() {
		Scenario scenario = new Scenario("TestGame");
		String serializedState = "valid_serialized_data";
		GameState gameState = scenario.deserializeGameState(serializedState);
		assertNotNull(gameState);
		assertEquals("TestGame", gameState.getScenario().getName());
		assertNotNull(gameState.getCurrentDate());
		assertTrue(gameState.getCurrentBudget() >= 0);
	}

## 5. Construction (Implementation)

### Class LoadGameController

```java
public List<String> getSavedGamesList() {
    GameRepository repository = new GameRepository();
    return repository.getSavedGamesList();
}

public boolean loadGameProgress(String selectedGame) {
    try {
        String serializedState = readFromFile(selectedGame);
        GameState gameState = Scenario.deserializeGameState(serializedState);
        return restoreGameState(gameState);
    } catch (IOException e) {
        return false;
    }
}

private String readFromFile(String selectedGame) throws IOException {
    GameRepository repository = new GameRepository();
    return repository.loadGame(selectedGame);
}

private boolean restoreGameState(GameState gameState) {
    // Implementation to restore the game state
    currentScenario = gameState.getScenario();
    currentDate = gameState.getCurrentDate();
    currentBudget = gameState.getCurrentBudget();
    activeTrains = gameState.getActiveTrains();
    completedDeliveries = gameState.getCompletedDeliveries();
    return true;
}
```

### Class Scenario

```java
public static GameState deserializeGameState(String serializedState) {
    // Implementation to convert serialized data back to game state
    GameStateData stateData = SerializationUtils.deserialize(serializedState, GameStateData.class);
    return new GameState(
        stateData.getScenario(),
        stateData.getCurrentDate(),
        stateData.getCurrentBudget(),
        stateData.getActiveTrains(),
        stateData.getCompletedDeliveries()
    );
}
```

### Class GameRepository

```java
public List<String> getSavedGamesList() {
    // Implementation to get list of available saved games
    return FileUtils.listFilesInDirectory("games/");
}

public String loadGame(String gameId) throws IOException {
    // Implementation to read game data from file
    String filePath = "games/" + gameId;
    return FileUtils.readFromFile(filePath);
}
```

## 6. Integration and Demo 

* A new option in the game menu allows loading previously saved games.
* The system displays a list of available saved games.
* After game selection:
  - The system reads the saved game data
  - Deserializes the game state
  - Restores all game elements:
    - Current scenario
    - Game date
    - Current budget
    - Active trains
    - Completed deliveries
  - Displays the loaded game details
  - Shows operation success
* For demo purposes, different saved games can be loaded to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, and Repository layers.
* The deserialization process ensures that all game state data is correctly restored from storage.
* The load operation includes validation to ensure data integrity.
