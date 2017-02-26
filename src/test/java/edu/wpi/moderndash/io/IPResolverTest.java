package edu.wpi.moderndash.io;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the IP resolver class. This only works with an internet connection.
 */
public class IPResolverTest {

    private IPResolver resolver = new IPResolver();

    @Test
    public void testNoTimeout() {
        assertFalse("Resolved impossibly quickly", resolver.canResolve("example.com", 0, TimeUnit.NANOSECONDS));
    }

    @Test
    public void testLocalhost() {
        // should only take ~50us max, but best to be safe
        assertTrue("Could not resolve 'localhost'",
                   resolver.canResolve("localhost", 25, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 500)
    public void testIpv4() {
        // google's DNS server
        assertTrue("Could not resolve google's DNS server 8.8.8.8",
                   resolver.canResolve("8.8.8.8", 500, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 500)
    public void testDomainName() {
        assertTrue("Could not resolve example.com",
                   resolver.canResolve("example.com", 500, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 200)
    public void testNotAccessible() {
        assertFalse("Could somehow resolve a non-existent host",
                    resolver.canResolve("", 100, TimeUnit.MILLISECONDS));
    }

    @Test(expected = NullPointerException.class)
    public void testNullHost() {
        resolver.canResolve(null, 1, TimeUnit.MILLISECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTimeout() {
        resolver.canResolve("", -1, TimeUnit.MILLISECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTimeoutUnit() {
        resolver.canResolve("", 1, null);
    }

}