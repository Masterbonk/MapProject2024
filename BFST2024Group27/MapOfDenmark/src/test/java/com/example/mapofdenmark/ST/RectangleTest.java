package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Line;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.Way;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    @Test
    void testIntersects() {
        Rectangle r1 = new Rectangle(0, 1, 0, 1);
        Rectangle r2 = new Rectangle(2, 3, 2, 3);

        assertFalse(r1.intersects(r2));
        assertEquals(r1.intersects(r2), r2.intersects(r1));

        Rectangle r3 = new Rectangle(0, 1, 0, 1);
        Rectangle r4 = new Rectangle(1, 2, 0, 1);

        assertTrue(r3.intersects(r4));
        assertEquals(r3.intersects(r4), r3.intersects(r4));

        Rectangle r5 = new Rectangle(0, 3, 0, 3);
        Rectangle r6 = new Rectangle(1, 2, 1, 2);

        assertTrue(r5.intersects(r6));
        assertEquals(r5.intersects(r6), r5.intersects(r6));
    }

    @Test
    void testArea() {
        Rectangle r = new Rectangle(0, 1, 0, 1);
        assertEquals(1, r.area());

        r = new Rectangle(0, 2, 0, 2);
        assertEquals(4, r.area());

        r = new Rectangle(-2, 1, 0, 1);
        assertEquals(3, r.area());
    }

    @Test
    void calculateUnionEnlargement(){
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

        double result = Rectangle.calculateUnionEnlargement(w0, w3);
        assertTrue(5.03d <= result);
        assertTrue(result <= 5.05d);
    }
}