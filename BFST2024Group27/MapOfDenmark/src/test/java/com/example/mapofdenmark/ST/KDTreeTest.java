package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class KDTreeTest {
    @BeforeEach
    void setUp() {
    }

    private Node[] getNodes16(){
        Node[] nodes = new Node[16];

        float[] lats = new float[]{3, 10, 12, 7, 9, 2, 14, 11, 8, 15, 1, 4, 0, 5, 6, 13};

        for(int i = 0; i < nodes.length; i++){
            nodes[i] = new Node(lats[i], i);
        }

        return nodes;
    }

    @Test
    void instantiateTree16Points(){
        Node[] nodes = getNodes16();

        KDTree<Node> tree = new KDTree<Node>(Node.class, nodes);

        assertEquals(nodes[7], tree.root.value);

        assertEquals(nodes[4], tree.root.leftChild.value);
        assertEquals(nodes[13], tree.root.rightChild.value);

        assertEquals(nodes[3], tree.root.leftChild.leftChild.value);
        assertEquals(nodes[2], tree.root.leftChild.rightChild.value);
        assertEquals(nodes[11], tree.root.rightChild.leftChild.value);
        assertEquals(nodes[9], tree.root.rightChild.rightChild.value);

        assertEquals(nodes[0], tree.root.leftChild.leftChild.leftChild.value);
        assertEquals(nodes[5], tree.root.leftChild.leftChild.rightChild.value);
        assertEquals(nodes[1], tree.root.leftChild.rightChild.leftChild.value);
        assertEquals(nodes[7], tree.root.leftChild.rightChild.rightChild.value);
        assertEquals(nodes[10], tree.root.rightChild.leftChild.leftChild.value);
        assertEquals(nodes[12], tree.root.rightChild.leftChild.rightChild.value);
        assertEquals(nodes[8], tree.root.rightChild.rightChild.leftChild.value);
        assertEquals(nodes[14], tree.root.rightChild.rightChild.rightChild.value);

        assertEquals(nodes[0], tree.root.leftChild.leftChild.leftChild.leftChild.value);
        assertEquals(nodes[3], tree.root.leftChild.leftChild.leftChild.rightChild.value);
        assertEquals(nodes[5], tree.root.leftChild.leftChild.rightChild.leftChild.value);
        assertEquals(nodes[4], tree.root.leftChild.leftChild.rightChild.rightChild.value);
        assertEquals(nodes[1], tree.root.leftChild.rightChild.leftChild.leftChild.value);
        assertEquals(nodes[2], tree.root.leftChild.rightChild.leftChild.rightChild.value);
        assertEquals(nodes[7], tree.root.leftChild.rightChild.rightChild.leftChild.value);
        assertEquals(nodes[6], tree.root.leftChild.rightChild.rightChild.rightChild.value);
        assertEquals(nodes[10], tree.root.rightChild.leftChild.leftChild.leftChild.value);
        assertEquals(nodes[11], tree.root.rightChild.leftChild.leftChild.rightChild.value);
        assertEquals(nodes[12], tree.root.rightChild.leftChild.rightChild.leftChild.value);
        assertEquals(nodes[13], tree.root.rightChild.leftChild.rightChild.rightChild.value);
        assertEquals(nodes[8], tree.root.rightChild.rightChild.leftChild.leftChild.value);
        assertEquals(nodes[9], tree.root.rightChild.rightChild.leftChild.rightChild.value);
        assertEquals(nodes[14], tree.root.rightChild.rightChild.rightChild.leftChild.value);
        assertEquals(nodes[15], tree.root.rightChild.rightChild.rightChild.rightChild.value);
    }

    @Test
    void findSplitNode16Points(){
        Node[] nodes = getNodes16();

        KDTree<Node> tree = new KDTree<Node>(Node.class, nodes);

        Node split = tree.findSplitNode(tree.root, 0, 0, 1.5, 15).value;
        assertEquals(nodes[4], split);

        split = tree.findSplitNode(tree.root, 10, 0, 15, 7).value;
        assertEquals(nodes[13], split);

        split = tree.findSplitNode(tree.root, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).value;
        assertEquals(tree.root.value, split);
    }

    @Test
    void query16Points(){
        Node[] nodes = getNodes16();

        KDTree<Node> tree = new KDTree<Node>(Node.class, nodes);

        ArrayList<Node> result = tree.query(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        assertEquals(16, result.size());

        result = tree.query(0,0,1.5d,15d);
        assertTrue(result.contains(nodes[0]));
        assertTrue(result.contains(nodes[1]));

        assertTrue(tree.util.inRange(nodes[12], 10, 0, 15, 7));

        result = tree.query(10,0,15,7);
        assertTrue(result.contains(nodes[10]));
        assertTrue(result.contains(nodes[11]));
        assertTrue(result.contains(nodes[12]));
        assertTrue(result.contains(nodes[13]));
        assertTrue(result.contains(nodes[14]));
    }

    /*
    @Test
    void instantiateTree4Ways(){
        //remember that lat is written first despite being the 'y'-axis
        //also, remember that lat is negated since objects are drawn from the top-left corner

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

        Way w0 = new Line(al0, (byte)0, false);
        Way w1 = new Line(al1, (byte)0, false);
        Way w2 = new Line(al2, (byte)0, false);
        Way w3 = new Line(al3, (byte)0, false);

        Way[] ways = new Way[]{w3, w2, w1, w0};

        //tree = new KDTree<Way>(Way.class, ways);

        
    }


    @Test
    void instantiateTree16Ways(){
        //remember that lat is written first despite being the 'y'-axis
        //also, remember that lat is negated since objects are drawn from the top-left corner

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

        al0.add(new Node(6,0));
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
        al9.add(new Node(21,29));

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

        Way w0 = new Line(al0, (byte)0, false);
        Way w1 = new Line(al1, (byte)0, false);
        Way w2 = new Line(al2, (byte)0, false);
        Way w3 = new Line(al3, (byte)0, false);
        Way w4 = new Line(al4, (byte)0, false);
        Way w5 = new Line(al5, (byte)0, false);
        Way w6 = new Line(al6, (byte)0, false);
        Way w7 = new Line(al7, (byte)0, false);
        Way w8 = new Line(al8, (byte)0, false);
        Way w9 = new Line(al9, (byte)0, false);
        Way w10 = new Line(al10, (byte)0, false);
        Way w11 = new Line(al11, (byte)0, false);
        Way w12 = new Line(al12, (byte)0, false);
        Way w13 = new Line(al13, (byte)0, false);
        Way w14 = new Line(al14, (byte)0, false);
        Way w15 = new Line(al15, (byte)0, false);

        Way[] ways = new Way[]{w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15};

        tree = new KDTree(ways);
        assertEquals(w6, tree.root.value);

        assertEquals(w4, tree.root.leftChild.value);
        assertEquals(w15, tree.root.rightChild.value);

        assertEquals(w4, tree.root.leftChild.leftChild.value);
        assertEquals(w0, tree.root.leftChild.rightChild.value);
        assertEquals(w10, tree.root.rightChild.leftChild.value);
        assertEquals(w9, tree.root.rightChild.rightChild.value);

        assertEquals(w3, tree.root.leftChild.leftChild.leftChild.value);
        assertEquals(w14, tree.root.leftChild.leftChild.rightChild.value);
        assertEquals(w0, tree.root.leftChild.rightChild.leftChild.value);
        assertEquals(w5, tree.root.leftChild.rightChild.rightChild.value);
        assertEquals(w15, tree.root.rightChild.leftChild.leftChild.value);
        assertEquals(w12, tree.root.rightChild.leftChild.rightChild.value);
        assertEquals(w13, tree.root.rightChild.rightChild.leftChild.value);
        assertEquals(w8, tree.root.rightChild.rightChild.rightChild.value);

        assertEquals(w3, tree.root.leftChild.leftChild.leftChild.leftChild.value);
        assertEquals(w4, tree.root.leftChild.leftChild.leftChild.rightChild.value);
        assertEquals(w14, tree.root.leftChild.leftChild.rightChild.leftChild.value);
        assertEquals(w6, tree.root.leftChild.leftChild.rightChild.rightChild.value);
        assertEquals(w0, tree.root.leftChild.rightChild.leftChild.leftChild.value);
        assertEquals(w1, tree.root.leftChild.rightChild.leftChild.rightChild.value);
        assertEquals(w5, tree.root.leftChild.rightChild.rightChild.leftChild.value);
        assertEquals(w2, tree.root.leftChild.rightChild.rightChild.rightChild.value);
        assertEquals(w15, tree.root.rightChild.leftChild.leftChild.leftChild.value);
        assertEquals(w10, tree.root.rightChild.leftChild.leftChild.rightChild.value);
        assertEquals(w12, tree.root.rightChild.leftChild.rightChild.leftChild.value);
        assertEquals(w11, tree.root.rightChild.leftChild.rightChild.rightChild.value);
        assertEquals(w13, tree.root.rightChild.rightChild.leftChild.leftChild.value);
        assertEquals(w9, tree.root.rightChild.rightChild.leftChild.rightChild.value);
        assertEquals(w8, tree.root.rightChild.rightChild.rightChild.leftChild.value);
        assertEquals(w7, tree.root.rightChild.rightChild.rightChild.rightChild.value);
    }

    @Test
    void findSplitNode(){


        tree = new KDTree((Point[])ways);
        Point split = tree.findSplitNode(tree.root, 23, 22, 32, 11).value;

        System.out.println(split.getCoord(0));
        assertEquals(w9, split);
    }

    @Test
    void query2Ways(){
        ArrayList<Node> al0 = new ArrayList<>();
        ArrayList<Node> al1 = new ArrayList<>();

        al0.add(new Node(4, 0));
        al0.add(new Node(3, 2));

        al1.add(new Node(7, 1));
        al1.add(new Node(6, 5));

        Way w0 = new Line(al0, (byte)0, false);
        Way w1 = new Line(al1, (byte)0, false);

        Way[] ways = new Way[]{w0, w1};

        KDTree tree = new KDTree(ways);

        ArrayList<Way> q0 = tree.query(0, -2, 2, 0); // empty
        ArrayList<Way> q1 = tree.query(0, -4, 2, -3); // encapsulates w0
        ArrayList<Way> q2 = tree.query(0, -4, 1, -3); // covers one node of w0
        ArrayList<Way> q3 = tree.query(0.5d, -4, 1.5d, -3); // covers edge of w0
        ArrayList<Way> q4 = tree.query(0, -7, 5, -3); // encapsulates w0 and w1

        assertEquals(0, q0.size());
        assertEquals(1, q1.size());
        assertEquals(1, q2.size());
        assertEquals(1, q3.size());
        assertEquals(2, q4.size());
    }

    @Test
    void query4Ways(){

    }

    @Test
    void query16Ways() {
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

        al0.add(new Node(6,0));
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
        al9.add(new Node(21,29));

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

        Way w0 = new Line(al0, (byte)0, false);
        Way w1 = new Line(al1, (byte)0, false);
        Way w2 = new Line(al2, (byte)0, false);
        Way w3 = new Line(al3, (byte)0, false);
        Way w4 = new Line(al4, (byte)0, false);
        Way w5 = new Line(al5, (byte)0, false);
        Way w6 = new Line(al6, (byte)0, false);
        Way w7 = new Line(al7, (byte)0, false);
        Way w8 = new Line(al8, (byte)0, false);
        Way w9 = new Line(al9, (byte)0, false);
        Way w10 = new Line(al10, (byte)0, false);
        Way w11 = new Line(al11, (byte)0, false);
        Way w12 = new Line(al12, (byte)0, false);
        Way w13 = new Line(al13, (byte)0, false);
        Way w14 = new Line(al14, (byte)0, false);
        Way w15 = new Line(al15, (byte)0, false);

        Way[] ways = new Way[]{w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15};

        tree = new KDTree(ways);

        ArrayList<Way> q0 = tree.query(25, 30, -35, -25); // completely covers w12

        // completely covers w8, covers one node in w9, and covers part of edge in w7
        ArrayList<Way> q1 = tree.query(23, 32, -22, -1);

        // empty
        ArrayList<Way> q2 = tree.query(0, 2, -2, -0);

        assertEquals(1, q0.size());
        assertEquals(3, q1.size());
        assertEquals(0, q2.size());
    }
     */
}