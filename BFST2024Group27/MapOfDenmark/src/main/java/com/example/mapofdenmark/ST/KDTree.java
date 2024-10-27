package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Way;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A KD-Tree implementation for classes that implement the Point interface
 * @param <T> A class that implements the Point interface
 */
public class KDTree<T extends Point> implements SearchTree<T>, Serializable {
    public class KDNode implements Serializable {
        public T value;
        public KDNode leftChild, rightChild;
        public byte depth;

        public KDNode(byte depth) {
            value = null;
            leftChild = null;
            rightChild = null;
            this.depth = depth;
        }

        public boolean isLeaf() {
            return leftChild == null;
        }
    }

    /**
     * The root of the KD-tree. Read-only.
     */
    public final KDNode root;
    public final KDTreeUtil<T> util;
    private ArrayList<T> reported;

    public KDTree(Class<T> clazz, T[] arr) {
        util = new KDTreeUtil<T>();
        util.sort(arr, 0);
        root = buildKDtree(clazz, arr, 0);
    }

    /**
     * Recursively builds subtrees in the KD-tree
     * @param clazz The class of the items.
     * @param arr The array of items in the current subtree.
     * @param depth the current depth in the KD-tree.
     * @return the root of the subtree.
     */
    private KDNode buildKDtree(Class<T> clazz, T[] arr, int depth) {
        KDNode node = new KDNode((byte)depth);

        if(arr.length == 0){
            return node;
        }

        //create leaf
        if (arr.length == 1) {
            node.value = arr[0];
            return node;
        }

        int medianIndex = (arr.length - 1) / 2;

        T[] L = (T[]) Array.newInstance(clazz, medianIndex + 1);
        T[] R = (T[]) Array.newInstance(clazz, arr.length - medianIndex - 1);

        //ArrayList<T> L = new ArrayList<T>(medianIndex + 1);
        //ArrayList<T> R = new ArrayList<T>(arr.length - medianIndex - 1);
        //T[] L = new T[medianIndex + 1];
        //T[] R = new T[arr.length - medianIndex - 1];

        for (int i = 0; i < L.length; i++) {
            //L.set(i, arr[i]);
            L[i] = arr[i];
        }
        for (int i = 0; i < R.length; i++) {
            //R.set(i, arr[medianIndex + i + 1]);
            R[i] = arr[medianIndex + i + 1];
        }

        int nextDepth = (depth + 1) % 2;

        util.sort(L, nextDepth);
        util.sort(R, nextDepth);

        //create internal node
        node.leftChild = buildKDtree(clazz, L, nextDepth);
        node.rightChild = buildKDtree(clazz, R, nextDepth);
        node.value = arr[medianIndex];

        return node;
    }

    /**
     * Finds all objects within the given query rectangle
     * @param minX The left bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @return an arraylist of the objects query the query
     */
    @Override
    public ArrayList<T> query(double minX, double minY, double maxX, double maxY) {
        reported = new ArrayList<T>();

        KDNode split = findSplitNode(root, minX, minY, maxX, maxY);
        searchKDTree(split, minX, minY, maxX, maxY, split.depth);

        return reported;
    }

    /**‰
     * Returns the split-node of the given query-rectangle
     * @param v the root of a subtree - usually the root
     * @param minX The left bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @return the split node
     */
    public KDNode findSplitNode(KDNode v, double minX, double minY, double maxX, double maxY) {
        int depth = 0;
        while (!v.isLeaf() && queryIsOutside(v, minX, minY, maxX, maxY, depth)){ // as long as node is not leaf and is outside range
            if(queryIsLessThan(v, maxX, maxY, depth)){
                v = v.leftChild;
            } else {
                v = v.rightChild;
            }
            depth = (depth + 1) % 2;
        }

        return v;
    }

    /**
     * Returns whether the split node is located in the left subtree.
     * @param v The node being inspected.
     * @param maxX The right edge of the query rectangle.
     * @param maxY The upper edge of the query rectangle.
     * @param depth The depth of the node. This determines whether the method will look at X- or Y-coordinate.
     * @return A boolean symbolising whether the splitnode is located in the left subtree.
     */
    private boolean queryIsLessThan(KDNode v, double maxX, double maxY, int depth){
        switch(depth){
            case 0:
                return maxX <= v.value.getCoord(0);
            case 1:
                return maxY <= v.value.getCoord(1);
            default:
                throw new IllegalArgumentException("Depth " + depth + " is not recognized!");
        }
    }

    private boolean queryIsOutside(KDNode v, double minX, double minY, double maxX, double maxY, int depth){
        switch(depth){
            case 0:
                return valueIsOutsideRange(v.value.getCoord(0), minX, maxX);
            case 1:
                return valueIsOutsideRange(v.value.getCoord(1), minY, maxY);
            default:
                throw new IllegalArgumentException("Depth " + depth + " is note recognized!");
        }
    }

    private boolean valueIsOutsideRange(double val, double min, double max){
        return max <= val || val < min;
    }

    private void searchKDTree(KDNode v, double minX, double minY, double maxX, double maxY, int depth) {
        if (v.isLeaf()){
            if (util.inRange(v.value, minX, minY, maxX, maxY)) {
                reported.add(v.value);
            }
            return;
        }

        if(queryGoesLeft(v, minX, minY, depth)) {
            searchKDTree(v.leftChild, minX, minY, maxX, maxY, (depth+1)%2);
        }

        if(queryGoesRight(v, maxX, maxY, depth)){
            searchKDTree(v.rightChild, minX, minY, maxX, maxY, (depth+1)%2);
        }
    }

    private boolean queryGoesLeft(KDNode v, double minX, double minY, int depth){
        switch (depth){
            case 0:
                return minX <= v.value.getCoord(0);
            case 1:
                return minY <= v.value.getCoord(1);
            default:
                throw new IllegalArgumentException("Depth " + depth + " is not recognized!");
        }
    }

    private boolean queryGoesRight(KDNode v, double maxX, double maxY, int depth){
        switch (depth){
            case 0:
                return maxX > v.value.getCoord(0);
            case 1:
                return maxY > v.value.getCoord(1);
            default:
                throw new IllegalArgumentException("Depth " + depth + " is not recognized!");
        }
    }
}
