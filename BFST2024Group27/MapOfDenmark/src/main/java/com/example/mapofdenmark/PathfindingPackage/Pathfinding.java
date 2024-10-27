package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.Model;
import com.example.mapofdenmark.DrawnObject;
import com.example.mapofdenmark.Pathway;
import com.example.mapofdenmark.ST.RTree;
import com.example.mapofdenmark.help_class.mathhelp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;


public class Pathfinding implements Serializable {
    EdgeWeightedDigraph G;
    RTree primary;
    RTree secondary;
    Dijkstra dijkstra;
    float[] pointAA;
    float[] pointBB;
    float[] intersectionA;
    float[] intersectionB;
    //int[] index = new int[2];

    public Pathfinding(EdgeWeightedDigraph G,  RTree primary, RTree secondary){
        this.G = G;
        this.primary = primary;
        this.secondary = secondary;
    }

    /**
     * Searches from {@code pointA} to {@code pointB} and returns the best path.
     *
     * @param condition Determines which mode of transportation is being used.
     * @param pointA    Starting node that contains starting point.
     * @param pointB    Ending node that contains ending point.
     * @return The best path.
     * once the nearest nodes in the Graph has been found, we turn Dijkstra algorithm from
     * a stack to an array of edges which will be used to draw the route on the map
     */
    public Edge[] helpPathfinding(float[] pointA, float[] pointB, byte condition){
        intersectionA=pointA;
        intersectionB =pointB;
        //help for pathfinding
        //find the closest vertex/intersection node
        float[] source = search(pointA, condition);
        float[] target = search(pointB, condition);
        pointAA =source;
        pointBB = target;

        if (source == null){
            return null;
        }
        if (target == null){
            return null;
        }

        //finds the path
        dijkstra = new Dijkstra(G, (int)source[2]-1, (int)target[2]-1, target[1], target[0], condition);
        //return the path as an array
        Stack<Edge> stack = dijkstra.finalPath();
        if (stack==null){
            return null;
        }else{
            return getPathToArray(stack);
        }
    }

    /**Returns the total distance of the route being drawn in Dijkstra including the distance
     * between the intended point and intersection point.*/
    public float getDistance(){
        float start = mathhelp.calculateDistance(pointAA[0],pointAA[1],intersectionA[0],intersectionA[1]);
        float end = mathhelp.calculateDistance(pointBB[0],pointBB[1],intersectionB[0],intersectionB[1]);
        return dijkstra.totalDistance()+ start+end;
    }

    /**
     * Returns the time of the route.
     */
    public String getTime(){
        //make in hours and minutes instead of decimal numbers
        float start = mathhelp.calculateDistance(pointAA[0],pointAA[1],intersectionA[0],intersectionA[1]);
        float end = mathhelp.calculateDistance(pointBB[0],pointBB[1],intersectionB[0],intersectionB[1]);
        float totalTime = dijkstra.totalTime() + ((start+end)/4.5f);
        int round = (int)totalTime;
        float minutes = (totalTime-round)*60;
        String string;
        if (minutes<10){
            string = round + ":" + "0"+String.valueOf(minutes).charAt(0) + " Hours";
        }else{
            string=round + ":" + String.valueOf(minutes).substring(0,2) + " Hours";
        }
        dijkstra = null;
        intersectionA=null;
        intersectionB=null;
        pointAA=null;
        pointBB=null;
        if (round<10){
            return "0"+string;
        }else{
            return string;
        }
    }

    /**
     * Converts the stack of edges to an array of edges
     */

    public Edge[] getPathToArray(Stack<Edge> stack){
        Edge[] array = new Edge[stack.size()];
            for (int i =0; i < array.length;i++) {
                array[i] = stack.pop();
            }
        return array;
    }

