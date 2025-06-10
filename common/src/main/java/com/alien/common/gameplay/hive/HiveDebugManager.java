package com.alien.common.gameplay.hive;

import com.alien.common.constant.HiveConstants;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;

import com.avp.AVP;

public class HiveDebugManager {

    private final Hive hive;

    public HiveDebugManager(Hive hive) {
        this.hive = hive;
    }

    public void tick() {
        var level = hive.level();
        var centerPos = hive.centerPosition();
        var centerBlockState = level.getBlockState(centerPos);
        var debugManager = hive.getDebugManager();

        if (debugManager.isDebugEnabled()) {
            runDebugRoutines();
        } else if (centerBlockState.is(HiveConstants.DEBUG_BLOCK)) {
            // Remove debug block if present.
            level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public void onHiveRemoved() {
        var level = hive.level();
        var centerPos = hive.centerPosition();

        if (isDebugEnabled() && isDebugMarkHiveCenterEnabled() && level.getBlockState(centerPos).is(HiveConstants.DEBUG_BLOCK)) {
            level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private void runDebugRoutines() {
        var level = hive.level();
        var centerPos = hive.centerPosition();
        var centerBlockState = level.getBlockState(centerPos);
        var debugManager = hive.getDebugManager();

        if (debugManager.isDebugMarkHiveCenterEnabled() && !centerBlockState.is(HiveConstants.DEBUG_BLOCK)) {
            // Set to debug block if not present.
            level.setBlock(centerPos, HiveConstants.DEBUG_BLOCK.defaultBlockState(), 3);
        }

        var hiveLeader = hive.getLeadershipManager().getLeaderOrNull();

        if (hiveLeader != null && debugManager.isDebugLeaderHighlightEnabled()) {
            if (hive.ageInTicks() % 20 == 0) {
                var effect = new MobEffectInstance(MobEffects.GLOWING, 40, 3, true, false, true);

                hiveLeader.addEffect(effect);
            }
        }

        if (debugManager.isDebugHiveMemberHighlightEnabled()) {
            var effect = new MobEffectInstance(MobEffects.GLOWING, 40, 3, true, false, true);

            hive.getMembershipManager()
                .getLoadedMembers()
                .forEach(entity -> {
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.addEffect(effect);
                    }
                });
        }
    }

    public boolean isDebugEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_ENABLED;
    }

    public boolean isDebugHiveMemberHighlightEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS;
    }

    public boolean isDebugLeaderHighlightEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_HIGHLIGHT_LEADER;
    }

    public boolean isDebugMarkHiveCenterEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_MARK_HIVE_CENTER;
    }
}
