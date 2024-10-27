package com.example.mapofdenmark;

import com.example.mapofdenmark.help_class.mathhelp;
import com.example.mapofdenmark.PathfindingPackage.Edge;
import com.example.mapofdenmark.PathfindingPackage.EdgeWeightedDigraph;

import java.io.Serializable;
import java.util.ArrayList;

public class Pathway extends Line implements Serializable {
    byte condition;//which vehicles can traverse
    byte direction;//if it is one way or not
    short speed;
    int[] intersection;

    /**
     * Constructs the line
     * @param type The byte corresponding to what type inside the category it is
     *
     * @param direction - if a way is one way or not
     * @param condition - a byte that represents which vehicles can traverse a line
     * */

    public Pathway(ArrayList<Node> wayList, byte type, byte direction, byte condition, short speed) {
    //2=car, 3=bike, 5=walk
        super(wayList, type);
        this.condition = condition;
        this.direction = direction;
        this.speed = speed;
        intersection = new int[wayList.size()];
        Node node;
        for (int i = 0; i < wayList.size(); i++){
            node = wayList.get(i);

            if (node.index==-1){
                intersection[i] = Model.index.size();
                node.index = Model.index.size();
                Model.index.add(0);
            }else if ((Model.index.get(node.index)==0)){
                intersection[i]=node.index;
                Model.index.set(node.index,Model.indexCounter++);
            }else{
                intersection[i] = node.index;
            }
        }
        node = wayList.get(0);
        if (Model.index.get(node.index)==0){
            Model.index.set(node.index,Model.indexCounter++);
        }else{
            intersection[0] = node.index;
        }
        node = wayList.get(wayList.size()-1);
        if (Model.index.get(node.index)==0){
            Model.index.set(node.index,Model.indexCounter++);
        }else{
            intersection[wayList.size()-1] = node.index;
        }
    }
    public byte getCondition(){
        return condition;
    }



    /**
     * Used to split lines up into edges for the graph used for pathfinding
     *
     * @param graph the graph where the edges are added to
     */

    public void addingEdgesToGraph(EdgeWeightedDigraph graph, int way){
        float weight = 0;//?
        int previous = 0;//assume start and end points are intersections
        for (int i = 2; i < coords.length; i=i+2) {
            weight += mathhelp.calculateDistance(coords[i - 2], coords[i - 1], coords[i], coords[i+1]);//calculates distance between each node to calculate total distance
            if (Model.index.get(intersection[i/2]) > 0) {//if a node has multiple lines that intersect it
                graph.addEdge(new Edge(coords[previous], coords[previous+1], Model.index.get(intersection[previous/2])-1, coords[i], coords[i+1], Model.index.get(intersection[i/2])-1,weight, direction, condition, speed, way));
                weight = 0;//weight gets reset
                previous = i;//next edge starts where this edge ends
            }
        }
    }



    public int[] getIntersection(){
        return intersection;
    }
}
