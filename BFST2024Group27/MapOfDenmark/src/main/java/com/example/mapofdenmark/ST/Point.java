package com.example.mapofdenmark.ST;

/**
 * For use in KDTree. This interface is for spatial data that can be represented as a 2-dimensional point.
 */
public interface Point {
    public float getMidX();
    public float getMidY();
    public float getCoord(int c);
}
