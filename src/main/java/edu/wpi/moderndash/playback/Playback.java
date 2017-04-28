package edu.wpi.moderndash.playback;

import com.google.inject.Singleton;
import edu.wpi.first.wpilib.logging.ObservableValued;
import edu.wpi.first.wpilib.logging.Recorder;
import edu.wpi.first.wpilib.logging.replay.Replay;
import edu.wpi.first.wpilib.logging.replay.ReplayManager;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.wpi.first.wpilib.logging.RecorderKt.createObserving;

/**
 * Handles playback for elements in the dashboard.
 */
@Log
@Singleton
public class Playback {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Map<ObservableValued<?>, Recorder<?>> recorders = new HashMap<>();
    private final Collection<ReplayManager<?>> replays = new HashSet<>();

    /**
     * Registers a valued object for playback. Has no effect if it's already registered.
     *
     * @throws IllegalStateException if playback is running when this is called
     */
    public void register(@NonNull ObservableValued<?> valued) {
        if (running.get()) {
            throw new IllegalStateException("Cannot register a valued while playback is running");
        }
        synchronized (recorders) {
            if (recorders.containsKey(valued)) {
                log.warning("The valued object '" + valued + "' has already been registered");
            }
            recorders.put(valued, createObserving(valued).start());
        }
    }

    /**
     * Unregisters a valued object from playback. Normally used if a component is removed from the dashboard.
     */
    public void unregister(@NonNull ObservableValued<?> valued) {
        synchronized (recorders) {
            recorders.remove(valued);
        }
    }

    /**
     * Checks if playback is currently running.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Starts running the playback.
     *
     * @throws IllegalStateException if playback is already running
     */
    public void startPlayback() {
        if (running.compareAndSet(false, true)) {
            recorders.values().stream()
                    .peek(Recorder::pause) // stop recording values for now
                    .map(recorder -> new Replay<>(recorder))
                    .map(replay -> new ReplayManager<>(replay, false))
                    .peek(replays::add)
                    .forEach(ReplayManager::start);
        } else {
            throw new IllegalStateException("Playback is already running");
        }
    }

    /**
     * Stops running the playback. Does nothing if playback isn't running.
     */
    public void stopPlayback() {
        recorders.keySet().forEach(v -> v.setEnableObservers(true));
        recorders.values().forEach(Recorder::start);
        replays.forEach(ReplayManager::stop);
        running.set(false);
    }

    public boolean isFinished() {
        return replays.stream().allMatch(ReplayManager::isFinished);
    }

}
