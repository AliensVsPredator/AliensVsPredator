package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.avp.AVP;
import com.avp.common.entity.living.yautja.Yautja;
import com.avp.common.util.AVPPredicates;

public class TripMineBlockEntity extends BlockEntity {

    private boolean triggered = false;

    private int countdown = 0;

    public TripMineBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.TRIP_MINE, pos, blockState);
    }

    @SuppressWarnings("unused")
    public static void serverTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        TripMineBlockEntity tripMineBlockEntity
    ) {
        if (level.isClientSide) {
            return;
        }

        var detectionArea = new AABB(blockPos).inflate(AVP.config.blockConfigs.TRIP_MINE_SEARCH_RADIUS);
        var entities = level.getEntitiesOfClass(LivingEntity.class, detectionArea, entity -> {
            if (entity instanceof Player player) {
                return !AVPPredicates.IS_IMMORTAL.test(player);
            }

            return !(entity instanceof Yautja);
        });

        if (!entities.isEmpty()) {
            if (!tripMineBlockEntity.isTriggered()) {
                tripMineBlockEntity.setTriggered(true);
                tripMineBlockEntity.setCountdown(100);
            } else {
                tripMineBlockEntity.decrementCountdown();

                if (tripMineBlockEntity.getCountdown() % 20 == 0) {
                    level.playSound(
                        null,
                        blockPos,
                        SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
                        SoundSource.BLOCKS,
                        1.0F,
                        0.8F + level.random.nextFloat() * 0.4F
                    );
                }

                if (tripMineBlockEntity.getCountdown() <= 0) {
                    level.explode(
                        null,
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 0.5,
                        blockPos.getZ() + 0.5,
                        4.0F, // Explosion power (adjust as needed)
                        Level.ExplosionInteraction.TNT
                    );
                    level.removeBlock(blockPos, false);
                }
            }
        } else {
            if (tripMineBlockEntity.isTriggered()) {
                tripMineBlockEntity.setTriggered(false);
                tripMineBlockEntity.setCountdown(0);
            }
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void decrementCountdown() {
        if (countdown > 0) {
            countdown--;
        }
    }
}
