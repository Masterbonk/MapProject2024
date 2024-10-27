package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.Model;
import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.PathfindingPackage.Edge;
import com.example.mapofdenmark.PathfindingPackage.EdgeWeightedDigraph;
import com.example.mapofdenmark.PathfindingPackage.Pathfinding;
import com.example.mapofdenmark.Pathway;
import com.example.mapofdenmark.ST.KDTree;
import com.example.mapofdenmark.ST.RTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PathfindingTest {
    Pathfinding pathfinding;
    EdgeWeightedDigraph graph;
    RTree prime;
    RTree sec;
    Set<Node> intersectionNodes = new HashSet<>();

    @BeforeEach
    void SetUp(){

        Model.index = new ArrayList<>();
        Model.indexCounter = 1;
        ArrayList<Node> e1 = new ArrayList<>();

        //intersection
        Node E1 = new Node(55.176179288540204F, 14.910964564076686F);
        Node E2 = new Node(55.167151155020115F, 14.910652568404666F);
        Node E3 = new Node(55.157170312689836F, 14.91928444866388F);
        Node E4 = new Node(55.14778128852701F, 14.943828108196104F);
        //intersection
        Node E5 = new Node(55.15045560275043F, 14.95641193363424F);
        e1.add(E1);
        e1.add(E2);
        e1.add(E3);
        e1.add(E4);
        e1.add(E5);

        ArrayList<Node> T5 = new ArrayList<>();
        //intersection
        Node T = new Node(55.155090656101514F, 14.93956416734517F);
        Node T2 = E5;
        T5.add(T);
        T5.add(T2);

        ArrayList<Node> x = new ArrayList<>();
        Node x1 = E1;
        //intersection
        Node x2 = new Node(55.173803662162605F, 14.932700262560733F);
        x.add(x1);
        x.add(x2);

        ArrayList<Node> u = new ArrayList<>();
        Node u1 = x2;
        Node u2 = new Node(55.16317099951053F, 14.93353225101945F);
        Node u3 = T;
        u.add(u1);
        u.add(u2);
        u.add(u3);

        ArrayList<Node> y = new ArrayList<>();
        Node y1 = x2;
        //intersection
        Node y2 = new Node(55.18882711472072F, 14.951627999996601F);
        y.add(y1);
        y.add(y2);

        ArrayList<Node> Å = new ArrayList<>();
        Node å1 = E1;
        Node å2 = new Node(55.18556162857404F, 14.926980341907035F);
        Node å3 = y2;
        Å.add(å1);
        Å.add(å2);
        Å.add(å3);


        //2=car, 3=bike, 5=walk
        Pathway E = new Pathway(e1,(byte) 3,(byte)1,(byte) 30, (short) 30);
        Pathway Tway = new Pathway(T5,(byte) 3,(byte)1,(byte) 30, (short) 30);
        Pathway U = new Pathway(u,(byte) 3,(byte)1,(byte) 2, (short) 30);
        Pathway X = new Pathway(x,(byte) 3,(byte)1,(byte) 15, (short) 30);
        Pathway Y = new Pathway(y,(byte) 3,(byte)1,(byte) 5, (short) 30);
        Pathway ÅÅ = new Pathway(Å,(byte) 3,(byte)1,(byte) 6, (short) 30);

        EdgeWeightedDigraph bettergraph = new EdgeWeightedDigraph(5);
        E.addingEdgesToGraph(bettergraph,1);
        Tway.addingEdgesToGraph(bettergraph,1);
        U.addingEdgesToGraph(bettergraph,1);
        X.addingEdgesToGraph(bettergraph,1);
        Y.addingEdgesToGraph(bettergraph,1);
        ÅÅ.addingEdgesToGraph(bettergraph,1);

        prime = new RTree(3);
        prime.insert(E);
        prime.insert(Tway);
        prime.insert(U);
        prime.insert(X);
        sec = new RTree(3);
        sec.insert(ÅÅ);
        sec.insert(Y);

        pathfinding = new Pathfinding(bettergraph,prime,sec);

    }


    @Test
    void updatedtest1(){

        float[] a = new float[2];
        a[0] = 55.178614158635504f;
        a[1] = 14.893180810771556f;

        float[] b = new float[2];
        b[0] = 55.194051335856564f;
        b[1] = 14.947780053375025f;

        //55.17588441127293, 14.91108557092948

        float[] intersection = pathfinding.search(a,(byte)30);
        assertEquals(intersection[0],55.176179288540204F);
        assertEquals(intersection[1],14.910964564076686F);

        Edge[] test = pathfinding.helpPathfinding(a,b, (byte) 5);
        assertEquals(test.length,2);

    }

    @Test
    void updatedtestcars(){

        float[] a = new float[2];
        a[0] = 55.178614158635504F;
        a[1] = 14.893180810771556F;
        float[] b = new float[2];
        b[0] = 55.194051335856564F;
        b[1] = 14.947780053375025F;


        Edge[] test = pathfinding.helpPathfinding(a,b, (byte) 2);
        assertEquals(test.length,1);
    }
    @Test
    void updatedtestbikes(){

        float[] a = new float[2];
        a[0] = 55.178614158635504F;
        a[1] = 14.893180810771556F;
        float[] b = new float[2];
        //55.16322731726706, 14.937727354452887
        b[0] = 55.16322731726706F;
        b[1] = 14.937727354452887F;

        Edge[] test = pathfinding.helpPathfinding(a,b, (byte) 3);

        assertEquals(test.length,2);
    }

    @Test
    void updatedtestwalk(){

        float[] a = new float[2];
        a[0] = 55.178614158635504F;
        a[1] = 14.893180810771556F;
        float[] b = new float[2];
        b[0] = 55.16322731726706F;
        b[1] = 14.937727354452887F;

        Edge[] test = pathfinding.helpPathfinding(a,b, (byte) 5);
        assertEquals(test.length,2);
    }

    //make correct square
    @Test
    void Pathfinding(){
        float[] coordinates = pathfinding.makeSquare(55.65907961789666f,12.616199708793909f,2);
        for (int i = 0; i < 4; i++){

        }

        float[] coordinates1 = pathfinding.makeSquare(55.65907961789666f,12.616199708793909f,4);

        assertNotEquals(coordinates[0], coordinates1[0]);
    }

    @Test
    void squaretest1(){
        float[] coordinates = pathfinding.makeSquare(55.65907961789666f,12.616199708793909f,1);

        assertEquals(55.659034729003906, coordinates[0]);
        assertEquals(12.616154670715332, coordinates[1]);
        assertEquals(55.65912628173828, coordinates[2]);
        assertEquals(12.616244316101074, coordinates[3]);
    }

    @Test
    void squaretest2(){
        Node roskilde = new Node(55.62622643076454f, 12.056594343167303f);

        float[] points = pathfinding.makeSquare(roskilde.lon,roskilde.lat,1);
        //0 = min x, 1 = miny, 2 =max x, 3 max y

        //System.out.println(points[0] + " " + points[1] + ", "+points[2] + " " + points[3] );

        assertTrue(points[0] < roskilde.lon);
        assertTrue(points[1] < roskilde.lat);
        assertTrue(points[2] > roskilde.lon);
        assertTrue(points[3] > roskilde.lat);

    }
    @Test
    void squareTestBiggerNumber(){
        Node roskilde = new Node(55.62622643076454f, 12.056594343167303f);

        float[] points = pathfinding.makeSquare(roskilde.lon,roskilde.lat,1000);
        //0 = min x, 1 = miny, 2 =max x, 3 max y

        //System.out.println(points[0] + " " + points[1] + ", "+points[2] + " " + points[3] );

        assertTrue(points[0] < roskilde.lon);
        assertTrue(points[1] < roskilde.lat);
        assertTrue(points[2] > roskilde.lon);
        assertTrue(points[3] > roskilde.lat);

    }

    @AfterEach
    void teardown(){
        intersectionNodes = null;
        graph = null;
        prime = new RTree(3);
        sec= new RTree(3);
        Model.index = null;
        pathfinding = null;
    }


}