package edu.wpi.moderndash.data.sources;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.exception.IllegalNetworkTableType;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataSources {

    /**
     * Creates a data source bound to a value in a network table.
     *
     * @param table        the network table containing the value to bind to
     * @param key          the key associated with the value
     * @param defaultValue the default value to use if the table doesn't contain a value for the given key
     * @param <T>          the type of the value of the source
     *
     * @throws NullPointerException    if {@code table}, {@code key}, or {@code defaultValue} is {@code null}
     * @throws IllegalNetworkTableType if the current value in the table with that key is of an incompatible type
     *                                 (for example, if the table has a value of type String but a source was attempted
     *                                 to be created with type Double)
     * @throws IllegalNetworkTableType if the value in the table is ever of a different type as the desired type. This
     *                                 may occur if the value is deleted, then a new value of a different type is added
     *                                 with the same key
     */
    public static <T> NetworkTableSource<T> tableSource(@NonNull ITable table,
                                                        @NonNull String key,
                                                        @NonNull T defaultValue) {
        return new NetworkTableSource<>(table, key, defaultValue);
    }

}
