package edu.wpi.moderndash.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.Preferences;
import edu.wpi.moderndash.io.MockIPResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrcRobotConnectionTest {

    private ITable dataTable;
    private Preferences preferences;
    private MockIPResolver ipResolver;
    private FrcRobotConnection connection;

    @Before
    public void setUp() {
        dataTable = NetworkTable.getTable(getClass().getName());
        preferences = new Preferences();
        ipResolver = new MockIPResolver(true);
        connection = new FrcRobotConnection(dataTable, preferences, ipResolver);
    }

    @After
    public void tearDown() {
        // Clear the data table after every test
        dataTable.getKeys().forEach(dataTable::delete);
    }

    @Test
    public void testUnstarted() {
        assertFalse(connection.isStarted());
        assertFalse(connection.isConnected());
        assertEquals(0, connection.teamNumber());
        assertEquals("roborio-0-frc.local", connection.robotIP());
        assertEquals(RobotState.UNKNOWN, connection.robotState());
    }

    @Test
    public void testPreferences() {
        connection.setConnected(true);
        preferences.setTeamNumber(1234);
        assertEquals(1234, connection.teamNumber());
        preferences.setUseMdns(true);
        assertEquals("roborio-1234-frc.local", connection.robotIP());
        preferences.setUseMdns(false);
        assertEquals("10.12.34.2", connection.robotIP());
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testQuery() {
        dataTable.putString("key", "something");
        // not connected
        assertFalse(connection.query("key").isPresent());
        // connected
        connection.setConnected(true);
        assertTrue(connection.query("key").isPresent());
        assertEquals("something", connection.query("key").get());
        // delete the key
        dataTable.delete("key");
        assertFalse(connection.query("key").isPresent());
        // set a different type
        dataTable.putNumber("key", 123);
        assertFalse(connection.query("key").isPresent());
    }

    @Test
    public void testRobotState() throws InterruptedException {
        assertEquals(RobotState.UNKNOWN, connection.robotState());
        connection.start();
        connection.setConnected(true);
        for (RobotState state : RobotState.values()) {
            dataTable.putString("State", state.name());
            Thread.sleep(50); // NetworkTables runs in its own thread. Wait a bit for the listener to get called.
            assertEquals(state, connection.robotState());
        }
        dataTable.putString("State", "not a real state");
        Thread.sleep(50); // wait for NetworkTables to call the listeners
        assertEquals(RobotState.UNKNOWN, connection.robotState());

        dataTable.delete("State");
        dataTable.putNumber("State", 0);
        Thread.sleep(50); // wait for NetworkTables to call the listeners
        assertEquals(RobotState.UNKNOWN, connection.robotState());
    }

    @Test
    public void testStart() throws InterruptedException {
        assertFalse(connection.isStarted());
        ipResolver.setCanResolve(false);
        connection.start();
        assertTrue(connection.isStarted());
        Thread.sleep(50); // Wait to make sure the connection test finished
        assertEquals(RobotState.DISCONNECTED, connection.robotState());
    }

    @Test
    public void testConnection() throws InterruptedException {
        ipResolver.setCanResolve(true);
        connection.start();
        Thread.sleep(50); // Wait to make sure the connection test finished
        assertTrue(connection.isConnected());

        ipResolver.setCanResolve(false);
        Thread.sleep(510); // Wait to make sure the connection test runs again
        assertFalse(connection.isConnected());
    }

}