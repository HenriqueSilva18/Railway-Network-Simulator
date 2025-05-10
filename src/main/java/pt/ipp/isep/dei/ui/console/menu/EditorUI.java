package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.CreateMapUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Runnable {
    public EditorUI() {
        // Constructor
    }

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Create New Map", new CreateMapUI()));
       /* options.add(new MenuItem("List My Maps", new ListMapsUI()));
        options.add(new MenuItem("Create Scenario", new CreateScenarioUI()));*/

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- EDITOR MENU -----------------------");

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}