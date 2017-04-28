package edu.wpi.moderndash.playback;

import java.util.concurrent.TimeUnit;

import lombok.Value;

/**
 * A playback configuration describes how the playback functionality will work. The default configuration
 * uses a 20ms step.
 */
@Value
public class PlaybackConfiguration {

    public static final int DEFAULT_TICK_PERIOD = 20;
    public static final TimeUnit DEFAULT_TICK_UNIT = TimeUnit.MILLISECONDS;

    int tickPeriod;
    TimeUnit tickUnit;

    /**
     * The default playback configuration.
     */
    public static final PlaybackConfiguration defaultConfiguration
            = new PlaybackConfiguration(DEFAULT_TICK_PERIOD, DEFAULT_TICK_UNIT);

}
