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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
} 