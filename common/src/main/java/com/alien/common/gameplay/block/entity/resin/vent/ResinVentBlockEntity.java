package com.alien.common.gameplay.block.entity.resin.vent;

import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.level.gameevent.listener.CryForHelpListener;
import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class ResinVentBlockEntity extends BlockEntity implements GameEventListener.Provider<CryForHelpListener> {

    private final CryForHelpListener cryForHelpListener;

    private @Nullable Hive hive;

    public ResinVentBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(AVPBlockEntityTypes.RESIN_VENT.get(), blockPos, blockState);

        var positionSource = new BlockPositionSource(blockPos);

        this.cryForHelpListener = new CryForHelpListener(positionSource);
    }

    public @Nullable Hive getHive() {
        return hive;
    }

    public void setHive(@Nullable Hive hive) {
        this.hive = hive;
    }

    public static void serverTick(Level level, BlockPos ventPos, BlockState blockState, ResinVentBlockEntity resinVentBlockEntity) {
        var hive = resinVentBlockEntity.getHive();

        if (hive != null) {
            if (
                // If the hive is not alive...
                !hive.isAlive()
                    // OR if the hive has moved such that the vent pos is no longer in range...
                    || !hive.getSpaceManager().isBlockPosWithinHive(ventPos)
            ) {
                // ...then we remove the hive reference as we can no longer use it.
                resinVentBlockEntity.setHive(null);
                hive.getVentManager().removeVent(ventPos);
                return;
            }

            hive.getVentManager().addVent(ventPos);
            return;
        }

        if (level.getGameTime() % 20 == 0) {
            // Try and set a hive.
            // FIXME: Check for hive variant here.
            HiveLevelData.getOrCreate(level)
                .andThen(hiveLevelData -> hiveLevelData.findNearestHive(ventPos))
                .filter(nearestHive -> nearestHive.isAlive() && nearestHive.getSpaceManager().isBlockPosWithinHive(ventPos))
                .ifSome(resinVentBlockEntity::setHive);
        }
    }

    @Override
    public @NotNull CryForHelpListener getListener() {
        return cryForHelpListener;
    }
}
