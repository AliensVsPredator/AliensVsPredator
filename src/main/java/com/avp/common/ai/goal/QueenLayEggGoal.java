package com.avp.common.ai.goal;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.util.AlienVariantUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Random;

public class QueenLayEggGoal extends Goal {

    private final Queen queen;
    private static final int cooldownTicks = 1200;
    private int timer;
    private final Random random;

    public QueenLayEggGoal(Queen queen) {
        this.queen = queen;
        this.random = new Random();
        this.timer = cooldownTicks;
    }

    @Override
    public boolean canUse() {
        var nearbyEggs = queen.level().getEntitiesOfClass(
                Ovamorph.class,
                queen.getBoundingBox().inflate(4), // 4 block radius
                entity -> entity.getType().is(AVPEntityTypeTags.OVAMORPHS)
        );
        return nearbyEggs.isEmpty() && queen.isAlive() && !queen.isAggressive();
    }

    @Override
    public void start() {
        timer = cooldownTicks;
        var queenPosition = queen.blockPosition();
        var isRoyal = random.nextInt(100) < 5;
        var ovamorphType = AlienVariantUtil.getOvamorphTypeFor(queen, isRoyal);

        if (queen.level() instanceof ServerLevel serverLevel) {
            ovamorphType.spawn(serverLevel, null, null, queenPosition, MobSpawnType.MOB_SUMMONED, false, false);
        }
    }

    @Override
    public void tick() {
        if (timer > 0) {
            timer--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
