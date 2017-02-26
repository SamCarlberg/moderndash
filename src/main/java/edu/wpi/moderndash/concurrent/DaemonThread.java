package edu.wpi.moderndash.concurrent;

/**
 * @see Thread
 * @see Thread#setDaemon(boolean)
 */
public class DaemonThread extends Thread {

    {
        setDaemon(true);
    }

    /**
     * Creates a new daemon thread with no target and the default name.
     */
    public DaemonThread() {
        super();
    }

    /**
     * Creates a new daemon thread with no target and the given name
     *
     * @param name the name of the thread
     */
    public DaemonThread(String name) {
        super(name);
    }

    /**
     * Creates a new daemon thread with the given target and the default name.
     *
     * @param target the action to run in this thread
     */
    public DaemonThread(Runnable target) {
        super(target);
    }

    /**
     * Creates a new daemon thread with the given target and name.
     *
     * @param target the action to run in this thread
     * @param name   the name of the thread
     */
    public DaemonThread(Runnable target, String name) {
        super(target, name);
    }


}
