package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

public class DisplayMapUI implements Runnable {
    private final Map map;

    public DisplayMapUI(Map map) {
        this.map = map;
    }

    @Override
    public void run() {
        System.out.println("\n--- MAP TOPOLOGY VIEW -----------------");

        // Display topological info
        System.out.println(map.getTopologicalView(true)); // true to show potential connections

        // Display ASCII art
        System.out.println("\nASCII Representation:");
        System.out.println(map.getASCIIArtView());

        // Show connection options if enough stations exist
        if (map.getStations().size() >= 2) {
            boolean build = Utils.confirm("\nDo you want to build connections between stations? (y/n)");
            if (build) {
                new BuildConnectionUI(map).run();
                // Show updated view after building
                System.out.println("\nUpdated Network View:");
                System.out.println(map.getASCIIArtView());
            }
        }

        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
}