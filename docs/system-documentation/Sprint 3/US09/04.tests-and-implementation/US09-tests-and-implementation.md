# US09 - Buy a Locomotive

## 4. Tests

### Locomotive Tests

**Test 1:** Check that it is not possible to purchase a locomotive when player has insufficient funds.

```java
@Test(expected = IllegalStateException.class)
public void ensurePlayerHasSufficientFunds() {
    Player player = new Player("player1", "pass");
    Scenario scenario = new Scenario("scenario1", startDate, endDate, 1000.0);
    Locomotive locomotive = new Locomotive("loc1", player, type, 1000.0, 10.0, 100.0, 50.0, 1950, 10.0, 5.0, 1950, 2000.0, 100.0, 12, 0.1);
    
    locomotive.purchaseLocomotive(player, scenario);
}
```

### BuyLocomotiveController Tests

**Test 2:** Check that getAvailableLocomotives returns correct DTO list.

```java
@Test
public void ensureGetAvailableLocomotivesReturnsCorrectList() {
    BuyLocomotiveController controller = new BuyLocomotiveController();
    List<LocomotiveDTO> result = controller.getAvailableLocomotives();
    
    assertNotNull(result);
    assertFalse(result.isEmpty());
    // Verify each DTO has required fields
    for (LocomotiveDTO dto : result) {
        assertNotNull(dto.getNameID());
        assertNotNull(dto.getType());
        assertTrue(dto.getAcquisitionPrice() > 0);
    }
}
```

**Test 3:** Check that purchaseLocomotive fails with invalid locomotive.

```java
@Test(expected = IllegalArgumentException.class)
public void ensurePurchaseFailsWithInvalidLocomotive() {
    BuyLocomotiveController controller = new BuyLocomotiveController();
    LocomotiveDTO invalidDTO = new LocomotiveDTO("invalid", null, 0.0, 0.0, 0.0, 0);
    
    controller.purchaseLocomotive(invalidDTO);
}
```

## 5. Construction (Implementation)

### Class BuyLocomotiveController

```java
public class BuyLocomotiveController {
    private final Repositories repositories;
    private final ApplicationSession applicationSession;
    
    public BuyLocomotiveController() {
        this.repositories = Repositories.getInstance();
        this.applicationSession = ApplicationSession.getInstance();
    }
    
    public List<LocomotiveDTO> getAvailableLocomotives() {
        UserSession session = applicationSession.getCurrentSession();
        Date currentDate = session.getCurrentDate();
        Scenario scenario = session.getCurrentScenario();
        List<LocomotiveType> types = scenario.getLocomotiveTypes();
        
        LocomotiveRepository repo = repositories.getLocomotiveRepository();
        List<Locomotive> locomotives = repo.getAvailableLocomotives(currentDate, types);
        
        return LocomotiveMapper.toDTO(locomotives);
    }
    
    public LocomotiveDTO getLocomotive(LocomotiveDTO dto) {
        LocomotiveRepository repo = repositories.getLocomotiveRepository();
        Locomotive locomotive = repo.getLocomotive(dto.getNameID());
        
        return LocomotiveMapper.toDTO(locomotive);
    }
    
    public boolean purchaseLocomotive(LocomotiveDTO dto) {
        UserSession session = applicationSession.getCurrentSession();
        Player player = session.getCurrentPlayer();
        Scenario scenario = session.getCurrentScenario();
        
        LocomotiveRepository repo = repositories.getLocomotiveRepository();
        Locomotive locomotive = repo.getLocomotive(dto.getNameID());
        
        return repo.purchaseLocomotive(locomotive, player, scenario);
    }
}
```

### Class Locomotive

```java
public class Locomotive {
    private final String nameID;
    private Player owner;
    private final LocomotiveType type;
    private final double power;
    private final double acceleration;
    private final double topSpeed;
    private double actualSpeed;
    private final int startYear;
    private final double fuelAcquisitionPrice;
    private final double fuelConsumptionPrice;
    private final int availabilityYear;
    private final double acquisitionPrice;
    private final double baseMaintenanceCost;
    private final int maintenanceFrequency;
    private final double wearAndTearFactor;
    
    public boolean purchaseLocomotive(Player player, Scenario scenario) {
        if (!scenario.hasSufficientFunds(acquisitionPrice, player)) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        if (!checkAvailability(scenario.getCurrentDate(), scenario.getLocomotiveTypes())) {
            throw new IllegalStateException("Locomotive not available");
        }
        
        player.removeBudget(acquisitionPrice);
        player.addLocomotive(this);
        this.owner = player;
        
        return true;
    }
    
    public boolean checkAvailability(Date currentDate, List<LocomotiveType> types) {
        return currentDate.getYear() >= availabilityYear && 
               types.contains(type);
    }
}
```

## 6. Integration and Demo

* A new option was added to the Player menu for purchasing locomotives.
* The locomotive purchase functionality integrates with:
  - Player management (budget and owned locomotives)
  - Scenario management (available locomotive types and date validation)
  - Session management (current player and scenario)
* For demo purposes, some locomotives are pre-configured in the system.

## 7. Observations

* The implementation follows a clean separation of concerns:
  - UI layer (BuyLocomotiveUI)
  - Controller layer (BuyLocomotiveController)
  - Domain layer (Locomotive, Player, Scenario)
  - Data access layer (LocomotiveRepository)