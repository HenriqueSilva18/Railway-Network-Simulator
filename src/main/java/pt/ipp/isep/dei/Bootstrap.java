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
        initializeMaps();
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

        // Add industries to Iberian Peninsula (15x12 grid)
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        if (iberianMap != null) {
            addIndustryToMap(iberianMap, "porto_port", seaport, 1, 1);
            addIndustryToMap(iberianMap, "barcelona_port", seaport, 13, 3);
            addIndustryToMap(iberianMap, "asturias_mine", coalMine, 4, 1);
            addIndustryToMap(iberianMap, "andalucia_farm", farm, 5, 10);
            addIndustryToMap(iberianMap, "basque_steel", steelMill, 7, 1);
            mapRepo.save(iberianMap);
        }

        // Add industries to France (10x12 grid)
        Map franceMap = mapRepo.getMap("france");
        if (franceMap != null) {
            addIndustryToMap(franceMap, "marseille_port", seaport, 8, 10);
            addIndustryToMap(franceMap, "loire_farm", farm, 5, 6);
            addIndustryToMap(franceMap, "paris_factory", factory, 5, 2);
            addIndustryToMap(franceMap, "normandy_farm", farm, 3, 1);
            mapRepo.save(franceMap);
        }

        // Add industries to North America (25x20 grid)
        Map northAmericaMap = mapRepo.getMap("north_america");
        if (northAmericaMap != null) {
            addIndustryToMap(northAmericaMap, "ny_port", seaport, 22, 8);
            addIndustryToMap(northAmericaMap, "sf_port", seaport, 2, 10);
            addIndustryToMap(northAmericaMap, "texas_oil", coalMine, 12, 15);
            addIndustryToMap(northAmericaMap, "midwest_farm", farm, 15, 8);
            addIndustryToMap(northAmericaMap, "detroit_factory", factory, 18, 7);
            addIndustryToMap(northAmericaMap, "canada_forest", forest, 12, 3);
            mapRepo.save(northAmericaMap);
        }

        // Add industries to British Isles (10x12 grid)
        Map britishMap = mapRepo.getMap("british_isles");
        if (britishMap != null) {
            addIndustryToMap(britishMap, "london_port", seaport, 8, 9);
            addIndustryToMap(britishMap, "wales_mine", coalMine, 3, 7);
            addIndustryToMap(britishMap, "scotland_forest", forest, 5, 2);
            addIndustryToMap(britishMap, "manchester_factory", factory, 5, 6);
            mapRepo.save(britishMap);
        }

        // Add industries to Scandinavia (15x18 grid)
        Map scandinaviaMap = mapRepo.getMap("scandinavia");
        if (scandinaviaMap != null) {
            addIndustryToMap(scandinaviaMap, "oslo_port", seaport, 5, 10);
            addIndustryToMap(scandinaviaMap, "swedish_forest", forest, 8, 5);
            addIndustryToMap(scandinaviaMap, "danish_farm", farm, 7, 15);
            addIndustryToMap(scandinaviaMap, "finland_mine", ironMine, 12, 4);
            mapRepo.save(scandinaviaMap);
        }

        // Add industries to Italy (8x15 grid)
        Map italyMap = mapRepo.getMap("italy");
        if (italyMap != null) {
            addIndustryToMap(italyMap, "genoa_port", seaport, 2, 5);
            addIndustryToMap(italyMap, "turin_factory", factory, 1, 3);
            addIndustryToMap(italyMap, "sicily_farm", farm, 4, 13);
            addIndustryToMap(italyMap, "milan_steel", steelMill, 2, 2);
            mapRepo.save(italyMap);
        }

        // Add industries to Central Europe (20x15 grid)
        Map centralEuropeMap = mapRepo.getMap("central_europe");
        if (centralEuropeMap != null) {
            addIndustryToMap(centralEuropeMap, "ruhr_steel", steelMill, 8, 5);
            addIndustryToMap(centralEuropeMap, "bohemia_mine", coalMine, 11, 7);
            addIndustryToMap(centralEuropeMap, "bavaria_farm", farm, 9, 9);
            addIndustryToMap(centralEuropeMap, "silesia_factory", factory, 14, 6);
            mapRepo.save(centralEuropeMap);
        }
        
        // Add industries to Japan (12x16 grid)
        Map japanMap = mapRepo.getMap("japan");
        if (japanMap != null) {
            addIndustryToMap(japanMap, "tokyo_port", seaport, 8, 8);
            addIndustryToMap(japanMap, "kyushu_mine", coalMine, 3, 13);
            addIndustryToMap(japanMap, "kansai_factory", factory, 7, 10);
            addIndustryToMap(japanMap, "hokkaido_forest", forest, 9, 2);
            addIndustryToMap(japanMap, "osaka_steel", steelMill, 6, 10);
            mapRepo.save(japanMap);
        }
    }

    private void addIndustryToMap(Map map, String nameID, Industry baseIndustry, int x, int y) {
        Industry newIndustry = new Industry(nameID, baseIndustry.getType(), baseIndustry.getSector(), 
            baseIndustry.getAvailabilityYear(), new Position(x, y));
        map.addIndustry(newIndustry);
    }

    private void addCities() {
        MapRepository mapRepo = Repositories.getInstance().getMapRepository();

        // Add cities to Iberian Peninsula (15x12 grid)
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        if (iberianMap != null) {
            addCityToMap(iberianMap, "madrid", 7, 5);
            addCityToMap(iberianMap, "barcelona", 13, 3);
            addCityToMap(iberianMap, "porto", 1, 1);
            addCityToMap(iberianMap, "lisbon", 1, 5);
            addCityToMap(iberianMap, "seville", 4, 9);
            addCityToMap(iberianMap, "valencia", 11, 6);
            mapRepo.save(iberianMap);
        }

        // Add cities to France (10x12 grid)
        Map franceMap = mapRepo.getMap("france");
        if (franceMap != null) {
            addCityToMap(franceMap, "paris", 5, 3);
            addCityToMap(franceMap, "lyon", 6, 7);
            addCityToMap(franceMap, "marseille", 7, 9);
            addCityToMap(franceMap, "bordeaux", 2, 7);
            addCityToMap(franceMap, "lille", 5, 1);
            mapRepo.save(franceMap);
        }

        // Add cities to North America (25x20 grid)
        Map northAmericaMap = mapRepo.getMap("north_america");
        if (northAmericaMap != null) {
            addCityToMap(northAmericaMap, "new_york", 22, 7);
            addCityToMap(northAmericaMap, "chicago", 16, 8);
            addCityToMap(northAmericaMap, "san_francisco", 2, 9);
            addCityToMap(northAmericaMap, "toronto", 19, 5);
            addCityToMap(northAmericaMap, "montreal", 21, 4);
            addCityToMap(northAmericaMap, "mexico_city", 10, 17);
            mapRepo.save(northAmericaMap);
        }

        // Add cities to British Isles (10x12 grid)
        Map britishMap = mapRepo.getMap("british_isles");
        if (britishMap != null) {
            addCityToMap(britishMap, "london", 7, 8);
            addCityToMap(britishMap, "manchester", 5, 5);
            addCityToMap(britishMap, "liverpool", 4, 5);
            addCityToMap(britishMap, "edinburgh", 6, 2);
            addCityToMap(britishMap, "dublin", 2, 5);
            addCityToMap(britishMap, "belfast", 3, 3);
            mapRepo.save(britishMap);
        }

        // Add cities to Scandinavia (15x18 grid)
        Map scandinaviaMap = mapRepo.getMap("scandinavia");
        if (scandinaviaMap != null) {
            addCityToMap(scandinaviaMap, "stockholm", 9, 7);
            addCityToMap(scandinaviaMap, "oslo", 4, 9);
            addCityToMap(scandinaviaMap, "copenhagen", 7, 14);
            addCityToMap(scandinaviaMap, "gothenburg", 6, 11);
            addCityToMap(scandinaviaMap, "helsinki", 12, 4);
            mapRepo.save(scandinaviaMap);
        }

        // Add cities to Italy (8x15 grid)
        Map italyMap = mapRepo.getMap("italy");
        if (italyMap != null) {
            addCityToMap(italyMap, "rome", 4, 8);
            addCityToMap(italyMap, "milan", 2, 3);
            addCityToMap(italyMap, "naples", 5, 10);
            addCityToMap(italyMap, "florence", 3, 6);
            addCityToMap(italyMap, "venice", 4, 4);
            addCityToMap(italyMap, "palermo", 4, 13);
            mapRepo.save(italyMap);
        }

        // Add cities to Central Europe (20x15 grid)
        Map centralEuropeMap = mapRepo.getMap("central_europe");
        if (centralEuropeMap != null) {
            addCityToMap(centralEuropeMap, "berlin", 10, 5);
            addCityToMap(centralEuropeMap, "vienna", 12, 8);
            addCityToMap(centralEuropeMap, "warsaw", 15, 6);
            addCityToMap(centralEuropeMap, "prague", 11, 7);
            addCityToMap(centralEuropeMap, "budapest", 13, 9);
            addCityToMap(centralEuropeMap, "munich", 9, 8);
            mapRepo.save(centralEuropeMap);
        }
        
        // Add cities to Japan (12x16 grid)
        Map japanMap = mapRepo.getMap("japan");
        if (japanMap != null) {
            addCityToMap(japanMap, "tokyo", 8, 7);
            addCityToMap(japanMap, "osaka", 6, 10);
            addCityToMap(japanMap, "kyoto", 7, 9);
            addCityToMap(japanMap, "sapporo", 9, 2);
            addCityToMap(japanMap, "nagoya", 7, 8);
            addCityToMap(japanMap, "fukuoka", 3, 13);
            mapRepo.save(japanMap);
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

        // Period 3: 1950-1970 (Post-War Boom)
        calendar.set(1950, Calendar.JANUARY, 1);
        Date startDate3 = calendar.getTime();
        calendar.set(1970, Calendar.DECEMBER, 31);
        Date endDate3 = calendar.getTime();

        // Period 4: 1970-1990 (Modern Era)
        calendar.set(1970, Calendar.JANUARY, 1);
        Date startDate4 = calendar.getTime();
        calendar.set(1990, Calendar.DECEMBER, 31);
        Date endDate4 = calendar.getTime();

        // Check if all maps exist before proceeding
        Map japanMap = mapRepo.getMap("japan");
        Map scandinaviaMap = mapRepo.getMap("scandinavia");
        Map centralEuropeMap = mapRepo.getMap("central_europe");
        Map iberianMap = mapRepo.getMap("iberian_peninsula");
        Map franceMap = mapRepo.getMap("france");
        Map britishMap = mapRepo.getMap("british_isles");
        Map italyMap = mapRepo.getMap("italy");
        Map northAmericaMap = mapRepo.getMap("north_america");

        // Create scenarios for each map in different time periods
        if (iberianMap != null) {
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Early Industrial", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Inter-War", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Economic Miracle", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, iberianMap, editor, "Iberian Modern Era", startDate4, endDate4, 4);
        }

        if (franceMap != null) {
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Belle Époque", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Reconstruction", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Les Trente Glorieuses", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, franceMap, editor, "French Modern Network", startDate4, endDate4, 4);
        }

        if (britishMap != null) {
            createScenarioForPeriod(scenarioController, britishMap, editor, "British Edwardian Era", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, britishMap, editor, "British Inter-War", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, britishMap, editor, "British Nationalisation", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, britishMap, editor, "British Modernisation", startDate4, endDate4, 4);
        }

        if (italyMap != null) {
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Giolitti Era", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Inter-War", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Economic Miracle", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, italyMap, editor, "Italian Modern Network", startDate4, endDate4, 4);
        }

        if (northAmericaMap != null) {
            createScenarioForPeriod(scenarioController, northAmericaMap, editor, "American Progressive Era", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, northAmericaMap, editor, "American Roaring Twenties", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, northAmericaMap, editor, "American Post-War Boom", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, northAmericaMap, editor, "American Modern Era", startDate4, endDate4, 4);
        }
        
        // Create scenarios for Japan
        if (japanMap != null) {
            createScenarioForPeriod(scenarioController, japanMap, editor, "Japanese Meiji Era", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, japanMap, editor, "Japanese Imperial Period", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, japanMap, editor, "Japanese Economic Miracle", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, japanMap, editor, "Japanese Bullet Train Era", startDate4, endDate4, 4);
            mapRepo.save(japanMap);
        }
        
        // Create scenarios for Scandinavia
        if (scandinaviaMap != null) {
            createScenarioForPeriod(scenarioController, scandinaviaMap, editor, "Nordic Industrial Revolution", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, scandinaviaMap, editor, "Nordic Interwar Period", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, scandinaviaMap, editor, "Nordic Welfare State", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, scandinaviaMap, editor, "Nordic Modern Networks", startDate4, endDate4, 4);
            mapRepo.save(scandinaviaMap);
        }
        
        // Create scenarios for Central Europe
        if (centralEuropeMap != null) {
            createScenarioForPeriod(scenarioController, centralEuropeMap, editor, "Central European Industrial Age", startDate1, endDate1, 1);
            createScenarioForPeriod(scenarioController, centralEuropeMap, editor, "Central European Reconstruction", startDate2, endDate2, 2);
            createScenarioForPeriod(scenarioController, centralEuropeMap, editor, "Central European Rebuilding", startDate3, endDate3, 3);
            createScenarioForPeriod(scenarioController, centralEuropeMap, editor, "Central European Modern Network", startDate4, endDate4, 4);
            mapRepo.save(centralEuropeMap);
        }
    }

    private void createScenarioForPeriod(CreateScenarioController controller, Map map, Editor editor, 
            String scenarioName, Date startDate, Date endDate, int scenarioNumber) {
        List<String> locomotiveTypes = new ArrayList<>();
        locomotiveTypes.add("Steam");
        if (scenarioNumber >= 2) locomotiveTypes.add("Diesel");
        if (scenarioNumber >= 3) locomotiveTypes.add("Electric");

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

    private void initializeMaps() {
        MapRepository mapRepo = Repositories.getInstance().getMapRepository();
        
        // Verify all maps exist
        List<Map> availableMaps = mapRepo.getAvailableMaps();
        
        // Ensure Japan map exists
        Map japanMap = mapRepo.getMap("japan");
        if (japanMap == null) {
            japanMap = Map.createMap("japan", Size.createSize(20, 15));
            mapRepo.add(japanMap);
        }
        
        // Ensure Scandinavia map exists
        Map scandinaviaMap = mapRepo.getMap("scandinavia");
        if (scandinaviaMap == null) {
            scandinaviaMap = Map.createMap("scandinavia", Size.createSize(25, 30));
            mapRepo.add(scandinaviaMap);
        }
        
        // Ensure Central Europe map exists
        Map centralEuropeMap = mapRepo.getMap("central_europe");
        if (centralEuropeMap == null) {
            centralEuropeMap = Map.createMap("central_europe", Size.createSize(30, 30));
            mapRepo.add(centralEuropeMap);
        }
    }
}