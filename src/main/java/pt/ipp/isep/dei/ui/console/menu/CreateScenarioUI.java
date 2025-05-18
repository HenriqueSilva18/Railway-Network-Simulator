package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.CreateScenarioController;
import pt.ipp.isep.dei.controller.template.AuthenticationController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateScenarioUI implements Runnable {
    private final CreateScenarioController controller;
    private final AuthenticationController authController;
    private final Scanner scanner;

    public CreateScenarioUI() {
        this.controller = new CreateScenarioController();
        this.authController = new AuthenticationController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            if (!isUserEditor()) {
                System.out.println("\nError: Only users with Editor role can create scenarios.");
                return;
            }

            System.out.println("\n=== Create Scenario ===");

            // Get available maps
            List<pt.ipp.isep.dei.domain.template.Map> availableMaps = controller.getAvailableMaps();
            if (availableMaps.isEmpty()) {
                System.out.println("No maps available to create scenarios.");
                return;
            }

            // Show and select map
            System.out.println("\nAvailable Maps:");
            for (int i = 0; i < availableMaps.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, availableMaps.get(i).getNameID());
            }

            pt.ipp.isep.dei.domain.template.Map selectedMap = selectMap(availableMaps);
            if (selectedMap == null) return;

            // Get scenario name
            String nameID = readScenarioName();
            if (nameID == null) return;

            // Get time period
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date[] dates = readDateRange(dateFormat);
            if (dates == null) return;
            Date startDate = dates[0];
            Date endDate = dates[1];

            // Get available industries
            List<Industry> mapIndustries = controller.getMapIndustries(selectedMap);
            if (mapIndustries.isEmpty()) {
                System.out.println("No industries available in the selected map.");
                return;
            }

            System.out.println("\nAvailable Industries:");
            for (int i = 0; i < mapIndustries.size(); i++) {
                Industry industry = mapIndustries.get(i);
                System.out.printf("%d. %s (%s)%n", i + 1, industry.getNameID(), industry.getType());
            }

            List<Industry> selectedIndustries = selectIndustries(mapIndustries);
            if (selectedIndustries == null) return;

            // Configure ports
            List<Cargo> portImports = new ArrayList<>();
            List<Cargo> portExports = new ArrayList<>();
            List<Cargo> portProduces = new ArrayList<>();
            List<Cargo> cargoList = controller.getCargoList();

            for (Industry industry : selectedIndustries) {
                if (industry.getType().equals("Port")) {
                    System.out.println("\nConfiguring port: " + industry.getNameID());
                    
                    // Select imports
                    System.out.println("\nSelect cargoes to import:");
                    for (int i = 0; i < cargoList.size(); i++) {
                        System.out.printf("%d. %s%n", i + 1, cargoList.get(i).getName());
                    }
                    List<Cargo> imports = selectCargoes(cargoList, "import");
                    if (imports == null) return;
                    portImports.addAll(imports);

                    // Select exports
                    System.out.println("\nSelect cargoes to export:");
                    List<Cargo> exports = selectCargoes(cargoList, "export");
                    if (exports == null) return;
                    portExports.addAll(exports);

                    // Select production
                    System.out.println("\nSelect cargoes to produce:");
                    List<Cargo> produces = selectCargoes(cargoList, "produce");
                    if (produces == null) return;
                    portProduces.addAll(produces);
                }
            }

            // Configure primary industries
            double genIndustryFactors = readGenerationFactor(selectedIndustries);
            if (genIndustryFactors < 0) return;

            // Configure cities
            List<City> mapCities = controller.getMapCities(selectedMap);
            float cityTrafficRates = readTrafficRate();
            if (cityTrafficRates < 0) return;

            // Select locomotive types
            List<String> locomotiveTypes = controller.getLocomotiveTypes();
            System.out.println("\nAvailable Locomotive Types:");
            for (int i = 0; i < locomotiveTypes.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, locomotiveTypes.get(i));
            }

            List<String> selectedLocomotiveTypes = selectLocomotiveTypes(locomotiveTypes);
            if (selectedLocomotiveTypes == null) return;

            // Confirm and create scenario
            System.out.println("\nScenario Summary:");
            System.out.println("Name: " + nameID);
            System.out.println("Map: " + selectedMap.getNameID());
            System.out.println("Period: " + new SimpleDateFormat("yyyy").format(startDate) + " to " + 
                             new SimpleDateFormat("yyyy").format(endDate));
            
            System.out.println("\nSelected Industries:");
            for (Industry industry : selectedIndustries) {
                System.out.println("- " + industry.getNameID() + " (" + industry.getType() + " - " + industry.getSector() + ")");
                if (industry.getType().equals("Port")) {
                    System.out.println("  Imports: " + formatCargoList(portImports));
                    System.out.println("  Exports: " + formatCargoList(portExports));
                    System.out.println("  Produces: " + formatCargoList(portProduces));
                }
            }
            
            System.out.println("\nGeneration Frequency Factor: " + genIndustryFactors);
            System.out.println("City Traffic Rate: " + cityTrafficRates);
            System.out.println("\nSelected Locomotive Types:");
            for (String type : selectedLocomotiveTypes) {
                System.out.println("- " + type);
            }

            if (Utils.confirm("Do you want to create this scenario? (y/n)")) {
                try {
                    Scenario scenario = controller.createScenario(nameID, selectedMap, startDate, endDate,
                            selectedIndustries, portImports, portExports, portProduces,
                            genIndustryFactors, selectedLocomotiveTypes, mapCities, cityTrafficRates);
                    System.out.println("\nScenario created successfully!");
                } catch (Exception e) {
                    System.out.println("Error creating scenario: " + e.getMessage());
                }
            } else {
                System.out.println("Scenario creation cancelled.");
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private pt.ipp.isep.dei.domain.template.Map selectMap(List<pt.ipp.isep.dei.domain.template.Map> availableMaps) {
        while (true) {
            try {
                int mapChoice = Utils.readIntegerFromConsole("Select a map (number): ");
                if (mapChoice < 1 || mapChoice > availableMaps.size()) {
                    System.out.println("Error: Invalid map selection. Please try again.");
                    continue;
                }
                return availableMaps.get(mapChoice - 1);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private String readScenarioName() {
        while (true) {
            String nameID = Utils.readLineFromConsole("Enter scenario name: ");
            if (nameID != null && !nameID.trim().isEmpty()) {
                return nameID;
            }
            System.out.println("Error: Scenario name cannot be empty. Please try again.");
        }
    }

    private Date[] readDateRange(SimpleDateFormat dateFormat) {
        while (true) {
            try {
                String startYearStr = Utils.readLineFromConsole("Enter start year (yyyy): ");
                int startYear = Integer.parseInt(startYearStr);
                if (startYear < 1900 || startYear > 2100) {
                    System.out.println("Error: Year must be between 1900 and 2100. Please try again.");
                    continue;
                }

                String endYearStr = Utils.readLineFromConsole("Enter end year (yyyy): ");
                int endYear = Integer.parseInt(endYearStr);
                if (endYear < 1900 || endYear > 2100) {
                    System.out.println("Error: Year must be between 1900 and 2100. Please try again.");
                    continue;
                }

                if (endYear < startYear) {
                    System.out.println("Error: End year must be after start year. Please try again.");
                    continue;
                }

                Date startDate = dateFormat.parse(startYearStr + "-01-01");
                Date endDate = dateFormat.parse(endYearStr + "-12-31");
                return new Date[]{startDate, endDate};
            } catch (ParseException | NumberFormatException e) {
                System.out.println("Error: Invalid year format. Please use yyyy format.");
            }
        }
    }

    private List<Industry> selectIndustries(List<Industry> mapIndustries) {
        while (true) {
            try {
                List<Industry> selectedIndustries = new ArrayList<>();
                String industrySelection = Utils.readLineFromConsole(
                        "Select industries (comma-separated numbers, e.g., 1,3,5): ");
                String[] selections = industrySelection.split(",");
                
                for (String selection : selections) {
                    int index = Integer.parseInt(selection.trim()) - 1;
                    if (index >= 0 && index < mapIndustries.size()) {
                        selectedIndustries.add(mapIndustries.get(index));
                    } else {
                        System.out.println("Error: Invalid industry selection: " + selection + ". Please try again.");
                        selectedIndustries.clear();
                        break;
                    }
                }
                
                if (!selectedIndustries.isEmpty()) {
                    return selectedIndustries;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid selection format. Please try again.");
            }
        }
    }

    private List<Cargo> selectCargoes(List<Cargo> cargoList, String operation) {
        while (true) {
            try {
                List<Cargo> selectedCargoes = new ArrayList<>();
                String selection = Utils.readLineFromConsole(
                        "Select cargoes to " + operation + " (comma-separated numbers): ");
                
                for (String item : selection.split(",")) {
                    int index = Integer.parseInt(item.trim()) - 1;
                    if (index >= 0 && index < cargoList.size()) {
                        selectedCargoes.add(cargoList.get(index));
                    } else {
                        System.out.println("Error: Invalid cargo selection: " + item + ". Please try again.");
                        selectedCargoes.clear();
                        break;
                    }
                }
                
                return selectedCargoes;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid selection format. Please try again.");
            }
        }
    }

    private double readGenerationFactor(List<Industry> selectedIndustries) {
        for (Industry industry : selectedIndustries) {
            if (industry.getType().equals("Primary")) {
                while (true) {
                    try {
                        double factor = Utils.readDoubleFromConsole(
                                "Enter generation frequency factor for primary industries: ");
                        if (factor <= 0) {
                            System.out.println("Error: Generation frequency factor must be positive. Please try again.");
                            continue;
                        }
                        return factor;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid generation frequency factor. Please try again.");
                    }
                }
            }
        }
        return 0;
    }

    private float readTrafficRate() {
        while (true) {
            try {
                float rate = Utils.readFloatFromConsole(
                        "Enter traffic rate for cities (1.0 is normal): ");
                if (rate <= 0) {
                    System.out.println("Error: Traffic rate must be positive. Please try again.");
                    continue;
                }
                return rate;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid traffic rate. Please try again.");
            }
        }
    }

    private List<String> selectLocomotiveTypes(List<String> locomotiveTypes) {
        while (true) {
            try {
                List<String> selectedTypes = new ArrayList<>();
                String selection = Utils.readLineFromConsole(
                        "Select locomotive types (comma-separated numbers, e.g., 1,2,3): ");
                
                for (String item : selection.split(",")) {
                    int index = Integer.parseInt(item.trim()) - 1;
                    if (index >= 0 && index < locomotiveTypes.size()) {
                        selectedTypes.add(locomotiveTypes.get(index));
                    } else {
                        System.out.println("Error: Invalid locomotive type selection: " + item + ". Please try again.");
                        selectedTypes.clear();
                        break;
                    }
                }
                
                if (!selectedTypes.isEmpty()) {
                    return selectedTypes;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid selection format. Please try again.");
            }
        }
    }

    private boolean isUserEditor() {
        List<UserRoleDTO> roles = authController.getUserRoles();
        if (roles == null) {
            return false;
        }
        return roles.stream().anyMatch(role -> role.getDescription().equals("EDITOR"));
    }

    private String formatCargoList(List<Cargo> cargoList) {
        if (cargoList.isEmpty()) {
            return "None";
        }
        return cargoList.stream()
                .map(Cargo::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");
    }
} 