package com.example.mapofdenmark;

import com.example.mapofdenmark.PathfindingPackage.Pathfinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;


public class Controller implements Serializable {
    double lastX;
    double lastY;
    boolean menuButton = true;//used to keep track of if the menu is open or not
    boolean addPointMode = false;//used to keep track of if add point mode is on or not
    float[] pointA;
    float[] pointB;
    byte condition;
    Pathfinding pathfinding;
    View view;
    ColorAdjust CA = new ColorAdjust();
    public Controller(Model model, View view) {
        this.view = view;
        pathfinding = new Pathfinding(model.graph, model.PrimaryRoads, model.SecondaryRoads);
        View.canvas.setOnMousePressed(e -> {
            lastX = e.getX();
            lastY = e.getY();
            if (e.isPrimaryButtonDown() && addPointMode){//if add point mode is on
                Point2D selectedPoint = view.mouseToModel(e.getX(), e.getY());//inverse-transforms the selected point so it can be drawn
                Iterator<Node> iterator = view.points.iterator();
                boolean draw = true;
                while (iterator.hasNext()){//looks through all the existing points
                    Node point = iterator.next();
                    if (Math.sqrt(Math.pow(selectedPoint.getX() - point.lon, 2)+Math.pow(selectedPoint.getY()-point.lat, 2))<=view.radius){//if the new point is within the radius of an existing point, it gets deleted instead
                        iterator.remove();
                        draw = false;//nothing will be added to the map
                    }
                }
                if (draw){
                    view.points.add(new Node((float)selectedPoint.getY(), (float)selectedPoint.getX()));
                }
                view.redraw();//always redraw so the canvas displays the correct points
                view.removeAllPoints.setDisable(view.points.isEmpty());
            }
        });

        View.canvas.setOnMouseDragged(e -> {
            view.resetMap.setDisable(false);
            if (!e.isPrimaryButtonDown()) {
                double dx = e.getX() - lastX;
                double dy = e.getY() - lastY;
                view.pan(dx, dy);
            }

            lastX = e.getX();
            lastY = e.getY();
        });

        View.canvas.setOnScroll(e -> {
            view.resetMap.setDisable(false);
            double factor = e.getDeltaY();
            view.zoom(e.getX(), e.getY(), Math.pow(1.01, factor));
        });

        view.menu.setOnAction(e -> {//clicking on the menu button
            if (menuButton){//makes it so the menu only loads when it hasn't been opened
                view.loadMenu();//menu is added onto canvas
                menuButton = false;//used to make sure only one menu opens and that the next time the button is clicked on, the menu closes
                view.buttonSelected(view.menu, true);//menu button becomes dark gray

            }else{//when the menu button is clicked on the second time, i.e. when the menu is already on the canvas
                view.closeMenu();
                menuButton = true;//next time you click on the menu button, it'll load the menu
                view.buttonSelected(view.menu, false);//the appearance of the menu button returns to normal
            }
        });

         /**
         * the route button, which is the one in charge of starting pathfinding algo
         */
        view.route.setOnAction(e -> {
            view.routeUsed = null;
            view.closeRoute();
            if (isRouteGoingToBeDisplayed()){//the information about the route is only displayed if both text boxes contain acceptable text
                view.routeUsed = pathfinding.helpPathfinding(pointA,pointB, condition);
                if (view.routeUsed==null){
                    view.changeRoute("NO PATH FOUND", "NO PATH FOUND");
                }else{
                    view.changeRoute(String.valueOf(pathfinding.getDistance()).substring(0,5) + "km", pathfinding.getTime());//used to determine the exact information displayed
                }
                view.redraw();
                view.loadRoute();//information about the route is added onto the menu/canvas
            }else if (!isRouteGoingToBeDisplayed()){//if the text inside the text boxes do not fit the criteria, then all the information gets removed from the menu
                view.closeRoute();
            }

        });
        view.addPoint.setOnAction(e -> {//add point button
            addPointMode = !addPointMode;//the add point mode is either turned on or off depending on the previous setting
            view.buttonSelected(view.addPoint, addPointMode);//the button changes color depending on the new setting
        });

        view.reset.setOnAction(e -> {//reset button
            view.resetMenu();//menu resets to before anything was added
            CA.setSaturation(0);
        });

        view.resetMap.setOnAction(e ->{
            view.resetMap();
            view.resetMap.setDisable(true);
        });

        view.removeAllPoints.setOnAction(e -> {
            pointA = null;
            pointB = null;
            view.points.clear();
            view.routeUsed = null;
            view.closeRoute();
            view.redraw();
            view.removeAllPoints.setDisable(true);
        });

        view.makeObj.setOnAction(e -> {
            try {
                model.save(model.fileName+".obj");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //first search bar

        view.dropdownbarTxt1.setOnKeyPressed(e-> {
            if (e.getCode() == KeyCode.ENTER && !view.dropdownbarTxt1.getEditor().getText().isEmpty()) {
                try {
                    float[] pointForSearch = makePoint(Address.parse(view.dropdownbarTxt1.getEditor().getText(), model.tst));
                    if (pointForSearch != null) {
                        pointA = pointForSearch;
                        view.points.add(new Node(-pointForSearch[0], pointForSearch[1]*0.56f)); //dot on map
                        view.redraw();
                        view.removeAllPoints.setDisable(false); //enables the button that clears dots
                    }
                } catch (Exception ignored) {}
            }
        });

        //second search bar
        view.dropdownbarTxt2.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.ENTER && !view.dropdownbarTxt2.getEditor().getText().isEmpty()) { // Only performs the search action on enter presses and if there is items in the box
                try {
                    float[] pointForSearch = makePoint(Address.parse(view.dropdownbarTxt2.getEditor().getText(), model.tst));
                    if(pointForSearch != null) {
                        pointB = pointForSearch;
                        view.points.add(new Node(-pointForSearch[0], pointForSearch[1]*0.56f)); //Creates a dot on the map
                        view.redraw();
                        view.removeAllPoints.setDisable(false); // Enables the button that clears dots
                    }
                } catch (Exception ignored) {}
            }
        });

        ObservableList<String> searches1 = FXCollections.observableArrayList(); // Lists that are observable by comboboxes
        ObservableList<String> searches2 = FXCollections.observableArrayList(); // Used to show autocompletes for searches
        view.dropdownbarTxt1.setOnKeyReleased(e->{ // Searchbar 1
            try{
                searches1.clear(); // Clears the items that are currently in the search box
                String result = view.dropdownbarTxt1.getEditor().getText();
                if (!result.isEmpty()) {
                    var a = Address.autocomplete(result, model.tst); // Searches the Ternary Search Tree with the input that is currently in searchbar 1
                    searches1.addAll(a);
                }
                view.dropdownbarTxt1.setItems(searches1); // Inserts the items from the list into the search boxes drop down
            }catch(Exception ignored) {}
        });
        view.dropdownbarTxt2.setOnKeyReleased(e->{ // Searchbar 2
            try{
                searches2.clear(); // Clears the items that are currently in the search box
                String result = view.dropdownbarTxt2.getEditor().getText();
                if (!result.isEmpty()) {
                    var a = Address.autocomplete(result, model.tst); // Searches the Ternary Search Tree with the input that is currently in searchbar 1
                    searches2.addAll(a);
                }
                view.dropdownbarTxt2.setItems(searches2); // Inserts the items from the list into the search boxes drop down
            }catch(Exception ignored) {}
        });
        view.buildings.setOnAction(e->{
            view.redraw(); // Redraws the canvas with the new setting selected
        });
        view.water.setOnAction(e->{
            view.redraw(); // Redraws the canvas with the new setting selected
        });
        view.color.setOnAction(e->{ // Switches on and off greyscale by setting the color saturation to -1(greyscale) or 0 (standard)
            if (!view.color.isSelected()) {
                CA.setSaturation(-1);
                view.primStage.getScene().getRoot().setEffect(CA);
            } else {
                CA.setSaturation(0);
                view.primStage.getScene().getRoot().setEffect(CA);
            }
            view.redraw();
        });
        view.grass.setOnAction(e->{
            view.redraw(); // Redraws the canvas with the new setting selected
        });
        view.road.setOnAction(e->{
            view.redraw(); // Redraws the canvas with the new setting selected
        });
        view.comboBox.setOnAction(e->{
            if (Objects.equals(view.comboBox.getSelectionModel().getSelectedItem(), "Drive")){
                condition = 2;
            } else if(Objects.equals(view.comboBox.getSelectionModel().getSelectedItem(), "Bike")){
                condition = 3;
            } else if (Objects.equals(view.comboBox.getSelectionModel().getSelectedItem(), "Walk")){
                condition = 5;
            }
        });
    }

    public boolean isRouteGoingToBeDisplayed(){//potentially used later on to determine if both text boxes have valid addresses. This is a method because its easier to edit
        return(pointA != null && pointB != null);
    }

    public float[] makePoint(TST.Vertex a) {
        return new float[]{a.lat, a.lon};
    }
}
