package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.Node;
import com.example.mapofdenmark.help_class.mathhelp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    Node[] nodearray;
    EdgeWeightedDigraph graph;
    @BeforeEach
    void setUp() {
        EdgeWeightedDigraph Graph = new EdgeWeightedDigraph(20);
        //new Edge(Node source, Node target, double distance, boolean oneWay, byte condition, int speed)
        //lat y / lon x
        Node zero = new Node(5.0f,3.0f);
        Node two = new Node(3.0f,1.0f);
        Node one = new Node(1.0f,6.0f);
        Node three = new Node(1.0f,2.0f);
        Node four = new Node(3.0f,6.0f);
        Node five = new Node(5.0f,6.0f);
        Node six = new Node(7.0f,5.0f);
        Node seven = new Node(3.0f,9.0f);
        Node eight = new Node(7.0f,8.0f);
        Node nine = new Node(7.0f,6.0f);

        nodearray = new Node[20];
        nodearray[0] = zero;
        nodearray[1] = one;
        nodearray[2] = two;
        nodearray[3] = three;
        nodearray[4] = four;
        nodearray[5] = five;
        nodearray[6] = six;
        nodearray[7] = seven;
        nodearray[8] = eight;
        nodearray[9] = nine;

        for (int i = 0; i < 10; i++){
            nodearray[i].setIndex(i);
            nodearray[i].converter();
        }

        Graph.addEdge(new Edge(zero.lat, zero.lon, zero.index,one.lat, one.lon, one.index,3.6f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(zero.lat, zero.lon, zero.index,six.lat, six.lon, six.index,2.5f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(zero.lat, zero.lon, zero.index,five.lat, five.lon, five.index,4.0f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(zero.lat, zero.lon, zero.index,four.lat, four.lon, four.index,2.6f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(zero.lat, zero.lon, zero.index,two.lat, two.lon, two.index,2.5f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(four.lat, four.lon, four.index,five.lat, five.lon, five.index,2.5f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(two.lat, two.lon, two.index,three.lat, three.lon, three.index,2.0f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(one.lat, one.lon, one.index, two.lat, two.lon, two.index,3.9f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(seven.lat, seven.lon, seven.index, four.lat, four.lon, four.index,2.2f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(seven.lat, seven.lon, seven.index, one.lat, one.lon, one.index,3.0f,(byte)1, (byte) 2,10,0));
        Graph.addEdge(new Edge(five.lat, five.lat, five.index, nine.lat, nine.lon, nine.index, 2.0f, (byte)1, (byte) 2, 10,0));
        Graph.addEdge(new Edge(five.lat, five.lon, five.index, eight.lat, eight.lon, eight.index, 4.0f, (byte)1, (byte) 2, 10, 0));
        Graph.addEdge(new Edge(seven.lat, seven.lon, seven.index, eight.lat, eight.lon, eight.index, 2.92f, (byte)1, (byte) 2, 10,0));
        Graph.addEdge(new Edge(three.lat, three.lon, three.index, one.lat, one.lon,one.index, 2.9f, (byte)6, (byte) 2, 10,0));

        graph = Graph;
    }
    void expansion(){
        nodearray[10] = new Node(4, 13);
        nodearray[11] = new Node(6,11);
        nodearray[12] = new Node(9,7);
        nodearray[13] = new Node(5,10);
        nodearray[14] = new Node(1,10);
        nodearray[15] = new Node(2,12);
        nodearray[16] = new Node(8,3);
        for (int i = 10; i < 17; i++){
            nodearray[i].setIndex(i);
            nodearray[i].converter();
        }
        graph.addEdge(new Edge(nodearray[7].lat, nodearray[7].lon, nodearray[7].index,nodearray[13].lat, nodearray[13].lon ,nodearray[13].index,2.5f,(byte)6,(byte)2, 10, 0));
        graph.addEdge(new Edge(nodearray[13].lat, nodearray[13].lon, nodearray[13].index, nodearray[11].lat, nodearray[11].lon, nodearray[11].index, 1.5f, (byte)1, (byte)2, 10, 0));
        graph.addEdge(new Edge(nodearray[11].lat ,nodearray[11].lon, nodearray[11].index, nodearray[10].lat, nodearray[10].lon, nodearray[10].index, 3, (byte)1, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[14].lat, nodearray[14].lon, nodearray[14].index, nodearray[7].lat, nodearray[7].lon, nodearray[7].index, 4, (byte)1, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[15].lat, nodearray[15].lon, nodearray[15].index, nodearray[1].lat, nodearray[1].lon, nodearray[1].index, 4.4f, (byte)1, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[12].lat, nodearray[12].lon, nodearray[12].index, nodearray[9].lat, nodearray[9].lon, nodearray[9].index, 2, (byte)6, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[8].lat, nodearray[8].lon, nodearray[8].index, nodearray[12].lat, nodearray[12].lon, nodearray[12].index, 2, (byte)1, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[6].lat, nodearray[6].lon, nodearray[6].index, nodearray[16].lat, nodearray[16].lon, nodearray[16].index, 3, (byte)1, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[12].lat, nodearray[12].lon, nodearray[12].index, nodearray[16].lat, nodearray[16].lon, nodearray[16].index, 4, (byte)6, (byte)2, 10,0));
        graph.addEdge(new Edge(nodearray[7].lat, nodearray[7].lon, nodearray[7].index, nodearray[10].lat, nodearray[10].lon, nodearray[10].index, 9, (byte)1, (byte)2, 10,0));
    }
    void fancyGraph(){
        graph = null;
        graph = new EdgeWeightedDigraph(22);
        nodearray = new Node[22];
        nodearray[0] = new Node(-2,10);
        nodearray[1] = new Node(1,2);
        nodearray[2] = new Node(4,5);
        nodearray[3] = new Node(2,8);
        nodearray[4] = new Node(3,7);
        nodearray[5] = new Node(6,2);
        nodearray[6] = new Node(4,3);
        nodearray[7] = new Node(8,8);
        nodearray[8] = new Node(5,4);
        nodearray[9] = new Node(5,4);
        nodearray[10] = new Node(8,4);
        nodearray[11] = new Node(6,9);
        nodearray[12] = new Node(6,7);
        nodearray[13] = new Node(2,5);
        nodearray[14] = new Node(2,6);
        nodearray[15] = new Node(3.83f, 10.27f);
        nodearray[16] = new Node(0,4);
        nodearray[17] = new Node(-2,2);
        nodearray[18] = new Node(-2,6);
        nodearray[19] = new Node(-4,4);
        nodearray[20] = new Node(0,6);
        nodearray[21] = new Node(0,8);
        for (int i = 0; i < 22; i++){
            nodearray[i].setIndex(i);
            nodearray[i].converter();
        }
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[6].lat, nodearray[6].lon, nodearray[6].index, 3, (byte)1, (byte) 2, 10,0));
        graph.addEdge(new Edge(nodearray[6].lat, nodearray[6].lon, nodearray[6].index, nodearray[9].lat, nodearray[9].lon, nodearray[9].index, 1.5f, (byte)1, (byte) 2, 10,0));
        graph.addEdge(new Edge(nodearray[9].lat, nodearray[9].lon, nodearray[9].index, nodearray[8].lat, nodearray[8].lon, nodearray[8].index, 3.5f, (byte)1, (byte) 2, 10,0));
        graph.addEdge(new Edge(nodearray[8].lat, nodearray[8].lon, nodearray[8].index, nodearray[7].lat, nodearray[7].lon, nodearray[7].index, 3.6f, (byte)1, (byte) 2, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[2].lat, nodearray[2].lon, nodearray[2].index, 3.1f, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[2].lat,nodearray[2].lon, nodearray[2].index, nodearray[12].lat, nodearray[12].lon, nodearray[12].index, 2.1f, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[12].lat, nodearray[12].lon, nodearray[12].index, nodearray[7].lat, nodearray[7].lon, nodearray[7].index, 1.6f, (byte)1, (byte) 15, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[5].lat, nodearray[5].lon, nodearray[5].index, 4, (byte)1, (byte) 2, 5,0));
        graph.addEdge(new Edge(nodearray[5].lat, nodearray[5].lon, nodearray[5].index, nodearray[10].lat, nodearray[10].lon, nodearray[10].index, 2.1f, (byte)1, (byte) 2, 5,0));
        graph.addEdge(new Edge(nodearray[8].lat, nodearray[8].lon, nodearray[8].index, nodearray[10].lat, nodearray[10].lon, nodearray[10].index, 2.2f, (byte)1, (byte) 2, 5,0));
        graph.addEdge(new Edge(nodearray[1].lat,nodearray[1].lon, nodearray[1].index, nodearray[13].lat, nodearray[13].lon, nodearray[13].index, 2.3f, (byte)1, (byte) 15, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[7].lat, nodearray[7].lon, nodearray[7].index, 6.6f, (byte)1, (byte) 5, 10,0));
        graph.addEdge(new Edge(nodearray[13].lat, nodearray[13].lon, nodearray[13].index, nodearray[12].lat, nodearray[12].lon, nodearray[12].index, 4, (byte)1, (byte) 15, 10,0));
        graph.addEdge(new Edge(nodearray[4].lat, nodearray[4].lon,nodearray[4].index, nodearray[11].lat, nodearray[11].lon, nodearray[11].index, 2.6f, (byte)6, (byte) 5, 10,0));
        graph.addEdge(new Edge(nodearray[4].lat, nodearray[4].lon, nodearray[4].index, nodearray[3].lat, nodearray[3].lon, nodearray[3].index, 1.1f, (byte)1, (byte) 10, 10,0));
        graph.addEdge(new Edge(nodearray[3].lat, nodearray[3].lon, nodearray[3].index, nodearray[11].lat, nodearray[11].lon, nodearray[11].index, 3, (byte)1, (byte) 6, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[4].lat, nodearray[4].lon, nodearray[4].index, 4, (byte)1, (byte) 30, 10,0));
        graph.addEdge(new Edge(nodearray[4].lat, nodearray[4].lon,nodearray[4].index, nodearray[14].lat, nodearray[14].lon, nodearray[14].index, 1.1f, (byte)1, (byte) 15, 10,0));
        graph.addEdge(new Edge(nodearray[14].lat, nodearray[14].lon, nodearray[14].index, nodearray[3].lat, nodearray[3].lon, nodearray[3].index, 1.5f, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[3].lat, nodearray[3].lon, nodearray[3].index, nodearray[15].lat, nodearray[15].lon, nodearray[15].index, 2.5f, (byte)1, (byte) 5, 10,0));
        graph.addEdge(new Edge(nodearray[15].lat, nodearray[15].lon, nodearray[15].index, nodearray[11].lat, nodearray[11].lon, nodearray[11].index, 2.5f, (byte)1, (byte) 30, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[16].lat, nodearray[16].lon, nodearray[16].index, 2, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[19].lat, nodearray[19].lon, nodearray[19].index,nodearray[16].lat, nodearray[16].lon, nodearray[16].index,  4, (byte)6, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[16].lat, nodearray[16].lon, nodearray[16].index, nodearray[18].lat, nodearray[18].lon, nodearray[18].index, 2.5f, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[18].lat, nodearray[18].lon, nodearray[18].index, nodearray[19].lat, nodearray[19].lon, nodearray[19].index, 2.1f, (byte)1, (byte) 15, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[17].lat, nodearray[17].lon, nodearray[17].index, 3, (byte)1, (byte) 10, 10,0));
        graph.addEdge(new Edge(nodearray[19].lat, nodearray[19].lon, nodearray[19].index, nodearray[17].lat, nodearray[17].lon, nodearray[17].index, 2.1f, (byte)6, (byte) 5, 10,0));
        graph.addEdge(new Edge(nodearray[1].lat, nodearray[1].lon, nodearray[1].index, nodearray[20].lat, nodearray[20].lon, nodearray[20].index, 4.5f, (byte)1, (byte) 3, 10,0));
        graph.addEdge(new Edge(nodearray[20].lat, nodearray[20].lon, nodearray[20].index, nodearray[21].lat, nodearray[21].lon, nodearray[21].index, 2, (byte)1, (byte) 30, 10,0));
        graph.addEdge(new Edge(nodearray[21].lat, nodearray[21].lon, nodearray[21].index,nodearray[0].lat, nodearray[0].lon, nodearray[0].index, 2.1f, (byte)6, (byte) 30, 10,0));


    }

    void checkPathSimple(int[] list, int start, int end, byte condition){
        Dijkstra algo = new Dijkstra(graph, nodearray[start].index, nodearray[end].index, nodearray[end].lon, nodearray[end].lat,condition);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath();
        for (int i = 0; i < list.length-1; i++){
            Edge edge = path.pop();
            assertEquals(list[i], edge.getSource());
            assertEquals(list[i+1], edge.w());
        }
        assertTrue(path.isEmpty());
    }
    void checkPath(int[] list, int start, int end, byte condition){
        Dijkstra algo = new Dijkstra(graph, nodearray[start].index, nodearray[end].index, nodearray[end].lon, nodearray[end].lat,condition);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath();
        for (int i = 0; i < list.length-1; i=i+2){
            Edge edge = path.pop();
            assertEquals(list[i], edge.getSource());
            assertEquals(list[i+1], edge.w());
        }
        assertTrue(path.isEmpty());
    }    @Test
    void carOnetoSeven(){
        fancyGraph();
        int[] array = new int[]{1,6,9,8,7};
        checkPathSimple(array, 1,7,(byte)2);
    }
    @Test
    void bikeOneToSeven(){
        fancyGraph();
        int[] array = new int[]{1,2,12,7};
        checkPathSimple(array, 1, 7, (byte)3);
    }
    @Test
    void walkOneToSeven(){
        fancyGraph();
        int[] array = new int[]{1,7};
        checkPathSimple(array, 1, 7, (byte)5);
    }
    @Test
    void bikeOneToEleven(){
        fancyGraph();
        int[] array = new int[]{1,4,14,3,11};
        checkPathSimple(array, 1,11,(byte)3);
    }
    @Test
    void walkOneToEleven(){
        fancyGraph();;
        int[] array = new int[]{1,4,11};
        checkPathSimple(array,1,11,(byte)5);
    }
    @Test
    void bikeOneToNineteen(){
        fancyGraph();
        int[] array = new int[]{1,16,18,19};
        checkPathSimple(array, 1, 19, (byte)3);
    }
    @Test
    void walkOneToNineteen(){
        fancyGraph();
        int[] array = new int[]{1,17,19, 17};
        checkPath(array, 1,19,(byte)5);
    }
    @Test
    void bikeOneToZero(){
        fancyGraph();
        int[] array = new int[]{1,20,21,0};
        checkPathSimple(array,1,0,(byte)3);
    }

    @Test
    void finalPath3() {
        int[] array = new int[]{0,2,3};
        checkPathSimple(array, 0,3,(byte)2);
        /*Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[3],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[3]);

        Edge e = path.pop();
        Edge a = path.pop();

        assertTrue(path.isEmpty());
        assertEquals(0,e.getSource());
        assertEquals(2,e.w());
        assertEquals(2, a.getSource());
        assertEquals(3, a.w());*/

    }

    @Test
    void finalpath8(){
        /*Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[8],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[8]);
        Edge a = path.pop();
        Edge b = path.pop();
        Edge c = path.pop();

        assertTrue(path.isEmpty());
        assertEquals(0, a.getSource());
        assertEquals(4,a.w());
        assertEquals(4, b.w());
        assertEquals(7,b.getSource());
        assertEquals(7, c.getSource());
        assertEquals(8,c.w());*/
        int[] array = new int[]{0,4,7,4,7,8};
        checkPath(array, 0,8,(byte)2);
    }
    @Test
    void finalPath5(){
        /*Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[5],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[5]);
        Edge e = path.pop();
        assertTrue(path.isEmpty());
        assertEquals(0, e.getSource());
        assertEquals(5, e.w());*/
        int[] array = new int[]{0,5};
        checkPathSimple(array, 0,5,(byte)2);

    }
    @Test
    void finalPath9(){
        /*Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[9],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[9]);
        Edge a = path.pop();
        Edge b = path.pop();
        assertTrue(path.isEmpty());
        assertEquals(0, a.getSource());
        assertEquals(5, a.w());
        assertEquals(5, b.getSource());
        assertEquals(9, b.w());*/
        int[] array = new int[]{0,5,9};
        checkPathSimple(array, 0,9,(byte)2);
    }
    @Test
    void finalPath1to9(){
        /*Dijkstra algo = new Dijkstra(graph, nodearray[1].index, nodearray[9],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[9]);
        Edge a = path.pop();
        Edge b = path.pop();
        Edge c = path.pop();

        assertTrue(path.isEmpty());
        assertEquals(0, a.getSource());
        assertEquals(1,a.w());
        assertEquals(0, b.getSource());
        assertEquals(5, b.w());
        assertEquals(5, c.getSource());
        assertEquals(9, c.w());*/
        int[] array = new int[]{0,1,0,5,5,9};
        checkPath(array, 1,9,(byte)2);
        //assertEquals(8, algo.totalDistance());
    }
    @Test
    void finalPath10(){
        expansion();
        /*Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[10],(byte) 0);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath(nodearray[10]);
        Edge a = path.pop();
        Edge c = path.pop();
        Edge d = path.pop();
        Edge e = path.pop();
        Edge f = path.pop();
        assertTrue(path.isEmpty());
        //0-4, 7-4, 7-13, 13-11, 11-10
        assertEquals(0, a.getSource());
        assertEquals(4, a.w());
        assertEquals(7, c.getSource());
        assertEquals(4, c.w());
        assertEquals(7, d.getSource());
        assertEquals(13, d.w());
        assertEquals(13, e.getSource());
        assertEquals(11, e.w());
        assertEquals(11, f.getSource());
        assertEquals(10, f.w());
        assertEquals(11.5, algo.totalDistance());*/
        int[] array = new int[]{0,4,7,4,7,13,13,11,11,10};
        checkPath(array, 0,10,(byte)2);
        System.out.println(mathhelp.calculateDistance(nodearray[1].lat, nodearray[1].lon, nodearray[15].lat, nodearray[15].lon));
    }
    @Test
    void finalPathZeroToTwelve(){
        expansion();
        int[] array = new int[]{0,4,7,4,7,8,8,12};
        checkPath(array, 0,12,(byte)2);
    }
    @Test
    void finalPathToFifteen(){
        expansion();
        int[] array = new int[]{0,1,15,1};
        checkPath(array, 0, 15,(byte)2);
    }
    @Test
    void finalPathZeroToFourteen(){
        expansion();
        int[] array = new int[]{0,4,7,4,14,7};
        checkPath(array, 0,14,(byte)2);
    }
    @Test
    void finalPath8to9(){
        expansion();
        int[] array = new int[]{8,12,9};
        checkPathSimple(array, 8,9,(byte)2);
    }
    @Test
    void finalPath8to16(){
        expansion();
        int[] array = new int[]{8,12,16};
        checkPathSimple(array, 8,16,(byte)2);
    }
    @Test
    void finalPath14to16(){
        expansion();
        int[] array = new int[]{14,7,8,12,16};
        checkPathSimple(array, 14,16,(byte)2);
    }
    @Test
    void finalPath16to14(){
        expansion();
        int[] array = new int[]{6,16,0,6,0,4,7,4,14,7};
        checkPath(array, 16,14,(byte)2);
    }
    @Test
    void finalPath2to9(){
        expansion();
        int[] array = new int[]{0,2,0,5,5,9};
        checkPath(array, 2,9,(byte)2);
    }
    @Test
    void finalPath2to10(){
        expansion();
        int[] array = new int[]{1,2,7,1,7,13,13,11,11,10};
        checkPath(array, 2,10,(byte)2);
    }
    @Test
    void finalPath9to2(){
        expansion();
        int[] array = new int[]{5,9,0,5,0,2};
        checkPath(array, 9,2,(byte)2);
    }



    @Test
    void totalDistance3(){
        Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[3].index, nodearray[3].lon, nodearray[3].lat,(byte) 2);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath();
        assertEquals(4.5, algo.totalDistance());
    }
    @Test
    void totalDistance9(){
        Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[9].index, nodearray[9].lon, nodearray[9].lat,(byte) 2);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath();
        assertEquals(6, algo.totalDistance());
    }
    @Test
    void totalDistance5(){
        Dijkstra algo = new Dijkstra(graph, nodearray[0].index, nodearray[5].index, nodearray[5].lon, nodearray[5].lat,(byte) 2);
        Stack<Edge> path = (Stack<Edge>) algo.finalPath();
        assertEquals(4, algo.totalDistance());
    }
    /*@Test
    void graphSmall(){
        for (Edge edge : graph.edges()){
            System.out.println(edge.source.index+" " + edge.target.index + "       " +mathhelp.calculateDistance(edge.source.lat, edge.source.lon, edge.target.lat, edge.target.lon) );
            assertTrue(mathhelp.calculateDistance(edge.source.lat, edge.source.lon, edge.target.lat, edge.target.lon)<=edge.weight);
        }
    }
    @Test
    void graphMedium(){
        expansion();
        graphSmall();
    }
    @Test

    void graphLarger(){
        fancyGraph();
        graphSmall();
    }*/
    @Test
    void proofSpeed(){
        fancyGraph();
        for (int y=0; y < 100;y++ ){
            for (int i = 0; i < 22; i++){
                for (int x = 0; x < 22; x++){
                    if (x==i){
                        continue;
                    }
                    Dijkstra algo = new Dijkstra(graph, nodearray[i].index, nodearray[x].index, nodearray[x].lon, nodearray[x].lat,(byte)2);
                    Dijkstra algo1 = new Dijkstra(graph, nodearray[i].index, nodearray[x].index, nodearray[x].lon, nodearray[x].lat,(byte)3);
                    Dijkstra algo2 = new Dijkstra(graph, nodearray[i].index, nodearray[x].index, nodearray[x].lon, nodearray[x].lat,(byte)5);
                }
            }
        }

    }


    @AfterEach
    void tearDown() {
        graph = null;

    }
}