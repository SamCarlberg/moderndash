package edu.wpi.moderndash.util;

import com.google.common.collect.ImmutableSet;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

import java.util.Optional;
import java.util.Set;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Class for helping with NetworkTables constants.
 */
@UtilityClass
public class NetworkTableUtils {

    private static final Set<Class<?>> validTypes = ImmutableSet.of(
            // single values
            Double.class,
            Integer.class,
            Long.class,
            Boolean.class,
            String.class,
            // array values (no ArrayData because it's deprecated)
            double[].class,
            Double[].class,
            int[].class,
            Integer[].class,
            long[].class,
            Long[].class,
            boolean[].class,
            Boolean[].class,
            String[].class
    );

    private static final String METADATA_TABLE_NAME = "~METADATA~";

    /**
     * The root network table for the dashboard.
     */
    public static final ITable rootTable = NetworkTable.getTable("SmartDashboard");

    /**
     * The table that holds all the subsystems.
     */
    public static final ITable subsystemsTable = rootTable.getSubTable("Subsystems");

    /**
     * Gets the metadata table of the given table, or nothing if that table doesn't have metadata.
     *
     * @param table the table to get the metadata table of
     */
    public Optional<ITable> metadataTable(ITable table) {
        if (table.getSubTables().contains(METADATA_TABLE_NAME)) {
            return Optional.of(table.getSubTable(METADATA_TABLE_NAME));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks if we're currently connected to network tables.
     */
    public boolean isConnected() {
        return NetworkTable.connections().length > 0;
    }

    /**
     * Checks if an object is of a type that can be stored in a network table.
     */
    public boolean isValidNetworkTableValue(@NonNull Object value) {
        return validTypes.contains(value.getClass());
    }

}