    /** We search for the nearest vertex node/point in our graph, by using an RTree of primary and secondary pathways
     * if we do not find a nearest neighbor node, we double the search field until a node is found
     * specifically we draw a square around the node and search for intersection nodes,
     * within the field by using makeSquare() and searchInSquare()
     * @return the nearest intersection node (a node existing within the "drawn" graph)
     * */
    public float[] search(float[] node, byte condition){
        float lon = node[1];
        float lat = node[0];
        int k = 1;

        float[] points = makeSquare(lon,lat, k);
        //0 = min x, 1 = miny, 2 =max x, 3 max y
        ArrayList<DrawnObject> tempprimary = primary.query(points[0],points[2],points[1],points[3]);
        ArrayList<DrawnObject> tempsecondary = secondary.query(points[0],points[2],points[1],points[3]);

        float[] vertexnodeprim = searchInSquare(convert(tempprimary),node, condition);
        float[] vertexnodesec = searchInSquare(convert(tempsecondary),node, condition);

        while(vertexnodeprim == null && vertexnodesec == null){
            k= k*2;
            if (k == Integer.MAX_VALUE || k <= 0 ){
                break;
            }
            points = makeSquare(lon,lat, k);
            tempprimary = primary.query(points[0],points[2],points[1],points[3]);
            tempsecondary = secondary.query(points[0],points[2],points[1],points[3]);
            vertexnodeprim = searchInSquare( convert(tempprimary), node, condition);
            vertexnodesec = searchInSquare(convert(tempsecondary), node, condition);
        }
        //nodes have been found, 3 cases:
        if (!(vertexnodeprim == null) && vertexnodesec == null) {
            return vertexnodeprim;
        } else if (vertexnodeprim == null && !(vertexnodesec == null)){
            return vertexnodesec;
        } else{
            vertexnodeprim =  searchInSquare( convert(tempprimary), node, condition);
            vertexnodesec = searchInSquare(convert(tempsecondary), node, condition);
            //finds the nearest node
            if (mathhelp.calculateDistance(vertexnodeprim[0], vertexnodeprim[1], node[0], node[1])<
                    mathhelp.calculateDistance(vertexnodesec[0], vertexnodesec[1], node[0], node[1])){
                return vertexnodeprim;
            } else {
                return vertexnodesec;
            }
        }
    }

    /**
     * @param lon the x coordinate
     * @param lat the y coordinate
     * @param k the integer that determines how big we make the square/ search field
     * @return an array with coordinates that if drawn, draws a square around the inserted coorinates*/
    public float[] makeSquare(float lon, float lat, int k){
        //note that the square is being made on the lat/lon that is used for map, in order
        //to use r tree efficiently

        //The square is calculated using the math from this stackoverflow thread:
        //https://stackoverflow.com/questions/15258078/latitude-longitude-and-meters
        //calculating square:
        float upperCornerNorth = lat + (0.0000449f*k); //yes //max y
        float leftCornerSouth = lat - (0.0000449f*k); //yes //min y
        float leftCornerWest =  lon - (0.0000449f*k)/(float)Math.cos(lat); // max x
        float upperCornerEast =  lon + (0.0000449f*k)/(float)Math.cos(lat); //min y

        float[] coordinates = new float[4];
        //0 = min x, 1 = miny, 2 =max x, 3 max y
        coordinates[0]= leftCornerWest;
        coordinates[1]= leftCornerSouth;
        coordinates[2]= upperCornerEast;
        coordinates[3]= upperCornerNorth;
        return coordinates;
    }
    /**coneverts the
     * @param oldlist to an arraylist consisting only of Pathway objects*/
    public ArrayList<Pathway> convert(ArrayList<DrawnObject> oldlist){
        ArrayList<Pathway> updated = new ArrayList<>();
        for (DrawnObject e: oldlist){
            if (e instanceof Pathway){
                updated.add((Pathway) e);
            }
        }
        return updated;
    }

    /** finds the nearest intersection point in
     * @param pathways by comparing the shortest distance to
     * @param target with the help of calculateDistance()
     * @param condition ensures that we find an intersection node in a traversable pathway
     * Specifically, we find the shortest distance by going through all distances
     * while keeping track of the shortest distance
     * */
    public float[] searchInSquare(ArrayList<Pathway> pathways, float[] target, byte condition){

        // go through the points in array list and return nearest neighbor
        float[] smallest = null;
        float distance;
        float min = (float)Double.POSITIVE_INFINITY;
        float[] temp;

        if (pathways.size() == 1 && pathways.get(0).getCondition() % condition == 0 ){
            //only one possibility
            return getSmallest(pathways.get(0).getCoords(false), target, pathways.get(0).getIntersection() );

        } else if (pathways.size() > 1){
            //find nearest node
            for (Pathway way: pathways){
                temp = getSmallest(way.getCoords(false), target, way.getIntersection());
                if (way.getCondition() %condition == 0) {
                    distance = mathhelp.calculateDistance(target[0], target[1], temp[0], temp[1]);
                    if (distance < min) {
                        min = distance;
                        smallest = temp;
                    }
                }

            }
            return smallest;
        } else{
            //no nodes were found when the square was made
            return null;
        }

    }
    /**returns the closest intersection to the intended point*/
    private float[] getSmallest(float[] points, float[] target, int[] intersection){
        float[] array = new float[3];
        float distance = (float)Double.POSITIVE_INFINITY;
        float temp;
        for (int i = 0; i < points.length; i=i+2){
            if (Model.index.get(intersection[i/2])>0) {
                temp = mathhelp.calculateDistance(points[i], points[i+1], target[0], target[1]);

                if (temp < distance) {
                    distance = temp;
                    array[0] = points[i];
                    array[1] = points[i+1];
                    array[2] = Model.index.get(intersection[i/2]);
                }
            }
        }
        return array;
    }

}
