package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {
    private final List<Player> players;

    public PlayerRepository() {
        this.players = new ArrayList<>();
    }

    public Player getPlayerByEmail(String email) {
        for (Player player : players) {
            if (player.getUsername().equals(email)) {
                return player;
            }
        }
        return null;
    }

    public void addPlayer(Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
} 