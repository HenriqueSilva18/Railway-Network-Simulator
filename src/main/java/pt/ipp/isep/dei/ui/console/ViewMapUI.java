package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.RailwayLineRepository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class ViewMapUI implements Runnable {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    @Override
    public void run() {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            System.out.println("No map selected. Please select a map first.");
            return;
        }

        displayMap(currentMap);
        displayLegend();
    }

    private void displayMap(Map map) {
        Size size = map.getSize();
        RailwayLineRepository railwayLineRepository = Repositories.getInstance().getRailwayLineRepository();
        List<RailwayLine> railwayLines = railwayLineRepository.getAll();

        // Print column numbers
        System.out.print("  ");
        for (int x = 0; x < size.getWidth(); x++) {
            System.out.print(x % 10 + " ");
        }
        System.out.println();

        StringBuilder legend = new StringBuilder();
        
        // Print each row
        for (int y = 0; y < size.getHeight(); y++) {
            // Print row number
            System.out.printf("%2d", y);
            
            // Print each cell in the row
            for (int x = 0; x < size.getWidth(); x++) {
                String symbol = getCellSymbol(map, x, y, railwayLines);
                System.out.print(" " + symbol);
                
                // Add to legend if needed
                addToLegend(map, x, y, legend);
            }
            
            // Print legend items for this row
            if (legend.length() > 0) {
                System.out.print("   " + legend);
                legend.setLength(0);  // Clear for next row
            }
            
            System.out.println();
        }

        // Display railway lines
        if (!railwayLines.isEmpty()) {
            System.out.println("\nRailway Lines:");
            for (RailwayLine line : railwayLines) {
                Station start = line.getStartStation();
                Station end = line.getEndStation();
                System.out.printf("%s%s -> %s (Length: %.2f)%s\n",
                    YELLOW,
                    start.getNameID(),
                    end.getNameID(),
                    line.getLength(),
                    RESET);
            }
        }
    }

    private String getCellSymbol(Map map, int x, int y, List<RailwayLine> railwayLines) {
        // Check for station first (highest priority)
        Optional<Station> station = map.getStations().stream()
                .filter(s -> s.getPosition().getX() == x && s.getPosition().getY() == y)
                .findFirst();
        if (station.isPresent()) {
            return GREEN + "S" + RESET;
        }

        // Check for city
        Optional<City> city = map.getCities().stream()
                .filter(c -> c.getPosition().getX() == x && c.getPosition().getY() == y)
                .findFirst();
        if (city.isPresent()) {
            return RED + "C" + RESET;
        }

        // Check for industry
        Optional<Industry> industry = map.getIndustries().stream()
                .filter(i -> i.getPosition().getX() == x && i.getPosition().getY() == y)
                .findFirst();
        if (industry.isPresent()) {
            return BLUE + "I" + RESET;
        }

        // Check if this cell is part of a railway line
        for (RailwayLine line : railwayLines) {
            if (isOnLine(x, y, line)) {
                return YELLOW + "=" + RESET;
            }
        }

        // Empty cell
        return ".";
    }

    private boolean isOnLine(int x, int y, RailwayLine line) {
        Station start = line.getStartStation();
        Station end = line.getEndStation();
        
        int x1 = start.getPosition().getX();
        int y1 = start.getPosition().getY();
        int x2 = end.getPosition().getX();
        int y2 = end.getPosition().getY();

        // Get all points on the line using Bresenham's algorithm
        List<Point> linePoints = getLinePoints(x1, y1, x2, y2);
        
        // Check if the given point is on the line
        return linePoints.stream().anyMatch(p -> p.x == x && p.y == y);
    }

    private List<Point> getLinePoints(int x1, int y1, int x2, int y2) {
        List<Point> points = new ArrayList<>();
        
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        int x = x1;
        int y = y1;
        
        while (true) {
            points.add(new Point(x, y));
            
            if (x == x2 && y == y2) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x = x + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y = y + sy;
            }
        }
        
        return points;
    }

    private static class Point {
        final int x;
        final int y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private void addToLegend(Map map, int x, int y, StringBuilder legend) {
        // Add station to legend
        Optional<Station> station = map.getStations().stream()
                .filter(s -> s.getPosition().getX() == x && s.getPosition().getY() == y)
                .findFirst();
        if (station.isPresent()) {
            legend.append("(Station at ").append(x).append(",").append(y).append(") ");
        }

        // Add city to legend
        Optional<City> city = map.getCities().stream()
                .filter(c -> c.getPosition().getX() == x && c.getPosition().getY() == y)
                .findFirst();
        if (city.isPresent()) {
            legend.append("(City: ").append(city.get().getNameID()).append(") ");
        }

        // Add industry to legend
        Optional<Industry> industry = map.getIndustries().stream()
                .filter(i -> i.getPosition().getX() == x && i.getPosition().getY() == y)
                .findFirst();
        if (industry.isPresent()) {
            legend.append("(Industry: ").append(industry.get().getNameID()).append(") ");
        }
    }

    private void displayLegend() {
        System.out.println("\nLegend:");
        System.out.println(RED + "C" + RESET + " - City");
        System.out.println(GREEN + "S" + RESET + " - Station");
        System.out.println(BLUE + "I" + RESET + " - Industry");
        System.out.println(YELLOW + "=" + RESET + " - Railway Line");
    }
} 