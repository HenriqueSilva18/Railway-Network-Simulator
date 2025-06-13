package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class SaveMapUI implements Runnable {
    private final MapController controller;

    public SaveMapUI() {
        this.controller = new MapController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Save Map ===\n");

        // Get current map
        Map currentMap = controller.getCurrentMap();
        if (currentMap == null) {
            System.out.println("No map is currently selected.");
            return;
        }

        // Save map
        if (controller.saveMap(currentMap)) {
            System.out.println("\nMap saved successfully!");
            System.out.println("Saved as: " + currentMap.getNameID() + ".map");
        } else {
            System.out.println("Failed to save map.");
        }
    }
} 