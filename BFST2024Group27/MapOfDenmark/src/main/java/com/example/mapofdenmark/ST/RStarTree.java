package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.DrawnObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;

/**
 * WARNING: Unfinished implementation
 */
public class RStarTree implements Serializable {
    public RNode root;
    final int M; // max number of entries per node
    final int m; // min number of entries per node
    final int p;
    int leafLevel;

    ArrayList<DrawnObject> reported;
    HashSet<Integer> hasBeenReinserted;

    /**
     * Creates an R*-tree as described in Beckmann's text 'The R*-tree: ...'. The items in {@code bulk} are then immediately, iteratively inserted.
     * @param M The max number of values or children in a node.
     * @param p The amount of nodes or values to be reinserted on overflow.
     */
    public RStarTree(int M, int p) {
        if (M < 2) {
            throw new IllegalArgumentException("The value M must be greater or equal to 2!");
        }
        leafLevel = 0;
        this.M = M;
        //this.m = M / 2;
        this.m = (int) 1;
        this.p = p;
        root = new RNode();
        reported = null;
        hasBeenReinserted = new HashSet<>();
    }

    /**
     * Creates an R*-tree as described in Beckmann's text 'The R*-tree: ...'. The items in {@code bulk} are then immediately, iteratively inserted.
     * @param M The max number of values or children in a node.
     * @param p The amount of nodes or values to be reinserted on overflow.
     * @param bulk The items to be immediately inserted.
     */
    public RStarTree(int M, int p, ArrayList<DrawnObject> bulk) {
        if (M < 2) {
            throw new IllegalArgumentException("The value M must be greater or equal to 2!");
        }
        leafLevel = 0;
        this.M = M;
        //this.m = M / 2;
        this.m = (int) 1;
        this.p = p;
        root = new RNode();
        reported = null;
        hasBeenReinserted = new HashSet<>();

        for (DrawnObject d : bulk) {
            insertData(d);
        }
    }

    /**
     * Insert an item in the R*-tree.
     * @param drawnObject The item to be inserted.
     */
    public void insertData(DrawnObject drawnObject) {
        insert(drawnObject);
        hasBeenReinserted.clear();
    }

    /**
     * Insert a given DrawnObject in the tree
     * @param drawnObject
     */
    private void insert(DrawnObject drawnObject) {
        Stack<RNode> stack = chooseLeaf(drawnObject);
        RNode c = stack.pop();

        RNode[] splitNodes = insert(c, drawnObject); // should be null or contain two nodes from a split
        if (c == root && splitNodes != null) { //if data was added to root and root was split, create new root
            root = new RNode();

            for (Integer i : hasBeenReinserted) {
                hasBeenReinserted.remove(i);
                hasBeenReinserted.add(i + 1);
            }
            leafLevel++;

            insert(root, splitNodes[0], 0);
            insert(root, splitNodes[1], 0);
            return;
        }

        while (stack.size() > 1) {
            RNode p = stack.pop(); // get internal node

            if (splitNodes != null) { //if c was split into two nodes, remove c and add the resulting nodes
                p.removeChild(c);
                insert(p, splitNodes[0], stack.size() - 1); // we know there is room for the first new node, because one was just removed
                splitNodes = insert(p, splitNodes[1], stack.size() - 1); // we do not know if this will result in a split
            }

            c = p;
        }

        if (splitNodes != null) { // if the root was split due to the split of a child
            // c is the child of the root that was split
            root.removeChild(c); // remove the root that was split
            insert(root, splitNodes[0], 0);
            splitNodes = insert(root, splitNodes[1], 0);

            if (splitNodes != null) {
                root = new RNode();
                for (Integer i : hasBeenReinserted) {
                    hasBeenReinserted.remove(i);
                    hasBeenReinserted.add(i + 1);
                }
                leafLevel++;
                insert(root, splitNodes[0], 0);
                insert(root, splitNodes[1], 0);
            }
        }
    }

