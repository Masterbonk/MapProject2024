package com.example.mapofdenmark;

import com.example.mapofdenmark.help_class.WayRef;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.shape.FillRule;

import static com.example.mapofdenmark.Model.id2way;
import static com.example.mapofdenmark.help_class.ColorLoader.color_area_converter;

public class ComplicatedArea extends DrawnObject implements Serializable {
    float[][] coords;

    /**
     * Creates a series of refrences to ways, then uses create groups to split them in a series of polygons.
     * Then lastly the varaibles x_coords and y_coords is filled with the right cordinates using fillCoords.
     *
     * @param ref ArrayList of refrences to ways, that the relation is made of.
     * @param areaType The type of area, determines which group the Relation is part of, and the color.
     */
    ComplicatedArea(ArrayList<Integer> ref, byte areaType){
        this.type = areaType;

        ArrayList<WayRef> array = new ArrayList<>();

        for (int i = 0; i < ref.size(); i++) {
            if(id2way.containsKey(ref.get(i))) {
                array.add(new WayRef(ref.get(i)));
            }
        }

        WayRef[][] list = createGroups(array);
        fillCoords(list);

        minX = coords[0][1]*0.56f;
        maxX = coords[0][1]*0.56f;
        minY = -coords[0][0];
        maxY = -coords[0][0];
        for (int i = 0; i < coords.length; i++) {
            for (int j = 2 ; j < coords[i].length ; j=j+2) {
                if(coords[i][j+1]*0.56f < minX){
                    minX = coords[i][j+1]*0.56f;
                }
                else if (coords[i][j+1]*0.56f > maxX){
                    maxX = coords[i][j+1]*0.56f;
                }

                if(-coords[i][j] < minY){
                    minY = -coords[i][j];
                }
                else if (-coords[i][j] > maxY){
                    maxY = -coords[i][j];
                }
            }
        }
    }

