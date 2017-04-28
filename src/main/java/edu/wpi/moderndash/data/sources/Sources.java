package edu.wpi.moderndash.data.sources;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 */
@Singleton
public class Sources {

    @Inject
    private ConnectionSource connectionSource;
    @Inject
    private RobotStateSource robotStateSource;


}
