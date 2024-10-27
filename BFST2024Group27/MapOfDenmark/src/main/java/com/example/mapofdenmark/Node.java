package com.example.mapofdenmark;

import com.example.mapofdenmark.ST.Point;

import java.io.Serializable;

public class Node implements Serializable, Point {
    public float lat, lon;
    public int index;

    public Node(float lat, float lon) {
            this.lat = lat;
            this.lon = lon;
            this.index = -1;
    }


    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
    public void setIndex(int i){
        index = i;
    }



    @Override
    public float getCoord(int c) {
        return switch (c) {
            case 0 -> lon;
            case 1 -> lat;
            default ->
                    throw new IllegalArgumentException("The coordinate corresponding to value " + c + " is not recognized!");
        };
    }

    @Override
    public float getMidX() {
        return lon*0.56f;
    }

    @Override
    public float getMidY() {
        return -lat;
    }

    //ONLY FOR TEST
    public void converter(){
        lat = 55.1250864f+(0.0891281f*(lat/14));
        lon = 14.8340821f+(0.1558471f*(lon/14));
    }

}