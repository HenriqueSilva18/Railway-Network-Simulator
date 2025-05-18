package pt.ipp.isep.dei.domain.template;

public class Position {
    private int x;
    private int y;
    private boolean occupied;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
} 