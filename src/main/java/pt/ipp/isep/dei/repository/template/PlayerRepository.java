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

    public boolean save(Player player) {
        if (player == null) {
            return false;
        }
        
        // Check if player already exists
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUsername().equals(player.getUsername())) {
                // Update existing player
                players.set(i, player);
                return true;
            }
        }
        
        // Add as new player
        players.add(player);
        return true;
    }

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
} 