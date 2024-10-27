module com.example.mapofdenmark {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens com.example.mapofdenmark to javafx.fxml;
    exports com.example.mapofdenmark;
    exports com.example.mapofdenmark.help_class;
    opens com.example.mapofdenmark.help_class to javafx.fxml;
    exports com.example.mapofdenmark.PathfindingPackage;
    opens com.example.mapofdenmark.PathfindingPackage to javafx.fxml;
}