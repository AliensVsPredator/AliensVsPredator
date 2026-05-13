package com.blib.engine.render.pipeline;

import org.jetbrains.annotations.ApiStatus;

/**
 * One stage of the engine's world-space render pipeline. Replaces the prior pattern where the mixin entry hard-coded a
 * sequence of 15 inline {@code Foo.render(...)} calls — adding a new render stage now means registering a
 * {@link WorldPass} with {@link WorldPassPipeline}, and re-ordering becomes a one-line change in the pipeline build
 * order instead of a search-and-edit across the mixin.
 * <p>
 * Passes are stateless wrappers over existing static renderers; the pipeline owns the ordering decision and the gating
 * for engine-active state.
 */
@ApiStatus.Internal
public interface WorldPass {

    /** Stable id for debugging / future per-pass toggles. */
    String id();

    /** Execute the pass against the current frame. Implementations typically delegate to a static renderer. */
    void render(WorldRenderFrame frame);
}