    /**
     * Insert RNode at a given level in the tree
     * @param node the RNode to be inserted
     * @param level the level of the RNode
     */
    private void insert(RNode node, int level) {
        Stack<RNode> stack = chooseInternal(node, level);
        RNode c = stack.pop();

        RNode[] splitNodes = insert(c, node, level); // should be null or contain two nodes from a split
        if (c == root && splitNodes != null) {
            root = new RNode();
            for (Integer i : hasBeenReinserted) {
                hasBeenReinserted.remove(i);
                hasBeenReinserted.add(i + 1);
            }
            leafLevel++;
            insert(root, splitNodes[0], 0);
            insert(root, splitNodes[1], 0);
            return;
        }

        while (stack.size() > 1) {
            RNode p = stack.pop(); // get internal node

            if (splitNodes != null) { //if c was split into two nodes, remove c and add the resulting nodes
                p.removeChild(c);
                insert(p, splitNodes[0], stack.size() - 1); // we know there is room for the first new node, because one was just removed
                splitNodes = insert(p, splitNodes[1], stack.size() - 1); // we do not know if this will result in a split
            }

            c = p;
        }

        if (splitNodes != null) {
            // c is the child of the root that was split
            root.removeChild(c); // remove the root that was split
            insert(root, splitNodes[0], 0);
            splitNodes = insert(root, splitNodes[1], 0);

            if (splitNodes != null) {
                root = new RNode();
                incrementLevels();
                insert(root, splitNodes[0], 0);
                insert(root, splitNodes[1], 0);
            }
        }
    }

    private void incrementLevels(){
        for (Integer i : hasBeenReinserted) {
            hasBeenReinserted.remove(i);
            hasBeenReinserted.add(i + 1);
        }
        leafLevel++;
    }

    /**
     * Insert a DrawnObject in a given leaf
     * @param leaf
     * @param drawnObject
     * @return Either an empty array or one containing two new RNodes from a split
     */
    private RNode[] insert(RNode leaf, DrawnObject drawnObject) {
        if (leaf.isLeaf()) {
            leaf.addValue(drawnObject);
            if (leaf.values.size() > M) {
                return overflowTreatment(leaf, leafLevel);
            }
            return null;
        } else {
            throw new IllegalArgumentException("The given node is not a leaf!");
        }
    }

