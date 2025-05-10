package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class PlayerUI implements Runnable {
    public PlayerUI() {
        // Constructor
    }

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        /*options.add(new MenuItem("Start New Game", new NewGameUI()));
        options.add(new MenuItem("Load Saved Game", new LoadGameUI()));
        options.add(new MenuItem("View Leaderboard", new LeaderboardUI()));*/

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PLAYER MENU -----------------------");

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}