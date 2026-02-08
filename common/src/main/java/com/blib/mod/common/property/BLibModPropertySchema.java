package com.blib.mod.common.property;

import com.blib.api.common.property.v1.BLibPropertySchema;

public class BLibModPropertySchema {

    static final BLibPropertySchema SCHEMA = BLibPropertySchema.builder()
        .withPropertyValueAlignment(true)
        .addComment("Number of world state entries per page in the GOAP debug HUD.")
        .addProperty(
            BLibModProperties.Goap.Debug.WORLD_STATE_PAGE_SIZE.key(),
            BLibModProperties.Goap.Debug.WORLD_STATE_PAGE_SIZE.defaultValue()
        )
        .addComment("Tick interval for GOAP debug updates on dedicated servers.")
        .addProperty(
            BLibModProperties.Goap.Debug.DEDICATED_TICK_INTERVAL.key(),
            BLibModProperties.Goap.Debug.DEDICATED_TICK_INTERVAL.defaultValue()
        )
        .build();

    private BLibModPropertySchema() {
        throw new UnsupportedOperationException();
    }
}
