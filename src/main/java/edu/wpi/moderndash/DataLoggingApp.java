package edu.wpi.moderndash;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.concurrent.DaemonThread;

import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DataLoggingApp extends Application {

    private static final String metaDataTableName = "~METADATA~";

    private final VBox box = new VBox();

    public static void main(String[] args) {
        NetworkTable.setIPAddress("localhost");
        NetworkTable.setClientMode();
        Application.launch(args);
    }

    private final Map<RobotComponent, PopOver> popOvers = new HashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(DaemonThread::new);

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws Exception {
        NetworkTable.getTable("LiveWindow").addSubTableListener(this::handle, true);
        Scene scene = new Scene(box);
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setScene(scene);
        stage.show();


        Thread fakeRobot = new Thread(() -> {
            System.out.println("Adding stuff to LW");
            NetworkTable lw = NetworkTable.getTable("LiveWindow");
            ITable drivebaseTable = lw.getSubTable("Drive Base");
            ITable md = drivebaseTable.getSubTable(metaDataTableName);
            md.putValue("Type", "Subsystem");
            md.putValue("Name", "Drive Base");
            ITable driveTable = drivebaseTable.getSubTable("Robot Drive 2");
            md = driveTable.getSubTable(metaDataTableName);
            md.putValue("Type", "RobotDrive");
            md.putValue("Name", "Robot Drive 2");
            drivebaseTable.putValue("Drive Type", "Tank"); // could also be "Arcade", "Mecanum"
            ITable leftMotor = driveTable.getSubTable("Left Motor");
            md = leftMotor.getSubTable(metaDataTableName);
            md.putValue("Type", "Motor");
            md.putValue("Name", "Left Motor");
            md.putValue("MinValue", -1);
            md.putValue("MaxValue", 1);
            leftMotor.putValue("Value", 0);
            ITable rightMotor = driveTable.getSubTable("Right Motor");
            md = rightMotor.getSubTable(metaDataTableName);
            md.putValue("Type", "Motor");
            md.putValue("Name", "Right Motor");
            md.putValue("MinValue", -1);
            md.putValue("MaxValue", 1);
            rightMotor.putValue("Value", 0);
            ITable gyro = drivebaseTable.getSubTable("Gyro");
            md = gyro.getSubTable(metaDataTableName);
            md.putValue("Type", "Gyro");
            md.putValue("Name", "Gyro");
            gyro.putValue("Value", 0);

            while (!Thread.interrupted()) {
                leftMotor.putValue("Value", Math.cos(System.currentTimeMillis() / 1e3));
                rightMotor.putValue("Value", Math.sin(System.currentTimeMillis() / 1e3));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }

        });
        fakeRobot.setDaemon(true);
        fakeRobot.start();
    }

    private PopOver currentPopover = null;

    /**
     * Creates a chart for a robot component and adds it to the chart map.
     *
     * @param rc the robot component to create a chart for
     */
    private void createChart(RobotComponent<Number, ?> rc) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Value");

        xAxis.setTickLabelsVisible(true);
        yAxis.setTickLabelsVisible(true);

        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);

        XYChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis) {
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
                // NOP to remove the circles from the data points
            }
        };
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(rc.getName() + " Values");
        rc.getDataStore().forEach((t, v) -> series.getData().add(new XYChart.Data<>(t, v)));
        rc.getDataStore().addListener((MapChangeListener<Double, Number>) change -> {
            Platform.runLater(() -> {
                // Data will never be "un-logged", so NBD
                series.getData().add(new XYChart.Data<>(change.getKey(), change.getValueAdded()));
            });
        });
        chart.getData().add(series);

        PopOver popOver = new PopOver(chart);
        popOver.setTitle(rc.getName() + " Values");
        popOver.setHeaderAlwaysVisible(true);
        rc.getControl().setOnMouseEntered(e -> {
            if (currentPopover != null && currentPopover != popOver) {
                currentPopover.hide(Duration.millis(100));
            }
            currentPopover = popOver;
            popOver.show(rc.getScene().getWindow());
        });
        popOvers.put(rc, popOver);
        scheduledExecutorService.scheduleAtFixedRate(() -> rc.log(rc.getControl().getValue()),
                                                     0L,
                                                     33,
                                                     TimeUnit.MILLISECONDS);
    }

    private static final List<String> defaultMdKeys = Arrays.asList("Name", "Type");
    private Set<RobotComponent> currentViews = new LinkedHashSet<>();

    private void handle(ITable table, String key, Object value, boolean isNew) {
        if (!isNew) {
            return;
        }
        if (value instanceof ITable) {
            ITable sub = (ITable) value;
            sub.addSubTableListener(this::handle, true);
            sub.addTableListener(this::handle, true);
        }
        System.out.printf("Table: %s, %s=%s\n", table, key, value);
        if (table.getSubTables().contains(metaDataTableName)) {
            System.out.println("  Got a component!");
            ITable mdTable = table.getSubTable(metaDataTableName);
            String type = mdTable.getString("Type", "Unknown");
            String name = mdTable.getString("Name", "Unknown");
            System.out.println("  " + name + " (is a " + type + ")");

            try {
                ComponentTracker.componentFor(name, type).ifPresent(component -> {
                    component.setName(name);
                    component.setType(type);
                    mdTable.getKeys().stream()
                            .filter(k -> !defaultMdKeys.contains(k))
                            .forEach(k -> component.getExtraData().put(k, mdTable.getValue(k)));
                    boolean has = currentViews.contains(component);
                    currentViews.add(component);
                    System.out.println("Component data type: " + component.getDataType());
                    if (Number.class.isAssignableFrom(component.getDataType())) {
                        // This is OK because we know the data type is a number
                        // noinspection unchecked
                        createChart((RobotComponent) component);
                    }
                    if (!has) {
                        Platform.runLater(() -> box.getChildren().addAll(component));
                    }

                    table.addTableListenerEx("Value", (s, k, v, n) -> component.getControl().setValue(v), 0xFF);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
