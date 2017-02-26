package edu.wpi.moderndash;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class SkinnedApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Region r = FXMLLoader.load(getClass().getResource("/edu/wpi/moderndash/MainWindow.fxml"));
        Scene scene = new Scene(r);
        stage.setTitle("FRC moderndash");
        stage.setScene(scene);
        stage.setMinHeight(r.getPrefHeight());
        stage.setMinWidth(r.getPrefWidth());
        stage.show();
    }

}
