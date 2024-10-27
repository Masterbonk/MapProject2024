package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Way;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * WARNING: Very high memory usage!
 * <br> A custom spatial data structure for storing and querying multidimensional information in the form of 2-dimensional rectangles.
 * The tree consists of layers of arrays.
 * The first 4 layers are internal. The last layer stores the values of the data structure.
 * Each level of the tree corresponds to one edge of the values' MBRs - see Way.getEdge().
 */
public class ETree implements SearchTree<Way>, Serializable {
    public class ENode implements Serializable {
        public Way way;
        public ENode[] assoc;

        public ENode(Way way) {
            assoc = null;
            this.way = way;
        }
    }

    /**
     * The first layer of the tree
     */
    ENode[] tree;

    public ETree(Way[] ways) {
        tree = buildArray(ways, 0);
    }

    /**
     * Builds an array in the tree at the given depth.
     * @param ways The ways to be stored in the current subtree.
     * @param depth The depth of the array.
     * @return The array created at this depth.
     */
    public ENode[] buildArray(Way[] ways, int depth) {
        if (depth > 4) {
            return null;
        }

        if(depth < 4) {
            SearchTreeUtil.sort(ways, depth);
        }

        if (depth > 1) {
            SearchTreeUtil.reverseArray(ways);
        }

        ENode[] nodes = new ENode[ways.length];

        for (int i = 0; i < ways.length; i++) {
            Way[] temp = new Way[i + 1];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = ways[j];
            }
            nodes[i] = new ENode(ways[i]);
            nodes[i].assoc = buildArray(temp, depth + 1);
        }

        return nodes;
    }

    /**
     * Query the tree.
     * @param minX The left bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @return An ArrayList of Ways within the query rectangle.
     */
    @Override
    public ArrayList<Way> query(double minX, double minY, double maxX, double maxY) {
        ENode[] eNodes = tree;

        for (int i = 0; i < 4; i++) {
            int newIndex = findLargestIndex(eNodes, minX, minY, maxX, maxY, i);
            if(newIndex == -1) return new ArrayList<>();
            eNodes = eNodes[newIndex].assoc;
        }

        ArrayList<Way> ways = new ArrayList<>();

        for (int i = 0; i < eNodes.length; i++) {
            //ways.set(i, eNodes[i].way);
            ways.add(eNodes[i].way);
        }

        return ways;
    }

    /**
     * Find the index of the next subtree to search given an array of nodes.
     * @param nodes The nodes found at the previous level of the tree.
     * @param minX  The left bound of the query rectangle.
     * @param minY  The lower bound of the query rectangle.
     * @param maxX  The right bound of the query rectangle.
     * @param maxY  The upper bound of the query rectangle.
     * @param depth  The depth of the new level of the tree being searched.
     * @return Returns the largest index among the nodes inside the query. Returns -1 if no nodes are in the query - including if the set is length 0.
     */
    public int findLargestIndex(ENode[] nodes, double minX, double minY, double maxX, double maxY, int depth) {
        int l = 0;
        int r = nodes.length - 1;

        int largestIndex = -1;

        while (l <= r) {
            int m = l + (r - l) / 2;

            if (inQuery(nodes[m], minX, minY, maxX, maxY, depth)) {
                largestIndex = m;
                l = m + 1;
            } else {
                r = m - 1;
            }
        }

        return largestIndex;
    }

    /**
     * Returns whether the given node is within the query, only looking at one edge of the node's MBR.
     * @param node The node being inspected.
     * @param minX The left bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @param depth The depth being considered.
     * @return A boolean symbolizing whether the node is within the query, looking at one edge of the MBR.
     */
    private boolean inQuery(ENode node, double minX, double minY, double maxX, double maxY, int depth) {
        if (depth <= 1) {
            return node.way.getEdge(depth) <= getOpposing(minX, minY, maxX, maxY, depth);
        } else {
            return node.way.getEdge(depth) >= getOpposing(minX, minY, maxX, maxY, depth);
        }
    }

    /**
     * Given a rectangle and a depth, return the edge opposite the edge corresponding to the depth.
     * @param minX The left bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @param depth The depth being considered.
     * @return The opposing edge of the one corresponding to the depth.
     */
    private double getOpposing(double minX, double minY, double maxX, double maxY, int depth) {
        switch (depth) {
            case 0:
                return maxX;
            case 1:
                return maxY;
            case 2:
                return minX;
            case 3:
                return minY;
            default:
                throw new IllegalArgumentException("Depth " + depth + " is not recognized!");
        }
    }
}
