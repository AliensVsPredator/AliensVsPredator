package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.Sizing;

/**
 * Immutable, serializable mirror of {@link Sizing}. Exists so persisted layouts can capture a snapshot of the live
 * (mutable) {@link Sizing} instance without exposing public mutable fields to Gson, and so {@link Sizing} itself stays
 * a runtime-only type with no JSON adapter coupling.
 * <p>
 * Each variant maps 1:1 onto the corresponding live {@link Sizing} subclass:
 * <ul>
 * <li>{@link Ratio} ↔ {@link Sizing.Ratio} (proportional split)</li>
 * <li>{@link FirstFixed} ↔ {@link Sizing.FirstFixed} (fixed-pixel first child)</li>
 * <li>{@link SecondFixed} ↔ {@link Sizing.SecondFixed} (fixed-pixel second child)</li>
 * </ul>
 * The convert pair {@link #of} / {@link #toMutable} round-trips losslessly.
 */
@ApiStatus.Internal
public sealed interface SizingDoc {

    record Ratio(float value) implements SizingDoc {

        @Override
        public Sizing toMutable() {
            return new Sizing.Ratio(value);
        }
    }

    record FirstFixed(int pixels) implements SizingDoc {

        @Override
        public Sizing toMutable() {
            return new Sizing.FirstFixed(pixels);
        }
    }

    record SecondFixed(int pixels) implements SizingDoc {

        @Override
        public Sizing toMutable() {
            return new Sizing.SecondFixed(pixels);
        }
    }

    /**
     * Build a fresh mutable {@link Sizing} from this doc. Each call produces a new instance so resize drags on the
     * loaded layout don't propagate back into a shared snapshot.
     */
    Sizing toMutable();

    /** Snapshot a live {@link Sizing} into its immutable doc form. */
    static SizingDoc of(Sizing s) {
        return switch (s) {
            case Sizing.Ratio r -> new Ratio(r.value);
            case Sizing.FirstFixed f -> new FirstFixed(f.pixels);
            case Sizing.SecondFixed sf -> new SecondFixed(sf.pixels);
        };
    }
}
