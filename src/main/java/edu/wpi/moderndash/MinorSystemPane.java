package edu.wpi.moderndash;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.util.NetworkTableUtils;

import javafx.scene.control.TitledPane;

/**
 * A pane representing a minor
 */
public class MinorSystemPane extends TitledPane {

    /**
     * Creates a new minor system pane with no title.
     */
    public MinorSystemPane() {
        this(null);
    }

    /**
     * Creates a new minor system name with the given title.
     *
     * @param title the title of the pane. If {@code null}, then no title will be shown.
     */
    public MinorSystemPane(String title) {
        super(title, null);
        setId(title);
        setCollapsible(false);
        getStylesheets().add("/edu/wpi/moderndash/main.css");
        getStyleClass().add("system-pane");
        ITable subsystemTable = NetworkTableUtils.subsystemsTable.getSubTable(title);
        // TODO populate with views for the data in the subsystem table
    }


}
