package com.blib.api.common.entity.v1.ai.goal.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.EnumSet;

import com.blib.api.common.time.v1.Cooldown;

public class DelayedAttackGoal extends MeleeAttackGoal {

    private final Runnable attackAnimationRunnable;

    private final Cooldown attackAnimationCooldown;

    private boolean ranAttackAnimation;

    public DelayedAttackGoal(
        PathfinderMob mob,
        double speedModifier,
        boolean bl,
        int delayTicksBeforeAttack,
        Runnable attackAnimationRunnable
    ) {
        super(mob, speedModifier, bl);
        this.attackAnimationCooldown = Cooldown.withCooldownTime(
            "attackAnimationCooldownInTicks",
            Duration.ofMillis(delayTicksBeforeAttack * 50L)
        );
        this.attackAnimationRunnable = attackAnimationRunnable;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void tick() {
        super.tick();
        attackAnimationCooldown.tick();

        if (
            // If target is not null
            mob.getTarget() != null
                // AND we ran the attack animation.
                && ranAttackAnimation
                // AND the animation cooldown has finished
                && !attackAnimationCooldown.isActive()
                // AND target is still within melee range
                && mob.isWithinMeleeAttackRange(mob.getTarget())
                // AND we still have line of sight of the target
                && mob.getSensing().hasLineOfSight(mob.getTarget())
        ) {
            resetAttackCooldown();
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(mob.getTarget());
            this.ranAttackAnimation = false;
        }
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        if (!ranAttackAnimation && canPerformAttack(target)) {
            // Play the animation.
            attackAnimationRunnable.run();
            this.ranAttackAnimation = true;
            // Reset the cooldown.
            attackAnimationCooldown.reset();
        }
    }
}
