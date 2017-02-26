package edu.wpi.moderndash;

import edu.wpi.moderndash.data.Valued;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;

/**
 * Tracks robot components.
 */
public class ComponentTracker {

    static {
        // Lets us load FXML from the NTListener thread
        Thread.currentThread().setContextClassLoader(ComponentTracker.class.getClassLoader());
    }

    private static final String fxmlResourcePath = "/edu/wpi/moderndash/";
    private static Map<String, RobotComponent<?, ?>> components = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T, C extends Control & Valued<T>>
    Optional<RobotComponent<T, C>> componentFor(String name, String type)
            throws IOException {
        if (!components.containsKey(name)) {
            RobotComponent<?, ?> v;
            String fxmlName = fxmlResourcePath + type + "View.fxml";
            if (ComponentTracker.class.getResource(fxmlName) != null) {
                v = FXMLLoader.load(ComponentTracker.class.getResource(fxmlName));
            } else {
                v = null;
            }
            components.put(name, v);
        }
        return Optional.ofNullable((RobotComponent<T, C>) components.get(name));
    }


}
