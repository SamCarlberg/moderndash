package edu.wpi.moderndash.data.sources;

import com.google.inject.Inject;
import edu.wpi.moderndash.robot.RobotConnection;
import edu.wpi.moderndash.robot.RobotState;

/**
 *
 */
public class RobotStateSource extends AbstractDataSource<RobotState> {

    @Inject
    public RobotStateSource(RobotConnection connection) {
        setName("Robot State");
        dataProperty().bind(connection.robotStateProperty());
        active.bind(connection.isConnectedProperty());
    }

}
