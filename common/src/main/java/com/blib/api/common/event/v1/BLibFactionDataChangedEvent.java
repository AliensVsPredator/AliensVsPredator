package com.blib.api.common.event.v1;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface BLibFactionDataChangedEvent {

    void invoke(ResourceLocation factionId, Kind kind);

    enum Kind {
        NAME,
        COLOR,
        CLAIM_VISIBILITY,
        BLOCK_BREAK_PROTECTION,
        BLOCK_INTERACT_PROTECTION,
        ENTITY_INTERACT_PROTECTION,
        NONLIVING_ENTITY_ATTACK_PROTECTION,
        ALLOW_PVP,
        ALLOW_EXPLOSIONS,
        ALLOW_MOB_GRIEFING
    }
}
