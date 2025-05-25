module ucr.lab.hashtableapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens ucr.lab.hashtableapp to javafx.fxml;
    exports ucr.lab.hashtableapp;
    exports controller;
    opens controller to javafx.fxml;
}