package com.avp.common.ai.goal.combat;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.util.AlienPredicates;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.EnumSet;

public class DelayedAttackGoal extends MeleeAttackGoal {

    private final int delayTicksBeforeAttack;

    private final Runnable attackAnimationRunnable;

    private int delayBeforeAttack;

    private boolean triggeredAttackAnimation;

    public DelayedAttackGoal(
        PathfinderMob mob,
        double speedModifier,
        boolean bl,
        int delayTicksBeforeAttack,
        Runnable attackAnimationRunnable
    ) {
        super(mob, speedModifier, bl);
        this.delayTicksBeforeAttack = delayTicksBeforeAttack;
        this.attackAnimationRunnable = attackAnimationRunnable;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.getHealth() < (mob.getMaxHealth() / 2)) {
            return false;
        }

        return super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.delayBeforeAttack = 0;
        this.triggeredAttackAnimation = false;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            if (delayBeforeAttack > 0) {
                delayBeforeAttack--;

                if (delayBeforeAttack == delayTicksBeforeAttack && !triggeredAttackAnimation) {
                    attackAnimationRunnable.run();
                    this.triggeredAttackAnimation = true;
                }
            } else {
                resetAttackCooldown();
                mob.swing(InteractionHand.MAIN_HAND);

                if (mob instanceof Alien alien) {
                    var detectionRange = 5.0;
                    AlienPredicates.prioritizeAndAttack(alien, detectionRange);
                } else {
                    mob.doHurtTarget(target);
                }
                this.triggeredAttackAnimation = false;
            }
        } else {
            this.delayBeforeAttack = adjustedTickDelay(10);
            this.triggeredAttackAnimation = false;
        }
    }
}
