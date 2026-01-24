package com.blib.api.common.entity.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class TeleportUtil {

    public static boolean teleport(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity.isAlive()) {
            double d0 = entity.getX() + (entity.getRandom().nextDouble() - (double) 0.5F) * (double) 64.0F;
            double d1 = entity.getY() + (double) (entity.getRandom().nextInt(64) - 32);
            double d2 = entity.getZ() + (entity.getRandom().nextDouble() - (double) 0.5F) * (double) 64.0F;
            return teleport(entity, d0, d1, d2);
        } else {
            return false;
        }
    }

    public static boolean teleportTowards(LivingEntity entity, Entity target) {
        Vec3 vec3 = new Vec3(entity.getX() - target.getX(), entity.getY((double) 0.5F) - target.getEyeY(), entity.getZ() - target.getZ());
        vec3 = vec3.normalize();
        double d1 = entity.getX() + (entity.getRandom().nextDouble() - (double) 0.5F) * (double) 8.0F - vec3.x * (double) 16.0F;
        double d2 = entity.getY() + (double) (entity.getRandom().nextInt(16) - 8) - vec3.y * (double) 16.0F;
        double d3 = entity.getZ() + (entity.getRandom().nextDouble() - (double) 0.5F) * (double) 8.0F - vec3.z * (double) 16.0F;
        return teleport(entity, d1, d2, d3);
    }

    public static boolean teleport(LivingEntity entity, double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (
            blockpos$mutableblockpos.getY() > entity.level().getMinBuildHeight() && !entity.level()
                .getBlockState(blockpos$mutableblockpos)
                .blocksMotion()
        ) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = entity.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);

        if (flag && !flag1) {
            Vec3 vec3 = entity.position();
            boolean flag2 = entity.randomTeleport(x, y, z, true);
            if (flag2) {
                entity.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                if (!entity.isSilent()) {
                    entity.level()
                        .playSound(
                            (Player) null,
                            entity.xo,
                            entity.yo,
                            entity.zo,
                            SoundEvents.ENDERMAN_TELEPORT,
                            entity.getSoundSource(),
                            1.0F,
                            1.0F
                        );
                    entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }
}
