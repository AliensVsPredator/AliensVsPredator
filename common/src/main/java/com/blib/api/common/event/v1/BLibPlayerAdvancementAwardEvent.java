package com.blib.api.common.event.v1;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface BLibPlayerAdvancementAwardEvent {

    void invoke(ServerPlayer player, AdvancementHolder advancementHolder, String criterionKey);
}
