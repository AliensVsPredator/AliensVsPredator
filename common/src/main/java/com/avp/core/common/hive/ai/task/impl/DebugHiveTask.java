package com.avp.core.common.hive.ai.task.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

import com.avp.core.common.hive.Hive;
import com.avp.core.common.hive.HiveConstants;
import com.avp.core.common.hive.ai.task.HiveTask;

public class DebugHiveTask extends HiveTask {

    public DebugHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void run() {
        var level = hive.level();
        var centerPos = hive.centerPosition();
        var centerBlockState = level.getBlockState(centerPos);

        if (hive.isDebugEnabled()) {
            if (hive.isDebugMarkHiveCenterEnabled() && !centerBlockState.is(HiveConstants.DEBUG_BLOCK)) {
                // Set to debug block if not present.
                level.setBlock(centerPos, HiveConstants.DEBUG_BLOCK.defaultBlockState(), 3);
            }

            var hiveLeader = hive.hiveLeaderOrNull();

            if (hiveLeader != null && hive.isDebugLeaderHighlightEnabled()) {
                if (hive.ageInTicks() % 20 == 0) {
                    var effect = new MobEffectInstance(MobEffects.GLOWING, 40, 3, true, false, true);

                    hiveLeader.addEffect(effect);
                }
            }

            if (hive.isDebugHiveMemberHighlightEnabled()) {
                var effect = new MobEffectInstance(MobEffects.GLOWING, 40, 3, true, false, true);

                hive.hiveMemberDataMap()
                    .keySet()
                    .stream()
                    .map(((ServerLevel) level)::getEntity)
                    .filter(Objects::nonNull)
                    .forEach(entity -> {
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.addEffect(effect);
                        }
                    });
            }

        } else if (centerBlockState.is(HiveConstants.DEBUG_BLOCK)) {
            // Remove debug block if present.
            level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
