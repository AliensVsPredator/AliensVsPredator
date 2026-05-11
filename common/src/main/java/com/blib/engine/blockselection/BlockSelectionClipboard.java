package com.blib.engine.blockselection;

import org.jetbrains.annotations.ApiStatus;

/**
 * Client-side mirror of the server's clipboard state. Updated by {@code S2CClipboardStatusPayload} after each Cut /
 * Copy operation; the panel reads {@link #hasContents()} to enable/disable the Paste button and {@link #sizeX()} /
 * {@link #sizeY()} / {@link #sizeZ()} for future paste-preview rendering.
 * <p>
 * Carries no actual block NBT — the heavy data lives server-side in {@code ServerBlockClipboard}, keyed implicitly by
 * the single-player session. Keeping NBT off the wire avoids per-paste packet roundtrips for what may be a sizable
 * snapshot; the server already has the data and can paste directly when {@code C2SPasteFromClipboardPayload} arrives.
 * <p>
 * Cleared on engine workspace close. Single-player only.
 */
@ApiStatus.Internal
public final class BlockSelectionClipboard {

    private static boolean hasContents;

    private static int sizeX;

    private static int sizeY;

    private static int sizeZ;

    /** Server-side timestamp (millis) when the clipboard was filled — used to display "copied N seconds ago". */
    private static long filledAtMs;

    private BlockSelectionClipboard() {}

    public static boolean hasContents() {
        return hasContents;
    }

    public static int sizeX() {
        return sizeX;
    }

    public static int sizeY() {
        return sizeY;
    }

    public static int sizeZ() {
        return sizeZ;
    }

    public static long filledAtMs() {
        return filledAtMs;
    }

    public static void update(boolean has, int sx, int sy, int sz, long when) {
        hasContents = has;
        sizeX = sx;
        sizeY = sy;
        sizeZ = sz;
        filledAtMs = when;
    }

    public static void clear() {
        hasContents = false;
        sizeX = 0;
        sizeY = 0;
        sizeZ = 0;
        filledAtMs = 0L;
    }
}
