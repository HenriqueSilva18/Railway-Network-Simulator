package pt.ipp.isep.dei;

import pt.ipp.isep.dei.controller.template.*;
import pt.ipp.isep.dei.domain.template.Employee;
import pt.ipp.isep.dei.domain.template.Organization;
import pt.ipp.isep.dei.domain.template.TaskCategory;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.OrganizationRepository;
import pt.ipp.isep.dei.repository.template.TaskCategoryRepository;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Bootstrap implements Runnable {

    public void run() {
        addTaskCategories();
        addOrganization();
        addUsers();
        addIndustries();
        addCities();
        addScenarios();
    }

    private void addOrganization() {
        OrganizationRepository organizationRepository = Repositories.getInstance().getOrganizationRepository();

        Organization organization = new Organization("Railway Simulator Inc.");
        organization.addEmployee(new Employee("admin@railway.app"));
        organization.addEmployee(new Employee("editor@railway.app"));
        organizationRepository.add(organization);
    }

    private void addTaskCategories() {
        TaskCategoryRepository taskCategoryRepository = Repositories.getInstance().getTaskCategoryRepository();

        taskCategoryRepository.add(new TaskCategory("Analysis"));
        taskCategoryRepository.add(new TaskCategory("Design"));
        taskCategoryRepository.add(new TaskCategory("Implementation"));
        taskCategoryRepository.add(new TaskCategory("Development"));
        taskCategoryRepository.add(new TaskCategory("Testing"));
        taskCategoryRepository.add(new TaskCategory("Deployment"));
        taskCategoryRepository.add(new TaskCategory("Maintenance"));
    }

    private void addUsers() {
        AuthenticationRepository authRepo = Repositories.getInstance().getAuthenticationRepository();
        PlayerRepository playerRepo = Repositories.getInstance().getPlayerRepository();

        // Add roles
        authRepo.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);
        authRepo.addUserRole(AuthenticationController.ROLE_EDITOR, AuthenticationController.ROLE_EDITOR);
        authRepo.addUserRole(AuthenticationController.ROLE_PLAYER, AuthenticationController.ROLE_PLAYER);

        // Add admin user
        authRepo.addUserWithRole("System Admin", "admin@railway.app", "admin123",
                AuthenticationController.ROLE_ADMIN);

        // Add editor user
        authRepo.addUserWithRole("Map Editor", "editor@railway.app", "editor123",
                AuthenticationController.ROLE_EDITOR);

        // Add player user
        authRepo.addUserWithRole("Player One", "player@email.com", "player123",
                AuthenticationController.ROLE_PLAYER);

        // Initialize player in player repository
        Player player = new Player("player@email.com", 500000);
        playerRepo.addPlayer(player);
    }

    private void addIndustries() {
        IndustryRepository industryRepo = Repositories.getInstance().getIndustryRepository();
        MapRepository mapRepo = Repositories.getInstance().getMapRepository();

        // Create base industry types
        Industry coalMine = new Industry("coal_mine", "Mining", "Primary", 1900, new Position(0, 0));
        Industry ironMine = new Industry("iron_mine", "Mining", "Primary", 1900, new Position(0, 0));
        Industry farm = new Industry("farm", "Agriculture", "Primary", 1900, new Position(0, 0));
        Industry forest = new Industry("forest", "Forestry", "Primary", 1900, new Position(0, 0));
        Industry steelMill = new Industry("steel_mill", "Manufacturing", "Transforming", 1900, new Position(0, 0));
        Industry factory = new Industry("factory", "Manufacturing", "Transforming", 1900, new Position(0, 0));
        Industry seaport = new Industry("seaport", "Port", "Port", 1900, new Position(0, 0));

        // Add base industries to repository
        industryRepo.add(coalMine);
        industryRepo.add(ironMine);
        industryRepo.add(farm);
        industryRepo.add(forest);
        industryRepo.add(steelMill);
        industryRepo.add(factory);
        industryRepo.add(seaport);

        // Add industries to Iberian Peninsula (12x9 grid)
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        if (iberianMap != null) {
            addIndustryToMap(iberianMap, "porto_port", seaport, 1, 4);
            addIndustryToMap(iberianMap, "barcelona_port", seaport, 10, 3);
            addIndustryToMap(iberianMap, "asturias_mine", coalMine, 4, 1);
            mapRepo.save(iberianMap);
        }

        // Add industries to France (10x10 grid)
        Map franceMap = mapRepo.getMap("france");
        if (franceMap != null) {
            addIndustryToMap(franceMap, "marseille_port", seaport, 8, 8);
            addIndustryToMap(franceMap, "loire_farm", farm, 5, 6);
            mapRepo.save(franceMap);
        }

        // Add industries to Italy (15x15 grid)
        Map italyMap = mapRepo.getMap("italy");
        if (italyMap != null) {
            addIndustryToMap(italyMap, "genoa_port", seaport, 2, 5);
            addIndustryToMap(italyMap, "turin_factory", factory, 1, 3);
            mapRepo.save(italyMap);
        }
    }

    private void addIndustryToMap(Map map, String nameID, Industry baseIndustry, int x, int y) {
        Industry newIndustry = new Industry(nameID, baseIndustry.getType(), baseIndustry.getSector(), 
            baseIndustry.getAvailabilityYear(), new Position(x, y));
        map.addIndustry(newIndustry);
    }

    private void addCities() {
        MapRepository mapRepo = Repositories.getInstance().getMapRepository();

        // Add cities to Iberian Peninsula (12x9 grid)
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        if (iberianMap != null) {
            addCityToMap(iberianMap, "madrid", 7, 5);
            addCityToMap(iberianMap, "barcelona", 10, 3);
            addCityToMap(iberianMap, "porto", 1, 1);
            mapRepo.save(iberianMap);
        }

        // Add cities to France (10x10 grid)
        Map franceMap = mapRepo.getMap("france");
        if (franceMap != null) {
            addCityToMap(franceMap, "paris", 5, 3);
            addCityToMap(franceMap, "lyon", 6, 7);
            addCityToMap(franceMap, "marseille", 7, 8);
            mapRepo.save(franceMap);
        }

        // Add cities to Italy (15x15 grid)
        Map italyMap = mapRepo.getMap("italy");
        if (italyMap != null) {
            addCityToMap(italyMap, "rome", 4, 8);
            addCityToMap(italyMap, "milan", 2, 3);
            addCityToMap(italyMap, "naples", 5, 10);
            mapRepo.save(italyMap);
        }
    }

    private void addCityToMap(Map map, String nameID, int x, int y) {
        List<HouseBlock> houseBlocks = new ArrayList<>();
        Position cityPosition = new Position(x, y);
        
        // Add a default house block for the city
        Position blockPosition = new Position(x, y); // Same position as city for now
        houseBlocks.add(new HouseBlock(blockPosition, true)); // true indicates it's occupied
        
        City city = new City(nameID, cityPosition, houseBlocks);
        map.addCity(city);
    }

    private void addScenarios() {
        CreateScenarioController scenarioController = new CreateScenarioController();
        MapRepository mapRepo = Repositories.getInstance().getMapRepository();
        EditorRepository editorRepo = Repositories.getInstance().getEditorRepository();
        ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();

        // Create an editor for scenarios
        Editor editor = new Editor("map_editor", "editor@railway.app");
        ApplicationSession.getInstance().setCurrentEditor(editor);

        // Create different time periods for scenarios
        Calendar calendar = Calendar.getInstance();
        
        // Period 1: 1900-1920 (Early Industrial)
        calendar.set(1900, Calendar.JANUARY, 1);
        Date startDate1 = calendar.getTime();
        calendar.set(1920, Calendar.DECEMBER, 31);
        Date endDate1 = calendar.getTime();

        // Period 2: 1920-1950 (Inter-War)
        calendar.set(1920, Calendar.JANUARY, 1);
        Date startDate2 = calendar.getTime();
        calendar.set(1950, Calendar.DECEMBER, 31);
        Date endDate2 = calendar.getTime();
        
        // Set a default current date halfway through the first scenario period
        calendar.set(1910, Calendar.JANUARY, 1);
        Date currentDate = calendar.getTime();
        scenarioRepo.setCurrentDate(currentDate);

        // Get maps
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        Map franceMap = mapRepo.getMap("france");
        Map italyMap = mapRepo.getMap("italy");

        // Remove any existing scenarios from maps
        if (iberianMap != null) {
            // Clear existing scenarios
            List<String> scenarios = new ArrayList<>(iberianMap.getScenarios());
            for (String scenario : scenarios) {
                if (!scenario.equals("scenario1") && !scenario.equals("scenario2")) {
                    iberianMap.getScenarios().remove(scenario);
                }
            }
        }
        
        if (franceMap != null) {
            // Clear existing scenarios
            List<String> scenarios = new ArrayList<>(franceMap.getScenarios());
            for (String scenario : scenarios) {
                if (!scenario.equals("scenario1") && !scenario.equals("scenario2")) {
                    franceMap.getScenarios().remove(scenario);
                }
            }
        }
        
        if (italyMap != null) {
            // Clear existing scenarios
            List<String> scenarios = new ArrayList<>(italyMap.getScenarios());
            for (String scenario : scenarios) {
                if (!scenario.equals("scenario1") && !scenario.equals("scenario2")) {
                    italyMap.getScenarios().remove(scenario);
                }
            }
        }
        
        // Save the maps after removing scenarios
        if (iberianMap != null) mapRepo.save(iberianMap);
        if (franceMap != null) mapRepo.save(franceMap);
        if (italyMap != null) mapRepo.save(italyMap);

        // Create scenarios for each map in different time periods
        if (iberianMap != null) {
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Early Industrial", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Inter-War", startDate2, endDate2, 2);
        }

        if (franceMap != null) {
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Belle Époque", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Reconstruction", startDate2, endDate2, 2);
        }

        if (italyMap != null) {
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Giolitti Era", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Inter-War", startDate2, endDate2, 2);
        }
    }

    private void createScenarioForPeriod(CreateScenarioController controller, Map map, Editor editor, 
            String scenarioName, Date startDate, Date endDate, int scenarioNumber) {
        List<String> locomotiveTypes = new ArrayList<>();
        locomotiveTypes.add("Steam");
        if (scenarioNumber >= 2) locomotiveTypes.add("Diesel");

        // Get the current cities and industries from the map
        List<City> cities = new ArrayList<>(map.getCities());
        List<Industry> industries = new ArrayList<>(map.getIndustries());

        // Create empty cargo lists for ports
        List<Cargo> portImports = new ArrayList<>();
        List<Cargo> portExports = new ArrayList<>();
        List<Cargo> portProduces = new ArrayList<>();

        // Adjust production and traffic rates based on the period
        double productionRate = 1.0 + (scenarioNumber * 0.1); // Increases with each period
        float trafficRate = 0.5f + (scenarioNumber * 0.1f); // Increases with each period

        // Format date range for display
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int startYear = cal.get(Calendar.YEAR);
        cal.setTime(endDate);
        int endYear = cal.get(Calendar.YEAR);
        String displayName = String.format("%s (%d-%d)", scenarioName, startYear, endYear);

        // Create the scenario
        Scenario scenario = controller.createScenario(
            "scenario" + scenarioNumber,
            displayName,
            map,
            startDate,
            endDate,
            industries,
            portImports,
            portExports,
            portProduces,
            productionRate,
            locomotiveTypes,
            cities,
            trafficRate
        );

        // Add the scenario to the map
        map.addScenario("scenario" + scenarioNumber);

        // Save the updated map
        Repositories.getInstance().getMapRepository().save(map);

        // Add the scenario to the editor repository
        EditorRepository editorRepo = Repositories.getInstance().getEditorRepository();
        if (!editorRepo.getEditors().contains(editor)) {
            editorRepo.addEditor(editor);
        }
        editorRepo.addScenarioToEditor(editor, scenario);
    }
}