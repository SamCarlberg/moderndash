package edu.wpi.moderndash.concurrent;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DaemonThreadTest {

    @Test
    public void testDefaultConstructor() {
        Thread t = new DaemonThread();
        assertTrue(t.isDaemon());
    }

    @Test
    public void testNamedConstructor() {
        Thread t = new DaemonThread("name");
        assertTrue(t.isDaemon());
    }

    @Test
    public void testTargetConstructor() {
        Thread t = new DaemonThread(() -> {});
        assertTrue(t.isDaemon());
    }

    @Test
    public void testTargetAndNamedConstructor() {
        Thread t = new DaemonThread(() -> {}, "name");
        assertTrue(t.isDaemon());
    }

}