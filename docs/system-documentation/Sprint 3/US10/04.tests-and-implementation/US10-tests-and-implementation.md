# US10 - Assign a train to a route

## 4. Tests

**Test 1:** Check that it is not possible to create an instance of the PointOfRoute class with null values.

	@Test(expected = IllegalArgumentException.class)
		public void ensureNullIsNotAllowed() {
		PointOfRoute instance = new PointOfRoute(null, null, null);
	}

**Test 2:** Check that it is not possible to assign a train to an invalid route.

	@Test(expected = IllegalArgumentException.class)
		public void ensureValidRouteAssignment() {
		RouteDTO routeDTO = new RouteDTO(null, null);
		TrainDTO trainDTO = new TrainDTO("Train1", 100);
		AssignTrainController controller = new AssignTrainController();
		controller.assignTrainToRoute(routeDTO, trainDTO);
	}

## 5. Construction (Implementation)

### Class AssignTrainController

```java
public List<RouteDTO> getAvailableRoutes() {
    RouteRepository routeRepository = Repositories.getInstance().getRouteRepository();
    List<Route> routes = routeRepository.getListOfRoutes();
    return RouteMapper.toDTO(routes);
}

public RouteDetailsDTO getRouteDetails(RouteDTO routeDTO) {
    Route route = RouteMapper.toModel(routeDTO);
    return RouteMapper.toDetailsDTO(route);
}

public List<PointOfRouteDTO> getRoutePoints(RouteDTO routeDTO) {
    Route route = RouteMapper.toModel(routeDTO);
    List<PointOfRoute> points = route.getPoints();
    return RouteMapper.toPointOfRouteDTOList(points);
}

public boolean setCargoMode(PointOfRouteDTO pointOfRouteDTO) {
    RouteRepository routeRepository = Repositories.getInstance().getRouteRepository();
    Route route = routeRepository.getRoute(pointOfRouteDTO.getRouteId());
    PointOfRoute point = route.getPointOfRoute(pointOfRouteDTO.getPointId());
    point.setCargoMode(pointOfRouteDTO.getCargoMode());
    return true;
}

public List<TrainDTO> getAvailableTrains() {
    TrainRepository trainRepository = Repositories.getInstance().getTrainRepository();
    List<Train> trains = trainRepository.getListOfTrains();
    return TrainMapper.toDTO(trains);
}

public TrainDetailsDTO getTrainDetails(TrainDTO trainDTO) {
    Train train = TrainMapper.toModel(trainDTO);
    return TrainMapper.toDetailsDTO(train);
}

public AssignmentDetailsDTO assignTrainToRoute(RouteDTO routeDTO, TrainDTO trainDTO) {
    Route route = RouteMapper.toModel(routeDTO);
    Train train = TrainMapper.toModel(trainDTO);
    return AssignmentMapper.createAssignmentDetails(route, train);
}
```

### Class RouteMapper

```java
public static List<RouteDTO> toDTO(List<Route> routes) {
    List<RouteDTO> routeDTOs = new ArrayList<>();
    for (Route route : routes) {
        routeDTOs.add(toDTO(route));
    }
    return routeDTOs;
}

public static RouteDTO toDTO(Route route) {
    return new RouteDTO(route.getId(), route.getName());
}

public static RouteDetailsDTO toDetailsDTO(Route route) {
    return new RouteDetailsDTO(route.getId(), route.getName(), route.getPoints());
}

public static List<PointOfRouteDTO> toPointOfRouteDTOList(List<PointOfRoute> points) {
    List<PointOfRouteDTO> pointDTOs = new ArrayList<>();
    for (PointOfRoute point : points) {
        pointDTOs.add(toPointOfRouteDTO(point));
    }
    return pointDTOs;
}
```

### Class TrainMapper

```java
public static List<TrainDTO> toDTO(List<Train> trains) {
    List<TrainDTO> trainDTOs = new ArrayList<>();
    for (Train train : trains) {
        trainDTOs.add(toDTO(train));
    }
    return trainDTOs;
}

public static TrainDTO toDTO(Train train) {
    return new TrainDTO(train.getName(), train.getCapacity());
}

public static TrainDetailsDTO toDetailsDTO(Train train) {
    return new TrainDetailsDTO(train.getName(), train.getCapacity(), train.getCurrentLoad());
}
```

## 6. Integration and Demo 

* A new option in the UI allows players to assign trains to routes.
* The system displays a list of available routes with valid stations.
* For each selected route, the system shows:
  - Route name
  - List of route points
  - Available cargo modes (FULL/HALF/AVAILABLE) for each point
* The system displays a list of available trains with their details.
* After train selection, the system shows:
  - Train details
  - Route details
  - Station details
  - List of cargoes to be picked up
  - Cargo modes for each point
* For demo purposes, trains can be assigned to different routes to demonstrate the functionality.

## 7. Observations

* The implementation follows a layered architecture pattern with clear separation of concerns between UI, Controller, Repository, and Domain layers.
* The use of DTOs helps in transferring data between layers while maintaining encapsulation.
* The system provides detailed information about routes, trains, and cargo modes.
* The validation of train-route assignments is handled at the domain level to ensure data integrity.
* Future improvements could include:
  - Adding support for multiple trains on the same route
  - Implementing route optimization algorithms
  - Adding support for dynamic cargo mode changes
  - Implementing train scheduling functionality