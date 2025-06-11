package com.avp.neoforge.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import com.avp.service.EventService;

public class NeoForgeEventService implements EventService {

    @Override
    public boolean beforeBlockBreak(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        var event = new BlockEvent.BreakEvent(level, blockPos, blockState, player);
        return !NeoForge.EVENT_BUS.post(event).isCanceled();
    }
}
