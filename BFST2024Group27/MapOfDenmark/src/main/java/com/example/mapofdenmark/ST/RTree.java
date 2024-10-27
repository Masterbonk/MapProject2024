package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.DrawnObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

/**
 * R-tree as defined in Guttman's text 'R-TREES'. Implements Bulk-loading as described in Leutenegger's text 'STR: ...'
 */
public class RTree implements Serializable {
    /**
     * The root of the tree.
     */
    private RNode root;
    public int rectanglesExamined;

    public RNode getRoot(){
        return root;
    }

    private final int M; // max number of entries per node
    private final int m; // min number of entries per node

    ArrayList<DrawnObject> reported;

    /**
     * Create a new R-tree with the given value of M.
     *
     * @param M The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @throws IllegalArgumentException if M is not greater than 1.
     */
    public RTree(int M) {
        if (M < 2) {
            throw new IllegalArgumentException("The value M must be greater or equal to 2!");
        }

        this.M = M;
        this.m = M / 2;
        root = new RNode();
        reported = null;
    }

    /**
     * Create and bulk-load a new R-tree with the given value of M and an ArrayList of DrawnObjects.
     *
     * @param M    The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @param bulk An ArrayList of DrawnObjects to be inserted into the tree.
     */
    public RTree(int M, ArrayList<DrawnObject> bulk) {
        if (M < 2) {
            throw new IllegalArgumentException("The value M must be greater or equal to 2!");
        }

        this.M = M;
        this.m = M / 2;
        reported = null;
        root = bulkLoad(M, bulk);
    }

    /**
     * Bulk-load the given list of items into the tree. Any existing elements in tree are cleared.
     *
     * @param n    The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @param bulk A list of items to be inserted.
     * @return The root of the new tree.
     */
    private RNode bulkLoad(int n, ArrayList<DrawnObject> bulk) {

        int r = bulk.size(); // Amount of rectangles

        if (r == 0) {
            return new RNode();
        }

        int P = (int) Math.ceil((double) r / n); // The amount of leaves needed
        int S = (int) Math.ceil(Math.sqrt(P)); // The approximate amount of slices needed (might be lower), and the amount of runs per slice

        ArrayList<ArrayList<DrawnObject>> runs = calculateRuns(n, S, bulk);

        ArrayList<RNode> leaves = new ArrayList<>();

        for (ArrayList<DrawnObject> run : runs) {
            RNode newNode = new RNode(run);
            leaves.add(newNode);
        }

        return buildBottomUp(n, leaves);
    }

    /**
     * Divide a given ArrayList into a series of runs.
     *
     * @param n    The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @param S    The approximate amount of slices, and the approximate amount of runs per slice.
     * @param bulk The ArrayList of items to be divided into runs.
     * @param <T>  The type of items - must implement BoundingBox.
     * @return An ArrayList of ArrayLists, each of which being a run.
     */
    public <T extends BoundingBox> ArrayList<ArrayList<T>> calculateRuns(int n, int S, ArrayList<T> bulk) {

        ArrayList<ArrayList<T>> runs = new ArrayList<>();

        ArrayList<T>[] slices = calculateSlices(n, S, bulk);

        for (ArrayList<T> slice : slices) { // for every vertical slice
            // sort on midY
            slice.sort(new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return Float.compare(o1.getMidY(), o2.getMidY());
                }
            });
            int runsInSlice = (int) Math.ceil((double) slice.size() / n);

