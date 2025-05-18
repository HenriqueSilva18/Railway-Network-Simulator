package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.ShowTextUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Runnable {

    public EditorUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Create Map", new CreateMapUI()));
        options.add(new MenuItem("List Maps", new ShowTextUI("List of maps functionality will be implemented here.")));
        options.add(new MenuItem("Edit Map", new ShowTextUI("Edit map functionality will be implemented here.")));
        options.add(new MenuItem("Delete Map", new ShowTextUI("Delete map functionality will be implemented here.")));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- EDITOR MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
} 