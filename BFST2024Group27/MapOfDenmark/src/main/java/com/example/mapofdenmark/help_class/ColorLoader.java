package com.example.mapofdenmark.help_class;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class ColorLoader implements Serializable {

    public static LineStats[] color_line_converter;
    public static Color[] color_area_converter;

    /**
     * Determines the color of an area
     */
    public static void createAreaColor(){
        color_area_converter = new Color[17];

        color_area_converter[0] = Color.rgb(198, 229, 159); // Base color
        color_area_converter[1] = Color.rgb(91, 201, 52); // Grass
        color_area_converter[2] = Color.rgb(126, 117, 76); //Construction
        color_area_converter[3] = Color.rgb(254, 212, 212); //Commercial
        color_area_converter[4] = Color.rgb(240, 220, 236); //Industry
        color_area_converter[5] = Color.rgb(164, 248, 113); //Allotsments
        color_area_converter[6] = Color.rgb(139, 196, 110); //Farmland
        color_area_converter[7] = Color.rgb(236, 212, 180); //Farmyard
        color_area_converter[8] = Color.rgb(62, 136, 22); //Forest
        color_area_converter[9] = Color.rgb(110, 211, 0); //General plant stuf
        color_area_converter[10] = Color.rgb(92, 191, 210); //Water stuff
        color_area_converter[11] = Color.rgb(52,52,52); //building
        color_area_converter[12] = Color.rgb(29,131,0); //grassland
        color_area_converter[13] = Color.rgb(23,114,17); //wood ie. forest
        color_area_converter[14] = Color.rgb(67,97,166); //water areas that is natural
        color_area_converter[15] = Color.rgb(190,182,88); //Sand or dessert
        color_area_converter[16] = Color.TRANSPARENT; //Transparent
    }


    /**
     * Determines the color of an area
     */
    public static void createLineColor(){
        color_line_converter = new LineStats[12];
        color_line_converter[0] = new LineStats(Color.rgb(119,111,111), 0.00005); //base color 0
        color_line_converter[1] = new LineStats(Color.rgb(92,102,128), 0.00002); //cycleway 1
        color_line_converter[2] = new LineStats(Color.rgb(78,82,90), 0.00008); //motorway 2
        color_line_converter[3] = new LineStats(Color.rgb(103,112,124), 0.00010); //Primary 3
        color_line_converter[4] = new LineStats(Color.rgb(123,127,129), 0.00007); //Secondary 4
        color_line_converter[5] = new LineStats(Color.rgb(133,127,139), 0.00005); //tertiary 5
        color_line_converter[6] = new LineStats(Color.rgb(86,94,97), 0.00005); //path/onlywalk 6
        color_line_converter[7] = new LineStats(Color.rgb(133,127,139), 0.00008); //trunk 7
        color_line_converter[8] = new LineStats(Color.rgb(68,72,80), 0.00008); //corridor
        color_line_converter[9] = new LineStats(Color.rgb(68,72,80), 0.00008); //crossing
        color_line_converter[10] = new LineStats(Color.rgb(157,185,255), 0.00009); //Raceway

    }

    public static class LineStats implements Serializable{
        Color color;
        double width;

        LineStats(Color color, double width){
            this.color = color;
            this.width = width;
        }

        public Color getColor() {
            return color;
        }

        public double getWidth() {
            return width;
        }
    }


}
