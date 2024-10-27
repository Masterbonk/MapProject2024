package com.example.mapofdenmark;

import com.example.mapofdenmark.PathfindingPackage.Edge;
import com.example.mapofdenmark.ST.RNode;
import com.example.mapofdenmark.ST.RSTree;
import com.example.mapofdenmark.ST.RStarTree;
import com.example.mapofdenmark.ST.RTree;
import com.example.mapofdenmark.help_class.mathhelp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class View implements Serializable {
    public static Canvas canvas = new Canvas(640, 500);
    public static GraphicsContext gc = canvas.getGraphicsContext2D();

    Affine trans = new Affine();
    Stage primStage;
    Model model;
    BorderPane pane;

    //different buttons later used by the controller
    Button menu;
    Button addPoint;
    Button makeObj = new Button("Make .obj file");
    Button route;
    Button reset = new Button();
    Button resetMap = new Button("Reset Map");
    Button removeAllPoints = new Button("Remove All Points");

    //checkboxes for each of the options
    CheckBox water = new CheckBox("Water");
    CheckBox road = new CheckBox("Roads");
    CheckBox buildings = new CheckBox("Buildings");
    CheckBox grass = new CheckBox("Grass");
    CheckBox color = new CheckBox("Color displayed");


    //different boxes for the menu used by methods
    VBox vbox1;
    VBox vbox;
    VBox display;
    Label scaleBarLabel;//displays the distance for the scale bar

    //values used for points
    double radius;
    float width;
    double range;


    //elements for the first portion of the menu

    ComboBox<String> comboBox;
    ComboBox<String> dropdownbarTxt1;
    ComboBox<String> dropdownbarTxt2;
    ObservableList<String> options;

    //import list from outside to see options for adress


    //used for the display information of a route
    Label insertDistance;
    Label insertTime;

    //used for settings portion of the menu
    Set<CheckBox> checkBoxes = new HashSet<>();

    Set<Node> points = new HashSet<>();//points drawn on map

    Edge[] routeUsed;

    public View(Model model, Stage primaryStage) {
        primStage = primaryStage;
        this.model = model;
        primaryStage.setTitle("Danmarkskortet");
        pane = new BorderPane(canvas);
        pane.setMinHeight(10);
        pane.setMinWidth(10);
        range = (canvas.getHeight() / (model.maxlat - model.minlat));
        radius = 0.035 * (canvas.getHeight() / 3.62174);
        width = 0.007f * (float) range;

        //sets all checkboxes to true
        water.setSelected(true);
        buildings.setSelected(true);
        grass.setSelected(true);
        road.setSelected(true);
        color.setSelected(true);
        routeUsed = null;

        //used for zoom scale bar
        scaleBarLabel = new Label(displayDistance());//used for the zoom scale bar - displays the distance
        //drawing the scale bar on the bottom
        //draws the horizontal line for the scale bar
        Line line = new Line();
        line.setStartX(590);
        line.setStartY(495);
        line.setEndX(635);
        line.setEndY(495);
        //next two drawnObjects draws the vertical line for the scale bar
        Line line1 = new Line();
        line1.setStartX(590);
        line1.setStartY(490);
        line1.setEndX(590);
        line1.setEndY(500);
        Line line2 = new Line();
        line2.setStartX(635);
        line2.setStartY(490);
        line2.setEndX(635);
        line2.setEndY(500);

        Group scaleBar = new Group(line, line1, line2); // Groups scalebar lines to insert them into hbox


        //next two drawnObjects draws the vertical line for the scale bar
        addPoint = new Button("Add point");//button for select mode
        resetMap.setDisable(true);
        removeAllPoints.setDisable(true);

        //code under is for the menu
        vbox = new VBox();//entire menu
        menu = new Button("Menu");//menu button
        route = new Button("Route");
        //suppose to remove the focus color so the buttons stay the same size
        menu.setStyle("-fx-focus-color: #626262;");//stackoverflow
        addPoint.setStyle("-fx-focus-color: #626262;");
        menu.setStyle("-fx-background-color: rgb(196,185,185)");//if a button is not selected, its appearance becomes one of a normal button
        addPoint.setStyle("-fx-background-color: rgb(196,185,185)");//if a button is not selected, its appearance becomes one of a normal button
        resetMap.setStyle("-fx-background-color: rgb(196,185,185)");
        makeObj.setStyle("-fx-background-color: rgb(196,185,185)");
        removeAllPoints.setStyle("-fx-background-color: rgb(196,185,185)");

        HBox top = new HBox(menu, addPoint, resetMap, removeAllPoints, makeObj);//the top bar with options - menu and add point mode
        pane.setTop(top);
        HBox scaleBarGroup = new HBox(scaleBar);//adds the label with distance used for the zoom scale bar
        scaleBarGroup.setAlignment(Pos.CENTER_RIGHT);
        scaleBarGroup.setPadding(new Insets(5, 5, 5, 5)); // Centers and adds padding the scalebar so it lines up with the number
        VBox bottom = new VBox(scaleBarLabel, scaleBarGroup); // puts the scalebar and the scalebar label in the same box
        bottom.setAlignment(Pos.BOTTOM_RIGHT);//label is set to the right side of the canvas
        bottom.setMinHeight(40);
        bottom.setMaxHeight(40);
        bottom.setStyle("-fx-background-color: #FFFFFF;");
        pane.setBottom(bottom); //for zoom scale
        canvas.requestFocus();


        //sets scene up
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.setWidth(primStage.getWidth());
        canvas.setHeight(primStage.getHeight());
        redraw();
        pan(-0.56 * model.minlon, model.maxlat);
        zoom(0, 0, range);
        //creates the menu and display of route - this doesn't add them to the canvas, only creates for later use
        drawMenu();
        drawRoute();

        //Listeners added to reformat the canvas whenever the size of the stage is changed by the user
        primStage.widthProperty().addListener(observable -> {
            canvas.setWidth(primStage.getWidth());
            redraw();
        });
        primStage.heightProperty().addListener(observable -> {
            canvas.setHeight(primStage.getHeight());
            redraw();
        });
    }

    /**
     * The previous drawing of the map gets erased and then a new map gets drawn with all the transformations added
     * Ways are drawn depending on the scale factor. The further zoomed in you are, the more drawnObjects are drawn.
     * The larger drawnObjects are always drawn, whereas the smaller ones are only visible when zoomed in
     */

    void redraw() {
        gc.setTransform(new Affine());
        if (water.isSelected()) {
            gc.setFill(Color.rgb(92, 191, 210));
        } else gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setTransform(trans);

        Point2D minPoint = null;
        Point2D maxPoint = null;
        try {
            minPoint = trans.inverseTransform(0, 0);
            maxPoint = trans.inverseTransform(canvas.getWidth(), canvas.getHeight());
        } catch (Exception ignored) {
        }

        float minX = (float) minPoint.getX();
        float minY = (float) minPoint.getY();
        float maxX = (float) maxPoint.getX();
        float maxY = (float) maxPoint.getY();

        System.out.println("[" + minX + " : " + maxX + "] x " + "[" + minY + " : " + maxY + "]");

        if (minX > maxX) {
            float temp = maxX;
            maxX = minX;
            minX = temp;
        }
        if (minY > maxY) {
            float temp = maxY;
            maxY = minY;
            minY = temp;
        }

        drawTree(gc, this, model.Islands, minX, maxX, minY, maxY);

        if (trans.getMxx() > 2000) {
            if (grass.isSelected()) {
                drawTree(gc, this, model.Forest, minX, maxX, minY, maxY);
            }
        }

        if (trans.getMxx() > 10000) {
            if (buildings.isSelected()) {
                drawTree(gc, this, model.Structure, minX, maxX, minY, maxY);
            }
        }
        if (trans.getMxx() > 10000) {
            if (water.isSelected()) {
                drawTree(gc, this, model.Water, minX, maxX, minY, maxY);
            }
        }
        if (trans.getMxx() > 10500) {
            if (road.isSelected()) {
                drawTree(gc, this, model.PrimaryRoads, minX, maxX, minY, maxY);
            }
        }
        if (trans.getMxx() > 20000) {
            if (road.isSelected()) {
                drawTree(gc, this, model.Walkways, minX, maxX, minY, maxY);
                drawTree(gc, this, model.SecondaryRoads, minX, maxX, minY, maxY);
            }
            if (buildings.isSelected()) {
                drawTree(gc, this, model.Buildings, minX, maxX, minY, maxY);
            }
        }
        if (trans.getMxx() > 30000) {
            drawTree(gc, this, model.Lines, minX, maxX, minY, maxY);
        }

        if (routeUsed != null) {

            for (Edge edge : routeUsed) {
                edge.drawEdge(Model.id2way, gc, width);
            }
        }

        for (var point : points) {//draws all the point every time the map is redrawn, so points stay forever
            //stackoverflow
            gc.strokeOval(point.lon - radius, point.lat - radius, 2 * radius, 2 * radius);//draws small circle
            gc.setFill(Color.RED);
            gc.fillOval(point.lon - radius, point.lat - radius, 2 * radius, 2 * radius);//colors it red
        }
        //testDrawTree();
        drawTestQueries(gc, this);
        //model.analyzeTrees(model.ForestList);
    }

    private void drawTestQueries(GraphicsContext gc, View view) {
        double[] q0 = new double[]{8.18d, 8.55d, -54.95d, -55.36d};
        double[] q1 = new double[]{8.25d, 8.345d, -55.195d, -55.1};
        double[] q2 = new double[]{8.419d, 8.446d, -55.08d, -55.055d};
        double[] q3 = new double[]{8.444d, 8.46d, -55.132d, -55.119d};

        drawQuery(q0);
        drawQuery(q1);
        drawQuery(q2);
        drawQuery(q3);
    }

    private void drawQuery(double[] q){
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.0005);
        gc.beginPath();

        gc.moveTo(q[0], q[2]);
        gc.lineTo(q[1], q[2]);

        gc.moveTo(q[1], q[2]);
        gc.lineTo(q[1], q[3]);

        gc.moveTo(q[1], q[3]);
        gc.lineTo(q[0], q[3]);

        gc.moveTo(q[0], q[3]);
        gc.lineTo(q[0], q[2]);

        gc.stroke();
    }

    /*private void testDrawTree() {
        for (RNode n : model.Forest.getRoot().children) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(0.0005);
            gc.beginPath();

            gc.moveTo(n.minX, n.minY);
            gc.lineTo(n.maxX, n.minY);

            gc.moveTo(n.maxX, n.minY);
            gc.lineTo(n.maxX, n.maxY);

            gc.moveTo(n.maxX, n.maxY);
            gc.lineTo(n.minX, n.maxY);

            gc.moveTo(n.minX, n.maxY);
            gc.lineTo(n.minX, n.minY);

            gc.stroke();
        }

        if (model.Forest.getRoot().children.get(0).isLeaf()) {
            for (RNode n : model.Forest.getRoot().children) {
                for (DrawnObject c : n.values) {
                    gc.setStroke(Color.PURPLE);
                    gc.setLineWidth(0.0005);
                    gc.beginPath();

                    gc.moveTo(c.minX, c.minY);
                    gc.lineTo(c.maxX, c.minY);

                    gc.moveTo(c.maxX, c.minY);
                    gc.lineTo(c.maxX, c.maxY);

                    gc.moveTo(c.maxX, c.maxY);
                    gc.lineTo(c.minX, c.maxY);

                    gc.moveTo(c.minX, c.maxY);
                    gc.lineTo(c.minX, c.minY);

                    gc.stroke();
                }
            }
        } else {
            for (RNode n : model.Forest.getRoot().children) {
                for (RNode c : n.children) {
                    gc.setStroke(Color.PURPLE);
                    gc.setLineWidth(0.0005);
                    gc.beginPath();

                    gc.moveTo(c.minX, c.minY);
                    gc.lineTo(c.maxX, c.minY);

                    gc.moveTo(c.maxX, c.minY);
                    gc.lineTo(c.maxX, c.maxY);

                    gc.moveTo(c.maxX, c.maxY);
                    gc.lineTo(c.minX, c.maxY);

                    gc.moveTo(c.minX, c.maxY);
                    gc.lineTo(c.minX, c.minY);

                    gc.stroke();
                }
            }
        }


    }
     */

    private void drawTree(GraphicsContext gc, View view, RTree tree, float minX, float maxX, float minY,
                          float maxY) {
        for (DrawnObject d : tree.query(minX, maxX, minY, maxY)) {
            d.draw(gc, view);
        }
    }

    private void drawTree(GraphicsContext gc, View view, RStarTree tree, float minX, float maxX, float minY,
                          float maxY) {
        for (DrawnObject d : tree.query(minX, maxX, minY, maxY)) {
            d.draw(gc, view);
        }
    }

    private void drawTree(GraphicsContext gc, View view, RSTree tree, float minX, float maxX, float minY,
                          float maxY) {
        for (DrawnObject d : tree.query(minX, maxX, minY, maxY)) {
            d.draw(gc, view);
        }
    }

    /**
     * The map gets translated to the position of the mouse
     * Then the map is redrawn to fit with the translation
     *
     * @param dx the x-coordinate of the position of the mouse
     * @param dy the y-coordinate of the position of the mouse
     */
    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        redraw();
    }

    /**
     * Zooms into wherever the mouse is
     * Then the distance displayed under the scale bar changes to be more correct
     * The entire map gets redrawn to fit the scale
     *
     * @param dx     The x-coordinate of the position of the mouse
     * @param dy     The y-coordinate of the position of the mouse
     * @param factor the scale factor of the zoom
     */
    void zoom(double dx, double dy, double factor) {//Zoom ud er over 1, zoom ind er under 1
        boolean check = true;
        if (trans.getMxx() > 182166.0 && factor > 1) {
            check = false;

        } else if (trans.getMxx() < 250.0 && factor < 1) {
            check = false;
        }

        if (check) {
            pan(-dx, -dy);
            trans.prependScale(factor, factor);
            pan(dx, dy);
            scaleBarLabel.setText(displayDistance());//updates the distance for scale bar
            if (10 / (trans.getMxx() / range) >= 0.1) {
                radius = (0.035 * (canvas.getHeight() / 3.62174)) / trans.getMxx();
                width = (0.007f * (float) range) / (float) trans.getMxx();
            } else {
                radius = 0.00035;//0.005
                width = 0.00007f;
            }

            redraw();
        }
    }

    /**
     * Inverse-transforms a point and returns the point inverse-transformed
     *
     * @param lastX x-coordinate of the point that needs to be transformed
     * @param lastY y-coordinate of the point that needs to be transformed
     * @return the point after being inverse-transformed
     */

    public Point2D mouseToModel(double lastX, double lastY) {
        try {
            return trans.inverseTransform(lastX, lastY);//returns point with the needed coordinates
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * The points on the scale factor gets inversed-transformed, then the distance between the two points gets calculated via Haversine
     *
     * @return the distance between the two points rounded to 5 s.f. as a string. Units are also included. The units are either km or m depending on the magnitude of the distance
     */
    public String displayDistance() {
        //takes the two points that the horizontal line of the scale bar consists of and then inverse-transforms it
        Point2D point1 = mouseToModel(590, 495);
        Point2D point2 = mouseToModel(635, 495);
        //590, 495, 635, 495
        float distance = mathhelp.calculateDistance((float) point1.getX(), (float) point1.getY(), (float) point2.getX(), (float) point2.getY());//calculates the distance between the two new points
        if (distance < 1.0) {
            return String.valueOf(distance * 1000).substring(0, 6) + " m";
        } else {
            return String.valueOf(distance).substring(0, 6) + " km";
        }
    }


    /**
     * This draws the menu used for navigation as well as general settings for the map.
     * This includes toggling which elements are on display and typing addresses in, allowing navigation between two points
     * This doesn't draw the menu on the canvas, it only creates it for later use
     */
    public void drawMenu() {
        //creating sections of the menu
        vbox1 = new VBox();//used for the top half of the menu. This includes everything to do with navigation
        VBox vboxMain = new VBox();//the top section of the top half of the menu which doesn't include the information about a route - this part is always displayed on the menu
        display = new VBox();//the bottom section of the top half of the menu. This displays information about the route such as distance and time
        VBox vbox2 = new VBox();//second half of the entire menu. Consists of options for the display of the map that can be toggled on or off
        //width of the entire menu is 170
        vbox.setStyle("-fx-background-color: #FFFFFF;");
        vbox.setMaxWidth(170);
        vbox.setMinWidth(170);
        vbox1.setMaxWidth(170);
        vbox2.setMaxWidth(170);
        vbox1.setMinHeight(canvas.getHeight() * 0.57);//top half is 57% of the menu
        vboxMain.setMinHeight(vbox1.getMinHeight() * 0.6);//top half of the top half of the menu is 60% of the top half
        vbox.setMinHeight(canvas.getHeight());//menu always has the same vertical length as the canvas

        //creating top half of the menu
        Text navigationTitle = new Text("Navigation");//title for the top half of the menu - the navigation portion of the menu
        navigationTitle.setFont(new Font(25));//duplicate


        //TEST dropdown input bar (some of it is yet to be used (changed)
        dropdownbarTxt1 = new ComboBox<>();
        dropdownbarTxt1.setEditable(true);
        dropdownbarTxt2 = new ComboBox<>();
        dropdownbarTxt2.setEditable(true);

        dropdownbarTxt1.setPromptText("Address 1");
        dropdownbarTxt2.setPromptText("Address 2");


        //code directly from library
        //title pane isnt from library
        //combobox/drop down list
        options =
                FXCollections.observableArrayList(//list of option in the combobox
                        "Walk",
                        "Drive",
                        "Bike"
                );
        comboBox = new ComboBox<>(options);//combobox for the mode of transportation the route will utilize
        comboBox.setPromptText("choose");//text inside the combobox that is there before the user has selected anything
        comboBox.setPrefWidth(150);//width of the combobox - used because or else the combobox will only be as wide as the text inside of it

        //route button
        route.setPrefWidth(150);//same reasoning as the combobox
        VBox.setMargin(route, new Insets(-3, 0, -3, 0));

        //second half of the menu
        var title = new Text("Settings");
        title.setFont(new Font(25));//duplicate

        //all options are added to a set
        checkBoxes.add(water);
        checkBoxes.add(color);
        checkBoxes.add(buildings);
        checkBoxes.add(road);
        checkBoxes.add(grass);

        //reset button
        reset.setText("Reset Menu");
        reset.setPrefWidth(150);//button is wider for visual purposes

        vboxMain.setSpacing(10);//elements inside of this box has a vertical spacing of 10 between each element
        vbox2.setSpacing(5);//elements in the settings portion of the menu has a spacing of 5 between each element
        //creates horizontal margin of 10
        vbox1.setStyle("-fx-padding: 10;");//since this box has a padding of 10, that means that the "subboxes" (vboxMain and display) are smaller, and therefore it looks like they also have this margin
        vbox2.setStyle("-fx-padding: 10;");
        Label transport = new Label("Transportation");
        transport.setFont(new Font(15));
        VBox.setMargin(transport, new Insets(-7, 0, -7, 0));

        //elements get added to respective region
        //elements in the top half of the top half of the menu
        vboxMain.getChildren().add(navigationTitle);
        VBox.setMargin(navigationTitle, new Insets(-7, 0, -2, 0));//navigation title is closer to the top and the element below it is slightly closer to it
        //vboxMain.getChildren().addAll(input1, input2); textboxes are added
        vboxMain.getChildren().add(dropdownbarTxt1);
        vboxMain.getChildren().add(dropdownbarTxt2);
        vboxMain.getChildren().add(transport);
        vboxMain.getChildren().add(comboBox);


        VBox.setVgrow(comboBox, Priority.ALWAYS);//combobox grows along with the width of the menu
        vboxMain.getChildren().add(route);//route button is added

        vbox1.getChildren().add(vboxMain);//top half of the top half gets added into the top half of the menu
        //elements in the bottom half/settings gets added
        vbox2.getChildren().add(title);
        vbox2.getChildren().addAll(water, road, buildings, grass, color);//checkboxes are added
        vbox2.getChildren().add(reset);//reset button is added
        //all portions of the menu is added to the menu
        vbox.getChildren().addAll(vbox1, vbox2);
    }

    /**
     * Loads the menu onto the canvas
     */
    public void loadMenu() {
        pane.setLeft(vbox);
    }


    /**
     * The menu closes, returning the map to normal
     */
    public void closeMenu() {
        pane.setLeft(null);//menu is removed because there's nothing on the left side of the pane
        //text in the text boxes are removed
        dropdownbarTxt1.getEditor().setText(null);
        dropdownbarTxt2.getEditor().setText(null);
        closeRoute();//any information about the route is removed from the menu
    }

    /**
     * This draws the distance between the two points inserted into the menu
     * The distance is displayed inside the menu
     */

    public void drawRoute() {
        Label distance = new Label("Distance: ");
        distance.setFont(new Font(15));//duplicate, font size
        insertDistance = new Label();//this label can change, used to display the distance of the route
        insertDistance.setFont(new Font(13));//duplicate, this label is slightly smaller than the heading
        Label time = new Label("Time: ");
        time.setFont(new Font(15));//duplicate
        insertTime = new Label();//this label can change
        insertTime.setFont(new Font(13));//duplicate

        VBox.setMargin(distance, new Insets(4, 0, 0, 0));//creates distance between this heading and the route button
        VBox.setMargin(insertDistance, new Insets(1, 0, 0, 0));//the actual distance displayed is very close to the heading
        VBox.setMargin(time, new Insets(7, 0, 0, 0));//more distance from the actual distance
        VBox.setMargin(insertTime, new Insets(1, 0, -20, 0));//the -20 ensures that the location of the settings doesn't change when the route information shows up.

        display.getChildren().addAll(distance, insertDistance, time, insertTime);//all elements get added to the proper box, but they are not added onto the canvas
    }

    /**
     * The distance and time displayed on the menu gets changed
     *
     * @param distance the new distance displayed
     * @param time     the new time displayed
     */
    public void changeRoute(String distance, String time) {
        insertDistance.setText(distance);
        insertTime.setText(time);
    }

    /**
     * The information for the route gets displayed on the canvas
     */
    public void loadRoute() {
        vbox1.getChildren().add(display);//route information is added to the canvas by adding it to the top half of the menu
    }

    /**
     * The information for the route gets removed from the menu and canvas
     */
    public void closeRoute() {
        vbox1.getChildren().removeAll(display);//the entire box containing the information about the route is removed from the top half of the menu
    }

    /**
     * Changes the appearance/style of a button depending on  if it's selected or not.
     * The button becomes dark gray if it's selected, or else the button is light gray (the standard setting)
     * The function is both responsible for the button turning dark gray and for a button becoming light gray again
     *
     * @param button   the button whose appearance/style will be change
     * @param selected whether a button is selected or not. Determines which color it will become
     */
    public void buttonSelected(Button button, boolean selected) {
        if (selected) {
            button.setStyle("-fx-background-color: rgb(128,128,128);");//if a button is selected, its color becomes a dark gray
        } else {
            button.setStyle("-fx-background-color: rgb(196,185,185)");//if a button is not selected, its appearance becomes one of a normal button
        }
    }

    /**
     * Menu gets reset to its original state, where all checkboxes are checked and text is removed.
     * The combobox is also reset
     * Previous information about a route is also removed from the menu
     */
    public void resetMenu() {
        closeRoute();//information about a route gets deleted from the menu, so when the menu is opened again, the route information is no longer displayed
        //text is removed from the text boxes

        for (var checkbox : checkBoxes) {//all checkboxes are checked, resetting the settings
            checkbox.setSelected(true);
        }
        //the combobox is reset to display default item
        comboBox.setValue(null);
        dropdownbarTxt2.setValue("");
        dropdownbarTxt1.setValue("");
        routeUsed = null;
        redraw();
    }

    /**
     * Resets the map on the canvas by removing all the transformations, which returns the map to the state it was at in the beginning
     * All the points added will stay on the map
     */
    public void resetMap() {
        //resets the transformations
        trans.setMxx(1);
        trans.setMyy(1);
        trans.setMzz(1);
        trans.setMxy(0);
        trans.setMxz(0);
        trans.setMyx(0);
        trans.setMyz(0);
        trans.setTy(0);
        trans.setTx(0);
        trans.setMzx(0);
        trans.setMzy(0);
        //repositions the map to its position at the beginning
        redraw();
        pan(-0.56 * model.minlon, model.maxlat);
        zoom(0, 0, range);
    }
}