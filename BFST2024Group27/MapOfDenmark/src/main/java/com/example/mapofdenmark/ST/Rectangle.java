package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.DrawnObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Rectangle implements Serializable {
    public float minX;
    public float maxX;
    public float minY;
    public float maxY;

    public Rectangle(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Returns a new rectangle that is an MBR of the union of two given rectangles.
     *
     * @param a The first rectangle.
     * @param b The second rectangle.
     * @return a new rectangle that is a minimum bounding rectangle of rectangles a and b.
     */
    public static Rectangle union(Rectangle a, Rectangle b) {
        float minX = Math.min(a.minX, b.minX);
        float minY = Math.min(a.minY, b.minY);
        float maxX = Math.max(a.maxX, b.maxX);
        float maxY = Math.max(a.maxY, b.maxY);
        return new Rectangle(minX, maxX, minY, maxY);
    }

    /**
     * Calculate the enlargement of the MBR.
     *
     * @param a The Rectangle to be expanded
     * @param b The Rectangle to be added to the MBR
     * @return The area of union(a,b) subtracted by the area of {@code a}.
     */
    public static double calculateEnlargement(Rectangle a, Rectangle b) {
        return union(a, b).area() - a.area();
    }

    public static float calculateEnlargement(float minX, float maxX, float minY, float maxY, BoundingBox box) {
        float enlargement = calculateEnlargement(minX, maxX, minY, maxY, box.getMinX(), box.getMaxX(), box.getMinY(), box.getMaxY());
        if (enlargement == Float.POSITIVE_INFINITY || enlargement == Float.NEGATIVE_INFINITY) {
            throw new RuntimeException("enlargement is equal to " + enlargement + "\n" + minX + " "+ maxX + " "+ minY + " "+ maxY + " "+ box.getMinX() + " "+ box.getMaxX() + " "+ box.getMinY() + " "+ box.getMaxY() + " ");
        }
        return enlargement;
    }

    public static float calculateEnlargement(float minX1, float maxX1, float minY1, float maxY1, float minX2, float maxX2, float minY2, float maxY2) {
        float unionMinX = Math.min(minX1, minX2);
        float unionMaxX = Math.max(maxX1, maxX2);
        float unionMinY = Math.min(minY1, minY2);
        float unionMaxY = Math.max(maxY1, maxY2);

        float unionSize = (unionMaxX - unionMinX) - (unionMaxY - unionMinY);
        float boundarySize = (maxX1 - minX1) * (maxY1 - minY1);

        float enlargement = Math.abs(unionSize - boundarySize);

        return enlargement;
    }

    public static double calculateUnionEnlargement(DrawnObject n1, DrawnObject n2) {
        return createRectangle(n1, n2).area() - n1.area() - n2.area();
    }

    public static float calculateDeadSpace(float minX1, float maxX1, float minY1, float maxY1, float minX2, float maxX2, float minY2, float maxY2) {
        float area1 = (maxX1 - minX1) * (maxY1 - minY1);
        float area2 = (maxX2 - minX2) * (maxY2 - minY2);

        float unionMinX = Math.min(minX1, minX2);
        float unionMaxX = Math.max(maxX1, maxX2);
        float unionMinY = Math.min(minY1, minY2);
        float unionMaxY = Math.max(maxY1, maxY2);

        float unionSize = (unionMaxX - unionMinX) * (unionMaxY - unionMinY);

        return unionSize - area1 - area2;
    }

    /**
     * Calculate the overlap given two rectangles.
     * @param minX1
     * @param maxX1
     * @param minY1
     * @param maxY1
     * @param minX2
     * @param maxX2
     * @param minY2
     * @param maxY2
     * @return The area of the overlap of the two rectangles.
     */
    public static float calculateOverlap(float minX1, float maxX1, float minY1, float maxY1, float minX2, float maxX2, float minY2, float maxY2){
        float overlapMinX = Math.max(minX1, minX2);
        float overlapMaxX = Math.min(maxX1, maxX2);
        float overlapMinY = Math.max(minY1, minY2);
        float overlapMaxY = Math.min(maxY1, maxY2);

        if(!intersects( minX1,  maxX1,  minY1,  maxY1,  minX2,  maxX2,  minY2,  maxY2)){
            return 0;
        }

        return Math.abs((overlapMaxX - overlapMinX) * (overlapMaxY - overlapMinY));
    }

    public static boolean intersects(float minX1, float maxX1, float minY1, float maxY1, float minX2, float maxX2, float minY2, float maxY2){
        return minX1 <= maxX2 && minX2 <= maxX1 && minY1 <= maxY2 && minY2 <= maxY1;
    }

    public boolean intersects(Rectangle other) {
        return minX <= other.maxX && other.minX <= maxX && minY <= other.maxY && other.minY <= maxY;
    }

    public boolean intersects(DrawnObject other) {
        return minX <= other.getEdge(2) && other.getEdge(0) <= maxX && minY <= other.getEdge(3) && other.getEdge(1) <= maxY;
    }

    public boolean intersects(float otherMinX, float otherMaxX, float otherMinY, float otherMaxY) {
        return minX <= otherMaxX && otherMinX <= maxX && minY <= otherMaxY && otherMinY <= maxY;
    }

    public double area() {
        return Math.abs((maxX - minX) * (maxY - minY));
    }

    public static Rectangle createRectangleFromRNodes(ArrayList<RNode> nodes) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (RNode node : nodes) {
            if (node.minX < minX) {
                minX = node.minX;
            }
            if (node.maxX > maxX) {
                maxX = node.maxX;
            }

            if (node.minY < minY) {
                minY = node.minY;
            }
            if (node.maxY > maxY) {
                maxY = node.maxY;
            }
        }

        return new Rectangle(minX, maxX, minY, maxY);
    }

    public static Rectangle createRectangleFromDrawnObjects(ArrayList<DrawnObject> drawnObjects) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (DrawnObject obj : drawnObjects) {
            minX = Math.min(minX, obj.getMinX());
            minY = Math.min(minY, obj.getMinY());
            maxX = Math.max(maxX, obj.getMaxX());
            maxY = Math.max(maxY, obj.getMaxY());
        }

        return new Rectangle(minX, maxX, minY, maxY);
    }

    public static Rectangle createRectangle(DrawnObject n1, DrawnObject n2) {
        return new Rectangle(Math.min(n1.getEdge(0), n2.getEdge(0)),
                Math.max(n1.getEdge(2), n2.getEdge(2)),
                Math.min(n1.getEdge(1), n2.getEdge(1)),
                Math.max(n1.getEdge(3), n2.getEdge(3)));
    }

    public static Rectangle createRectangle(Rectangle oldRectangle, DrawnObject drawnObject) {
        float minX = Math.min(oldRectangle.minX, drawnObject.getEdge(0));
        float minY = Math.min(oldRectangle.minY, drawnObject.getEdge(1));
        float maxX = Math.max(oldRectangle.maxX, drawnObject.getEdge(2));
        float maxY = Math.max(oldRectangle.maxY, drawnObject.getEdge(3));
        return new Rectangle(minX, maxX, minY, maxY);
    }
}
