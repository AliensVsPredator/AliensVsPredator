package com.avp.common.entity.living.human.marine.ai;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.action.AttackAction;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.goap.TypedIdentifier;
import com.avp.common.goap.state.GOAPBlackboard;

public class MarineRangedAttackAction extends AttackAction<Marine> {

    // Controls whether the marine can fire right the gun.
    private static final TypedIdentifier<Integer> COOLDOWN = new TypedIdentifier<>("cooldown");

    // Controls sound queues and other time-based gun effects.
    private static final TypedIdentifier<Integer> TICK_PROGRESS = new TypedIdentifier<>("tickProgress");

    public MarineRangedAttackAction() {
        super(CombatResponse.FightType.RANGED);
    }

    @Override
    protected void performAttack(Marine context, LivingEntity target, GOAPBlackboard blackboard) {
        var itemStack = context.getMainHandItem();

        // FIXME:
        // if (itemStack.getItem() instanceof GunItem gunItem) {
        // shootGun(context, target, blackboard, gunItem, itemStack);
        // }
    }

    // FIXME:
    // private static void shootGun(Marine context, LivingEntity target, GOAPBlackboard blackboard, GunItem gunItem,
    // ItemStack itemStack) {
    // var fireMode = gunItem.getGunConfig().getDefaultFireMode();
    // // Decrement cooldown on every access/tick.
    // var currentCooldown = blackboard.getOrDefault(COOLDOWN, 0) - 1;
    // // Get current tick progress.
    // var tickProgress = blackboard.getOrDefault(TICK_PROGRESS, 0);
    //
    // // Update the cooldown.
    // blackboard.set(COOLDOWN, currentCooldown);
    // // Always look at the target while shooting.
    // context.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
    // context.getLookControl().setLookAt(target);
    //
    // if (currentCooldown <= 0) {
    // GunShootContext.create(context, itemStack, tickProgress)
    // .map(GunShootContext::shoot)
    // .filter(result -> result == GunShootResult.SHOT)
    // // Reset the cooldown.
    // .ifSome($ -> blackboard.set(COOLDOWN, fireMode.cooldownInTicks() * fireMode.consumedAmmunitionPerShot()));
    // }
    //
    // // Increase tick progress.
    // blackboard.set(TICK_PROGRESS, tickProgress + 1);
    // }
}
