package com.example.mapofdenmark;

import java.util.*;
import java.io.Serializable;

/**
 *  This Ternary Search Tree is based on the implementation from:
 *  Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TST implements Serializable
{
    private Vertex root; // root of trie
    public static class Vertex implements Serializable
    {
        char c; // character for the Vertex
        Vertex left, mid, right; // left, middle, and right subtries

        float lat; // Latitude values for addresses
        float lon; // Longitude values for addresses
    }

    /**
    * Fetches a given element from the search tree
    * @param key The given search string to retrieve
     */
    public Vertex get(String key){
        return get(root, key, 0);
    }
    private Vertex get(Vertex x, String key, int d) { //Follows the given key one char at a time all the way down the tree and returns the vertex at the final char if it exists
        if (x == null) return null;
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1)
            return get(x.mid, key, d+1);
        else return x;
    }

    /**
     * Inserts a string into the ternary search tree
     * @param key The given string to insert
     * @param lat The latitude value to save at the end of the inserted string
     * @param lon The longitude value to save at the end of the inserted string
     */
    public void put(String key, float lat, float lon) { // Calls the put statement below makes that statement start at the root of the tree
        root = put(root, key,  lat,  lon, 0);
    }
    private Vertex put(Vertex x, String key, float lat, float lon, int d) { //Navigates the tree through each char in the key and for any missing char it inserts a vertex with the corresponding char and a null value, on the final char of the key the value is inserted in Vertex.val
        char c = key.charAt(d);
        if (x == null) { x = new Vertex(); x.c = c; }
        if (c < x.c) x.left = put(x.left, key, lat, lon, d);
        else if (c > x.c) x.right = put(x.right, key, lat, lon, d);
        else if (d < key.length() - 1)
            x.mid = put(x.mid, key, lat, lon, d+1);
        else {
            x.lat = lat;
            x.lon = lon;
        }
        return x;
    }

    /**
     * Autocompletes a search given a string
     * @param pre The search that has been typed previously
     * @param results The amount of results wanted to collect
     * @return Returns a List of results
     */
    public List<String> autocomplete(String pre, int results) // Calls the collect statement below and starts at the root of the tree
    {
        List<String> q = new ArrayList<>(); //Queue for collecting any hit in the searches
        collect(get(root, pre, 0), pre, q, results,true);
        return q;
    }
    private void collect(Vertex x, String pre, List<String> q, int results, boolean first) { //Travels through the tree starting where the pre String ends
        if (q.size() < results) {
            if (x == null) return;
            if (first) { // boolean first is used so the first char that is traveled over does not get picked up, as this would create 2x of the last character unintentionally
                if (x.mid != null) collect(x.mid, pre, q, results, false); // Travels down in the tree without collecting the current char
            } else {
                if (x.left != null) collect(x.left, pre, q, results, false); // Travels to the left in the tree
                if (x.right != null) collect(x.right, pre, q, results, false); // Travels to the right in the tree
                if (x.mid != null) collect(x.mid, pre + x.c, q, results, false); // Travels downwards in the tree and picks up the character on the current vertex
            }
            if (first) { // boolean first is used so a duplicate character is not added in case the initial String pre is a successful hit
                if (x.lat != 0f) q.add(pre); // Adds the pre string to the queue
            } else {
                if (x.lat != 0f) q.add(pre + x.c); // Adds the assembled pre string to the queue and adds the current nodes character as this has not been picked up yet
            }
        }
    }
}
