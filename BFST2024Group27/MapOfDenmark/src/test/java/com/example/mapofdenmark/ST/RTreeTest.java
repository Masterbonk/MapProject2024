package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.DrawnObject;
import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {

    private Way[] getWays16() {
        Way[] ways = new Way[16];

        float[] lats = new float[]{3, 10, 12, 7, 9, 2, 14, 11, 8, 15, 1, 4, 0, 5, 6, 13};

        for (int i = 0; i < ways.length; i++) {
            ArrayList<Node> list = new ArrayList<>();
            list.add(new Node(lats[i], i));
            list.add(new Node(lats[i] + 1, i + 1));
            ways[i] = new Line(list,(byte)0);
        }

        return ways;
    }

    @Test
    void testRNodeIsLeaf() {
        RNode n1 = new RNode();

        assertTrue(n1.isLeaf());

        RNode n2 = new RNode();
        RNode n3 = new RNode();
        RNode n4 = new RNode();
        RNode n5 = new RNode();
        n1.children.add(n2);
        n1.children.add(n3);
        n2.children.add(n4);
        n3.children.add(n5);

        assertFalse(n1.isLeaf());
    }

    @Test
    void testInsertDepth1Tree() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();

        w0Nodes.add(new Node(5, 5));
        w1Nodes.add(new Node(10, 10));
        w2Nodes.add(new Node(10, 10));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);

        assertTrue(tree.getRoot().isLeaf());
        assertEquals(w0, tree.getRoot().values.get(0));
        assertEquals(w1, tree.getRoot().values.get(1));
        assertEquals(w2, tree.getRoot().values.get(2));
    }

    @Test
    void testInsertDepth2TreeInOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 1));

        w1Nodes.add(new Node(0, 2));
        w1Nodes.add(new Node(1, 3));

        w2Nodes.add(new Node(0, 4));
        w2Nodes.add(new Node(1, 5));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 11));

        w4Nodes.add(new Node(0, 12));
        w4Nodes.add(new Node(1, 13));

        w5Nodes.add(new Node(0, 14));
        w5Nodes.add(new Node(1, 15));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);

        assertTrue(tree.getRoot().isLeaf());
        assertSame(tree.getRoot().values.get(0), w0);
        assertSame(tree.getRoot().values.get(1), w1);
        assertSame(tree.getRoot().values.get(2), w2);

        tree.insert(w3);

        // check root
        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());

        //Check leaves
        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);
        assertTrue(left.isLeaf()); // check left and right are leaves
        assertTrue(right.isLeaf());

        assertEquals(3, left.values.size());
        assertEquals(1, right.values.size());

        //check values of leaves
        assertEquals(w0, left.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w1, left.values.get(1));
        assertEquals(w2, left.values.get(2));
        assertEquals(w3, right.values.get(0));

        // insert last two nodes
        tree.insert(w4);
        tree.insert(w5);

        // check root
        // assertNull(tree.root.boundary);
        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());

        assertTrue(left.isLeaf()); // check left and right are leaves
        assertTrue(right.isLeaf());

        //check values of leaves
        assertEquals(w0, left.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w1, left.values.get(1));
        assertEquals(w2, left.values.get(2));
        assertEquals(w3, right.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w4, right.values.get(1));
        assertEquals(w5, right.values.get(2));
    }

    @Test
    void testInsertDepth2TreeOutOfOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 1));

        w1Nodes.add(new Node(0, 2));
        w1Nodes.add(new Node(1, 3));

        w2Nodes.add(new Node(0, 4));
        w2Nodes.add(new Node(1, 5));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 11));

        w4Nodes.add(new Node(0, 12));
        w4Nodes.add(new Node(1, 13));

        w5Nodes.add(new Node(0, 14));
        w5Nodes.add(new Node(1, 15));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w5);
        tree.insert(w1);

        assertTrue(tree.getRoot().isLeaf());
        assertSame(tree.getRoot().values.get(0), w0);
        assertSame(tree.getRoot().values.get(1), w5);
        assertSame(tree.getRoot().values.get(2), w1);

        tree.insert(w4);

        // check root
        // assertNull(tree.root.boundary);
        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());

        //Check leaves
        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);
        assertTrue(left.isLeaf()); // check left and right are leaves
        assertTrue(right.isLeaf());

        //check values of leaves
        assertEquals(w0, left.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w1, left.values.get(1)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w5, right.values.get(0));
        assertEquals(w4, right.values.get(1));

        // insert last two nodes
        tree.insert(w2);
        tree.insert(w3);

        // check root
        // assertNull(tree.root.boundary);
        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());

        assertTrue(left.isLeaf()); // check left and right are leaves
        assertTrue(right.isLeaf());

        //check values of leaves
        assertEquals(w0, left.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w1, left.values.get(1));
        assertEquals(w2, left.values.get(2));
        assertEquals(w5, right.values.get(0)); // check the first leaves in each subtree are the ways furthest apart
        assertEquals(w4, right.values.get(1));
        assertEquals(w3, right.values.get(2));
    }

    @Test
    void testCalculateEnlargement() {
        RTree tree = new RTree(3);

        Rectangle r1 = new Rectangle(0, 1, 0, 1);
        Rectangle r2 = new Rectangle(0, 1, 0, 1);

        double result = Rectangle.calculateEnlargement(r1, r2);

        assertEquals(0, result);

        r1 = new Rectangle(0, 1, 0, 1);
        r2 = new Rectangle(0, 3, 0, 3);

        result = Rectangle.calculateEnlargement(r1, r2);

        assertEquals(1, r1.area());
        assertEquals(9, r2.area());
        assertEquals(8, result);
    }

    @Test
    void testQueryDepth1Tree() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        w0Nodes.add(new Node(5, 5));
        Way w0 = new Line(w0Nodes,(byte)0);

        ArrayList<Node> w1Nodes = new ArrayList<>();
        w1Nodes.add(new Node(10, 10));
        Way w1 = new Line(w1Nodes,(byte)0);

        ArrayList<Node> w2Nodes = new ArrayList<>();
        w2Nodes.add(new Node(15, 15));
        Way w2 = new Line(w2Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);

        ArrayList<DrawnObject> result = tree.query(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        assertEquals(3, result.size());
        assertTrue(result.contains(w0));
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w2));

        result = tree.query(9.9f * 0.56f, 15f * 0.56f, -15, -10);
        assertEquals(2, result.size());
        assertFalse(result.contains(w0));
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w2));

        result = tree.query(10 * 0.56f, 15f * 0.56f, 0, 10);
        assertEquals(0, result.size());
        assertFalse(result.contains(w0));
        assertFalse(result.contains(w1));
        assertFalse(result.contains(w2));
    }

    @Test
    void testQueryDepth2Tree() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 1));

        w1Nodes.add(new Node(0, 2));
        w1Nodes.add(new Node(1, 3));

        w2Nodes.add(new Node(0, 4));
        w2Nodes.add(new Node(1, 5));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 11));

        w4Nodes.add(new Node(0, 12));
        w4Nodes.add(new Node(1, 13));

        w5Nodes.add(new Node(0, 14));
        w5Nodes.add(new Node(1, 15));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);
        tree.insert(w3);
        tree.insert(w4);
        tree.insert(w5);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());
        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);
        assertEquals(3, left.values.size());
        assertEquals(3, right.values.size());

        // check root
        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());
        assertTrue(tree.getRoot().values.isEmpty());

        //check leaves
        assertTrue(left.isLeaf());
        assertTrue(right.isLeaf());

        assertTrue(left.values.contains(w0));
        assertTrue(left.values.contains(w1));
        assertTrue(left.values.contains(w2));
        assertTrue(right.values.contains(w3));
        assertTrue(right.values.contains(w4));
        assertTrue(right.values.contains(w5));

        ArrayList<DrawnObject> result = tree.query(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        assertEquals(6, result.size());

        result = tree.query(4.5f * 0.56f, 10.5f * 0.56f, -10, 10);
        assertEquals(2, result.size());
    }

    @Test
    void testInsert3GroupsInOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();
        ArrayList<Node> w6Nodes = new ArrayList<>();
        ArrayList<Node> w7Nodes = new ArrayList<>();
        ArrayList<Node> w8Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 0));
        w1Nodes.add(new Node(0, 1));
        w1Nodes.add(new Node(1, 1));
        w2Nodes.add(new Node(0, 2));
        w2Nodes.add(new Node(1, 2));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 10));
        w4Nodes.add(new Node(0, 11));
        w4Nodes.add(new Node(1, 11));
        w5Nodes.add(new Node(0, 12));
        w5Nodes.add(new Node(1, 12));

        w6Nodes.add(new Node(0, 20));
        w6Nodes.add(new Node(1, 20));
        w7Nodes.add(new Node(0, 21));
        w7Nodes.add(new Node(1, 21));
        w8Nodes.add(new Node(0, 22));
        w8Nodes.add(new Node(1, 22));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);
        Way w6 = new Line(w6Nodes,(byte)0);
        Way w7 = new Line(w7Nodes,(byte)0);
        Way w8 = new Line(w8Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);
        tree.insert(w3);
        tree.insert(w4);
        tree.insert(w5);
        tree.insert(w6);
        tree.insert(w7);
        tree.insert(w8);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(3, tree.getRoot().children.size());

        RNode left = tree.getRoot().children.get(0);
        RNode mid = tree.getRoot().children.get(1);
        RNode right = tree.getRoot().children.get(2);

        assertTrue(left.isLeaf());
        assertTrue(mid.isLeaf());
        assertTrue(right.isLeaf());
        assertEquals(3, left.values.size());
        assertEquals(3, mid.values.size());
        assertEquals(3, right.values.size());

        assertEquals(w0, left.values.get(0));
        assertEquals(w1, left.values.get(1));
        assertEquals(w2, left.values.get(2));

        assertEquals(w3, mid.values.get(0));
        assertEquals(w4, mid.values.get(1));
        assertEquals(w5, mid.values.get(2));

        assertEquals(w6, right.values.get(0));
        assertEquals(w7, right.values.get(1));
        assertEquals(w8, right.values.get(2));
    }

    @Test
    void testInsert3GroupsOutOfOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();
        ArrayList<Node> w6Nodes = new ArrayList<>();
        ArrayList<Node> w7Nodes = new ArrayList<>();
        ArrayList<Node> w8Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 0));
        w1Nodes.add(new Node(0, 1));
        w1Nodes.add(new Node(1, 1));
        w2Nodes.add(new Node(0, 2));
        w2Nodes.add(new Node(1, 2));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 10));
        w4Nodes.add(new Node(0, 11));
        w4Nodes.add(new Node(1, 11));
        w5Nodes.add(new Node(0, 12));
        w5Nodes.add(new Node(1, 12));

        w6Nodes.add(new Node(0, 20));
        w6Nodes.add(new Node(1, 20));
        w7Nodes.add(new Node(0, 21));
        w7Nodes.add(new Node(1, 21));
        w8Nodes.add(new Node(0, 22));
        w8Nodes.add(new Node(1, 22));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);
        Way w6 = new Line(w6Nodes,(byte)0);
        Way w7 = new Line(w7Nodes,(byte)0);
        Way w8 = new Line(w8Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w3);
        tree.insert(w6);
        tree.insert(w1);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());
        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);
        assertEquals(3, left.values.size());
        assertEquals(1, right.values.size());

        assertEquals(w0, left.values.get(0));
        assertEquals(w1, left.values.get(1));
        assertEquals(w3, left.values.get(2));
        assertEquals(w6, right.values.get(0));

        tree.insert(w4);

        left = tree.getRoot().children.get(0);
        RNode mid = tree.getRoot().children.get(1);
        right = tree.getRoot().children.get(2);


        assertEquals(3, tree.getRoot().children.size());
        assertEquals(1, left.values.size());
        assertEquals(2, mid.values.size());
        assertEquals(2, right.values.size());

        assertEquals(w6, left.values.get(0)); // because the previous left node was split, the right node was moved to the left
        assertEquals(w0, mid.values.get(0));
        assertEquals(w1, mid.values.get(1));
        assertEquals(w4, right.values.get(0));
        assertEquals(w3, right.values.get(1));

        tree.insert(w7);
        tree.insert(w2);
        tree.insert(w5);
        tree.insert(w8);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(3, tree.getRoot().children.size());

        left = tree.getRoot().children.get(0);
        mid = tree.getRoot().children.get(1);
        right = tree.getRoot().children.get(2);

        assertTrue(left.isLeaf());
        assertTrue(mid.isLeaf());
        assertTrue(right.isLeaf());
        assertEquals(3, left.values.size());
        assertEquals(3, mid.values.size());
        assertEquals(3, right.values.size());

        assertEquals(w6, left.values.get(0));
        assertEquals(w7, left.values.get(1));
        assertEquals(w8, left.values.get(2));

        assertEquals(w0, mid.values.get(0));
        assertEquals(w1, mid.values.get(1));
        assertEquals(w2, mid.values.get(2));

        assertEquals(w4, right.values.get(0));
        assertEquals(w3, right.values.get(1));
        assertEquals(w5, right.values.get(2));
    }

    @Test
    void testQuery3GroupsInOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();
        ArrayList<Node> w6Nodes = new ArrayList<>();
        ArrayList<Node> w7Nodes = new ArrayList<>();
        ArrayList<Node> w8Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 0));
        w1Nodes.add(new Node(0, 1));
        w1Nodes.add(new Node(1, 1));
        w2Nodes.add(new Node(0, 2));
        w2Nodes.add(new Node(1, 2));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 10));
        w4Nodes.add(new Node(0, 11));
        w4Nodes.add(new Node(1, 11));
        w5Nodes.add(new Node(0, 12));
        w5Nodes.add(new Node(1, 12));

        w6Nodes.add(new Node(0, 20));
        w6Nodes.add(new Node(1, 20));
        w7Nodes.add(new Node(0, 21));
        w7Nodes.add(new Node(1, 21));
        w8Nodes.add(new Node(0, 22));
        w8Nodes.add(new Node(1, 22));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);
        Way w6 = new Line(w6Nodes,(byte)0);
        Way w7 = new Line(w7Nodes,(byte)0);
        Way w8 = new Line(w8Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w1);
        tree.insert(w2);
        tree.insert(w3);
        tree.insert(w4);
        tree.insert(w5);
        tree.insert(w6);
        tree.insert(w7);
        tree.insert(w8);

        ArrayList<DrawnObject> result = tree.query(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        assertEquals(9, result.size());

        result = tree.query(-100, -10, -10, 10);
        assertEquals(0, result.size());

        result = tree.query(0.5f * 0.56f, 1.5f * 0.56f, -0.5f, -0.5f);
        assertEquals(1, result.size());
        assertEquals(w1, result.get(0));

        result = tree.query(0.5f * 0.56f, 11.5f * 0.56f, -0.5f, -0.5f);
        assertEquals(4, result.size());
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w2));
        assertTrue(result.contains(w3));
        assertTrue(result.contains(w4));
    }

    @Test
    void testQuery3GroupsOutOfOrder() {
        RTree tree = new RTree(3);

        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();
        ArrayList<Node> w6Nodes = new ArrayList<>();
        ArrayList<Node> w7Nodes = new ArrayList<>();
        ArrayList<Node> w8Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w0Nodes.add(new Node(1, 0));
        w1Nodes.add(new Node(0, 1));
        w1Nodes.add(new Node(1, 1));
        w2Nodes.add(new Node(0, 2));
        w2Nodes.add(new Node(1, 2));

        w3Nodes.add(new Node(0, 10));
        w3Nodes.add(new Node(1, 10));
        w4Nodes.add(new Node(0, 11));
        w4Nodes.add(new Node(1, 11));
        w5Nodes.add(new Node(0, 12));
        w5Nodes.add(new Node(1, 12));

        w6Nodes.add(new Node(0, 20));
        w6Nodes.add(new Node(1, 20));
        w7Nodes.add(new Node(0, 21));
        w7Nodes.add(new Node(1, 21));
        w8Nodes.add(new Node(0, 22));
        w8Nodes.add(new Node(1, 22));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);
        Way w6 = new Line(w6Nodes,(byte)0);
        Way w7 = new Line(w7Nodes,(byte)0);
        Way w8 = new Line(w8Nodes,(byte)0);

        tree.insert(w0);
        tree.insert(w3);
        tree.insert(w6);
        tree.insert(w1);
        tree.insert(w4);
        tree.insert(w7);
        tree.insert(w2);
        tree.insert(w5);
        tree.insert(w8);

        ArrayList<DrawnObject> result = tree.query(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        assertEquals(9, result.size());

        result = tree.query(-100, -10, -10, 10);
        assertEquals(0, result.size());

        result = tree.query(0.5f * 0.56f, 1.5f * 0.56f, -0.5f, -0.5f);
        assertEquals(1, result.size());
        assertEquals(w1, result.get(0));

        result = tree.query(0.5f * 0.56f, 11.5f * 0.56f, -0.5f, -0.5f);
        assertEquals(4, result.size());
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w2));
        assertTrue(result.contains(w3));
        assertTrue(result.contains(w4));
    }

    @Test
    void testInsertLargeTree() {
        RTree tree = new RTree(3);

        Way[] ways = new Way[27];

        ArrayList<Node> nodes = null;

        for (int i = 0; i < ways.length; i++) {
            nodes = new ArrayList<>();
            nodes.add(new Node(i, 0));
            nodes.add(new Node(i, 1));
            ways[i] = new Line(nodes,(byte)0);
        }

        tree.insert(ways[0]);
        tree.insert(ways[16]);
        tree.insert(ways[26]);
        tree.insert(ways[1]);

        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);

        System.out.println("[" + left.minX + " : " + left.maxX + "]   [" + left.minY + " : " + left.maxY + "]");
        System.out.println("[" + right.minX + " : " + right.maxX + "]   [" + right.minY + " : " + right.maxY + "]");

        tree.insert(ways[25]);

        assertEquals(ways[0], left.values.get(0));
        assertEquals(ways[1], left.values.get(1));

        assertEquals(ways[26], right.values.get(0));
        assertEquals(ways[16], right.values.get(1));
        assertEquals(ways[25], right.values.get(2));

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());
        assertEquals(2, left.values.size());
        assertEquals(3, right.values.size());

        tree.insert(ways[24]);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(3, tree.getRoot().children.size());
        assertEquals(2, tree.getRoot().children.get(0).values.size());
        assertEquals(3, tree.getRoot().children.get(1).values.size());
        assertEquals(1, tree.getRoot().children.get(2).values.size());

        RNode oldRoot = tree.getRoot();

        tree.insert(ways[23]);

        RNode newRoot = tree.getRoot();

        assertNotSame(oldRoot, newRoot);

        left = tree.getRoot().children.get(0);
        right = tree.getRoot().children.get(1);

        assertFalse(left.isLeaf());
        assertFalse(right.isLeaf());

        assertEquals(1, left.children.size());
        assertEquals(3, right.children.size());

        assertTrue(left.children.get(0).isLeaf());
        assertTrue(right.children.get(0).isLeaf());
        assertTrue(right.children.get(1).isLeaf());
        assertTrue(right.children.get(2).isLeaf());

        assertTrue(left.children.get(0).values.contains(ways[0]));
        assertTrue(left.children.get(0).values.contains(ways[1]));

        assertTrue(right.children.get(0).values.contains(ways[26]));
        assertTrue(right.children.get(0).values.contains(ways[25]));
        assertTrue(right.children.get(1).values.contains(ways[24]));
        assertTrue(right.children.get(1).values.contains(ways[23]));
        assertTrue(right.children.get(2).values.contains(ways[16]));
    }

    @Test
    void testCalculateLeafSlices() {
        int M = 3;

        RTree tree = new RTree(M);

        ArrayList<DrawnObject> bulk = new ArrayList<>();

        ArrayList<Node>[] ways = new ArrayList[12];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new ArrayList<Node>();
        }

        ways[0].add(new Node(0, 0));
        ways[1].add(new Node(0, 1));
        ways[2].add(new Node(0, 2));
        ways[3].add(new Node(10, 0));
        ways[4].add(new Node(10, 1));
        ways[5].add(new Node(10, 2));
        ways[6].add(new Node(0, 10));
        ways[7].add(new Node(0, 11));
        ways[8].add(new Node(0, 12));
        ways[9].add(new Node(10, 10));
        ways[10].add(new Node(10, 11));
        ways[11].add(new Node(10, 12));

        DrawnObject[] drawnObjects = new DrawnObject[12];

        for (int i = 0; i < ways.length; i++) {
            drawnObjects[i] = new Line(ways[i],(byte)0);
            bulk.add(drawnObjects[i]);
        }

        int r = bulk.size();
        int S = (int) Math.ceil(Math.sqrt((double) r / M));
        ArrayList<DrawnObject>[] slices = tree.calculateSlices(M, S, bulk);

        assertEquals(2, S);
        assertEquals(2, slices.length);
        assertEquals(6, slices[0].size());
        assertEquals(6, slices[1].size());
        for (int i = 0; i < 6; i++) {
            assertTrue(slices[0].contains(drawnObjects[i]));
        }
        for (int i = 0; i < 6; i++) {
            System.out.println(i);
            assertTrue(slices[1].contains(drawnObjects[6 + i]));
        }
    }

    @Test
    void testCalculateLeafSlicesNotFull() {
        int M = 3;

        RTree tree = new RTree(M);

        ArrayList<DrawnObject> bulk = new ArrayList<>();

        ArrayList<Node>[] ways = new ArrayList[7];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new ArrayList<Node>();
        }

        ways[0].add(new Node(0, 0));
        ways[1].add(new Node(0, 1));
        ways[2].add(new Node(0, 2));
        ways[3].add(new Node(10, 0));
        ways[4].add(new Node(10, 1));
        ways[5].add(new Node(10, 2));
        ways[6].add(new Node(0, 10));

        DrawnObject[] drawnObjects = new DrawnObject[11];

        for (int i = 0; i < ways.length; i++) {
            drawnObjects[i] = new Line(ways[i],(byte)0);
            bulk.add(drawnObjects[i]);
        }

        int r = bulk.size();
        int S = (int) Math.ceil(Math.sqrt((double) r / M));
        ArrayList<DrawnObject>[] slices = tree.calculateSlices(M, S, bulk);

        assertEquals(2, S);
        assertEquals(2, slices.length);
        assertEquals(6, slices[0].size());
        assertEquals(1, slices[1].size());
        for (int i = 0; i < 6; i++) {
            assertTrue(slices[0].contains(drawnObjects[i]));
        }
        for (int i = 0; i < 1; i++) {
            System.out.println(i);
            assertTrue(slices[1].contains(drawnObjects[6 + i]));
        }
    }

    @Test
    void testCalculateLeafRuns() {
        int M = 3;

        RTree tree = new RTree(M);

        ArrayList<DrawnObject> bulk = new ArrayList<>();

        ArrayList<Node>[] ways = new ArrayList[12];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new ArrayList<Node>();
        }

        ways[0].add(new Node(0, 0));
        ways[1].add(new Node(0, 1));
        ways[2].add(new Node(0, 2));
        ways[3].add(new Node(-10, 0));
        ways[4].add(new Node(-10, 1));
        ways[5].add(new Node(-10, 2));
        ways[6].add(new Node(0, 10));
        ways[7].add(new Node(0, 11));
        ways[8].add(new Node(0, 12));
        ways[9].add(new Node(-10, 10));
        ways[10].add(new Node(-10, 11));
        ways[11].add(new Node(-10, 12));

        DrawnObject[] drawnObjects = new DrawnObject[12];

        for (int i = 0; i < ways.length; i++) {
            drawnObjects[i] = new Line(ways[i],(byte)0);
            bulk.add(drawnObjects[i]);
        }

        int r = bulk.size();
        int S = (int) Math.ceil(Math.sqrt((double) r / M));
        ArrayList<ArrayList<DrawnObject>> runs = tree.calculateRuns(M, S, bulk);

        assertEquals(4, runs.size());

        for (int i = 0; i < drawnObjects.length; i++) {
            System.out.println("Slice: " + (i / 3) + "    run: " + (i % 3));

            assertEquals(drawnObjects[i], runs.get(i / 3).get(i % 3));
        }
    }

    @Test
    void testCalculateLeafRuns1() {
        int M = 3;

        RTree tree = new RTree(M);

        ArrayList<DrawnObject> bulk = new ArrayList<>();

        ArrayList<Node>[] ways = new ArrayList[4];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new ArrayList<Node>();
        }

        ways[0].add(new Node(0, 0));
        ways[1].add(new Node(0, 1));
        ways[2].add(new Node(0, 2));
        ways[3].add(new Node(-10, 0));

        DrawnObject[] drawnObjects = new DrawnObject[4];

        for (int i = 0; i < ways.length; i++) {
            drawnObjects[i] = new Line(ways[i],(byte)0);
            bulk.add(drawnObjects[i]);
        }

        int r = bulk.size();
        double P = Math.ceil((double) r / M);
        int S = (int) Math.ceil(Math.sqrt(P));
        ArrayList<ArrayList<DrawnObject>> runs = tree.calculateRuns(M, S, bulk);

        assertEquals(2, runs.size());
        ArrayList<DrawnObject> r0 = runs.get(0);
        ArrayList<DrawnObject> r1 = runs.get(1);

        assertEquals(3, r0.size());
        assertEquals(1, r1.size());
    }

    @Test
    void testCalculateLeafSlices1(){
        int M = 5;
        RTree tree = new RTree(M);
        ArrayList<DrawnObject> bulk = new ArrayList<>();

        Way[] ways = new Way[21];
        for(int i = 0; i < ways.length; i++){
            ArrayList<Node> list = new ArrayList<>();
            list.add(new Node(i, i));
            ways[i] = new Line(list,(byte)0);
            bulk.add(ways[i]);
        }

        assertEquals(21, bulk.size());

        int S = (int) Math.ceil(Math.sqrt((double) bulk.size() / M));
        ArrayList<DrawnObject>[] slices = tree.calculateSlices(M, S, bulk);

        assertEquals(2, slices.length);

        for(int i = 0; i < 15; i++){
            assertTrue(slices[0].contains(ways[i]));
        }


        for(int i = 0; i < slices[1].size(); i++){
            DrawnObject d = slices[1].get(i);
            System.out.println((d.getMidX()/0.56d) + " ; " + -d.getMidY());
        }

        assertTrue(slices[1].contains(ways[15]));
        assertTrue(slices[1].contains(ways[15+1]));
        assertTrue(slices[1].contains(ways[15+2]));
        assertTrue(slices[1].contains(ways[15+3]));
        assertTrue(slices[1].contains(ways[15+4]));
        assertTrue(slices[1].contains(ways[15+5]));

        assertEquals(15, slices[0].size());
        assertEquals(6, slices[1].size());
    }

    @Test
    void testCalculateRuns2(){
        int M = 5;
        RTree tree = new RTree(M);
        ArrayList<DrawnObject> bulk = new ArrayList<>();

        Way[] ways = new Way[21];
        for(int i = 0; i < ways.length; i++){
            ArrayList<Node> list = new ArrayList<>();
            list.add(new Node(i, i));
            ways[i] = new Line(list,(byte)0);
            bulk.add(ways[i]);
        }

        int S = (int) Math.ceil(Math.sqrt((double) bulk.size() / M));
        ArrayList<ArrayList<DrawnObject>> runs = tree.calculateRuns(M, S, bulk);

        assertEquals(5, runs.size());
    }

    @Test
    void testBulkLoad() {
        // add to first level
        ArrayList<Node> w0Nodes = new ArrayList<>();
        ArrayList<Node> w1Nodes = new ArrayList<>();
        ArrayList<Node> w2Nodes = new ArrayList<>();
        ArrayList<Node> w3Nodes = new ArrayList<>();
        ArrayList<Node> w4Nodes = new ArrayList<>();
        ArrayList<Node> w5Nodes = new ArrayList<>();
        ArrayList<Node> w6Nodes = new ArrayList<>();
        ArrayList<Node> w7Nodes = new ArrayList<>();
        ArrayList<Node> w8Nodes = new ArrayList<>();
        ArrayList<Node> w9Nodes = new ArrayList<>();
        ArrayList<Node> w10Nodes = new ArrayList<>();
        ArrayList<Node> w11Nodes = new ArrayList<>();

        w0Nodes.add(new Node(0, 0));
        w1Nodes.add(new Node(1, 1));
        w2Nodes.add(new Node(2, 2));

        w3Nodes.add(new Node(10, 0));
        w4Nodes.add(new Node(11, 1));
        w5Nodes.add(new Node(12, 2));

        w6Nodes.add(new Node(0, 10));
        w7Nodes.add(new Node(1, 11));
        w8Nodes.add(new Node(2, 12));

        w9Nodes.add(new Node(10, 10));
        w10Nodes.add(new Node(11, 11));
        w11Nodes.add(new Node(12, 12));

        Way w0 = new Line(w0Nodes,(byte)0);
        Way w1 = new Line(w1Nodes,(byte)0);
        Way w2 = new Line(w2Nodes,(byte)0);
        Way w3 = new Line(w3Nodes,(byte)0);
        Way w4 = new Line(w4Nodes,(byte)0);
        Way w5 = new Line(w5Nodes,(byte)0);
        Way w6 = new Line(w6Nodes,(byte)0);
        Way w7 = new Line(w7Nodes,(byte)0);
        Way w8 = new Line(w8Nodes,(byte)0);
        Way w9 = new Line(w9Nodes,(byte)0);
        Way w10 = new Line(w10Nodes,(byte)0);
        Way w11 = new Line(w11Nodes,(byte)0);

        ArrayList<DrawnObject> bulk = new ArrayList<>();
        bulk.add(w0);
        bulk.add(w1);
        bulk.add(w2);
        bulk.add(w3);
        bulk.add(w4);
        bulk.add(w5);
        bulk.add(w6);
        bulk.add(w7);
        bulk.add(w8);
        bulk.add(w9);
        bulk.add(w10);
        bulk.add(w11);

        RTree tree = new RTree(3, bulk);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(2, tree.getRoot().children.size());

        RNode left = tree.getRoot().children.get(0);
        RNode right = tree.getRoot().children.get(1);

        assertFalse(left.isLeaf());
        assertFalse(right.isLeaf());

        assertEquals(3, left.children.size());
        assertEquals(1, right.children.size());

        RNode leftLeft = left.children.get(0);
        RNode leftMid = left.children.get(1);
        RNode leftRight = left.children.get(2);
        RNode rightLeft = right.children.get(0);

        assertTrue(leftLeft.isLeaf());
        assertTrue(leftMid.isLeaf());
        assertTrue(leftRight.isLeaf());
        assertTrue(rightLeft.isLeaf());

        assertEquals(3, leftLeft.values.size());
        assertEquals(3, leftMid.values.size());
        assertEquals(3, leftRight.values.size());
        assertEquals(3, rightLeft.values.size());

        for(DrawnObject d : leftMid.values){
            System.out.println((d.getMidX()/0.56d) + " ; " + -d.getMidY());
        }

        assertTrue(leftLeft.values.contains(w3));
        assertTrue(leftLeft.values.contains(w4));
        assertTrue(leftLeft.values.contains(w5));

        assertTrue(leftMid.values.contains(w9));
        assertTrue(leftMid.values.contains(w10));
        assertTrue(leftMid.values.contains(w11));

        assertTrue(leftRight.values.contains(w0));
        assertTrue(leftRight.values.contains(w1));
        assertTrue(leftRight.values.contains(w2));

        assertTrue(rightLeft.values.contains(w6));
        assertTrue(rightLeft.values.contains(w7));
        assertTrue(rightLeft.values.contains(w8));
    }

    @Test
    void testBulkLoad1() {
        Way[] ways = new Way[21];
        ArrayList<DrawnObject> bulk = new ArrayList<>();

        for(int i = 0; i < ways.length; i++){
            ArrayList<Node> list = new ArrayList<>();
            list.add(new Node(-i, i));
            ways[i] = new Line(list,(byte)0);
            bulk.add(ways[i]);
        }

        RTree tree = new RTree(5, bulk);

        assertFalse(tree.getRoot().isLeaf());
        assertEquals(5, tree.getRoot().children.size());

        assertEquals(5, tree.getRoot().children.get(0).values.size());
        assertEquals(5, tree.getRoot().children.get(1).values.size());
        assertEquals(5, tree.getRoot().children.get(2).values.size());
        assertEquals(5, tree.getRoot().children.get(3).values.size());
        assertEquals(1, tree.getRoot().children.get(4).values.size());

        for(int i = 0; i < 5; i++){
            System.out.println(i);
            assertTrue(tree.getRoot().children.get(0).values.contains(ways[i]));
            assertTrue(tree.getRoot().children.get(1).values.contains(ways[5+i]));
            assertTrue(tree.getRoot().children.get(2).values.contains(ways[10+i]));
            assertTrue(tree.getRoot().children.get(3).values.contains(ways[15+i]));
        }
        assertTrue(tree.getRoot().children.get(4).values.contains(ways[20]));
    }
}