package org.avp.common.entity.ai;

import org.avp.api.entity.ai.ProgressKey;

public class AVPProgressions {
    // Generic
    public static final ProgressKey MOVE_TO_TARGET = new ProgressKey("move_to_target");

    // Ovamorph
    public static final ProgressKey OPEN = new ProgressKey("open");
    public static final ProgressKey RELEASE_FACEHUGGER = new ProgressKey("release_facehugger");

    // Parasite
    public static final ProgressKey ATTACH_TO_HOST = new ProgressKey("attach_to_host");

    private AVPProgressions() {
        throw new UnsupportedOperationException();
    }
}
