package com.blib.engine.ui;

import org.jetbrains.annotations.ApiStatus;

/**
 * How a {@link DockNode.Split} divides its rectangle between its two children. Mutable so the workspace screen can
 * resize panels by drag-updating an existing instance instead of rebuilding the dock tree each frame.
 * <ul>
 * <li>{@link Ratio} — first child gets {@code value * total}, second gets the remainder. Use for resizable workspace
 * regions like a viewport / sidebar pair where both should grow with the window.</li>
 * <li>{@link FirstFixed} — first child gets exactly {@code pixels}, second takes the rest. Use for top-anchored
 * fixed-height bars (menu bars, toolbars) and left-anchored fixed-width panels (outliner).</li>
 * <li>{@link SecondFixed} — second child gets exactly {@code pixels}, first takes the rest. Use for bottom-anchored
 * bars (status bar, content browser) and right-anchored panels (details).</li>
 * </ul>
 */
@ApiStatus.Internal
public sealed interface Sizing permits Sizing.Ratio, Sizing.FirstFixed, Sizing.SecondFixed {

    final class Ratio implements Sizing {

        public float value;

        public Ratio(float value) {
            if (value <= 0f || value >= 1f) {
                throw new IllegalArgumentException("ratio must be in (0, 1), got " + value);
            }

            this.value = value;
        }
    }

    final class FirstFixed implements Sizing {

        public int pixels;

        public FirstFixed(int pixels) {
            if (pixels < 0) {
                throw new IllegalArgumentException("pixels must be non-negative, got " + pixels);
            }

            this.pixels = pixels;
        }
    }

    final class SecondFixed implements Sizing {

        public int pixels;

        public SecondFixed(int pixels) {
            if (pixels < 0) {
                throw new IllegalArgumentException("pixels must be non-negative, got " + pixels);
            }

            this.pixels = pixels;
        }
    }
}
