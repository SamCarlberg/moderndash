package edu.wpi.moderndash.util;

import edu.wpi.first.wpilibj.tables.ITable;

import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.binding.Binding;
import javafx.beans.binding.ObjectBinding;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for creating JavaFX bindings to values stored in network tables.
 */
@UtilityClass
public class NetworkTableBindings {

    /**
     * Creates a generic binding to a value in a network table.
     *
     * @param table        the table containing the value to bind to
     * @param key          the key associated with the value to bind to
     * @param defaultValue the default value to use if the key doesn't exist
     * @param <T>          the type of the value to bind to
     *
     * @return
     */
    public <T> Binding<T> createBinding(@NonNull ITable table,
                                        @NonNull String key,
                                        @NonNull T defaultValue) {
        if (!NetworkTableUtils.isValidNetworkTableValue(defaultValue)) {
            throw new IllegalArgumentException(
                    "No objects of type " + defaultValue.getClass().getName() + " can be in a network table," +
                            " and are therefore unbindable");
        }
        return new NetworkTableBinding<>(table, key, defaultValue);
    }

    private class NetworkTableBinding<T> extends ObjectBinding<T> {

        /**
         * The table this is bound to.
         */
        private final ITable table;
        /**
         * The key this is bound to.
         */
        private final String key;

        /**
         * The current value
         */
        private AtomicReference<T> value = new AtomicReference<>(null);

        @SuppressWarnings("unchecked")
        private NetworkTableBinding(@NonNull ITable table,
                                    @NonNull String key,
                                    @NonNull T defaultValue) {
            this.table = table;
            this.key = key;
            this.value.set(defaultValue);

            System.out.println("Adding table listener");
            table.addTableListenerEx(key, (s, k, v, n) -> {
                System.out.println("Current value: " + this.value.get());
                System.out.println("NT change");
                this.value.set((T) v);
                System.out.println("New value: " + this.value.get());
                System.out.println("Invalidating");
                invalidate();
            }, 0xFF);
        }

        @Override
        protected T computeValue() {
            System.out.println("computeValue()");
            return value.get();
        }

    }

}
