package edu.wpi.moderndash;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * App preferences.
 */
public class Preferences {

    // The team number to use.
    // Connects to roborio-TEAM-frc.local, or falls back to 10.TE.AM.2 if that fails
    private final IntegerProperty teamNumber = new SimpleIntegerProperty(this, "team-number", 0);
    private final BooleanProperty useMdns = new SimpleBooleanProperty(this, "use-mDNS", true);

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

}
