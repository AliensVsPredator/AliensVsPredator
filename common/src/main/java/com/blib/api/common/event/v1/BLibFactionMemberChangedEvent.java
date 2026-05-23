package com.blib.api.common.event.v1;

import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.faction.v1.FactionMember;

@FunctionalInterface
public interface BLibFactionMemberChangedEvent {

    void invoke(ResourceLocation factionId, FactionMember member, boolean added);
}
