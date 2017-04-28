package edu.wpi.moderndash.data.sources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.wpi.moderndash.robot.RobotConnection;

/**
 * A data source for the connection status of the robot.
 */
@Singleton
public class ConnectionSource extends AbstractDataSource<Boolean> {

    @Inject
    public ConnectionSource(RobotConnection connection) {
        setName("Robot Connection");
        data.bind(connection.isConnectedProperty());
        active.bind(connection.isConnectedProperty());
    }

}
