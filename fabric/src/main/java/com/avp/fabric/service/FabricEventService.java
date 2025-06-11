package com.avp.fabric.service;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.service.EventService;

public class FabricEventService implements EventService {

    @Override
    public boolean beforeBlockBreak(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, blockPos, blockState, null);
    }
}
