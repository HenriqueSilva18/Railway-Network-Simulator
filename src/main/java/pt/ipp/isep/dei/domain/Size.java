package pt.ipp.isep.dei.domain;

public class Size {
    private int width;
    private int height;

    public Size(int width, int height) {
        if (!validateDimensions(width, height)) {
            throw new IllegalArgumentException("Invalid map dimensions");
        }
        this.width = width;
        this.height = height;
    }

    public boolean validateDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}