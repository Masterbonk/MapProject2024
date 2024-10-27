package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.Way;
import com.example.mapofdenmark.help_class.RedBlackBSTInteger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;


public class Edge implements Serializable {
    //node class

    protected final float sourceLat;
    protected final float sourceLon;
    protected final int sourceIndex;
    protected final float targetLat;
    protected final float targetLon;
    protected final int targetIndex;
    protected final float weight; // edge weight
    int carSpeed;
    byte oneWay;
    byte condition;//which mode of transport can traverse over the edge
    int way;

    /**
     * makes a weighted directed edge from the source to the target
     * @param distance the total distance of the edge
     * @param oneWay whether the edge is oneWay or not
     * @param condition a byte that represents which modes of transport can traverse over it
     * @param sourceLat and
     * @param sourceLon is the starting vertex of the edge
     * @param targetLat and
     * @param targetLon is the endpoint of the edge
     * @param sourceIndex is the index of the starting vertex
     * @param targetIndex is the index of th end vertex
     * @param speed determines max speed for cars to traverse the edge
     * @param way is a reference to the specific way the edge is made upon, which allows us to draw the edges later
     */
    public Edge(float sourceLat, float sourceLon, int sourceIndex, float targetLat, float targetLon, int targetIndex, float distance, byte oneWay, byte condition, int speed, int way)
    {
        this.sourceLat = sourceLat;
        this.sourceLon = sourceLon;
        this.sourceIndex = sourceIndex;
        this.targetLat = targetLat;
        this.targetLon = targetLon;
        this.targetIndex = targetIndex;
        this.oneWay = oneWay;
        this.condition = condition;
        weight = distance;
        carSpeed = speed;
        this.way = way;
    }
    /**returns the weight of the edge*/
    public float weight(byte mode){
        if (mode==2){//car
            return weight/carSpeed;
        }else if (mode==3){//bike
            return weight/16;
        }else{//walk
            return weight/4.5f;
        }
    }
    public float distance(){
        return weight;
    }
    /**returns the source of the edge*/
    public int getSource()
    { return sourceIndex; }
    /**returns the target the edge*/
    public int w()
    { return targetIndex; }
    public float getLon (int n){
        if (n==sourceIndex){
            return sourceLon;
        }else{
            return targetLon;
        }
    }
    public float getLat(int n){
        if (n==sourceIndex){
            return sourceLat;
        }else{
            return targetLat;
        }
    }

    /**
     *
     * @param source the vertex that has already been identified
     * @return the other vertex in the edge - the one that is not identified
     */
    public int other(int source){
        if (source==this.sourceIndex){
            return targetIndex;
        }
        return this.sourceIndex;
    }
    /**@return the condition that indicates which vehicles may traverse the edge*/
    public byte getCondition(){
        return condition;
    }
    /**draws the edge's ways on the map*/
    public void drawEdge(RedBlackBSTInteger<Way> map, GraphicsContext gc, float width){
        gc.beginPath();
        float[] array = map.get(way).getCoords(false);
        int index = 0;
        for (int i = 0; i < array.length; i=i+2){
            if (array[i]==sourceLat && array[i+1]==sourceLon){
                index = i;
                gc.moveTo(array[i+1]*0.56f, -array[i]);
                gc.setStroke(Color.DARKMAGENTA);
                gc.setLineWidth(width);
                break;
            }
        }
        for (int i = index+2; i< array.length; i=i+2){
            gc.lineTo(array[i+1]*0.56, -array[i]);
            if (array[i]==targetLat && array[i+1]==targetLon){
                gc.stroke();
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(0.00005);
                break;
            }
        }
    }
}
