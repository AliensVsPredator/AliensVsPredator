package com.blib.internal.client.shader;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry of BLib's custom GLSL shaders. Each loader's client startup iterates {@link #ALL} and registers them against
 * MC's shader-load lifecycle.
 */
@ApiStatus.Internal
public final class BLibShaders {

    private static final List<BLibShader> MUTABLE = new ArrayList<>();

    public static final List<BLibShader> ALL = Collections.unmodifiableList(MUTABLE);

    private static BLibShader register(BLibShader shader) {
        MUTABLE.add(shader);
        return shader;
    }

    private BLibShaders() {
        throw new UnsupportedOperationException();
    }
}
