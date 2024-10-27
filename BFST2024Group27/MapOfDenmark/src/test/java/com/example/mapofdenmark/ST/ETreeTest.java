package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ETreeTest {

    private Way[] getWays4() {
        ArrayList<Node> al0 = new ArrayList<>();
        ArrayList<Node> al1 = new ArrayList<>();
        ArrayList<Node> al2 = new ArrayList<>();
        ArrayList<Node> al3 = new ArrayList<>();

        al0.add(new Node(4, 0));
        al0.add(new Node(3, 2));

        al1.add(new Node(7, 1));
        al1.add(new Node(6, 5));

        al2.add(new Node(0, 3));
        al2.add(new Node(5, 4));

        al3.add(new Node(1, 6));
        al3.add(new Node(2, 7));

        Way w0 = new Line(al0,(byte)0);
        Way w1 = new Line(al1,(byte)0);
        Way w2 = new Line(al2,(byte)0);
        Way w3 = new Line(al3,(byte)0);

        return new Way[]{w0, w1, w2, w3};
    }

    private Way[] getWays16() {
        ArrayList<Node> al0 = new ArrayList<>();
        ArrayList<Node> al1 = new ArrayList<>();
        ArrayList<Node> al2 = new ArrayList<>();
        ArrayList<Node> al3 = new ArrayList<>();
        ArrayList<Node> al4 = new ArrayList<>();
        ArrayList<Node> al5 = new ArrayList<>();
        ArrayList<Node> al6 = new ArrayList<>();
        ArrayList<Node> al7 = new ArrayList<>();
        ArrayList<Node> al8 = new ArrayList<>();
        ArrayList<Node> al9 = new ArrayList<>();
        ArrayList<Node> al10 = new ArrayList<>();
        ArrayList<Node> al11 = new ArrayList<>();
        ArrayList<Node> al12 = new ArrayList<>();
        ArrayList<Node> al13 = new ArrayList<>();
        ArrayList<Node> al14 = new ArrayList<>();
        ArrayList<Node> al15 = new ArrayList<>();

        al0.add(new Node(6, 0));
        al0.add(new Node(2, 13));

        al1.add(new Node(1, 3));
        al1.add(new Node(4, 12));

        al2.add(new Node(3, 6));
        al2.add(new Node(8, 24));

        al3.add(new Node(15, 1));
        al3.add(new Node(20, 5));

        al4.add(new Node(5, 8));
        al4.add(new Node(11, 7));

        al5.add(new Node(9, 4));
        al5.add(new Node(7, 28));

        al6.add(new Node(29, 9));
        al6.add(new Node(0, 19));

        al7.add(new Node(13, 21));
        al7.add(new Node(10, 31));

        al8.add(new Node(16, 25));
        al8.add(new Node(12, 30));

        al9.add(new Node(17, 22));
        al9.add(new Node(21, 29));

        al10.add(new Node(31, 18));
        al10.add(new Node(14, 20));

        al11.add(new Node(26, 17));
        al11.add(new Node(23, 23));

        al12.add(new Node(27, 26));
        al12.add(new Node(30, 27));

        al13.add(new Node(18, 14));
        al13.add(new Node(24, 16));

        al14.add(new Node(28, 2));
        al14.add(new Node(19, 15));

        al15.add(new Node(22, 10));
        al15.add(new Node(25, 11));

        Way w0 = new Line(al0,(byte)0);
        Way w1 = new Line(al1,(byte)0);
        Way w2 = new Line(al2,(byte)0);
        Way w3 = new Line(al3,(byte)0);
        Way w4 = new Line(al4,(byte)0);
        Way w5 = new Line(al5,(byte)0);
        Way w6 = new Line(al6,(byte)0);
        Way w7 = new Line(al7,(byte)0);
        Way w8 = new Line(al8,(byte)0);
        Way w9 = new Line(al9,(byte)0);
        Way w10 = new Line(al10,(byte)0);
        Way w11 = new Line(al11,(byte)0);
        Way w12 = new Line(al12,(byte)0);
        Way w13 = new Line(al13,(byte)0);
        Way w14 = new Line(al14,(byte)0);
        Way w15 = new Line(al15,(byte)0);

        return new Way[]{w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15};
    }

    @Test
    void findLargestIndex4Ways() {
        Way[] ways = getWays4();

        ETree tree = new ETree(new Way[0]);

        ETree.ENode[] nodes;
        int depth;
        int result;

        //depth 0
        depth = 0;
        nodes = tree.buildArray(ways, depth);

        //null set of nodes
        assertThrows(RuntimeException.class, () -> {
            tree.findLargestIndex(null, -10, -10, 10, 10, 0);
        });

        // empty set of nodes
        result = tree.findLargestIndex(new ETree.ENode[]{}, -10, -10, 10, 10, depth);
        assertEquals(-1, result);

        // query entirely west of ways
        result = tree.findLargestIndex(nodes, -20, -10, -10, 10, depth);
        assertEquals(-1, result);

        // large query range surrounding the ways
        result = tree.findLargestIndex(nodes, -10, -10, 10, 10, depth);
        assertEquals(3, result);

        // query covering the westernmost nodes in [wo...w3]
        result = tree.findLargestIndex(nodes, -10, -10, 3.5 * 0.56d, 10, depth);
        assertEquals(2, result);

        // query covering [w4]
        result = tree.findLargestIndex(nodes, 5.5 * 0.56d, -10, 6.5 * 0.56d, 10, depth);
        assertEquals(3, result);

        // query entirely east of w4
        result = tree.findLargestIndex(nodes, 6.5 * 0.56d, -10, 6.5 * 0.56d, 10, depth);
        assertEquals(3, result);


        //depth 1
        depth = 1;
        nodes = tree.buildArray(ways, depth);

        // Query covers w1 which has the southernmost point of all ways, so it is at index 0
        result = tree.findLargestIndex(nodes, -10, -8, 10, -6, depth);
        assertEquals(0, result);

        // Query covers the edge in w1
        result = tree.findLargestIndex(nodes, -10, -6.5d, 10, -6.5d, depth);
        assertEquals(0, result);

        // Query covers the edge in w1
        result = tree.findLargestIndex(nodes, -10, -6.5d, 10, -4.5d, depth);
        assertEquals(1, result);


        //depth 2
        depth = 2;
        nodes = tree.buildArray(ways, depth);

        // Query is entirely west of ways
        result = tree.findLargestIndex(nodes, -10, -10, -5, 10, depth);
        assertEquals(3, result);

        // Query is east south of ways
        result = tree.findLargestIndex(nodes, 10, -8, 20, -8, depth);
        assertEquals(-1, result);

        // Query covers w3
        result = tree.findLargestIndex(nodes, 5.5 * 0.56d, -8, 10 * 0.56d, -8, depth);
        assertEquals(0, result);

        // Query covers w0
        result = tree.findLargestIndex(nodes, 0d, -8, 2.5d * 0.56d, -8, depth);
        assertEquals(3, result);


        //depth 3
        depth = 3;
        nodes = tree.buildArray(ways, depth);

        // Query is south of ways
        result = tree.findLargestIndex(nodes, -10, -20, 10, -10, depth);
        assertEquals(3, result);

        // Query is north of ways
        result = tree.findLargestIndex(nodes, -10, 10, 10, 20, depth);
        assertEquals(-1, result);

        // Query covers w0
        result = tree.findLargestIndex(nodes, 0, -4, 2, -3, depth);
        assertEquals(2, result);
    }

    @Test
    void queryPoints4Ways() {
        Way[] ways = getWays4();

        ETree tree = new ETree(ways);

        ArrayList<Way> result = new ArrayList<>();

        assertEquals(ways[0], tree.tree[0].assoc[0].assoc[0].assoc[0].way);

        result = tree.query(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        assertEquals(4, result.size());

        result = tree.query(-1*0.56, -5, 2.5d*0.56, -2);

        assertEquals(1, result.size());
        assertEquals(ways[0], result.get(0));

        result = tree.query(-100, -100, -99, -99);

        assertNull(result);

    }

    @Test
    void findLargestIndexVerticalLines() {
        ArrayList<Node> al0 = new ArrayList<>();
        ArrayList<Node> al1 = new ArrayList<>();
        ArrayList<Node> al2 = new ArrayList<>();
        ArrayList<Node> al3 = new ArrayList<>();
        ArrayList<Node> al4 = new ArrayList<>();
        ArrayList<Node> al5 = new ArrayList<>();
        ArrayList<Node> al6 = new ArrayList<>();
        ArrayList<Node> al7 = new ArrayList<>();

        al0.add(new Node(-8, 0));
        al0.add(new Node(8, 0));

        al1.add(new Node(-7, 1));
        al1.add(new Node(7, 1));

        al2.add(new Node(-6, 2));
        al2.add(new Node(6, 2));

        al3.add(new Node(-5, 3));
        al3.add(new Node(5, 3));

        al4.add(new Node(-4, 4));
        al4.add(new Node(4, 4));

        al5.add(new Node(-3, 5));
        al5.add(new Node(3, 5));

        al6.add(new Node(-2, 6));
        al6.add(new Node(2, 6));

        al7.add(new Node(-1, 7));
        al7.add(new Node(1, 7));

        Way w0 = new Line(al0,(byte)0);
        Way w1 = new Line(al1,(byte)0);
        Way w2 = new Line(al2,(byte)0);
        Way w3 = new Line(al3,(byte)0);
        Way w4 = new Line(al4,(byte)0);
        Way w5 = new Line(al5,(byte)0);
        Way w6 = new Line(al6,(byte)0);
        Way w7 = new Line(al7,(byte)0);

        Way[] ways = new Way[]{w0, w1, w2, w3, w4, w5, w6, w7};

        ETree tree = new ETree(new Way[0]);

        ETree.ENode[] nodes;
        int depth;
        int result;

        //depth 0
        depth = 0;
        nodes = tree.buildArray(ways, depth);

        // large query range surrounding the ways
        result = tree.findLargestIndex(nodes, -10, -10, 10, 10, depth);
        assertEquals(7, result);

        // query box only covering w0
        result = tree.findLargestIndex(nodes, -0.5d * 0.56d, -10, 0.5d * 0.56d, 10, depth);
        assertEquals(0, result);

        // query box covering [w0...w4]
        result = tree.findLargestIndex(nodes, -0.5d * 0.56d, -10, 4.5d * 0.56d, 10, depth);
        assertEquals(4, result);

        // query box above [w0...w4]
        result = tree.findLargestIndex(nodes, -0.5d * 0.56d, 10, 4.5d * 0.56d, 20, depth);
        assertEquals(4, result);

        // query box below [w0...w4]
        result = tree.findLargestIndex(nodes, -0.5d * 0.56d, -20, 4.5d * 0.56d, -10, depth);
        assertEquals(4, result);

        //depth 1
        depth = 1;
        nodes = tree.buildArray(ways, depth);

        // [w0...w7]
        result = tree.findLargestIndex(nodes, -10, -10, 10, 10, depth);
        assertEquals(7, result);

        // [w0]
        result = tree.findLargestIndex(nodes, -10, -8.5d, 10, -7.5d, depth);
        assertEquals(0, result);

        // [w0..w4]
        result = tree.findLargestIndex(nodes, -10, 7.5d, 10, 8.5d, depth);
        assertEquals(7, result);
    }

}