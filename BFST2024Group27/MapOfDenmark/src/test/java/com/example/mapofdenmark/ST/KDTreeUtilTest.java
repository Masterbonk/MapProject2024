package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KDTreeUtilTest {
    @Test
    void inRange(){

        KDTreeUtil<Node> util = new KDTreeUtil<Node>();
        Node n = new Node(5,5);

        assertTrue(util.inRange(n, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertTrue(util.inRange(n, 4,4,6,6));
        assertFalse(util.inRange(n, 0, 0, 1, 1));
    }
}