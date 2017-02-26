package edu.wpi.moderndash.data.sources;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.data.AbstractLoggable;
import edu.wpi.moderndash.exception.IllegalNetworkTableType;
import edu.wpi.moderndash.util.NetworkTableUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import lombok.NonNull;

class NetworkTableSource<T> extends AbstractLoggable<T> implements DataSource<T> {

    private final AtomicReference<T> data = new AtomicReference<>(null);
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final ObjectProperty<T> dataProperty = new SimpleObjectProperty<>(this, "dataProperty", null);

    @SuppressWarnings("unchecked")
    NetworkTableSource(@NonNull ITable table,
                       @NonNull String key,
                       @NonNull T defaultValue) {
        if (!NetworkTableUtils.isValidNetworkTableValue(defaultValue)) {
            throw new IllegalNetworkTableType(defaultValue.getClass());
        }
        data.set(defaultValue);
        dataProperty.set(defaultValue);

        table.addTableListenerEx(key, (s, k, v, n) -> {
            if (v == null) {
                // Value was removed, make the source inactive
                active.set(false);
                return;
            }
            if (!v.getClass().isAssignableFrom(defaultValue.getClass())) {
                // The type of value we're looking for isn't compatible with the
                // type of the value in the table
                active.set(false);
                throw new IllegalNetworkTableType(defaultValue.getClass());
            }
            active.set(true);
            data.set((T) v);
            dataProperty.set((T) v);
            log((T) v);
        }, 0xFF); // just grab everything
    }

    public ReadOnlyProperty<T> dataProperty() {
        return dataProperty;
    }

    @Override
    public T get() {
        return data.get();
    }

    @Override
    public boolean active() {
        return active.get();
    }

}
