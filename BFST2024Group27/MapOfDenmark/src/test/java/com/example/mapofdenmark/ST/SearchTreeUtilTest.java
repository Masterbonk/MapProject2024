package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SearchTreeUtilTest {

    @Test
    void getEdge(){
        Node n0 = new Node(0, -1);
        Node n1 = new Node(0, 2);
        Node n2 = new Node(-3, 0);
        Node n3 = new Node(4, 0);

        ArrayList<Node> al = new ArrayList<>();
        al.add(n0);
        al.add(n1);
        al.add(n2);
        al.add(n3);
        Way w = new Line(al,(byte)0);

        assertEquals(-1*0.56f, w.getEdge(0));
        assertEquals(-4, w.getEdge(1));
        assertEquals(2*0.56f, w.getEdge(2));
        assertEquals(3, w.getEdge(3));
    }

    @Test
    void mergeSort() {
        //remember that lat is written first despite being the 'y'-axis
        //also, remember that lat is negated since objects are drawn from the top-left corner
        Node n0 = new Node(4, 0);
        Node n1 = new Node(3, 2);

        Node n2 = new Node(7, 1);
        Node n3 = new Node(6, 5);

        Node n4 = new Node(0, 3);
        Node n5 = new Node(5, 4);

        Node n6 = new Node(1, 6);
        Node n7 = new Node(2, 7);

        ArrayList<Node> al0 = new ArrayList<>();
        ArrayList<Node> al1 = new ArrayList<>();
        ArrayList<Node> al2 = new ArrayList<>();
        ArrayList<Node> al3 = new ArrayList<>();

        al0.add(n0);
        al0.add(n1);

        al1.add(n2);
        al1.add(n3);

        al2.add(n4);
        al2.add(n5);

        al3.add(n6);
        al3.add(n7);

        Way w0 = new Line(al0,(byte)0);
        Way w1 = new Line(al1,(byte)0);
        Way w2 = new Line(al2,(byte)0);
        Way w3 = new Line(al3,(byte)0);

        Way[] ways = new Way[]{w3, w2, w1, w0};

        Way[] exp0 = new Way[]{w0, w1, w2, w3};
        Way[] exp1 = new Way[]{w1, w2, w0, w3};
        Way[] exp2 = new Way[]{w0, w2, w1, w3};
        Way[] exp3 = new Way[]{w1, w0, w3, w2};

        SearchTreeUtil.sort(ways, 0);
        //System.out.println("Most west: " + ways[0].getEdge(0));
        assertArrayEquals(exp0, ways);

        SearchTreeUtil.sort(ways, 1);
        //System.out.println("Most east: " + ways[0].getEdge(1));
        assertArrayEquals(exp1, ways);

        SearchTreeUtil.sort(ways, 2);
        //System.out.println("Most south: " + ways[0].getEdge(2));
        assertArrayEquals(exp2, ways);

        SearchTreeUtil.sort(ways, 3);
        //System.out.println("Most north: " + ways[0].getEdge(3));
        assertArrayEquals(exp3, ways);
    }

    @Test
    void inRange(){
        ArrayList<Node> al0 = new ArrayList<>(); // point inside
        ArrayList<Node> al1 = new ArrayList<>(); // point outside
        ArrayList<Node> al2 = new ArrayList<>(); // multiple points - all inside
        ArrayList<Node> al3 = new ArrayList<>(); // multiple points - some inside, some outside
        ArrayList<Node> al4 = new ArrayList<>(); // line going through
        ArrayList<Node> al5 = new ArrayList<>(); // line going through with point inside
        ArrayList<Node> al6 = new ArrayList<>(); // box around

        al0.add(new Node(5,5));
        /*
        al1.add(new Node(-1, -1));

        al2.add(new Node(0, 0));
        al2.add(new Node(2,2));
        al2.add(new Node(10, 10));

        al3.add(new Node(5, 8));
        al3.add(new Node(8,12));
        al3.add(new Node(7,13));

        al4.add(new Node(5, -1));
        al4.add(new Node(5, 11));

        al5.add(new Node(5, -1));
        al5.add(new Node(5, 5));
        al5.add(new Node(5, 11));

        al6.add(new Node(-1, -1));
        al6.add(new Node(-1, 11));
        al6.add(new Node(11, 11));
        al6.add(new Node(11, -1));

         */

        Way w0 = new Line(al0,(byte)0);
        /*
        Way w1 = new Line(al1, (byte)0, false, false, (byte)0);
        Way w2 = new Line(al2, (byte)0, false, false, (byte)0);
        Way w3 = new Line(al3, (byte)0, false, false, (byte)0);
        Way w4 = new Line(al4, (byte)0, false, false, (byte)0);
        Way w5 = new Line(al5, (byte)0, false, false, (byte)0);
        Way w6 = new Line(al6, (byte)0, false, false, (byte)0);

         */

        assertTrue(SearchTreeUtil.inRange(w0, 0, -10, 10, 0));
        /*
        assertFalse(SearchTreeUtil.inRange(w1, 0, -10, 10, 0));
        assertTrue(SearchTreeUtil.inRange(w2, 0, -10, 10, 0));
        assertTrue(SearchTreeUtil.inRange(w3, 0, -10, 10, 0));
        assertTrue(SearchTreeUtil.inRange(w4, 0, -10, 10, 0));
        assertTrue(SearchTreeUtil.inRange(w5, 0, -10, 10, 0));
        assertTrue(SearchTreeUtil.inRange(w6, 0, -10, 10, 0));
         */
    }
}