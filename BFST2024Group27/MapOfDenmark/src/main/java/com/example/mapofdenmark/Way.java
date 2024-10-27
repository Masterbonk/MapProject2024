package com.example.mapofdenmark;

import com.example.mapofdenmark.PathfindingPackage.EdgeWeightedDigraph;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Way extends DrawnObject implements Serializable {
    float[] coords;

    /**
     * Loads a node array containing all the nodes in the way in order of how it is read
     *
     * @param way The arraylist of nodes that the way is made of.
     */
    public Way(ArrayList<Node> way, byte type) {
        this.type = type;
        coords = new float[way.size()*2];
        int x = 0;
        for (Node node : way){
            coords[x] = node.lat;
            coords[x+1] = node.lon;
            x+=2;
        }

        minX = coords[1]*0.56f;
        maxX = coords[1]*0.56f;
        minY = -coords[0];
        maxY = -coords[0];

        for (int i = 2; i < coords.length; i=i+2) {
            if (coords[i+1]*0.56f < minX) {
                minX = coords[i+1]*0.56f;
            } else if (coords[i+1]*0.56f > maxX) {
                maxX = coords[i+1]*0.56f;
            }

            if (-coords[i] < minY) {
                minY = -coords[i];
            } else if (-coords[i] > maxY) {
                maxY = -coords[i];
            }
        }
    }

    /**
     * Returns the coodinate array, but checkes whether it has to be reversed first
     *
     * @param isReversed The boolean deciding whether it should be reversed in this instance, false it is not reversed
     * @return The coordinate array in the form of nodes.
     */
    public float[] getCoords(boolean isReversed) {
        if (!isReversed) {
            return coords;
        } else {
            float[] temp = new float[coords.length];
            for (int i = 0; i < coords.length; i=i+2) {
                temp[i] = coords[coords.length - 2 - i];
                temp[i+1] = coords[coords.length - 1 - i];
            }

            return temp;
        }
    }

    public void addingEdgesToGraph(EdgeWeightedDigraph graph, int way) {
        Model.id2way.delete(way);
    }

    /**
     * Gets the start coordinates, gets end coordinates instead if it has to be reversed
     *
     * @param reversed The boolean determining whether you get the start or end coords.
     * @return An array of size 2, with index 0 being x, and index 1 being y
     */
    public float[] getStartCoords(boolean reversed) {
        if (!reversed) {
            return new float[]{coords[1]*0.56f, -coords[0]};
        } else {
            return new float[]{coords[coords.length - 1]*0.56f, -coords[coords.length - 2]};
        }
    }

    /**
     * Gets the end coordinates, gets start coordinates instead if it has to be reversed
     *
     * @param reversed The boolean determining whether you get the start or end coords.
     * @return An array of size 2, with index 0 being x, and index 1 being y
     */
    public float[] getEndCoords(boolean reversed) {
        if (!reversed) {
            return new float[]{coords[coords.length - 1]*0.56f, -coords[coords.length - 2]};
        } else {
            return new float[]{coords[1]*0.56f, -coords[0]};
        }
    }


}
