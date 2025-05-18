package pt.ipp.isep.dei.domain.template;

public class HouseBlock {
    private final Position position;
    private final boolean automaticPlacement;

    public HouseBlock(Position position, boolean automaticPlacement) {
        this.position = position;
        this.automaticPlacement = automaticPlacement;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isAutomaticPlacement() {
        return automaticPlacement;
    }
} 