            for (int i = 0; i < runsInSlice - 1; i++) { // for every run
                ArrayList<T> run = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    run.add(slice.get(i * n + j));
                }
                runs.add(run);
            }

            ArrayList<T> run = new ArrayList<>();
            int missing = slice.size() - ((runsInSlice - 1) * n);
            for (int i = 0; i < missing; i++) {
                int index = (runsInSlice - 1) * n + i;
                run.add(slice.get(index));
            }
            runs.add(run);
        }

        return runs;
    }

    /**
     * Divide a given ArrayList into a series of slices.
     *
     * @param n    The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @param S    The approximate amount of slices. Also the amount of runs per slice.
     * @param bulk The ArrayList of items to be divided into slices.
     * @param <T>  The type of items - must implement BoundingBox.
     * @return An array of ArrayLists, each of which being a slice.
     */
    public <T extends BoundingBox> ArrayList<T>[] calculateSlices(int n, int S, ArrayList<T> bulk) {

        // sort on midX
        bulk.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return Float.compare(o1.getMidX(), o2.getMidX());
            }
        });
        int r = bulk.size();
        int valuesPerSlice = S * n;
        int numberOfSlices = (int) Math.ceil((double) r / valuesPerSlice);
        ArrayList<T>[] slices = new ArrayList[numberOfSlices];

        // fill the first S-1 slices
        for (int i = 0; i < slices.length - 1; i++) {
            slices[i] = new ArrayList<>();
            for (int j = 0; j < valuesPerSlice; j++) {
                slices[i].add(bulk.get(i * valuesPerSlice + j));
            }
        }

        //fill the last slice
        slices[slices.length - 1] = new ArrayList<>();
        int missing = r - ((slices.length - 1) * valuesPerSlice);

        for (int j = 0; j < missing; j++) {
            int index = (slices.length - 1) * valuesPerSlice + j;
            slices[slices.length - 1].add(bulk.get(index));
        }

        if ((slices.length - 1) * valuesPerSlice + missing != r) {
            throw new RuntimeException("Not all rectangles are added to slices!");
        }

        return slices;
    }

    /**
     * Recursively construct the R-tree given an ArrayList of leaf-nodes.
     *
     * @param n     The max amount of values in a leaf node, and the max amount of children in an internal node. Must be greater than 1.
     * @param nodes The nodes of current level of the tree.
     * @return The root of the tree.
     */
    RNode buildBottomUp(int n, ArrayList<RNode> nodes) {
        int r = nodes.size();
        double P = Math.ceil((double) r / n);
        int S = (int) Math.ceil(Math.sqrt(P));

        ArrayList<ArrayList<RNode>> runs = calculateRuns(n, S, nodes);

        if (runs.size() == 1) {
            RNode newRoot = new RNode();
            newRoot.children = runs.get(0);
            return newRoot;
        }

        ArrayList<RNode> newNodes = new ArrayList<>();
        for (int i = 0; i < runs.size(); i++) {
            RNode node = new RNode();
            node.setChildren(runs.get(i));
            newNodes.add(node);
        }

        return buildBottomUp(n, newNodes);
    }

    /**
     * Insert a DrawnObject into the tree.
     * @param drawnObject The DrawnObject to be inserted.
     */
    public void insert(DrawnObject drawnObject) {
        //Find the best leaf
        Stack<RNode> stack = chooseLeaf(drawnObject);
        RNode c = stack.pop();

        //Insert the DrawnObject
        RNode[] splitNodes = insert(c, drawnObject); // should be null or contain two nodes from a split

        //If the DrawnObject was inserted at the root and the root was split
        if (c == root && splitNodes != null) {
            root = new RNode();
            insert(root, splitNodes[0]);
            insert(root, splitNodes[1]);
            return;
        }

        //Moving back up the tree, insert new nodes from splits and update MBRs
        while (stack.size() > 1) {
            RNode p = stack.pop(); // get internal node
            p.updateBoundary();
            if (splitNodes != null) { //if c was split into two nodes, remove c and add the resulting nodes
                p.removeChild(c);
                insert(p, splitNodes[0]); // we know there is room for the first new node, because one was just removed
                splitNodes = insert(p, splitNodes[1]); // we do not know if this will result in a split
            }

            c = p;
        }

        root.updateBoundary();

        //If there was a split in a child of the root
        if (splitNodes != null) {
            // c is the child of the root that was split
            root.removeChild(c); // remove the root that was split
            insert(root, splitNodes[0]);
            splitNodes = insert(root, splitNodes[1]);

            //If there was a split in the root
            if (splitNodes != null) {
                root = new RNode();
                insert(root, splitNodes[0]);
                insert(root, splitNodes[1]);
            }
        }
    }

    /**
     * Insert a DrawnObject into a leaf and split if necessary.
     *
     * @param leaf        The leaf the DrawnObject should be inserted into.
     * @param drawnObject The DrawnObject to be inserted.
     * @return an array with two leaf-nodes, if {@code leaf} was split. Otherwise, returns null.
     * @throws IllegalArgumentException if {@code leaf} is not a leaf-node.
     */
    private RNode[] insert(RNode leaf, DrawnObject drawnObject) {
        if (leaf.isLeaf()) {
            leaf.addValue(drawnObject);
            if (leaf.values.size() > M) {
                return splitLeaf(leaf);
            }
            return null;
        } else {
            throw new IllegalArgumentException("The given node is not a leaf!");
        }
    }

    /**
     * Insert an RNode as the child of another RNode and split if necessary.
     *
     * @param parent The parent RNode.
     * @param child  The RNode to be inserted as the child of {@code parent}.
     * @return an array with two internal nodes if {@code parent} was split. Otherwise, returns null.
     */
    private RNode[] insert(RNode parent, RNode child) {
        parent.addChild(child);
        if (parent.children.size() > M) {
            return splitInternal(parent);
        }
        return null;
    }

    /**
     * Finds the path to the best-suited leaf-node for the given DrawnObject.
     *
     * @param drawnObject The DrawnObject to be inserted.
     * @return A Stack of RNodes with the path from the leaf to the root.
     */
    private Stack<RNode> chooseLeaf(DrawnObject drawnObject) {
        RNode N = root;
        Stack<RNode> stack = new Stack<>();
        stack.push(N);

        while (!N.isLeaf()) {
            N = chooseSubtree(N, drawnObject);
            stack.push(N);
        }

        return stack;
    }

    /**
     * Choose the best subtree for a given drawnObject to be placed into.
     *
     * @param parent      The parent whose children are the roots of the subtrees.
     * @param drawnObject The DrawnObject to be inserted.
     * @return The root of the chosen subtree.
     */
    private RNode chooseSubtree(RNode parent, DrawnObject drawnObject) {
        double minEnlargement = Double.MAX_VALUE;
        RNode bestChild = null;

        for (RNode child : parent.children) {
            double enlargement = Rectangle.calculateEnlargement(child.minX, child.maxX, child.minY, child.maxY, drawnObject);
            if (enlargement < minEnlargement) {
                minEnlargement = enlargement;
                bestChild = child;
            }
        }

        if (bestChild == null)
            throw new RuntimeException("bestChild is null - children.size(): " + parent.children.size());

        return bestChild;
    }

    /**
     * Splits a leaf. Returns an array with two new RNodes that together store the previous node's values.
     *
     * @param leaf The leaf to be split.
     * @return An array containing two new leaves.
     */
    private RNode[] splitLeaf(RNode leaf) {
        ArrayList<DrawnObject> values = new ArrayList<>(leaf.values);

        DrawnObject[] seeds = pickSeedsLeaf(values);

        ArrayList<DrawnObject> group1 = new ArrayList<>();
        ArrayList<DrawnObject> group2 = new ArrayList<>();

        group1.add(seeds[0]);
        group2.add(seeds[1]);

        values.remove(seeds[0]);
        values.remove(seeds[1]);

        while (!values.isEmpty()) {
            if (m - group1.size() == values.size()) {
                group1.addAll(values);
                break;
            } else if (m - group2.size() == values.size()) {
                group2.addAll(values);
                break;
            } else {
                pickNextValue(group1, group2, values);
            }
        }

        RNode n1 = new RNode(group1);
        RNode n2 = new RNode(group2);

        return new RNode[]{n1, n2};
    }

    /**
     * Choose the two values, called seeds, in a leaf node who together create the worst MBR.
     * This is named QuadraticSplit in Guttman's text
     *
     * @param values The list of values from which the seeds will be picked.
     * @return An array containing two items from {@code values}.
     */
    private DrawnObject[] pickSeedsLeaf(ArrayList<DrawnObject> values) {
        DrawnObject n1 = values.get(0);
        DrawnObject n2 = values.get(1);
        double maxD = Rectangle.calculateUnionEnlargement(n1, n2);

        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = i + 1; j < values.size(); j++) {
                double d = Rectangle.calculateUnionEnlargement(values.get(i), values.get(j));

                if (d > maxD) {
                    n1 = values.get(i);
                    n2 = values.get(j);
                    maxD = d;
                }
            }
        }

        return new DrawnObject[]{n1, n2};
    }

    /**
     * Splits an internal node. Returns an array with two new RNodes that together store the previous node's children.
     *
     * @param internal The internal node to be split.
     * @return An array containing two new internal nodes.
     */
    private RNode[] splitInternal(RNode internal) {
        ArrayList<RNode> children = new ArrayList<>(internal.children);

        RNode[] seeds = pickSeedsInternal(children);

        ArrayList<RNode> group1 = new ArrayList<>();
        ArrayList<RNode> group2 = new ArrayList<>();

        group1.add(seeds[0]);
        group2.add(seeds[1]);

        children.remove(seeds[0]);
        children.remove(seeds[1]);

        while (!children.isEmpty()) {
            if (m - group1.size() == children.size()) {
                group1.addAll(children);
                break;
            } else if (m - group2.size() == children.size()) {
                group2.addAll(children);
                break;
            } else {
                pickNextChild(group1, group2, children);
            }
        }

        RNode n1 = new RNode();
        RNode n2 = new RNode();

        n1.setChildren(group1);
        n2.setChildren(group2);

        return new RNode[]{n1, n2};
    }

    /**
     * Choose the two child-nodes, called seeds, in an internal node who together create the worst MBR.
     *
     * @param children The list of nodes from which the seeds will be picked.
     * @return An array containing two items from {@code children}.
     */
    private RNode[] pickSeedsInternal(ArrayList<RNode> children) {
        // redundancy - checks the pair 0 1 twice
        RNode n1 = children.get(0);
        RNode n2 = children.get(1);
        double maxD = Rectangle.calculateDeadSpace(n1.minX, n1.maxX, n1.minY, n1.maxY, n2.minX, n2.maxX, n2.minY, n2.maxY); //Rectangle.createRectangle(n1.boundary, n2.boundary).area() - n1.boundary.area() - n2.boundary.area();

        RNode c1 = null;
        RNode c2 = null;

        for (int i = 0; i < children.size() - 1; i++) {
            c1 = children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                c2 = children.get(j);
                //Rectangle newRect = Rectangle.createRectangle(list.get(i).boundary, list.get(j).boundary);
                double d = Rectangle.calculateDeadSpace(c1.minX, c1.maxX, c1.minY, c1.maxY, c2.minX, c2.maxX, c2.minY, c2.maxY); //newRect.area() - list.get(i).boundary.area() - list.get(j).boundary.area();

                if (d > maxD) {
                    n1 = children.get(i);
                    n2 = children.get(j);
                    maxD = d;
                }
            }
        }

        return new RNode[]{n1, n2};
    }

    /**
     * Given three ArrayLists of RNodes, pick one node from the third list and place into one of the first two.
     *
     * @param group1     The first group of nodes.
     * @param group2     The second group of nodes.
     * @param unassigned The list of yet unassigned nodes to be placed in one of the prior groups.
     */
    private void pickNextChild(ArrayList<RNode> group1, ArrayList<RNode> group2, ArrayList<RNode> unassigned) {
        Rectangle r1 = Rectangle.createRectangleFromRNodes(group1);
        Rectangle r2 = Rectangle.createRectangleFromRNodes(group2);

        RNode greatestPreferenceRNode = null;
        double maxDiff = Double.NEGATIVE_INFINITY; // otherwise always non-negative
        double maxDiffD1 = 0;
        double maxDiffD2 = 0;

        for (RNode rNode : unassigned) {
            //these should be non-negative - the area can only grow or remain the same
            //Rectangle expanded1 = Rectangle.createRectangle(r1, rNode.boundary);
            //Rectangle expanded2 = Rectangle.createRectangle(r2, rNode.boundary);
            double d1 = Rectangle.calculateEnlargement(r1.minX, r1.maxX, r1.minY, r1.maxY, rNode.minX, rNode.maxX, rNode.minY, rNode.maxY); //calculateEnlargement(r1, expanded1);
            double d2 = Rectangle.calculateEnlargement(r2.minX, r2.maxX, r2.minY, r2.maxY, rNode.minX, rNode.maxX, rNode.minY, rNode.maxY); //calculateEnlargement(r2, expanded2);

            double diff = Math.abs(d1 - d2);

            if (diff > maxDiff) {
                maxDiff = diff;
                greatestPreferenceRNode = rNode;
                maxDiffD1 = d1;
                maxDiffD2 = d2;
            }
        }

        if (maxDiffD1 < maxDiffD2) { // if one rectangle would have to be enlarged less than the other
            group1.add(greatestPreferenceRNode);
        } else if (maxDiffD1 > maxDiffD2) {
            group2.add(greatestPreferenceRNode);
        } else if (r1.area() < r2.area()) { // otherwise, pick the one with the smallest area
            group1.add(greatestPreferenceRNode);
        } else if (r1.area() > r2.area()) {
            group2.add(greatestPreferenceRNode);
        } else if (group1.size() < group2.size()) { // otherwise, pick the one with the fewest elements
            group1.add(greatestPreferenceRNode);
        } else if (group1.size() > group2.size()) {
            group2.add(greatestPreferenceRNode);
        } else { // else, pick at random
            double ran = Math.random();
            if (ran < 0.5d) {
                group1.add(greatestPreferenceRNode);
            } else {
                group2.add(greatestPreferenceRNode);
            }
        }

        unassigned.remove(greatestPreferenceRNode);
    }

    /**
     * Given three ArrayLists of DrawnObjects, pick one node from the third list and place into one of the first two.
     *
     * @param group1     The first group of DrawnObjects.
     * @param group2     The second group of DrawnObjects.
     * @param unassigned The list of yet unassigned DrawnObjects to be placed in one of the prior groups.
     */
    private void pickNextValue(ArrayList<DrawnObject> group1, ArrayList<DrawnObject> group2, ArrayList<DrawnObject> unassigned) {
        // The algorithm as described in PickNext - not in Quadratic Split

        Rectangle r1 = Rectangle.createRectangleFromDrawnObjects(group1);
        Rectangle r2 = Rectangle.createRectangleFromDrawnObjects(group2);

        DrawnObject greatestPreferenceObject = null;
        double maxDiff = Double.NEGATIVE_INFINITY; // otherwise always non-negative
        double maxDiffD1 = 0;
        double maxDiffD2 = 0;

        for (DrawnObject obj : unassigned) {
            //these should be non-negative - the area can only grow or remain the same
            Rectangle expanded1 = Rectangle.createRectangle(r1, obj);
            Rectangle expanded2 = Rectangle.createRectangle(r2, obj);
            double d1 = Rectangle.calculateEnlargement(r1, expanded1);
            double d2 = Rectangle.calculateEnlargement(r2, expanded2);

            double diff = Math.abs(d1 - d2);

            if (diff > maxDiff) {
                maxDiff = diff;
                greatestPreferenceObject = obj;
                maxDiffD1 = d1;
                maxDiffD2 = d2;
            }
        }

        if (maxDiffD1 < maxDiffD2) { // if one rectangle would have to be enlarged less than the other
            group1.add(greatestPreferenceObject);
        } else if (maxDiffD1 > maxDiffD2) {
            group2.add(greatestPreferenceObject);
        } else if (r1.area() < r2.area()) { // otherwise, pick the one with the smallest area
            group1.add(greatestPreferenceObject);
        } else if (r1.area() > r2.area()) {
            group2.add(greatestPreferenceObject);
        } else if (group1.size() < group2.size()) { // otherwise, pick the one with the fewest elements
            group1.add(greatestPreferenceObject);
        } else if (group1.size() > group2.size()) {
            group2.add(greatestPreferenceObject);
        } else { // else, pick at random
            double ran = Math.random();
            if (ran < 0.5d) {
                group1.add(greatestPreferenceObject);
            } else {
                group2.add(greatestPreferenceObject);
            }
        }

        unassigned.remove(greatestPreferenceObject);
    }

    /**
     * Query the R-tree.
     *
     * @param minX The left bound of the query rectangle.
     * @param maxX The right bound of the query rectangle.
     * @param minY The lower bound of the query rectangle.
     * @param maxY The upper bound of the query rectangle.
     * @return An ArrayList of DrawnObjects inside the query rectangle.
     */
    public ArrayList<DrawnObject> query(float minX, float maxX, float minY, float maxY) {
        rectanglesExamined = 0;
        Rectangle queryRect = new Rectangle(minX, maxX, minY, maxY);

        reported = new ArrayList<>();
        search(root, queryRect);

        return reported;
    }

    /**
     * The recursive Search algorithm described in Guttman's text.
     *
     * @param node           The root of the subtree being searched.
     * @param queryRectangle The query rectangle.
     */
    private void search(RNode node, Rectangle queryRectangle) {
        if (node.isLeaf()) {
            for (DrawnObject drawnObject : node.values) {
                rectanglesExamined++;
                if (queryRectangle.intersects(drawnObject)) {
                    reported.add(drawnObject);
                }
            }
        } else {
            for (RNode E : node.children) {
                rectanglesExamined++;
                if (queryRectangle.intersects(E.minX, E.maxX, E.minY, E.maxY)) { //queryRectangle.intersects(E.boundary)) {
                    search(E, queryRectangle);
                }
            }
        }
    }
}
