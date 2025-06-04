package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.RailwayLine;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.repository.template.*;

import java.util.*;

public class BuildRailwayLineController {
    private final RailwayLineRepository railwayLineRepository;
    private final StationRepository stationRepository;
    private final PlayerRepository playerRepository;

    public BuildRailwayLineController() {
        Repositories repositories = Repositories.getInstance();
        this.railwayLineRepository = repositories.getRailwayLineRepository();
        this.stationRepository = repositories.getStationRepository();
        this.playerRepository = repositories.getPlayerRepository();
    }

    public List<Station> getAvailableStations() {
        // Get current map
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return new ArrayList<>();
        }

        // Get stations from the map
        List<Station> stations = currentMap.getStations();
        
        // Store stations in the repository for later retrieval
        for (Station station : stations) {
            stationRepository.add(station);
        }
        
        return stations;
    }

    public boolean canBuildRailwayLine(Station station1, Station station2) {
        if (station1 == null || station2 == null || station1.equals(station2)) {
            return false;
        }

        // Check if railway line already exists between these stations
        if (railwayLineRepository.exists(station1, station2)) {
            return false;
        }

        // Get current player
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }

        // Find direct path (simplified)
        List<Position> path = findDirectPath(station1, station2);
        if (path == null || path.isEmpty()) {
            return false;  // No valid path found
        }

        double cost = calculatePathCost(path);
        return currentPlayer.getCurrentBudget() >= cost;
    }

    public RailwayLine buildRailwayLine(Station station1, Station station2) {
        if (station1 == null || station2 == null || station1.equals(station2)) {
            return null;
        }

        // Check if railway line already exists between these stations
        if (railwayLineRepository.exists(station1, station2)) {
            return null;
        }

        // Get current player
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return null;
        }

        // Find the path using simplified algorithm
        List<Position> path = findDirectPath(station1, station2);
        if (path == null || path.isEmpty()) {
            return null;
        }

        // Calculate cost
        double cost = calculatePathCost(path);

        // Check if player has enough budget
        if (currentPlayer.getCurrentBudget() < cost) {
            return null;
        }

        // Deduct construction cost
        if (!currentPlayer.deductFromBudget(cost)) {
            return null;
        }

        // Generate a unique ID for the railway line
        String lineId = "line_" + station1.getNameID() + "_" + station2.getNameID();

        // Create and save the railway line with the path
        RailwayLine railwayLine = new RailwayLine(lineId, station1, station2, path);
        if (railwayLineRepository.save(railwayLine)) {
            // Update player in repository
            playerRepository.save(currentPlayer);
            return railwayLine;
        }

        // If save failed, refund the player
        currentPlayer.addToBudget(cost);
        playerRepository.save(currentPlayer);
        return null;
    }

    // Simplified method to find a direct path between two stations using Bresenham's line algorithm
    public List<Position> findDirectPath(Station start, Station end) {
        // Get positions of stations
        Position startPos = start.getPosition();
        Position endPos = end.getPosition();
        
        // Use Bresenham's line algorithm to find direct path
        return bresenhamLine(startPos.getX(), startPos.getY(), endPos.getX(), endPos.getY());
    }
    
    // Bresenham's line algorithm implementation
    private List<Position> bresenhamLine(int x0, int y0, int x1, int y1) {
        List<Position> path = new ArrayList<>();
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            path.add(new Position(x0, y0));
            
            if (x0 == x1 && y0 == y1) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
        
        return path;
    }

    private List<Position> getNeighbors(Position pos, Map gameMap) {
        List<Position> neighbors = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0, -1, -1, 1, 1};  // Include diagonals
        int[] dy = {0, -1, 0, 1, -1, 1, -1, 1};  // Include diagonals

        for (int i = 0; i < dx.length; i++) {
            int newX = pos.getX() + dx[i];
            int newY = pos.getY() + dy[i];
            Position newPos = new Position(newX, newY);

            // Check if position is within map bounds
            if (newX >= 0 && newX < gameMap.getSize().getWidth() &&
                newY >= 0 && newY < gameMap.getSize().getHeight()) {
                
                // Check if position is empty (not a city or industry)
                if (isPositionEmpty(newPos, gameMap)) {
                    neighbors.add(newPos);
                }
            }
        }

        return neighbors;
    }

    private boolean isPositionEmpty(Position pos, Map gameMap) {
        // Check if position contains a city
        boolean hasCity = gameMap.getCities().stream()
            .anyMatch(city -> city.getPosition().equals(pos));
        if (hasCity) return false;

        // Check if position contains an industry
        boolean hasIndustry = gameMap.getIndustries().stream()
            .anyMatch(industry -> industry.getPosition().equals(pos));
        if (hasIndustry) return false;

        // Position is empty
        return true;
    }

    private double heuristic(Position a, Position b) {
        // Using diagonal distance as heuristic
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        return Math.max(dx, dy);
    }

    private List<Position> reconstructPath(Node endNode) {
        List<Position> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(0, current.pos);
            current = current.parent;
        }
        return path;
    }

    public double calculatePathCost(List<Position> path) {
        // Base cost per segment
        double baseCost = 1000;
        
        // Cost multiplier for diagonal segments
        double diagonalMultiplier = 1.4;
        
        double totalCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            // Check if segment is diagonal
            boolean isDiagonal = current.getX() != next.getX() && current.getY() != next.getY();
            
            totalCost += isDiagonal ? baseCost * diagonalMultiplier : baseCost;
        }
        
        return totalCost;
    }

    private static class Node implements Comparable<Node> {
        Position pos;
        Node parent;
        double g;  // Cost from start to current
        double h;  // Estimated cost from current to end
        double f;  // g + h

        Node(Position pos) {
            this.pos = pos;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }
} 