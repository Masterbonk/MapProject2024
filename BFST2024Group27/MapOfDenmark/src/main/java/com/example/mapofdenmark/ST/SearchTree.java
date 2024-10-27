package com.example.mapofdenmark.ST;

import com.example.mapofdenmark.Way;

import java.io.Serializable;
import java.util.ArrayList;

public interface SearchTree<T extends Point> {
    public ArrayList<T> query(double minX, double maxX, double minY, double maxY);
}
