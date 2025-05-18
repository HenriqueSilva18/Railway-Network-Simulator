package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuildRailwayLineController;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.domain.template.RailwayLine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BuildRailwayLineUI implements Runnable {
    private final BuildRailwayLineController controller;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public BuildRailwayLineUI() {
        this.controller = new BuildRailwayLineController();
    }

    private int readIntFromConsole(String message) {
        while (true) {
            try {
                System.out.print(message + ": ");
                String input = reader.readLine();
                if (input == null) {
                    return 0; // Cancel if null
                }
                input = input.trim();
                
                if (input.isEmpty()) {
                    return 0; // Cancel if empty
                }
                
                return Integer.parseInt(input);
            } catch (IOException e) {
                System.out.println("Error reading input. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private String readStringFromConsole(String message) {
        try {
            System.out.print(message + ": ");
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Error reading input.");
            return null;
        }
    }

    @Override
    public void run() {
        System.out.println("\n=== Build Railway Line ===\n");

        try {
            // Get available stations
            List<Station> stations = controller.getAvailableStations();
            
            if (stations.size() < 2) {
                System.out.println("You need at least 2 stations to build a railway line.");
                return;
            }

            // Display first station options
            System.out.println("\nAvailable stations:");
            for (int i = 0; i < stations.size(); i++) {
                System.out.printf("%d - %s\n", i + 1, stations.get(i).getNameID());
            }
            System.out.println("0 - Cancel");

            // Select first station
            System.out.println("\nSelect the first station (1-" + stations.size() + ", 0 to cancel)");
            int firstChoice = readIntFromConsole("Enter station number");
            
            if (firstChoice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            if (firstChoice < 1 || firstChoice > stations.size()) {
                System.out.println("Invalid station number. Operation cancelled.");
                return;
            }
            
            final Station selectedStation = stations.get(firstChoice - 1);
            System.out.println("Selected: " + selectedStation.getNameID());

            // Create list of remaining stations
            List<Station> remainingStations = stations.stream()
                .filter(s -> !s.equals(selectedStation))
                .collect(Collectors.toList());

            // Display second station options
            System.out.println("\nAvailable stations for connection:");
            for (int i = 0; i < remainingStations.size(); i++) {
                System.out.printf("%d - %s\n", i + 1, remainingStations.get(i).getNameID());
            }
            System.out.println("0 - Cancel");

            // Select second station
            System.out.println("\nSelect the second station (1-" + remainingStations.size() + ", 0 to cancel)");
            int secondChoice = readIntFromConsole("Enter station number");
            
            if (secondChoice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            if (secondChoice < 1 || secondChoice > remainingStations.size()) {
                System.out.println("Invalid station number. Operation cancelled.");
                return;
            }
            
            Station station2 = remainingStations.get(secondChoice - 1);
            System.out.println("Selected: " + station2.getNameID());
            
            // Direct build without expensive check to avoid potential hanging in pathfinding
            System.out.printf("\nRailway line details:");
            System.out.printf("\nFrom: %s", selectedStation.getNameID());
            System.out.printf("\nTo: %s", station2.getNameID());

            System.out.println("\nDo you want to build this railway line? (y/n)");
            String confirm = readStringFromConsole("Enter y/n");
            if (confirm == null || !confirm.toLowerCase().startsWith("y")) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            // Build the railway line
            RailwayLine builtLine = controller.buildRailwayLine(selectedStation, station2);
            
            if (builtLine != null) {
                System.out.println("\nRailway line built successfully!");
                System.out.printf("Length: %.2f units\n", builtLine.getLength());
                System.out.println(builtLine);
            } else {
                System.out.println("\nFailed to build railway line. This could be due to:");
                System.out.println("- Railway line already exists");
                System.out.println("- Insufficient funds");
                System.out.println("- No valid path available");
            }
            
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 