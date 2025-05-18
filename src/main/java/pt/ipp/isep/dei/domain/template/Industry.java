package pt.ipp.isep.dei.domain.template;

public class Industry {
    private final String name;
    private final int x;
    private final int y;

    public Industry(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
} 