package edu.wpi.moderndash;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.moderndash.dsl.BuiltInViews;
import edu.wpi.moderndash.robot.RobotConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ModernDash extends Application {

    @Inject
    private Preferences preferences;
    @Inject
    private RobotConnection connection;

    public static void main(String[] args) {
        NetworkTable.setIPAddress("localhost");
        NetworkTable.setClientMode();
        NetworkTable.setUpdateRate(0.010);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Set up the injector -- THIS MUST BE FIRST
        Injector injector = Guice.createInjector(new ModerndashModule());
        injector.injectMembers(this);

        // Set the size of the view grid
        preferences.setNumColumns(12);
        preferences.setNumRows(6);

        // Load UI elements
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/moderndash/MainWindow.fxml"),
                null, null,
                injector::getInstance);
        Pane root = loader.load();

        BuiltInViews bv = new BuiltInViews();
        bv.init();

        connection.start();

        Scene scene = new Scene(root);
        stage.setTitle("FRC moderndash");
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> {
            System.out.println("Main window closing, exiting");
            System.exit(0);
        });

        stage.show();
    }

}
