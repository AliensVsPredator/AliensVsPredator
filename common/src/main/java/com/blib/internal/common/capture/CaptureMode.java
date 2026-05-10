package com.blib.internal.common.capture;

import org.jetbrains.annotations.ApiStatus;

/**
 * What flavor of block capture the user requested. {@link #GENERAL} produces a single {@code .nbt} file under the
 * project's {@code captures/} folder; {@link #JIGSAW} splits the volume into ≤48³ sub-pieces, stitches them with jigsaw
 * connector blocks, and writes them into the project's datapack tree alongside an auto-generated pool JSON.
 * <p>
 * Wire-encoded as the enum's ordinal — keep the order stable across releases.
 */
@ApiStatus.Internal
public enum CaptureMode {

    GENERAL,

    JIGSAW;

    public static CaptureMode fromOrdinal(int ordinal) {
        var values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return GENERAL;
        }
        return values[ordinal];
    }
}
