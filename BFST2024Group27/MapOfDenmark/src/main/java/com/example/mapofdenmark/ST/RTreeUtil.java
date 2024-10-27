package com.example.mapofdenmark.ST;


import java.io.Serializable;
import java.util.ArrayList;

public class RTreeUtil implements Serializable {

    /**
     * Recursively sorts an ArrayList of RNodes between the indices {@code l} and {@code r} for a given depth {@code depth}.
     * @param list The ArrayList to be sorted.
     * @param l The lowest index of the subarray to be sorted (inclusive).
     * @param r The highest index of the subarray to be sorted (exclusive).
     * @param depth The depth determines which instance field the nodes will be sorted on. For {@code depth} = 0, RNode.getMidX() is used. For {@code depth} = 1, RNode.getMidY() is used.
     */
    public static void sort(ArrayList<RNode> list, int l, int r, int depth) {
        if (l < r) {

            // Find the middle point
            int m = l + (r - l) / 2;

            // Sort first and second halves
            sort(list, l, m, depth);
            sort(list, m + 1, r, depth);

            // Merge the sorted halves
            merge(list, l, m, r, depth);
        }
    }

    private static void merge(ArrayList<RNode> list, int l, int m, int r, int depth) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        // Create temp arrays
        RNode[] L = new RNode[n1];
        RNode[] R = new RNode[n2];

        // Copy data to temp arrays
        for (int i = 0; i < n1; ++i)
            L[i] = list.get(l + i);
        for (int j = 0; j < n2; ++j)
            R[j] = list.get(m + 1 + j);

        // Merge the temp arrays

        // Initial indices of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (depth == 0 && L[i].getMidX() <= R[j].getMidX() || depth == 1 && L[i].getMidY() <= R[j].getMidY()) { //L[i].getEdge(depth) <= R[j].getEdge(depth)) {
                list.set(k, L[i]); // arr[k] = L[i];
                i++;
            } else {
                list.set(k, R[j]); //arr[k] = R[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of L[] if any
        while (i < n1) {
            list.set(k, L[i]); //arr[k] = L[i];
            i++;
            k++;
        }

        // Copy remaining elements of R[] if any
        while (j < n2) {
            list.set(k, R[j]); //arr[k] = R[j];
            j++;
            k++;
        }
    }
}
