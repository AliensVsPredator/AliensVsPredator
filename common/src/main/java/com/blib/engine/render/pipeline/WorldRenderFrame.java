package com.blib.engine.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;

/**
 * Per-frame parameter bundle passed to every {@link WorldPass}. The fields mirror the arguments
 * {@code DebugRenderer.render} hands the engine's mixin entry, hoisted into a single value so the pipeline can dispatch
 * passes without exploding into 5-argument method signatures everywhere.
 *
 * @param poseStack    the pose stack already positioned at the camera
 * @param bufferSource the multi-buffer source for queued geometry
 * @param camX         camera world position X
 * @param camY         camera world position Y
 * @param camZ         camera world position Z
 */
@ApiStatus.Internal
public record WorldRenderFrame(
    PoseStack poseStack,
    MultiBufferSource.BufferSource bufferSource,
    double camX,
    double camY,
    double camZ
) {}
