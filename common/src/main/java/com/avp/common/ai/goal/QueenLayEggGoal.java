package com.avp.common.ai.goal;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.concurrent.TimeUnit;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.AlienPredicates;
import com.avp.common.util.AlienVariantUtil;

public class QueenLayEggGoal extends Goal {

    private static final int MAX_EGG_LAY_COOLDOWN_IN_TICKS = (int) TimeUnit.MINUTES.toSeconds(1) * 20;

    private static final int MAX_EGG_SCAN_COOLDOWN_IN_TICKS = 20;

    private final Queen queen;

    private int eggLayCooldownInTicks;

    private int eggScanCooldownInTicks;

    public QueenLayEggGoal(Queen queen) {
        this.queen = queen;
        this.eggLayCooldownInTicks = MAX_EGG_LAY_COOLDOWN_IN_TICKS;
        this.eggScanCooldownInTicks = MAX_EGG_SCAN_COOLDOWN_IN_TICKS;
    }

    @Override
    public boolean canUse() {
        if (eggLayCooldownInTicks > 0) {
            // Egg laying is currently under cooldown, decrement and return.
            eggLayCooldownInTicks--;
            return false;
        }

        if (eggScanCooldownInTicks > 0) {
            // Egg scanning is currently under cooldown, decrement and return.
            eggScanCooldownInTicks--;
            return false;
        }

        // Queen must be alive to lay eggs.
        return queen.isAlive()
            // AND Queen must not be in an aggressive state.
            && !queen.isAggressive()
            // AND Queen must have no target before she lays an egg.
            && queen.getTarget() == null
            && queen.hiveManager()
                .hive()
                // AND Queen must have a hive...
                .isSomeAnd(
                    // And that hive must be alive...
                    hive -> hive.isAlive()
                        // AND chunk loaded...
                        && hive.isChunkLoaded()
                        // AND the queen must be within the hive to lay eggs there.
                        && hive.isEntityWithinHive(queen)
                )
            // AND there must be no other friendly eggs nearby already.
            && noFriendlyEggsNearby();
    }

    private boolean noFriendlyEggsNearby() {
        // Reset scanning cooldown regardless of scanner outcome.
        eggScanCooldownInTicks = MAX_EGG_SCAN_COOLDOWN_IN_TICKS;
        // Scan for friendly ovamorphs.
        return queen.level()
            .getEntitiesOfClass(
                Ovamorph.class,
                // 4 block radius
                queen.getBoundingBox().inflate(4),
                // Must be tagged as an ovamorph...
                entity -> entity.getType().is(AVPEntityTypeTags.OVAMORPHS)
                    // AND must NOT be an enemy alien to the queen (so either a neutral egg, or same strain and same
                    // hive).
                    && !AlienPredicates.areAliensEnemies(queen, entity)
            )
            // If the list of FRIENDLY eggs is empty, then the queen is good to lay an egg.
            .isEmpty();
    }

    @Override
    public void start() {
        // Reset egg lay cooldown since it's (almost) guaranteed that the queen is about to lay an egg.
        eggLayCooldownInTicks = MAX_EGG_LAY_COOLDOWN_IN_TICKS;
        var level = queen.level();
        // Egg has a 5% chance of being royal.
        var isRoyal = queen.getRandom().nextInt(100) < 5;
        var ovamorphType = AlienVariantUtil.getOvamorphTypeFor(queen, isRoyal);

        var ovamorph = ovamorphType.create(level);

        if (ovamorph == null) {
            return;
        }

        ovamorph.setPos(queen.position());

        level.playSound(null, queen, AVPSoundEvents.ENTITY_OVAMORPH_LAID.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        level.addFreshEntity(ovamorph);
    }
}
