package com.blib.api.client.animation.v1.track;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class AzAnimationTrackContainer<T> {

    private final Map<String, AzAnimationTrack<T>> animationTracksByName;

    public AzAnimationTrackContainer() {
        this.animationTracksByName = new Object2ObjectArrayMap<>();
    }

    @SafeVarargs
    public final void add(
        AzAnimationTrack<T> track,
        AzAnimationTrack<T>... tracks
    ) {
        animationTracksByName.put(track.name(), track);

        for (var extraTrack : tracks) {
            animationTracksByName.put(extraTrack.name(), extraTrack);
        }
    }

    /**
     * Registers a track against a typed handle. Validates that the handle's name matches the track's
     * name — a mismatch is a registration bug and throws immediately. Returns the container for
     * chaining.
     */
    public AzAnimationTrackContainer<T> register(AzTrackHandle<T> handle, AzAnimationTrack<T> track) {
        if (!handle.name().equals(track.name())) {
            throw new IllegalArgumentException(
                "Track handle/track name mismatch: handle declares '%s' but track is named '%s'"
                    .formatted(handle.name(), track.name())
            );
        }

        animationTracksByName.put(track.name(), track);
        return this;
    }

    public @Nullable AzAnimationTrack<T> getOrNull(String trackName) {
        return animationTracksByName.get(trackName);
    }

    public Collection<AzAnimationTrack<T>> getAll() {
        return animationTracksByName.values();
    }
}
