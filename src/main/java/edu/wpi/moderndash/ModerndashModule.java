package edu.wpi.moderndash;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.data.sources.Sources;
import edu.wpi.moderndash.io.IPResolver;
import edu.wpi.moderndash.playback.PlaybackConfiguration;
import edu.wpi.moderndash.robot.FrcRobotConnection;
import edu.wpi.moderndash.robot.RobotConnection;
import edu.wpi.moderndash.robot.RobotStateObserver;

/**
 * Guice injection module. All globally-required values should be bound here rather than being defined in some
 * static "Globals" class. For example, rather than defining
 * <pre><code>
 *     public final class NetworkTableGlobals {
 *         public static final ITable ROBOT_INFO_TABLE = NetworkTable.getTable("...");
 *     }
 *     ...
 *     private ITable robotInfo = NetworkTableGlobals.ROBOT_INFO_TABLE:
 * </code></pre>
 *
 * it's better to bind it in this module
 * <pre><code>
 *     bind(ITable.class)
 *          .annotatedWith(Names.named("..."))
 *          .toInstance(NetworkTables.getTable("...");
 *     ...
 *    {@literal @}Inject
 *    {@literal @}Named("...")
 *     private ITable robotInfo;
 * </code></pre>
 *
 * It's a little more work to get set up, but has a few advantages:
 *
 * <ul>
 * <li>Values can be changed to mock or stub implementations in tests, which is impossible to do with global static state</li>
 * <li>Values defined in a module can be used in FXML and custom controllers</li>
 * </ul>
 *
 * Bound values defined in this (or any) Guice module should be injected into constructors. The only exception to this
 * rule should be for FXML controllers, which must have a public default constructor (overloaded constructors are allowed,
 * but are pointless since the FXML loader only calls the default constructor).
 */
public class ModerndashModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RobotConnection.class).to(FrcRobotConnection.class);
        bind(Preferences.class).asEagerSingleton();
        bind(ITable.class)
                .annotatedWith(Names.named("RobotInfoTable"))
                .toProvider(() -> NetworkTable.getTable("RobotInfo"));
        bind(IPResolver.class).asEagerSingleton();
        bind(RobotStateObserver.class).asEagerSingleton();
        bind(PlaybackConfiguration.class).toInstance(PlaybackConfiguration.defaultConfiguration);
        bind(Sources.class).toInstance(new Sources());
    }

}
