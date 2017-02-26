package edu.wpi.moderndash.io;

import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * A mock IP resolver that lets tests determine whether or not an IP can be resolved.
 */
@AllArgsConstructor
public class MockIPResolver extends IPResolver {

    /**
     * Flags this resolver as being able to immediately resolve any IP.
     */
    @Setter
    private boolean canResolve;

    @Override
    public boolean canResolve(@NonNull String host,
                              long timeout,
                              @NonNull TimeUnit timeoutUnit) {
        return canResolve;
    }

}
