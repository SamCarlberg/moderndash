package edu.wpi.moderndash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 *
 */
@Data
@AllArgsConstructor
public class NetworkTableEntry {

    private @NonNull String key;
    private @NonNull String value;

    public NetworkTableEntry() {
        this("", "");
    }

}