    private RNode[] insert(RNode parent, RNode child, int level) {
        parent.addChild(child);
        if (parent.children.size() > M) {
            return overflowTreatment(parent, level);
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

    private Stack<RNode> chooseInternal(RNode node, int level){
        RNode N = root;
        Stack<RNode> stack = new Stack<>();
        stack.push(N);

        for(int i = 0; i < level; i++){
            N = chooseSubtree(N, node);
            stack.push(N);
        }

        return stack;
    }

    private RNode chooseSubtree(RNode parent, BoundingBox rectangle) {
        boolean pointsToLeaves = parent.children.get(0).isLeaf();
        RNode bestNode = null;

        if (pointsToLeaves) {
            double minOverlapEnlargement = Double.POSITIVE_INFINITY;

            for (RNode leaf : parent.children) {
                double overlapEnlargement = 0;
                for (DrawnObject d : leaf.values) {
                    overlapEnlargement += Rectangle.calculateOverlap(rectangle.getMinX(), rectangle.getMaxX(),
                            rectangle.getMinY(), rectangle.getMaxY(),
                            d.getMinX(), d.getMaxX(), d.getMinY(), d.getMaxY());
                }

                if (overlapEnlargement < minOverlapEnlargement) {
                    minOverlapEnlargement = overlapEnlargement;
                    bestNode = leaf;
                } else if (overlapEnlargement == minOverlapEnlargement) {
                    //resolve tie
                    double enlargement1 = Rectangle.calculateEnlargement(bestNode.minX, bestNode.maxX, bestNode.minY, bestNode.maxY, rectangle);
                    double enlargement2 = Rectangle.calculateEnlargement(leaf.minX, leaf.maxX, leaf.minY, leaf.maxY, rectangle);

                    if (enlargement2 < enlargement1) {
                        bestNode = leaf;
                    } else {
                        continue;
                    }
                }
            }
        } else {
            double minAreaEnlargement = Double.POSITIVE_INFINITY;
            for (RNode child : parent.children) {
                double areaEnlargement = Rectangle.calculateEnlargement(child.minX, child.maxX, child.minY, child.maxY, rectangle);
                if (areaEnlargement < minAreaEnlargement) {
                    minAreaEnlargement = areaEnlargement;
                    bestNode = child;
                }
            }
        }
        return bestNode;
    }

    private RNode[] overflowTreatment(RNode node, int level) {
        if (level != 0 && !hasBeenReinserted.contains(level)) {
            hasBeenReinserted.add(level);
            reinsert(node, level);
            return null;
        } else if (level == leafLevel) {
            return splitLeaf(node);
        } else {
            return splitInternal(node);
        }
    }

    /**
     * Reinsert a number ({@code p}) of values or children into the R*-tree.
     * @param node The parent/container of the children/values to be reinserted.
     * @param level The level at which the children or values should be reinserted at.
     */
    private void reinsert(RNode node, int level) {
        if (node.isLeaf()) {
            //sort in descending order
            node.values.sort(new Comparator<DrawnObject>() {
                @Override
                public int compare(DrawnObject o1, DrawnObject o2) {
                    float deltaX1 = Math.abs(node.getMidX() - o1.getMidX());
                    float deltaY1 = Math.abs(node.getMidY() - o1.getMidY());
                    float deltaX2 = Math.abs(node.getMidX() - o2.getMidX());
                    float deltaY2 = Math.abs(node.getMidY() - o2.getMidY());

                    float dist1 = deltaX1 * deltaX1 + deltaY1 * deltaY1;
                    float dist2 = deltaX2 * deltaX2 + deltaY2 * deltaY2;
                    return Float.compare(dist2, dist1);
                }
            });

            DrawnObject[] reinsertedValues = new DrawnObject[p];
            for (int i = 0; i < p; i++) {
                reinsertedValues[i] = node.values.get(0);
                node.values.remove(0);
            }
            for (DrawnObject d : reinsertedValues) {
                insert(d);
            }
        } else {
            node.children.sort(new Comparator<RNode>() {
                @Override
                public int compare(RNode o1, RNode o2) {
                    float deltaX1 = Math.abs(node.getMidX() - o1.getMidX());
                    float deltaY1 = Math.abs(node.getMidY() - o1.getMidY());
                    float deltaX2 = Math.abs(node.getMidX() - o2.getMidX());
                    float deltaY2 = Math.abs(node.getMidY() - o2.getMidY());

                    float dist1 = deltaX1 * deltaX1 + deltaY1 * deltaY1;
                    float dist2 = deltaX2 * deltaX2 + deltaY2 * deltaY2;
                    return Float.compare(dist2, dist1);
                }
            });
            RNode[] reinsertedNodes = new RNode[p];
            for (int i = 0; i < p; i++) {
                reinsertedNodes[i] = node.children.get(0);
                node.removeChild(node.children.get(0));
            }
            for (RNode n : reinsertedNodes) {
                insert(n, level);
            }
        }
    }

    /**
     * Splits a leaf. Returns an array with two new RNodes that together store the previous node's values
     *
     * @param leaf
     * @return
     */
    private RNode[] splitLeaf(RNode leaf) {
        int axis = chooseAxis(leaf.values);
        int split = chooseSplitIndex(leaf.values, axis);


        ArrayList<DrawnObject> list1 = new ArrayList<>();
        ArrayList<DrawnObject> list2 = new ArrayList<>();

        for (int i = 0; i <= split; i++) {
            list1.add(leaf.values.get(i));
        }
        for (int i = split + 1; i < leaf.values.size(); i++) {
            list2.add(leaf.values.get(i));
        }

        RNode n1 = new RNode(list1);
        RNode n2 = new RNode(list2);

        return new RNode[]{n1, n2};
    }

    /**
     * Splits an internal node. Returns an array with two new RNodes that together store the previous node's children
     *
     * @param internal
     * @return
     */
    private RNode[] splitInternal(RNode internal) {
        int axis = chooseAxis(internal.children);
        int split = chooseSplitIndex(internal.children, axis);

        ArrayList<RNode> list1 = new ArrayList<>();
        ArrayList<RNode> list2 = new ArrayList<>();

        for (int i = 0; i <= split; i++) {
            list1.add(internal.children.get(i));
        }
        for (int i = split + 1; i < internal.children.size(); i++) {
            list2.add(internal.children.get(i));
        }

        RNode n1 = new RNode();
        RNode n2 = new RNode();

        n1.setChildren(list1);
        n2.setChildren(list2);

        return new RNode[]{n1, n2};
    }

    //return 0 for minX, 1 for maxX, 2 for minY, and 3 for maxY
    private <T extends BoundingBox> int chooseAxis(ArrayList<T> rectangles) {
        int bestAxis = -1;
        double minMarginSum = Double.POSITIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            sortRectangles(rectangles, i);
            double marginSum = calculateDistributions(rectangles);

            if (marginSum < minMarginSum) {
                minMarginSum = marginSum;
                bestAxis = i;
            }
        }

        return bestAxis;
    }

    private <T extends BoundingBox> void sortRectangles(ArrayList<T> rectangles, int depth) {
        switch (depth) {
            case 0:
                rectangles.sort(new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return Float.compare(o1.getMinX(), o2.getMinX());
                    }
                });
                break;
            case 1:
                rectangles.sort(new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return Float.compare(o1.getMaxX(), o2.getMaxX());
                    }
                });
                break;
            case 2:
                rectangles.sort(new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return Float.compare(o1.getMinY(), o2.getMinY());
                    }
                });
                break;
            case 3:
                rectangles.sort(new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return Float.compare(o1.getMaxY(), o2.getMaxY());
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("No such depth!");
        }
    }

    private <T extends BoundingBox> int chooseSplitIndex(ArrayList<T> rectangles, int axis) {
        sortRectangles(rectangles, axis);

        int bestSplit = -1;
        float minOverlap = Float.POSITIVE_INFINITY;

        for (int i = 1; i <= M - (2 * m) + 2; i++) {
            float overlap = calculateOverlap(rectangles, i);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                bestSplit = i;
            }
        }

        return bestSplit;
    }

    private <T extends BoundingBox> float calculateOverlap(ArrayList<T> rectangles, int split) {
        float minX1 = Float.POSITIVE_INFINITY;
        float maxX1 = Float.NEGATIVE_INFINITY;
        float minY1 = Float.POSITIVE_INFINITY;
        float maxY1 = Float.NEGATIVE_INFINITY;

        float minX2 = Float.POSITIVE_INFINITY;
        float maxX2 = Float.NEGATIVE_INFINITY;
        float minY2 = Float.POSITIVE_INFINITY;
        float maxY2 = Float.NEGATIVE_INFINITY;

        for (int i = 0; i <= split; i++) {
            minX1 = Math.min(minX1, rectangles.get(i).getMinX());
            maxX1 = Math.max(maxX1, rectangles.get(i).getMaxX());
            minY1 = Math.min(minY1, rectangles.get(i).getMinY());
            maxY1 = Math.max(maxY1, rectangles.get(i).getMaxY());
        }

        for (int i = split + 1; i < rectangles.size(); i++) {
            minX2 = Math.min(minX2, rectangles.get(i).getMinX());
            maxX2 = Math.max(maxX2, rectangles.get(i).getMaxX());
            minY2 = Math.min(minY2, rectangles.get(i).getMinY());
            maxY2 = Math.max(maxY2, rectangles.get(i).getMaxY());
        }

        return Rectangle.calculateOverlap(minX1, maxX1, minY1, maxY1, minX2, maxX2, minY2, maxY2);
    }

    private <T extends BoundingBox> double calculateDistributions(ArrayList<T> rectangles) {
        double sum = 0;
        int x = 0;
        for (int k = 1; k <= M - (2 * m) + 2; k++) {
            x++;
            sum += getMargin(rectangles, 0, (m - 1) + k); // left - the first (m-1)+k entries
            sum += getMargin(rectangles, (m - 1) + k, rectangles.size()); // right - the remaining entries
        }

        return sum;
    }

    /**
     * @param rectangles
     * @param lo         the lowest index (inclusive)
     * @param hi         the highest index (exclusive)
     * @param <T>
     * @return
     */
    private <T extends BoundingBox> double getMargin(ArrayList<T> rectangles, int lo, int hi) {
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (int i = lo; i < hi; i++) {
            minX = Math.min(minX, rectangles.get(i).getMinX());
            maxX = Math.max(maxX, rectangles.get(i).getMaxX());
            minY = Math.min(minY, rectangles.get(i).getMinY());
            maxY = Math.max(maxY, rectangles.get(i).getMaxY());
        }

        return 2 * (maxX - minX + maxY - minY); // the margin of the bounding box
    }

    public ArrayList<DrawnObject> query(float minX, float maxX, float minY, float maxY) {
        Rectangle queryRect = new Rectangle(minX, maxX, minY, maxY);

        reported = new ArrayList<>();
        search(root, queryRect);

        return reported;
    }

    private void search(RNode node, Rectangle queryRectangle) {
        if (node.isLeaf()) {
            for (DrawnObject drawnObject : node.values) {
                if (queryRectangle.intersects(drawnObject)) {
                    reported.add(drawnObject);
                }
            }
        } else {
            for (RNode E : node.children) {
                if (queryRectangle.intersects(E.minX, E.maxX, E.minY, E.maxY)) { //queryRectangle.intersects(E.boundary)) {
                    search(E, queryRectangle);
                }
            }
        }
    }
}
