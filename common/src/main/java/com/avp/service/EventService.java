package com.avp.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(forRemoval = true)
public interface EventService {

    boolean beforeBlockBreak(Level level, BlockPos blockPos, BlockState blockState, Player player);
}
