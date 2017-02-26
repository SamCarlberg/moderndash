package edu.wpi.moderndash.views;

import edu.wpi.moderndash.data.Valued;

import org.controlsfx.control.RangeSlider;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.Styleable;

/**
 * A value bar shows a value
 */
public class ValueBar extends RangeSlider implements Valued<Double> {

    // Default values

    private static final double DEFAULT_MIN = -1;
    private static final double DEFAULT_MAX = +1;
    private static final double DEFAULT_CENTER = 0;

    /**
     * Creates a new value bar with the range (-1, 1) with an initial value of zero.
     */
    public ValueBar() {
        this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_CENTER);
    }

    /**
     * Creates a new value bar with the range (min, max) with an initial value of the average of min and max.
     *
     * @param min the minimum value of this value bar
     * @param max the maximum value of this value bar
     */
    public ValueBar(double min, double max) {
        this(min, max, min + (max - min) / 2);
    }

    /**
     * Creates a new value bar with the range ({@code min}, {@code max}) with an initial value of {@code center}.
     *
     * @param min    the minimum value of this value bar
     * @param max    the maximum value of this value bar
     * @param center the center value of this value bar
     */
    public ValueBar(double min, double max, double center) {
        super(min, max, center, center);
        getStylesheets().add(ValueBar.class.getResource("value-bar.css").toExternalForm());
        getStyleClass().add("value-bar");
        setShowTickMarks(true);
        setShowTickLabels(true);
        setMajorTickUnit((max - min) / 4);

        setMin(min);
        setMax(max);
        setCenter(center);

        value.addListener((obs, o, n) -> {
            double c = getCenter();
            if (n >= c) {
                setLowValue(c);
                setHighValue(n);
            } else {
                setLowValue(n);
                setHighValue(c);
            }
        });

        setValue((Double) center);

    }

    // Center

    private final DoubleProperty center = new SimpleDoubleProperty(this, "center", DEFAULT_CENTER);

    public DoubleProperty centerProperty() {
        return center;
    }

    public double getCenter() {
        return center.get();
    }

    public void setCenter(double center) {
        this.center.set(center);
    }

    // Value

    private Property<Double> value = new SensibleDoubleProperty(this, "value", DEFAULT_CENTER);

    @Override
    public Property<Double> valueProperty() {
        return value;
    }

    /**
     * This only exists to the FXML loader can find the correct {@link #setValue(Object)} method. This is only an issue
     * because the FXML loader can't find the correct method due to type erasure.
     *
     * @throws UnsupportedOperationException if this method is ever called. Seriously. Don't call it.
     * @deprecated This method only exists so the FXMLLoader can find the correct method.
     */
    @Deprecated
    public void setValue(double value) {
        setValue(Double.valueOf(value));
    }

    /**
     * This only exists to the FXML loader can find the correct {@link #setValue(Object)} method. This is only a problem
     * because the FXML loader can't find the correct method due to type erasure.
     *
     * @throws UnsupportedOperationException if this method is ever called. Seriously. Don't call it.
     * @deprecated This method only exists so the FXMLLoader can find the correct method.
     */
    @Deprecated
    public void setValue(Number value) {
        throw new UnsupportedOperationException("Call setValue(Double) instead");
    }

    @Override
    public void setValue(Double value) {
        if (value < getMin() || value > getMax()) {
            throw new IllegalArgumentException(String.format("%s not in range (%s, %s)", value, getMin(), getMax()));
        }
        this.value.setValue(value);
    }

    @Override
    public Double getValue() {
        return value.getValue();
    }

    private static class SensibleDoubleProperty extends SimpleObjectProperty<Double> {

        public SensibleDoubleProperty(Object bean, String name, double initialValue) {
            super(bean, name, initialValue);
        }

    }

}
