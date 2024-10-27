package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.help_class.mathhelp;
import com.example.mapofdenmark.help_class.PriorityQueue;

import java.io.Serializable;
import java.util.Stack;

public class Dijkstra implements Serializable {
    private final Edge[] edgeTo;

    private final float[] timeTo; //time it takes to travel to the point from the source
    private PriorityQueue pq;
    private final int target;
    private final byte condition; // mode of transport
    private final float targetLon;
    private final float targetLat;
    private float totalDistance;


    /**
     * This is actually A* algorithm, used to find the fastest path for pathfinding.
     * The algorithm runs as soon as the object is created, and then afterwards methods can be used to get the total distance and time
     * @param G graph
     * @param s source vertex index
     * @param target target vertex index
     * @param targetLon longitude of target
     * @param targetLat latitute of target
     * @param condition which mode of transportation is used. 2==car, 3==bike, 5==walk
     *
     * based on the Dijkstra algorithm from "Algorithms Fourth Edition" by Robert Sedgewick and Kevin Wayne
     */
    public Dijkstra(EdgeWeightedDigraph G, int s, int target, float targetLon, float targetLat,  byte condition)
    {
        edgeTo = new Edge[G.getV()];
        timeTo = new float[G.getV()];
        pq = new PriorityQueue(G.getV());
        this.condition = condition;
        this.target = target;
        this.targetLon = targetLon;
        this.targetLat = targetLat;
        totalDistance = 0;
        //shortest time it takes to get to each vertex starts off as infinity at the beginning
        for (int v = 0; v < G.getV(); v++) {
            timeTo[v] = (float) Double.POSITIVE_INFINITY;
        }
        timeTo[s] = 0.0f;
        pq.insert(s, 0.0f);
        while (!pq.isEmpty()){
            int current = pq.delMin();

            if (current==target){//when target is found, then stops
                break;
            }
            relax(G, current);
        }
    }
    /** finds the path from the source in G to the target v
     *  keeps track of shortest path that has been found until now in G
     * */
    private void relax(EdgeWeightedDigraph G, int v)
    {
        for(Edge edge : G.adj(v))
        {
            int w = edge.other(v);
            if (determineCondition(edge, w)){
                if (timeTo[w] > timeTo[v] + edge.weight(condition))
                {
                    timeTo[w] = timeTo[v] + edge.weight(condition);
                    edgeTo[w] = edge;
                    float priority = timeTo[w] + h(w, edge); //(maybe h(w, target) instead), h(w,v)
                    if (pq.contains(w)) pq.change(w, priority);//priority
                    else pq.insert(w, priority);//priority
                }
            }
        }

    }

    /**
     *Used to determine if an edge can be traversed or not depending on the condition stored inside the edge
     *
     * @param e The edge that is being looked at
     * @param w the vertex that is supposed to be pointing outwards from the source
     * @return whether the edge can be traversed or not
     */
    public boolean determineCondition(Edge e, int w){
        //0=car, 1=bike, 2=walk

        //if the mode of transport is walking, then as long as the edge allows walking, then the edge can be traversed
        if (condition==5){//the mode of transport that is being used
            //the condition is composed of prime factors, so if the condition is divisible by a certain prime factor, then that means that a certain vehicle can traverse over it
            return e.getCondition() % 5 == 0;
        }else if (condition==2){
            if (e.getCondition()%2==0){
                if (e.oneWay%2==0&&e.oneWay>0){
                    return w == e.w();//a oneway edge can only traverse over car if the vertex pointing away from the source is the same as the vertex stored in the edge as w()
                }else if (e.oneWay%2==0) {
                    return w == e.sourceIndex;//opposite direction as the one given
                }else{
                    return true;//if the edge is not oneway, then it does not matter, and as long as the condition is divisible by 2, then it can be traversed
                }

            }
        }else if (condition==3){
            //same logic as cars
            if (e.getCondition()%3==0){
                if ((e.oneWay%3==0&&e.oneWay<0)||e.oneWay%5==0){//%5 means that bikes go the opposite way as the rest of the way
                    return w == e.sourceIndex;//opposite as the one given
                }else if (e.oneWay%3==0){
                    return w==e.w();}//a oneway edge can only traverse over car if the vertex pointing away from the source is the same as the vertex stored in the edge as w()
                else{
                    return true;//if the edge is not oneway, then it does not matter, and as long as the condition is divisible by 2, then it can be traversed
                }


            }
        }
        return false;
    }

    /**@return the path from source s to target as a stack*/
    public Stack<Edge> finalPath(){
        if (!(timeTo[target] < Double.POSITIVE_INFINITY)) return null;
        Stack<Edge> path = new Stack<>();
        int current = target;
        for (Edge e = edgeTo[current]; e != null; e = edgeTo[current]){
            path.push(e);
            current = e.other(current);
            totalDistance+=e.distance();
        }
        return path;
    }

    /**
     * Calculates the heuristic for A* by using the haversine formula to find the lowest estimate
     * @param i index of the current vertex we are on
     * @param e edge being looked at
     * @return the fastest possible estimate from the current vertex to the target vertex
     */
    private float h(int i, Edge e){
        float speed;
        if (condition==2){//speed depends on mode of transport used
            speed = 130f;
        }else if (condition==3){
            speed = 16f;
        }else{
            speed = 4.5f;
        }
        return (mathhelp.calculateDistance(targetLat, targetLon, e.getLat(i), e.getLon(i)))/speed;
    }

    /**@return the final distance of the route that has been found*/
    public float totalDistance(){
        return totalDistance;
    }
    /** @return the total time */
    public float totalTime(){
        return timeTo[target];
    }

}
