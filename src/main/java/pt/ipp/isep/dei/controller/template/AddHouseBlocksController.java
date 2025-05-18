package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.HouseBlock;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddHouseBlocksController {
    private final Map currentMap;
    private final Random random;

    public AddHouseBlocksController() {
        this.currentMap = ApplicationSession.getInstance().getCurrentMap();
        this.random = new Random();
    }

    public List<HouseBlock> assignBlocks(int numBlocks) {
        List<HouseBlock> blocks = new ArrayList<>();
        List<Position> availablePositions = currentMap.getAvailablePositions(numBlocks);
        
        for (Position position : availablePositions) {
            HouseBlock block = new HouseBlock(position, true);
            blocks.add(block);
            currentMap.markPositionOccupied(position);
        }
        
        return blocks;
    }

    public Position promptForCoordinates() {
        // This method will be called by the UI to get coordinates
        return null; // UI will handle the actual input
    }

    public Position requestNewPosition() {
        // This method will be called by the UI when a position is invalid
        return null; // UI will handle the actual input
    }
} 