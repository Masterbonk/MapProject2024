package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.DrawnObject;

import java.io.Serializable;
import java.util.ArrayList;

public class RNode implements Serializable, BoundingBox {
    public ArrayList<RNode> children;
    public ArrayList<DrawnObject> values;
    //public Rectangle boundary;
    public float minX = Float.POSITIVE_INFINITY;
    public float maxX = Float.NEGATIVE_INFINITY;
    public float minY = Float.POSITIVE_INFINITY;
    public float maxY = Float.NEGATIVE_INFINITY;

    /**
     * Create a new empty RNode.
     */
    public RNode() {
        children = new ArrayList<>();
        values = new ArrayList<>();
    }

    /**
     * Create a new leaf RNode with a given ArrayList of values.
     * @param values The values of the leaf.
     */
    public RNode(ArrayList<DrawnObject> values) {
        children = new ArrayList<>();
        this.values = values;
        updateBoundary();
    }

    /**
     * Returns whether the node is a leaf.
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Add a child to the node.
     * @param child Child to be added.
     */
    public void addChild(RNode child){
        children.add(child);
        updateBoundary();
    }

    /**
     * Remove child from node.
     * @param child Child to be removed.
     */
    public void removeChild(RNode child){
        children.remove(child);
        updateBoundary();
    }

    /**
     * Add value to the node.
     * @param value The value to be added.
     */
    public void addValue(DrawnObject value){
        values.add(value);
        updateBoundary();
    }

    /**
     * Set the children of the node to a given ArrayList.
     * @param children The new ArrayList of children.
     */
    public void setChildren(ArrayList<RNode> children){
        this.children = children;
        updateBoundary();
    }

    /**
     * Updates the MBR of the node.
     */
    public void updateBoundary() {
        minX = Float.POSITIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;

        if (isLeaf()) {
            if (values == null || values.isEmpty()) {
                //throw new RuntimeException("Values is empty or null!");
            }
            for (DrawnObject d : values) {
                minX = Math.min(minX, d.getMinX());
                maxX = Math.max(maxX, d.getMaxX());
                minY = Math.min(minY, d.getMinY());
                maxY = Math.max(maxY, d.getMaxY());
            }
        } else {
            if (children == null || children.isEmpty()) {
                throw new RuntimeException("children is empty or null!");
            }
            for (RNode n : children) {
                minX = Math.min(minX, n.minX);
                maxX = Math.max(maxX, n.maxX);
                minY = Math.min(minY, n.minY);
                maxY = Math.max(maxY, n.maxY);
            }
        }
    }

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public float getMinY() {
        return minY;
    }

    public float getMidX() {
        return (minX + maxX) / 2f;
    }

    public float getMidY() {
        return (minY + maxY) / 2f;
    }

    @Override
    public float getMaxX() {
        return maxX;
    }

    @Override
    public float getMaxY() {
        return maxY;
    }
}