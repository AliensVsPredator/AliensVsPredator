package com.blib.api.common.event.v1;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface BLibPlayerTrackingEntityEvent {

    void invoke(Entity trackedEntity, Player player);
}
