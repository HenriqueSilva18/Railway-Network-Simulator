package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuildRailwayLineController;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.RailwayLine;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.controller.template.ApplicationSession;


import java.util.List;
import java.util.Scanner;

public class BuildRailwayLineUI implements Runnable {

    private final BuildRailwayLineController controller;
    private Station selectedFirstStation;
    private Station selectedSecondStation;


    public BuildRailwayLineUI() {
        this.controller = new BuildRailwayLineController();
    }


    @Override
    public void run() {
        System.out.println("### Build Railway Line ###");
        // Get current player from the application session
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            System.out.println("No player is currently logged in.");
            return;
        }

        // Get available stations
        List<Station> stations = controller.getAvailableStations();

        if (stations.size() < 2) {
            System.out.println("You need at least two stations to build a railway line.");
            return;
        }

        // Select first station
        selectedFirstStation = selectStation("Select the first station:", stations, null);
        if (selectedFirstStation == null) {
            System.out.println("Operation canceled.");
            return;
        }

        // Select second station (excluding the first one)
        selectedSecondStation = selectStation("Select the second station:", stations, selectedFirstStation);
        if (selectedSecondStation == null) {
            System.out.println("Operation canceled.");
            return;
        }

        // Ask for line options
        boolean isDoubleTrack = Utils.readBooleanFromConsole("Is the railway line a double track? (y/n)");
        boolean isElectrified = Utils.readBooleanFromConsole("Is the railway line electrified? (y/n)");


        // Find path and calculate cost
        List<Position> path = controller.findDirectPath(selectedFirstStation, selectedSecondStation);
        if (path == null || path.isEmpty()) {
            System.out.println("Could not find a valid path between the selected stations.");
            return;
        }

        double cost = controller.calculatePathCost(path, isDoubleTrack, isElectrified);

        // Display cost and current budget
        System.out.printf("Estimated construction cost: $%.2f%n", cost);
        System.out.printf("Your current budget: $%.2f%n", currentPlayer.getCurrentBudget());

        // Check if player can afford it
        if (currentPlayer.getCurrentBudget() < cost) {
            System.out.println("You do not have enough funds to build this railway line.");
            return;
        }


        // Confirm action
        System.out.print("Do you want to build this railway line? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().trim().toLowerCase();


        if ("y".equals(confirmation)) {
            // Build railway line
            RailwayLine builtLine = controller.buildRailwayLine(selectedFirstStation, selectedSecondStation, isDoubleTrack, isElectrified);

            if (builtLine != null) {
                System.out.println("Railway line built successfully!");
                System.out.printf("New budget: $%.2f%n", currentPlayer.getCurrentBudget());
            } else {
                System.out.println("Failed to build railway line. This could be due to:");
                System.out.println("- The railway line already exists.");
                System.out.println("- Insufficient funds (double-check).");
                System.out.println("- No valid path available.");
            }
        } else {
            System.out.println("Operation canceled.");
        }
    }


    private Station selectStation(String prompt, List<Station> stations, Station exclude) {
        System.out.println(prompt);
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).equals(exclude)) continue;
            System.out.printf("%d. %s%n", i + 1, stations.get(i).getNameID());
        }

        int choice = Utils.readIntegerFromConsole("Enter the station number: ") - 1;

        if (choice >= 0 && choice < stations.size()) {
            Station selected = stations.get(choice);
            if (selected.equals(exclude)) {
                System.out.println("You cannot select the same station twice.");
                return null;
            }
            return selected;
        } else {
            System.out.println("Invalid selection.");
            return null;
        }
    }
}