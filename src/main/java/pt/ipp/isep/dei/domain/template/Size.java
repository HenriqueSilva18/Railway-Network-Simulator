package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;

public class Size implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int width;
    private int height;

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