package com.blib.service;

import com.blib.event.BLibEventRouter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BLibEventService {

    BLibEventRouter<LevelTickEvent> afterLevelTick();

    BLibEventRouter<BlockBreakEvent> beforeBlockBreak();

    BLibEventRouter<LevelTickEvent> beforeLevelTick();

    BLibEventRouter<TagsUpdatedEvent> onTagsUpdated();

    interface BlockBreakEvent {
        boolean invoke(Level level, Player player, BlockPos blockPos, BlockState blockState);
    }

    interface LevelTickEvent {
        void invoke(Level level);
    }

    interface TagsUpdatedEvent {
        void invoke(RegistryAccess registryAccess, boolean fromClientPacket);
    }
}
