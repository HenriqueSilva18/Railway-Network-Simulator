package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
} 