package com.blib.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BLibBlockBreakEvent {

    boolean invoke(Level level, Player player, BlockPos blockPos, BlockState blockState);
}
