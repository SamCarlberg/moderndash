package edu.wpi.moderndash;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BorderedTitlePaneTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Internal Window Example");

        Pane pane = new AnchorPane();
        for (int i = 0; i < 5; i++) {
            Node n = new TextField("Text " + i);
            InternalWindow w = new InternalWindow("Window " + i, n);
            w.setLayoutX(i * 200);
            pane.getChildren().add(w);
        }
        pane.setStyle("-fx-background-color: #222222");
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
