package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.ShowTextUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.ui.console.ViewScenarioLayoutUI;
import pt.ipp.isep.dei.ui.console.SaveMapUI;
import pt.ipp.isep.dei.ui.console.LoadMapUI;

import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Runnable {

    public EditorUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Create Map", new CreateMapUI()));
        options.add(new MenuItem("List Maps", new ShowTextUI("List of maps functionality will be implemented here.")));
        options.add(new MenuItem("Edit Map", new EditMapUI()));
        options.add(new MenuItem("Save Map", new SaveMapUI()));
        options.add(new MenuItem("Load Map", new LoadMapUI()));
        options.add(new MenuItem("Create Scenario", new CreateScenarioUI()));
        options.add(new MenuItem("View Scenario Layout", new ViewScenarioLayoutUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- EDITOR MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
} 