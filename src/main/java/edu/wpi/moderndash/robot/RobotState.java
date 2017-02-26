package edu.wpi.moderndash.robot;

/**
 * Potential states for a robot to be in.
 */
public enum RobotState {
    /**
     * There is no connection to the robot.
     */
    DISCONNECTED,
    /**
     * The robot is disabled.
     */
    DISABLED,
    /**
     * The robot is running its autonomous mode.
     */
    AUTONOMOUS,
    /**
     * The robot is running its teleoperated mode.
     */
    TELEOP,
    /**
     * The robot is running in test mode.
     */
    TEST,
    /**
     * An unknown state.
     */
    UNKNOWN,
}
