package edu.wpi.moderndash.io;

import com.google.inject.Singleton;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * Class for testing if an IP can be resolved.
 */
@Log
@Singleton
public class IPResolver {

    /**
     * Are we running on a Unix system?
     */
    private static final boolean unix;

    static {
        unix = !System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    /**
     * Tries to resolve a network host with the given timeout. This is a blocking method.
     *
     * @param host        the host to try to resolve
     * @param timeout     the timeout. If zero, the timeout is infinite.
     * @param timeoutUnit the unit of the timeout period
     *
     * @return true if the host could be resolved within the given timeout, false if it couldn't be resolved or if
     * the resolution attempt exceeded the timeout period
     *
     * @throws NullPointerException if either {@code host} or {@code timeoutUnit} is {@code null}
     */
    public boolean canResolve(@NonNull String host, long timeout, @NonNull TimeUnit timeoutUnit) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        boolean resolved = false;
        try {
            resolved = canPing(host, timeout, timeoutUnit);
        } catch (IOException e) {
            log.log(Level.FINE, "Ping process failed", e);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Interrupted while pinging " + host, e);
        }
        return resolved;
    }

    /**
     * Pings a host to see if a connection can be established to it.
     *
     * @param host    the host to ping
     * @param timeout the process timeout
     */
    private static boolean canPing(String host, long timeout, TimeUnit timeoutUnit) throws IOException, InterruptedException {
        // ping -c 1 example.com (unix)
        // ping -n 1 example.com (windows)
        Process p = new ProcessBuilder("ping", unix ? "-c" : "-n", "1", host).start();
        p.waitFor(timeout, timeoutUnit);
        if (p.isAlive()) {
            // Ping process is still running. It exceeded the timeout period
            log.fine(String.format("Timeout period %d %s exceeded while pinging %s",
                                   timeout, timeoutUnit.name().toLowerCase(), host));
            p.destroyForcibly();
            return false;
        }
        return IOUtils.readLines(p.getInputStream(), "UTF-8").stream()
                .map(String::toLowerCase)
                .anyMatch(line -> line.matches(".*(ttl|TTL|reply from).*"));
    }

}
