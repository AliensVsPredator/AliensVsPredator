package com.blib.mod.common.property;

import com.blib.api.common.property.v1.BLibPropertyKey;
import com.blib.api.common.property.v1.serializer.BLibPropertySerializers;

public class BLibModProperties {

    public static class Goap {

        private static final BLibPropertyKey.Parent GOAP = BLibPropertyKey.parent("goap");

        public static class Debug {

            private static final BLibPropertyKey.Parent DEBUG = GOAP.child("debug");

            public static final BLibModProperty<Integer> WORLD_STATE_PAGE_SIZE = new BLibModProperty<>(
                DEBUG.leaf("world_state_page_size", BLibPropertySerializers.INT),
                30
            );

            public static final BLibModProperty<Integer> DEDICATED_TICK_INTERVAL = new BLibModProperty<>(
                DEBUG.leaf("dedicated_tick_interval", BLibPropertySerializers.INT),
                10
            );
        }
    }

    private BLibModProperties() {
        throw new UnsupportedOperationException();
    }
}
