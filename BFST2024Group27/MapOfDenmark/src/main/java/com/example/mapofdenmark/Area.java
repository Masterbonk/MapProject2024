package com.example.mapofdenmark;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;

import static com.example.mapofdenmark.help_class.ColorLoader.color_area_converter;

public class Area extends Way implements Serializable {

    /**
     * Constructs the area
     * @param way The arraylist of nodes, that the area is made of.
     * @param type The int value which is turned to a byte value (max size 127), determines the color of the area.
     * */
    public Area(ArrayList<Node> way, byte type){
        super(way,type);
    }

    GraphicsContext gc = null;
    View view = null;

    /**
     *  Calls choseColor() to determine the color of the polygon, then draws the polygon.
     * @param gc The graphicsContext the area is drawn on
     * */
    @Override
    public void draw(GraphicsContext gc, View view) {
        this.gc = gc;
        this.view = view;
        double[] x_coords = new double[(coords.length/2)+1];
        double[] y_coords = new double[(coords.length/2)+1];
        for (int i = 0; i< coords.length;i=i+2){
            x_coords[i/2] = coords[i+1]*0.56f;
            y_coords[i/2] = -coords[i];
        }


        gc.setFill(color_area_converter[type]);
        gc.fillPolygon(x_coords,y_coords, coords.length/2);
        gc.setFill(Color.WHITE);
    }

    public void draw(Color c) {
        if(gc == null ||view == null) return;

        double[] x_coords = new double[(coords.length/2)+1];
        double[] y_coords = new double[(coords.length/2)+1];
        for (int i = 0; i< coords.length;i=i+2){
            x_coords[i/2] = coords[i+1]*0.56f;
            y_coords[i/2] = -coords[i];
        }


        gc.setFill(c);
        gc.fillPolygon(x_coords,y_coords, coords.length/2);
        gc.setFill(Color.WHITE);
    }
}
