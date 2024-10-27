package com.example.mapofdenmark;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.Serializable;

import static com.example.mapofdenmark.help_class.ColorLoader.createAreaColor;
import static com.example.mapofdenmark.help_class.ColorLoader.createLineColor;

public class Main extends Application implements Serializable {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./src/main/resources/com/example/mapofdenmark"));
        fileChooser.setTitle("Choose a file to generate a map over or cancel to select the default one");
        createAreaColor();
        createLineColor();
        String filename = "./src/main/resources/com/example/mapofdenmark/denmark-latest.osm.obj";
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) filename = selectedFile.getAbsolutePath();
        var model = Model.load(filename);
        var view = new View(model, primaryStage);
        new Controller(model, view);
    }
}
