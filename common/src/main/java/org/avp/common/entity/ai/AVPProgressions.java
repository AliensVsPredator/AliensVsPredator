package org.avp.common.entity.ai;

import org.avp.api.entity.ai.progress.ProgressKey;

public class AVPProgressions {
    // Ovamorph
    public static final ProgressKey OPEN = new ProgressKey("open");
    public static final ProgressKey RELEASE_FACEHUGGER = new ProgressKey("release_facehugger");

    private AVPProgressions() {
        throw new UnsupportedOperationException();
    }
}
