package edu.wpi.moderndash.views;

import edu.wpi.moderndash.data.Valued;

import javafx.beans.property.Property;
import javafx.scene.control.TextField;

/**
 * Default viewer for values that don't have a specific view.
 */
public class DefaultViewer extends TextField implements Valued<Object> {

    public DefaultViewer() {
        setEditable(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Property valueProperty() {
        return textProperty();
    }

    @Override
    public void setValue(Object value) {
        setText(String.valueOf(value));
    }

    @Override
    public String getValue() {
        return getText();
    }

}
