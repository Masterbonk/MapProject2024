package com.example.mapofdenmark;

import com.example.mapofdenmark.ST.BoundingBox;
import com.example.mapofdenmark.ST.Point;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class DrawnObject implements BoundingBox, Point, Serializable {
    byte type;

    float minX;
    float maxX;
    float minY;
    float maxY;

    DrawnObject() {

    }

    public abstract void draw(GraphicsContext gc, View view);

    public float getCoord(int c) {
        return switch (c) {
            case 0 -> (minX + maxX) / 2f;
            case 1 -> (minY + maxY) / 2f;
            default ->
                    throw new IllegalArgumentException("The coordinate corresponding to value " + c + " is not recognized!");
        };
    }

    /**
     * Return the edge of the DrawnObject's MBR corresponding to the given value of {@code edge}.
     * <br> 0 corresponds to minX.
     * <br> 1 corresponds to minY.
     * <br> 2 corresponds to maxX.
     * <br> 3 corresponds to maxY.
     *
     * @param edge The index of the edge being requested.
     * @return The requested edge.
     * @throws IllegalArgumentException If the given value of {@code edge} is not recognized.
     */
    public float getEdge(int edge) {
        return switch (edge) {
            case 0 -> minX;
            case 1 -> minY;
            case 2 -> maxX;
            case 3 -> maxY;
            default -> throw new IllegalArgumentException("The edge number " + edge + " is not recognized!");
        };
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMidX() {
        return (minX + maxX) / 2f;
    }

    public float getMidY() {
        return (minY + maxY) / 2f;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float area() {
        return Math.abs((maxX - minX) * (maxY - minY));
    }
}
