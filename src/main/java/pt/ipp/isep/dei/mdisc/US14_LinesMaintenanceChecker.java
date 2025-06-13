package pt.ipp.isep.dei.mdisc;

import pt.ipp.isep.dei.mdisc.util.*;

import java.io.IOException;
import java.util.*;
// import java.awt.Desktop;
import java.io.File;

import static pt.ipp.isep.dei.mdisc.util.FilesUtils.*;

public class US14_LinesMaintenanceChecker {
    // static String fileName = "scenario1_lines";
    // static String csvPath = "docs/mdisc/data/"+fileName+".csv";
    // static String dotPath = "docs/mdisc/us14/dot/"+fileName+".dot";
    // static String pngPath = "docs/mdisc/us14/png/"+fileName+".png";

    static Map<String, Station> stations = new HashMap<>();
    static Map<String, List<Edge>> fullGraph = new HashMap<>();
    static Map<String, List<Edge>> maintenanceGraph = new HashMap<>();
    static List<String> stationOrder = new ArrayList<>();

//    The Player wants:
//    4. to see a route that passes only once by each railway line to carry out maintenance on the lines, in Scenario 1;
//      - NO the graph nieghter semi , eulurian
//    5. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 2;
//      - No disconnected
//    6. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 3;
//      - SEMI-Eulurian -Yes (ony tart with 2 options and give them to playr)
//    7. to see a route that passes only once by each electrified railway line to carry out maintenance by the team of electricians, in Scenario 4.
//      - YES THE GRF EUlrian - any option

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        System.out.println("\nPlease enter the path to your railway scenario CSV file:");
        String csvPath = sc.nextLine().trim();

        if (!FilesUtils.fileExists(csvPath)) {
            System.out.println("Error: The specified scenario file does not exist.");
            return;
        }

        String baseFileName = new File(csvPath).getName().replaceFirst("[.][^.]+$", "");

        String dotPath = "docs/mdisc/us14/dot/" + baseFileName + ".dot";
        String pngPath = "docs/mdisc/us14/png/" + baseFileName + ".png";

        new File("docs/mdisc/us14/dot").mkdirs();
        new File("docs/mdisc/us14/png").mkdirs();


        int maintenanceType = chooseMaintenanceType(sc);
        System.out.println("Selected maintenance type: " +
                (maintenanceType == 1 ? "All the lines" : "Only the electrified lines"));

        System.out.println("\nLoading railway scenario data from .csv file...");
        loadCSV(csvPath, stations, fullGraph, stationOrder, 1);

        maintenanceGraph = new HashMap<>();
        loadCSV(csvPath, stations, maintenanceGraph, stationOrder, maintenanceType);
        System.out.println("Loading complete\n");

        System.out.println("Exporting network to .dot file...");
        exportToDOT(dotPath, fullGraph);
        System.out.println("Export network to .png file....");
        dotToPNG(dotPath, pngPath);
        System.out.println("Export complete\n");
        openFile(pngPath);

        List<String> route = FleuryAlgorithm.computeEulerianPath(maintenanceGraph, stationOrder, sc);
        if (route == null) { // if disconnected or non Eulerian
            return;
        }

        System.out.println("Maintenance route:");
        for (int i = 0; i < route.size(); i++) {
            System.out.print(route.get(i));
            if (i < route.size() - 1) System.out.print(" -> ");
        }
        System.out.println("\n");
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
