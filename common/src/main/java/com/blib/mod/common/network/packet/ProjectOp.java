package com.blib.mod.common.network.packet;

/**
 * Discriminator for {@link S2CProjectOpResultPayload} so the client knows which originating action a success/failure
 * reply corresponds to. Encoded on the wire as the enum's ordinal — keep the order stable across releases or older
 * clients will mis-route results. New variants are appended only.
 */
public enum ProjectOp {

    CREATE,

    DELETE,

    OPEN,

    RELOAD,

    CAPTURE;

    public static ProjectOp fromOrdinal(int ordinal) {
        var values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return CREATE;
        }
        return values[ordinal];
    }
}
