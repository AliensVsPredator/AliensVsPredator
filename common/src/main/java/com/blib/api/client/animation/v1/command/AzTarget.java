package com.blib.api.client.animation.v1.command;

import java.util.List;
import java.util.function.Consumer;

import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;

/**
 * The set of tracks an action applies to.
 *
 * <p>Construct via the static factories: {@link #track(String)}, {@link #tracks(String...)}, or
 * {@link #allTracks()}. With a static import these read naturally:</p>
 *
 * <pre>{@code
 * import static com.blib.api.client.animation.v1.command.AzTarget.*;
 *
 * AzCommand.replay()
 *     .play(track("right_arm"), "claw_swipe", PLAY_ONCE)
 *     .setSpeed(allTracks(), 1.5f)
 *     .cancel(tracks("head", "tail"))
 *     .build();
 * }</pre>
 *
 * <p>Targets that name nonexistent tracks silently skip those entries — resolution happens at
 * dispatch time against whatever tracks the animator actually has.</p>
 */
public sealed interface AzTarget {

    static AzTarget track(String name) {
        return new Single(name);
    }

    static AzTarget tracks(String... names) {
        return new Multiple(List.of(names));
    }

    static AzTarget allTracks() {
        return All.INSTANCE;
    }

    /**
     * Resolves this target against the given container and applies {@code action} to each
     * matching track. Missing tracks (in {@link Single} or {@link Multiple}) are skipped.
     */
    void forEach(AzAnimationTrackContainer<?> container, Consumer<AzAnimationTrack<?>> action);

    record Single(String trackName) implements AzTarget {

        @Override
        public void forEach(AzAnimationTrackContainer<?> container, Consumer<AzAnimationTrack<?>> action) {
            var track = container.getOrNull(trackName);

            if (track != null) {
                action.accept(track);
            }
        }
    }

    record Multiple(List<String> trackNames) implements AzTarget {

        @Override
        public void forEach(AzAnimationTrackContainer<?> container, Consumer<AzAnimationTrack<?>> action) {
            for (var name : trackNames) {
                var track = container.getOrNull(name);

                if (track != null) {
                    action.accept(track);
                }
            }
        }
    }

    record All() implements AzTarget {

        public static final All INSTANCE = new All();

        @Override
        public void forEach(AzAnimationTrackContainer<?> container, Consumer<AzAnimationTrack<?>> action) {
            container.getAll().forEach(action);
        }
    }
}
