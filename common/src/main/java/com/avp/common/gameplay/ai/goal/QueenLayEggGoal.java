package com.avp.common.gameplay.ai.goal;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.util.AlienPredicates;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.TimeUnit;

import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class QueenLayEggGoal extends Goal {

    private static final int MAX_EGG_LAY_COOLDOWN_IN_TICKS = (int) TimeUnit.MINUTES.toSeconds(1) * 20;

    private final Queen queen;

    private int eggLayCooldownInTicks;

    public QueenLayEggGoal(Queen queen) {
        this.queen = queen;
        this.eggLayCooldownInTicks = MAX_EGG_LAY_COOLDOWN_IN_TICKS;
    }

    @Override
    public boolean canUse() {
        if (eggLayCooldownInTicks > 0) {
            // Egg laying is currently under cooldown, decrement and return.
            eggLayCooldownInTicks--;
            return false;
        }

        // Queen must be alive to lay eggs.
        return queen.isAlive()
            // AND Queen must have an ovipositor.
            && queen.getOvipositorManager().hasOvipositor()
            // AND Queen must not be irradiated.
            && !queen.isIrradiated()
            // AND Queen must not be in an aggressive state.
            && !queen.isAggressive()
            // AND Queen must have no target before she lays an egg.
            && queen.getTarget() == null
            && queen.getHiveManager()
                .hive()
                // AND Queen must have a hive...
                .isSomeAnd(
                    // And that hive must be alive...
                    hive -> hive.isAlive()
                        // AND chunk loaded...
                        && hive.isChunkLoaded()
                        // AND the queen must be within the hive to lay eggs there.
                        && hive.getSpaceManager().isEntityWithinHive(queen)
                )
            // AND there must be no other friendly eggs nearby already.
            && noFriendlyEggsNearby();
    }

    @Override
    public void start() {
        // Reset egg lay cooldown since it's (almost) guaranteed that the queen is about to lay an egg.
        eggLayCooldownInTicks = MAX_EGG_LAY_COOLDOWN_IN_TICKS;
        var level = queen.level();
        // Egg has a 5% chance of being royal.
        var isRoyal = queen.getRandom().nextInt(100) < 5;
        var ovomorphType = Ovomorph.getType(queen.getVariant(), isRoyal);

        var ovomorph = ovomorphType == null
            ? null
            : ovomorphType.create(level);

        if (ovomorph == null) {
            return;
        }

        ovomorph.setPos(queen.getOvipositorManager().getEggLayingPosition());
        ovomorph.setPersistenceRequired();
        // Transfer genes.
        queen.getGeneManager().transfer(ovomorph.getGeneManager(), false);

        level.playSound(null, queen, AVPSoundEvents.ENTITY_OVOMORPH_LAID.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        level.addFreshEntity(ovomorph);
    }

    private boolean noFriendlyEggsNearby() {
        // Reset scanning cooldown regardless of scanner outcome.
        eggLayCooldownInTicks = MAX_EGG_LAY_COOLDOWN_IN_TICKS;
        // Scan for friendly ovomorphs.
        return queen.level()
            .getEntitiesOfClass(
                Ovomorph.class,
                // 4 block radius
                getBoundingBoxAtEggLayingPosition(),
                // Must be tagged as an ovomorph...
                entity -> entity.getType().is(AVPEntityTypeTags.OVOMORPHS)
                    // AND must NOT be an enemy alien to the queen (so either a neutral egg, or same strain and same
                    // hive).
                    && !AlienPredicates.areAliensEnemies(queen, entity)
            )
            // If the list of FRIENDLY eggs is empty, then the queen is good to lay an egg.
            .isEmpty();
    }

    private AABB getBoundingBoxAtEggLayingPosition() {
        var eggPos = queen.getOvipositorManager().getEggLayingPosition();
        var halfSize = 0.5;

        return new AABB(
            eggPos.x - halfSize,
            eggPos.y - queen.level().dimensionType().height(),
            eggPos.z - halfSize,
            eggPos.x + halfSize,
            eggPos.y + 5,
            eggPos.z + halfSize
        );
    }
}
