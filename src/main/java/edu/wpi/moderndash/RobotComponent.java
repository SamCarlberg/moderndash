package edu.wpi.moderndash;

import com.google.common.base.Stopwatch;

import edu.wpi.moderndash.data.Loggable;
import edu.wpi.moderndash.data.Valued;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import lombok.NonNull;

/**
 * A robot component is a UI component that contains a single control with a label that
 *
 * @param <T> the type of the data to view
 * @param <C> the control for viewing the data
 */
@DefaultProperty("control")
public class RobotComponent<T, C extends Control & Valued<T>> extends VBox implements Loggable<T> {

    private final ObservableMap<Double, T> dataStore = FXCollections.observableMap(new ConcurrentHashMap<>());
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    private final Label nameLabel = new Label();

    public RobotComponent() {
        getChildren().add(nameLabel);
    }

    // Name

    private StringProperty name = new SimpleStringProperty(this, "name") {{
        addListener((obs, o, n) -> nameLabel.setText(n));
    }};

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    // Type

    private StringProperty type = new SimpleStringProperty(this, "type");

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type.set(type);
    }

    public String getType() {
        return type.get();
    }

    // Extra data

    private ObservableMap<String, Object> extraData = FXCollections.observableHashMap();

    public ObservableMap<String, Object> getExtraData() {
        return extraData;
    }

    // Data type

    private ObjectProperty<Class<?>> dataType = new SimpleObjectProperty<>(this, "data-type");

    public void setDataType(@NonNull Class<?> dataType) {
        this.dataType.set(dataType);
    }

    public Class<?> getDataType() {
        return dataType.get();
    }

    // Control

    private ObjectProperty<C> controlProperty = new SimpleObjectProperty<C>(this, "control", null) {{
        addListener((obs, oldControl, newControl) -> {
            getChildren().remove(oldControl);
            getChildren().add(newControl);
            newControl.valueProperty().addListener((_obs, oldData, newData) -> log(newData));
        });
    }};

    public ObjectProperty<C> controlProperty() {
        return controlProperty;
    }

    public void setControl(@NonNull C control) {
        controlProperty().setValue(control);
    }

    public C getControl() {
        return controlProperty().getValue();
    }

    @Override
    public ObservableMap<Double, T> getDataStore() {
        return dataStore;
    }

    @Override
    public void log(@NonNull T data) {
        if (!stopwatch.isRunning()) {
            stopwatch.start();
        }
        dataStore.put(stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1e3, data);
    }

}
