package com.example.mapofdenmark;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.mapofdenmark.help_class.ColorLoader.color_line_converter;

public class Line extends Way implements Serializable {


    public Line(ArrayList<Node> way, byte type) {
        super(way, type);
    }
    /**
     * Checks the type and category, then draws the line of nodes,
     * with specific colors and width, depending on the result
     * @param gc The graphixcontext that the drawing is made on
     */
    @Override
    public void draw(GraphicsContext gc, View view) {
        if (!view.road.isSelected()) return;
        gc.beginPath();

        gc.moveTo(coords[1]*0.56f, -coords[0]);

        for (int i = 2; i < coords.length; i=i+2) {
            gc.lineTo(coords[i+1]*0.56f, -coords[i]);
        }

        gc.setStroke(color_line_converter[type].getColor());
        gc.setLineWidth(color_line_converter[type].getWidth());
        gc.stroke();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.00005);

    }


}



