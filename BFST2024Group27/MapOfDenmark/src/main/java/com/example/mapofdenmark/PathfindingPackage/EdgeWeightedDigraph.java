package com.example.mapofdenmark.PathfindingPackage;

import com.example.mapofdenmark.help_class.Bag;
import java.io.Serializable;

public class EdgeWeightedDigraph implements Serializable {
    private int V; // number of vertices
    private int E; // number of edges
    private Bag<Edge>[] adj; // adjacency lists
    /**represents a Digraph as an array with bags, where each bag consists of the edges
     * the source(index place of array) points towards/targets*/
    public EdgeWeightedDigraph(int V)
    {
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        //we store all the vertices in an empty array, or rather an array with empty bags
        for (int v = 0; v < V; v++)
            adj[v] = new Bag<>();
    }

    /**returns the total number of Vertices*/
    public int getV() { return V; }



    /**ads an edge to Digraph*/
    public void addEdge(Edge e)
    {
        //since this is technically an undirected graph, the edge is adjacent to both vertices
        adj[e.getSource()].add(e);
            adj[e.w()].add(e);

        E++;
    }

    /**returns all adjacent edges to v*/
    public Iterable<Edge> adj(int v)
    { return adj[v]; }
}
