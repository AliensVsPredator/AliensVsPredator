package com.blib.engine.render.pipeline;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered list of {@link WorldPass}es executed each frame in registration order. The mixin entry calls
 * {@link #render(WorldRenderFrame)}; the pipeline iterates its passes. Passes are added at engine bootstrap;
 * reorderable by changing the build order in {@link EngineWorldPasses}.
 */
@ApiStatus.Internal
public final class WorldPassPipeline {

    private final List<WorldPass> passes = new ArrayList<>();

    public void add(WorldPass pass) {
        passes.add(pass);
    }

    public List<WorldPass> passes() {
        return Collections.unmodifiableList(passes);
    }

    public void render(WorldRenderFrame frame) {
        for (var p : passes) {
            try {
                p.render(frame);
            } catch (RuntimeException e) {
                // A single faulty pass shouldn't blank the whole frame; log via the standard renderer log facility once
                // available. For now swallow + continue so user-facing visuals still render.
                org.slf4j.LoggerFactory.getLogger(WorldPassPipeline.class)
                    .warn("[BLib] world pass '{}' threw; continuing pipeline", p.id(), e);
            }
        }
    }
}
