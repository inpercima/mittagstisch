package net.inpercima.mittagstisch.model;

/**
 * Represents a crop region as percentages (0–100) of the original image dimensions.
 */
public record CropBox(int xStart, int yStart, int xEnd, int yEnd) {

    /**
     * Returns {@code true} when all coordinates are within [0, 100] and the region
     * has a non-zero area.
     */
    public boolean isValid() {
        return xStart >= 0 && yStart >= 0 && xEnd <= 100 && yEnd <= 100
                && xStart < xEnd && yStart < yEnd;
    }
}
