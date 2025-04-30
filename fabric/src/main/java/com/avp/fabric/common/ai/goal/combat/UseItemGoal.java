package com.avp.fabric.common.ai.goal.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

import com.avp.fabric.common.item.GunItem;
import com.avp.fabric.common.item.yautja_items.ShurikenItem;
import com.avp.fabric.common.item.yautja_items.SmartDiscItem;
import com.avp.fabric.common.util.ItemGoalUtil;

public class UseItemGoal extends Goal {

    private final int delayTicksBeforeAttack;

    private int ticksUntilNextAttack;

    private int delayBeforeAttack;

    private long lastCanUseCheck;

    private final PathfinderMob entity;

    private final Runnable attackAnimationRunnable;

    private boolean triggeredAttackAnimation;

    private Path path;

    public UseItemGoal(PathfinderMob entity, Runnable attackAnimationRunnable) {
        this.entity = entity;
        this.delayTicksBeforeAttack = 1;
        this.attackAnimationRunnable = attackAnimationRunnable;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        var gameTime = entity.level().getGameTime();
        if (gameTime - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            if (entity.getTarget() != null) {
                this.path = entity.getNavigation().createPath(entity.getTarget(), 0);
            }

            if (entity.getTarget() != null && entity.isWithinMeleeAttackRange(entity.getTarget())) {
                return false;
            }

            return !entity.getMainHandItem().isEmpty() && entity.getTarget() != null && entity.hasLineOfSight(
                entity.getTarget()
            );
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.getTarget() != null && entity.isWithinMeleeAttackRange(entity.getTarget())) {
            return false;
        }

        return !entity.getMainHandItem().isEmpty() && entity.getTarget() != null && entity.hasLineOfSight(entity.getTarget());
    }

    @Override
    public void start() {
        if (entity.getHealth() > (entity.getMaxHealth() / 2)) {
            entity.getNavigation().moveTo(this.path, 0.6F);
        }
        entity.startUsingItem(ProjectileUtil.getWeaponHoldingHand(entity, entity.getMainHandItem().getItem()));
        entity.setAggressive(true);
        this.delayBeforeAttack = 0;
    }

    @Override
    public void stop() {
        var livingEntity = entity.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            entity.setTarget(null);
        }

        entity.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        var livingEntity = entity.getTarget();

        if (livingEntity != null) {
            entity.getLookControl().setLookAt(entity.getTarget(), 180.0F, 180.0F);
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingEntity);
        }
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            if (delayBeforeAttack > 0) {

                if (delayBeforeAttack == delayTicksBeforeAttack && !triggeredAttackAnimation) {
                    attackAnimationRunnable.run();
                    this.triggeredAttackAnimation = true;
                }

                delayBeforeAttack--;
            } else {
                resetAttackCooldown();
                entity.swing(InteractionHand.MAIN_HAND);
                if (entity.getMainHandItem().getItem() instanceof GunItem) {
                    ItemGoalUtil.shootBullet(entity);
                }

                if (entity.getMainHandItem().getItem() instanceof ShurikenItem) {
                    ItemGoalUtil.shootShuriken(entity);
                }

                if (entity.getMainHandItem().getItem() instanceof SmartDiscItem) {
                    ItemGoalUtil.shootSmartDisc(entity);
                }
                this.triggeredAttackAnimation = false;
            }
        } else {
            this.delayBeforeAttack = adjustedTickDelay(10);
            this.triggeredAttackAnimation = false;
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity livingEntity) {
        return this.isTimeToAttack() && entity.getSensing().hasLineOfSight(livingEntity);
    }
}
