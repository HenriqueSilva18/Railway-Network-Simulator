package pt.ipp.isep.dei.domain.template;

public class Size {
    private final int width;
    private final int height;

    private Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static boolean validateSize(int width, int height) {
        return width > 0 && height > 0;
    }

    public static Size createSize(int width, int height) {
        if (!validateSize(width, height)) {
            throw new IllegalArgumentException("Invalid size dimensions");
        }
        return new Size(width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
} 