package edu.wpi.moderndash.property;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A property specifically for enums that offers a few convenience methods.
 *
 * @param <E> the type of the enum in the property
 */
public class EnumProperty<E extends Enum<E>> extends SimpleObjectProperty<E> {

    // Cache bindings because they're immutable, so no need to create multiples of the same binding
    // Would use EnumMap, but the type information is lost during compilation
    private final Map<E, BooleanExpression> bindings = new HashMap<>();

    public EnumProperty(Object bean, String name, E initialValue) {
        super(bean, name, initialValue);
    }

    /**
     * Gets a boolean expression that equals {@code true} when this enum property
     * has the given value and false when it doesn't.
     *
     * @param value the value to check for
     */
    public BooleanExpression hasValue(E value) {
        return bindings.computeIfAbsent(value, e -> Bindings.createBooleanBinding(() -> e == getValue(), this));
    }

}
