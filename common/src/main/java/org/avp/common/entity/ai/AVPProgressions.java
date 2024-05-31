package org.avp.common.entity.ai;

import org.avp.api.entity.ai.ProgressKey;

public class AVPProgressions {
    public static final ProgressKey ATTACH_TO_HOST = new ProgressKey("attach_to_host");
    public static final ProgressKey FIND_A_TARGET = new ProgressKey("find_a_target");
    public static final ProgressKey MOVE_TO_TARGET = new ProgressKey("move_to_target");

    private AVPProgressions() {
        throw new UnsupportedOperationException();
    }
}
