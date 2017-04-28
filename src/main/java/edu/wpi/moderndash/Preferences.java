package edu.wpi.moderndash;

import com.google.inject.Singleton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * App preferences.
 */
@Singleton
public class Preferences {

    // The team number to use.
    // Connects to roborio-TEAM-frc.local, or falls back to 10.TE.AM.2 if that fails
    private final IntegerProperty teamNumber = new SimpleIntegerProperty(this, "team-number", 0);
    private final BooleanProperty useMdns = new SimpleBooleanProperty(this, "use-mDNS", true);
    private final IntegerProperty numColumns = new SimpleIntegerProperty(this, "num-columns", 12);
    private final IntegerProperty numRows = new SimpleIntegerProperty(this, "num-rows", 6);

    public int getTeamNumber() {
        return teamNumber.get();
    }

    public IntegerProperty teamNumberProperty() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        if (teamNumber < 0) {
            throw new IllegalArgumentException("Team number must be positive");
        }
        this.teamNumber.set(teamNumber);
    }

    public boolean useMdns() {
        return useMdns.get();
    }

    public BooleanProperty useMdnsProperty() {
        return useMdns;
    }

    public void setUseMdns(boolean useMdns) {
        this.useMdns.set(useMdns);
    }

    public int getNumColumns() {
        return numColumns.get();
    }

    public IntegerProperty numColumnsProperty() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns.set(numColumns);
    }

    public int getNumRows() {
        return numRows.get();
    }

    public IntegerProperty numRowsProperty() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows.set(numRows);
    }

    @Override
    public String toString() {
        return "Preferences(" + "teamNumber=" + getTeamNumber() + ',' +
                "useMdns=" + useMdns() + ',' +
                "numColumns=" + getNumColumns() + ',' +
                "numRows=" + getNumRows() +
                ')';
    }
}