    /**
     * Sorts the input array, into groups, determined by whether these ways are connected or not.
     * @param array The arrayList of WayRef that is checked for groups.
     * @return A double array of WayRef, Sorted into groups, with the first array being the groups, and the second being a reference to a WayRef
     */
    public WayRef[][] createGroups(ArrayList<WayRef> array){
        ArrayList<WayRef> tempArray = new ArrayList<>();
        ArrayList<ArrayList<WayRef>> resultingArray = new ArrayList<>();

        ArrayList<Integer> integers_skipped = new ArrayList<>();

        boolean[] marked = new boolean[array.size()];
        for (boolean b: marked) {
            b = false;
        }


        int former1 = 0;
        int former2 = 0;

        float[] startCoord = new float[2];
        for(int i = 0; i< array.size(); i++) {
            if (!marked[i]) {

                boolean last = false;
                if (i == array.size() - 1) {
                    last = true;
                }

                byte connection = 0;
                if (i > 0) { //If it is not the first way we check
                    connection = checkConnected(array.get(former1), array.get(i));
                }
                if (tempArray.isEmpty()) { //The first way is added to it's own group
                    tempArray.add(array.get(i));
                    marked[i] = true;
                    if(array.size() > i+1){
                        byte temp = checkOwnConnection(array.get(i+1), array.get(i));
                        if(temp == 2){
                            array.get(i).setReverse(true);
                        }
                    }

                    startCoord = array.get(i).getRefWay().getEndCoords(array.get(i).isReverse());
                } else if (connection > 0) { //If above 0 a connection is found.
                    if (connection == 2) { //If result is 2, the current way needs to be reversed
                        array.get(i).setReverse(true);
                    }
                    tempArray.add(array.get(i));
                    former2 = former1;
                    former1 = i;
                    marked[i] = true;
                    if (last) {
                        if (!Arrays.equals(array.get(i).getRefWay().getEndCoords(array.get(i).isReverse()), startCoord)) {
                            boolean found_nothing = true;
                            for (int j = 0; j < array.size(); j++) {
                                if (former1 != j && former2 != j) {
                                    byte check = checkConnected(array.get(former1), array.get(j));
                                    if (check > 0) {
                                        if (!marked[j]) {
                                            if (j != 0) {
                                                if (check == 2) { //If result is 2, the current way needs to be reversed
                                                    array.get(i).setReverse(true);
                                                }
                                                marked[j] = true;

                                                found_nothing = false;

                                                if (i != 0) {
                                                    former2 = former1;
                                                    former1 = j;
                                                } else {
                                                    former1 = 0;
                                                    former2 = 0;
                                                }
                                                i = j;

                                                tempArray.add(array.get(i));
                                                if (integers_skipped.contains(j)) {
                                                    integers_skipped.remove(integers_skipped.indexOf(j));
                                                } else integers_skipped.add(i);
                                                break;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                } else { //If the way is not connected to the last one, we create a new group, and put the old one in our new arraylist.

                    boolean found_nothing = true;
                    for (int j = 0; j < array.size(); j++) {
                        if (former1 != j && former2 != j) {
                            byte check = checkConnected(array.get(former1), array.get(j));
                            if (check > 0) {
                                if (!marked[j]) {
                                    if (j != 0) {
                                        if (check == 2) { //If result is 2, the current way needs to be reversed
                                            array.get(i).setReverse(true);
                                        }
                                        marked[j] = true;

                                        found_nothing = false;

                                        if (i != 0) {
                                            former2 = former1;
                                            former1 = j;
                                        } else {
                                            former1 = 0;
                                            former2 = 0;
                                        }
                                        i = j;

                                        tempArray.add(array.get(i));
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    if (found_nothing) {
                        former2 = former1;
                        if (former1 != array.size() - 1) former1 += 1;
                        marked[i] = true;
                        resultingArray.add(tempArray);
                        tempArray = new ArrayList<>();
                        tempArray.add(array.get(i));

                        if(array.size() > i+1){
                            byte temp = checkOwnConnection(array.get(i+1), array.get(i));
                            if(temp == 2){
                                array.get(i).setReverse(true);
                            }
                        }

                        startCoord = array.get(i).getRefWay().getEndCoords(!array.get(i).isReverse());
                    }
                }
            }
        }
        resultingArray.add(tempArray);
        //Here we convert our array of array
        WayRef[][] l = new WayRef[resultingArray.size()][];
        for (int i = 0; i < resultingArray.size(); i++) {
            l[i] = resultingArray.get(i).toArray(new WayRef[resultingArray.get(i).size()]);
        }
        return l;
    }

    private byte checkConnected(WayRef o, WayRef n){
        byte checker = 0; //No connection
        if(Arrays.equals(o.getRefWay().getEndCoords(o.isReverse()), n.getRefWay().getStartCoords(false))){
            checker = 1; //Connection - not reversed
        }

        if(Arrays.equals(o.getRefWay().getEndCoords(o.isReverse()), n.getRefWay().getStartCoords(true))){
            checker = 2; //Connection - reversed
        }
        return checker;
    }

    private byte checkOwnConnection(WayRef o, WayRef n){
        byte checker = 0; //No connection
        if(Arrays.equals(o.getRefWay().getStartCoords(false), n.getRefWay().getEndCoords(false))||
                Arrays.equals(o.getRefWay().getStartCoords(true), n.getRefWay().getEndCoords(false))){
            checker = 1; //Connection - not reversed
        }

        if(Arrays.equals(o.getRefWay().getStartCoords(false), n.getRefWay().getEndCoords(true))||
                Arrays.equals(o.getRefWay().getStartCoords(true), n.getRefWay().getEndCoords(true))){
            checker = 2; //Connection - reversed
        }
        return checker;
    }

    private Object[] checkEveryPossibleNextElement(ArrayList<WayRef> array, ArrayList<WayRef> tempArray, int former1, int former2, boolean[] marked, int i){
        boolean found_nothing = true;
        int result_i = 0;
        boolean[] result_marked = marked;
        int result_former1 = former1;
        int result_former2 = former2;

        for (int j = 0; j < array.size(); j++) {
            if (former1 != j && former2 != j) {
                byte check = checkConnected(array.get(former1), array.get(j));
                if (check > 0) {
                    if (!result_marked[j]) {
                        if (j != 0) {
                            if (check == 2) { //If result is 2, the current way needs to be reversed
                                array.get(i).setReverse(true);
                            }
                            result_marked[j] = true;

                            found_nothing = false;

                            if (i != 0) {
                                former2 = former1;
                                former1 = j;
                            } else {
                                former1 = 0;
                                former2 = 0;
                            }
                            i = j;

                            tempArray.add(array.get(i));
                            break;
                        }
                    }
                }

            }
        }

        return new Object[]{found_nothing, result_i, result_former1, result_former2};
    }

    /**
     * Fills x_coords and y_coords with the coordinates from the WayRef's. Uses the private function, giveLengthOfWayCollection
     * @param array The double array of WayRef which cordinates is used to fill the x_coords and y_coords
     */
    private void fillCoords(WayRef[][] array){

        coords = new float[array.length][];

        for (int i = 0; i < coords.length; i++) { //How many groups
            coords[i] = new float[giveLengthOfWayCollection(array[i])];
            int index = 0;
            for (int j = 0; j < array[i].length; j++) { //How many ways in that group
                Way currentWay =  id2way.get(array[i][j].getReference());
                float[] coord = currentWay.getCoords(array[i][j].isReverse());

                for (int d = 0; d< coord.length;d=d+2){ //How many nodes each way has
                    coords[i][index] = coord[d];
                    coords[i][index+1] = coord[d+1];
                    index+=2;
                }
            }
        }
    }

    /**
     * Finds the collective length of all ways refrenced in the array.
     * @param list The array to be checked for all it's ways length
     * @return The length
     */
    private int giveLengthOfWayCollection(WayRef[] list){
        int result = 0;
        for (WayRef w: list) {
            result+=w.getRefWay().coords.length;
        }
        return result;
    }

    /**
     * The draw function, matches the fill color with the type of object, then draws all the polygons <br>
     * <br>
     * Utelises EVEN_ODD fill rule to create holes in areas that is drawn on multiple times.
     * @param gc GraphicsContext that determines what is drawn.
     */
    @Override
    public void draw(GraphicsContext gc, View view) {
        gc.setFill(color_area_converter[type]);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.00005);
        gc.beginPath();

        for (int i = 0; i < coords.length; i++) {
            gc.moveTo(coords[i][1]*0.56f,-coords[i][0]);
            for (int j = 2; j < coords[i].length; j=j+2) {
                gc.lineTo(coords[i][j+1]*0.56f,-coords[i][j]);
            }
        }
        gc.moveTo(coords[0][0]*0.56f, -coords[0][1]);
        gc.closePath();
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.fill();
        gc.setFillRule(FillRule.NON_ZERO);

    }
}
