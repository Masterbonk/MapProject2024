package com.example.mapofdenmark.help_class;


import java.io.Serializable;

public class PriorityQueue implements Serializable {
    private int[] index;//location of a vertex in the queue - used so values can be changed. Everything is off by one, so if index[i]==2, then we treat is as if it was 1
    private int[] keyLocation;//used to figure out which vertex is in a certain index in the priority queue
    private float[] weights;//change this to vertices and then use weight
    private int N;

/** makes a priorityQueue with length MaxN
 *  weights stores the weight from min to max, starting at index 1 = weights[1]
 *  index stores the index spot of the vertex in the keys array, for example
 *  keyLocation represents which vertex is in an index of the priority queue, so keyLocation[1] = 2 means that edge 2 is in weights[2]
 *  vertex 0, with weight 5, is stored as index[0] = 3 (3-1=2), because weights[2] = 5
 *
 *  based on the IndexMin Priority Queue from "Algorithms Fourth Edition" by Robert Sedgewick and Kevin Wayne
 *  **/
    public PriorityQueue(int maxN) {
        weights = new float[maxN + 1];
        index = new int[maxN];//size is the amount of vertices
        keyLocation = new int[maxN + 1];
        N = 0;
    }
/** determines whether the integer k exists inside the queue
 * specifically if the index spot for the integer is never updated, we know it does not exist
 * since k=0**/
    public boolean contains(int k) {
        return index[k] != 0;
    }
/**inserts the weighted vertex in this case, inserting the weight in keys, and inserting the index of keys in index and inserts the vertex number into the keyLocation array
 * Swim makes sure that the priorityQueue is still prioritized **/
    public void insert(int k, float key) {
        weights[++N] = key;
        keyLocation[N] = k;
        index[k] = N+1;//needs to be off by one
        swim(N);
    }
    /** as long as k is bigger than one and k/2 is smaller than k,
     * we exchange k with k/2
     * through each iteration we update k
     * once our while loop has finished we have updated our array so that it is still prioritized after size (min)
     * we divide with 2 since our data is stored in a heap, we therefore know that the parent of node k is at k/2
     * makes the integer k, swim up the heap, until the "maximum range" is reached
     * */
    public void swim(int k) {
        while (k > 1 && less(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }
    /**We swap i and j:
     * we store the index value of both keys and index
     * we then set the values of i to j
     * afterward we used the stored data to update j to i's data
     * index[] and keyLocation[] is also updated
     **/

    public void exch(int i, int j) {
        float temp = weights[i];
        int tempLocation = keyLocation[i];
        weights[i] = weights[j];
        keyLocation[i] = keyLocation[j];
        index[keyLocation[i]]=i+1;//the index of the vertex (keyLocation[i]) is updated to the new index that it was moved to (j)
        weights[j] = temp;
        keyLocation[j] = tempLocation;
        index[keyLocation[j]]=j+1;

    }

    /** BEHAVES LIKE SWIM, BUT MOVES IN OPPOSITE DIRECTION
     * */
    public void sink(int k){
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && less(j, j+1)) j++;
            if (!less(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    /** returns whether i is smaller/less than j in size.
     * Returns true if j is smaller than i
     * If true is returned, then that means that i and j needs to switch around, since i needs to be smaller**/
    public boolean less(int i, int j){
         return (weights[i]>weights[j]);
    }

    /**returns the smallest weight*/
    public float min(){
        return weights[1];
    }

    /**safely deletes the smallest weight in the heap/priorityQueue
     * */
    public int delMin(){
        int min = keyLocation[1];
        exch(1, N--);
        index[min] = 0;//since the edge is removed, the value is changed to 0
        sink(1);
        weights[N+1] = 0.0f;
        keyLocation[N+1] = -1;
        return min;//return the previous min edge
    }
    /**simply changes the value of a key, determined by k
     since we can't know if the new value is bigger or smaller, we run both swim and sink
     * */
    public void change(int k, float item){
        weights[index[k]-1] = item;
        swim(index[k]-1);
        sink(index[k]-1);

    }
    public boolean isEmpty(){
        return N == 0;
    }

    //ONLY USED FOR TESTING
    public float keys(int i){
        return weights[i];
    }
    public int edgeLocation(int i ){
        return keyLocation[i];
    }
    public int index(int i){
        return index[i];
    }
    public int getN(){
        return N;
    }
}