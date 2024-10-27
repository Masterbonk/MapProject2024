package com.example.mapofdenmark.ST;

import java.io.Serializable;

public class KDTreeUtil<T extends Point> implements Serializable {
    /**
     * Sorts the given array of ways based on one of 4 instance variables using Merge Sort. Sorts based on Way.getEdge(depth).
     *
     * @param arr   The array of ways to be sorted
     * @param depth Used to determine which instance variable to base the sort on
     */
    public void sort(T[] arr, int depth) {
        sort(arr, 0, arr.length - 1, depth);
    }

    // Main function that sorts arr[l..r] using
    // merge()
    void sort(T[] arr, int l, int r, int depth) {
        if (l < r) {

            // Find the middle point
            int m = l + (r - l) / 2;

            // Sort first and second halves
            sort(arr, l, m, depth);
            sort(arr, m + 1, r, depth);

            // Merge the sorted halves
            merge(arr, l, m, r, depth);
        }
    }

    void merge(Point[] arr, int l, int m, int r, int depth) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        // Create temp arrays
        Point[] L = new Point[n1];
        Point[] R = new Point[n2];

        // Copy data to temp arrays
        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        // Merge the temp arrays

        // Initial indices of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i].getCoord(depth) <= R[j].getCoord(depth)) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of L[] if any
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        // Copy remaining elements of R[] if any
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    /**
     * Returns true if the box surrounding the way and the box formed by the range overlap or intersect.
     * This does not ensure that there are any edges between nodes in the path inside the range.
     *
     * @param t
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @return a boolean signifying whether the way is within a given range.
     */
    public boolean inRange(T t, double minX, double minY, double maxX, double maxY) {
        double x = t.getCoord(0);
        double y = t.getCoord(1);

        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }
}
