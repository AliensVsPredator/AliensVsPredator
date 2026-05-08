package com.blib.internal.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blib.mod.BLib;

/**
 * Registry of BLib's custom GLSL shaders. Each loader's client startup iterates {@link #ALL} and registers them against
 * MC's shader-load lifecycle; consumers reference shaders by name (e.g. {@link #ENGINE_SELECTION}) and check
 * {@code instance() != null} before drawing (the reference is populated asynchronously by the resource loader).
 */
@ApiStatus.Internal
public final class BLibShaders {

    private static final List<BLibShader> MUTABLE = new ArrayList<>();

    public static final List<BLibShader> ALL = Collections.unmodifiableList(MUTABLE);

    /**
     * Selection-volume shader: pass-through {@code POSITION_COLOR} pipeline. Used by the engine-mode selection renderer
     * to draw the selected entity's bounding volume in a translucent overlay color. Kept deliberately minimal so it's a
     * clean foundation for layering more interesting effects (rim glow, pulse, etc.) later.
     */
    public static final BLibShader ENGINE_SELECTION = register(
        new BLibShader(BLib.MOD.resources().createLocation("engine_selection"), DefaultVertexFormat.POSITION_COLOR)
    );

    private static BLibShader register(BLibShader shader) {
        MUTABLE.add(shader);
        return shader;
    }

    private BLibShaders() {
        throw new UnsupportedOperationException();
    }
}
