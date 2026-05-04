package com.blib.api.client.animation.v1.track;

/**
 * A typed track identifier. Declared as a {@code static final} on the animator class that owns the track, parameterized
 * by the animatable type:
 *
 * <pre>{@code
 * public class WarriorAnimator extends AzEntityAnimator<Warrior> {
 *
 *     public static final AzTrackHandle<Warrior> BODY = AzTrackHandle.declare("body");
 *
 *     public static final AzTrackHandle<Warrior> RIGHT_ARM = AzTrackHandle.declare("right_arm");
 *
 *     @Override
 *     public void registerTracks(AzAnimationTrackContainer<Warrior> container) {
 *         container.register(BODY, AzAnimationTrack.builder(this, BODY).build());
 *         container.register(RIGHT_ARM, AzAnimationTrack.builder(this, RIGHT_ARM).build());
 *     }
 * }
 * }</pre>
 * <p>
 * Used at the dispatch site via {@link com.blib.api.client.animation.v1.command.AzTarget#track(AzTrackHandle)}.
 * Compared to passing track names as strings, the handle catches typos at compile time and binds the track to its
 * owning animator family via the {@code <T>} parameter.
 * </p>
 * <p>
 * The {@code <T>} parameter does not currently propagate into {@code AzCommand} — the command type is non-generic, so a
 * handle from one animator family can still be used in a command dispatched to another. That gap is addressed by the
 * runtime track lookup (which logs / throws on miss after Phase 2.9) and would be closed by a future Phase 4
 * generic-command refactor.
 * </p>
 */
public final class AzTrackHandle<T> {

    private final String name;

    private AzTrackHandle(String name) {
        this.name = name;
    }

    /**
     * Declares a track handle with the given name, scoped to the animatable type {@code T}. The handle is a pure
     * compile-time tag — it does not register the track. Pair with
     * {@link AzAnimationTrackContainer#register(AzTrackHandle, AzAnimationTrack)} inside the animator's
     * {@code registerTracks(...)} to bind the handle to a real track.
     */
    public static <T> AzTrackHandle<T> declare(String name) {
        return new AzTrackHandle<>(name);
    }

    public String name() {
        return name;
    }
}
