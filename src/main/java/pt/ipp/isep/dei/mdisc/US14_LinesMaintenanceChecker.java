package pt.ipp.isep.dei.mdisc;

import pt.ipp.isep.dei.mdisc.util.*;

import java.io.IOException;
import java.util.*;

import static pt.ipp.isep.dei.mdisc.util.FilesUtils.*;

public class US14_LinesMaintenanceChecker {
    static String csvPath = "docs/mdisc/data/scenario4_lines.csv";
    static String dotPath = "docs/mdisc/us14/dot/scenario1_lines.dot";
    static String pngPath = "docs/mdisc/us14/png/scenario1_lines.png";

    static Map<String, Station> stations = new HashMap<>();
    static Map<String, List<Edge>> graph = new HashMap<>();
    static List<String> stationOrder = new ArrayList<>();

//    The Player wants:
//    4. to see a route that passes only once by each railway line to carry out maintenance on the lines, in Scenario 1;
//    5. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 2;
//    6. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 3;
//    7. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 4.

    // TODO SIMPLIFICAR METODO MAIN
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        // TODO choose scenario

        int maintenanceType = chooseMaintenanceType(sc);
        System.out.println("Selected maintenance type: " +
                (maintenanceType == 1 ? "All the lines\n" : "Only the electrified lines\n"));

        System.out.println("\nLoading railway scenario data from .csv file...");
        loadCSV(csvPath, stations, graph, stationOrder, maintenanceType); // TODO verificar se foi lido corretamente
        System.out.println("Loading complete.\n");


        Map<String,Integer> degreeMap = FleuryAlgorithm.calculateNodeDegrees(graph);

        boolean[][] closure = MatrixUtils.computeTransitiveClosure(graph, stationOrder); // TODO ver se da para chamar funcao US13
        String ref = null;
        for (String s : stationOrder) {
            if (degreeMap.getOrDefault(s, 0) > 0) { ref = s; break; }
        }
        if (ref != null) {
            int refIdx = stationOrder.indexOf(ref);
            for (String s : stationOrder) {
                if (degreeMap.getOrDefault(s, 0) > 0) {
                    int idx = stationOrder.indexOf(s);
                    if (!closure[refIdx][idx]) {
                        System.out.println("Graph is disconnected: cannot perform a single maintenance route over all lines.");
                        return;
                    }
                }
            }
        }

        List<String> oddStations = new ArrayList<>();
        int oddCount = FleuryAlgorithm.checkEulerian(degreeMap, oddStations);
        if (oddCount != 0 && oddCount != 2) {
            System.out.println("Graph is neither Eulerian nor semi-Eulerian (" + oddCount + " odd-degree vertices).");
            return;
        }

        // FIXME SO PARA TESTAR
        System.out.println("Available stations with their degrees:");
        List<String> availableStations = new ArrayList<>();
        for (String station : stationOrder) {
            if (degreeMap.getOrDefault(station, 0) > 0) {
                System.out.println(station + " (degree: " + degreeMap.get(station) + ")");
                availableStations.add(station);
            }
        }

        if (oddCount == 2) {
            System.out.println("\nSemi-Eulerian: odd-degree stations: " + oddStations);
        } else {
            System.out.println("\nEulerian: all vertices have even degree.");
        }

        String start = chooseStartStation(sc, availableStations, oddStations, oddCount);
        System.out.println("Starting maintenance route at: " + start + "\n");

        List<String> route = FleuryAlgorithm.findEulerianPath(graph, stationOrder, start);
        System.out.println("Maintenance route:");
        for (int i = 0; i < route.size(); i++) {
            System.out.print(route.get(i));
            if (i < route.size() - 1) System.out.print(" -> ");
        }
        System.out.println("\n");

        System.out.println("Exporting network to .dot file...\n");
        exportToDOT(dotPath, graph);
        System.out.println("Export to png file....");
        dotToPNG(dotPath, pngPath);
        System.out.println("Export complete.");

    }

    private static String chooseStartStation(Scanner sc, List<String> availableStations, List<String> oddStations, int oddCount) {
        String startStation = null;
        boolean validChoice = false;

        while (!validChoice) {
            List<String> stationsToShow = (oddCount == 2) ? oddStations : availableStations;

            if (oddCount == 2) {
                System.out.println("\nFor a semi-Eulerian graph, you should start at one of these odd-degree stations:");
            } else {
                System.out.println("\nEnter the number of the station you want to start from:");
            }

            for (int i = 0; i < stationsToShow.size(); i++) {
                String station = stationsToShow.get(i);
                System.out.println("[" + (i + 1) + "]: " + station + " (degree: " + graph.get(station).size() + ")");
            }

            try {
                int choice = sc.nextInt();

                if (choice >= 1 && choice <= stationsToShow.size()) {
                    startStation = stationsToShow.get(choice - 1);
                    validChoice = true;  // Set to true when a valid choice is made
                } else {
                    System.out.println("Invalid choice. Please enter a valid option number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.next();
            }
        }

        return startStation;
    }


    private static int chooseMaintenanceType(Scanner sc) {
        int maintenanceType = 0;

        while (maintenanceType != 1 && maintenanceType != 2) {
            System.out.println("Enter maintenance type:" +
                    "\n[1]: All the lines" +
                    "\n[2]: Only the electrified lines\n");

            try {
                maintenanceType = sc.nextInt();
                if (maintenanceType != 1 && maintenanceType != 2) {
                    System.out.println("Invalid option. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number (1 or 2).");
                sc.next();
            }
        }

        return maintenanceType;
    }

}
