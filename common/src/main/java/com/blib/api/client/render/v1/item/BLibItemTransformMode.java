package com.blib.api.client.render.v1.item;

/**
 * Which transform set a {@link BLibTunableItemTransforms} reads from. Mirrors the idle / blocking
 * split that {@link BLibGeoBoneItemRendererConfig} carries. The tuner command keeps a separate override map for each
 * mode so you can dial in the held-but-idle pose independently from the actively-blocking pose.
 */
public enum BLibItemTransformMode {
    IDLE,
    BLOCKING
}
