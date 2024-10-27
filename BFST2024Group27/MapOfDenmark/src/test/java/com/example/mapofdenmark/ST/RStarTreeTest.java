package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RStarTreeTest {
    @Test
    void testInsert() {
        RStarTree tree = new RStarTree(3, 1);

        Float[] floats = new Float[]{0f, 2f, 5f, 8f, 10.5f, 12f, 14f, 14.5f, 17f, 18f};
        Way[] ways = new Way[10];

        for (int i = 0; i < 10; i++) {
            ArrayList<Node> list = new ArrayList<>();
            list.add(new Node(0, floats[i]));
            list.add(new Node(1, floats[i]));
            ways[i] = new Line(list,(byte)0);
        }
        ArrayList<Node> list = new ArrayList<>();
        list.add(new Node(0, 8f));
        list.add(new Node(1, 8.25f));
        ways[3] = new Line(list,(byte)0);

        list = new ArrayList<>();
        list.add(new Node(0, 10.5f));
        list.add(new Node(1, 10f));
        ways[4] = new Line(list,(byte)0);
        list = new ArrayList<>();
        list.add(new Node(0, 13));
        list.add(new Node(1, 14.5f));
        ways[7] = new Line(list,(byte)0);

        tree.insertData(ways[0]);
        tree.insertData(ways[2]);
        tree.insertData(ways[7]);
        tree.insertData(ways[8]);

        assertFalse(tree.root.isLeaf());
        assertEquals(2, tree.root.children.size());

        RNode left = tree.root.children.get(0);
        RNode right = tree.root.children.get(1);

        assertTrue(left.values.contains(ways[0]));
        assertTrue(left.values.contains(ways[2]));
        assertTrue(right.values.contains(ways[7]));
        assertTrue(right.values.contains(ways[8]));

        tree.insertData(ways[6]);

        assertEquals(3, right.values.size());
        assertTrue(right.values.contains(ways[6]));

        tree.insertData(ways[9]);

        assertEquals(3, tree.root.children.size());

        left = tree.root.children.get(0);
        RNode mid = tree.root.children.get(1);
        right = tree.root.children.get(2);

        assertTrue(left.values.contains(ways[0]));
        assertTrue(left.values.contains(ways[2]));
        assertTrue(mid.values.contains(ways[6]));
        assertTrue(mid.values.contains(ways[7]));
        assertTrue(right.values.contains(ways[8]));
        assertTrue(right.values.contains(ways[9]));

        //tree.insertData(ways[5]);
        tree.insertData(ways[4]);
        assertTrue(mid.values.contains(ways[5]));
        assertTrue(mid.values.contains(ways[4]));

        tree.insertData(ways[3]);
        assertTrue(left.values.contains(ways[3]));

        System.out.println("Insert ways[4]");
        tree.insertData(ways[4]);

        assertEquals(2, tree.root.children.size());

        left = tree.root.children.get(0);
        right = tree.root.children.get(1);

        assertEquals(2, left.children.size());
        assertEquals(2, right.children.size());

        RNode leftLeft = left.children.get(0);
        RNode leftRight = left.children.get(1);
        RNode rightLeft = right.children.get(0);
        RNode rightRight = right.children.get(1);

        assertTrue(leftLeft.values.contains(ways[0]));
        assertTrue(leftLeft.values.contains(ways[2]));
        assertTrue(leftLeft.values.contains(ways[3]));

        assertTrue(leftRight.values.contains(ways[4]));
        assertTrue(leftRight.values.contains(ways[5]));

        assertTrue(rightLeft.values.contains(ways[6]));
        assertTrue(rightLeft.values.contains(ways[7]));

        assertTrue(rightRight.values.contains(ways[8]));
        assertTrue(rightRight.values.contains(ways[9]));


        System.out.println("Insert ways[1]");
        tree.insertData(ways[1]);

        left = tree.root.children.get(0);
        right = tree.root.children.get(1);
        leftLeft = left.children.get(0);
        leftRight = left.children.get(1);
        rightLeft = right.children.get(0);
        rightRight = right.children.get(1);

        assertTrue(leftLeft.values.contains(ways[0]));
        assertTrue(leftLeft.values.contains(ways[1]));
        assertTrue(leftLeft.values.contains(ways[2]));

        assertTrue(leftRight.values.contains(ways[3]));
        assertTrue(leftRight.values.contains(ways[4]));
        assertTrue(leftRight.values.contains(ways[5]));

        assertTrue(rightLeft.values.contains(ways[6]));
        assertTrue(rightLeft.values.contains(ways[7]));

        assertTrue(rightRight.values.contains(ways[8]));
        assertTrue(rightRight.values.contains(ways[9]));
    }
}