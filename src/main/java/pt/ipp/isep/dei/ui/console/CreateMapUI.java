package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.CreateMapController;
import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

public class CreateMapUI implements Runnable {
    private final CreateMapController controller;

    public CreateMapUI() {
        this.controller = new CreateMapController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- CREATE MAP -------------------------");

        String name = Utils.readLineFromConsole("Enter map name: ");
        int width = Utils.readIntegerFromConsole("Enter map width: ");
        int height = Utils.readIntegerFromConsole("Enter map height: ");

        try {
            Map createdMap = controller.createNewMap(name, width, height);
            if (createdMap != null) {
                System.out.println("Map created successfully!");
                System.out.println("Name: " + createdMap.getNameID());
                System.out.println("Size: " + createdMap.getSize().getWidth() + "x" + createdMap.getSize().getHeight());
            }
        } catch (Exception e) {
            System.out.println("Error creating map: " + e.getMessage());
        }
    }
}