package com.avp.fabric.common.entity.ai.sensor.combat;

import com.bvanseg.just.functional.option.None;
import com.bvanseg.just.functional.option.Option;
import com.bvanseg.just.functional.option.Some;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;

import java.util.Objects;
import java.util.Set;

import com.avp.common.goap.GOAPSensor;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.fabric.common.entity.ai.GOAPConstants;
import com.avp.fabric.common.entity.ai.util.CombatResponse;
import com.avp.fabric.common.entity.ai.util.ItemType;

public class CombatResponseSensor<T extends LivingEntity> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var hasRangedWeapon = worldState.getOrDefault(GOAPConstants.ITEM_TYPES_IN_INVENTORY, Set.of()).contains(ItemType.rangedWeapon())
            || isRangedWeaponEquippedInMainHand(worldState);
        var nearestTargetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        switch (nearestTargetOption) {
            case None<? extends LivingEntity> none -> worldState.set(GOAPConstants.COMBAT_RESPONSE, CombatResponse.rest());
            case Some<? extends LivingEntity> some -> {
                var target = some.unwrap();

                // Run away from targets that can kill the entity with one more attack.
                var willTargetOneShotMe = target instanceof Mob mob
                    && mob.getTarget() == context
                    && target.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                    && Objects.requireNonNull(target.getAttribute(Attributes.ATTACK_DAMAGE)).getValue() > context.getHealth();

                // Run away from swelling or ignited creepers that are too close.
                var isCreeperAboutToExplode = target instanceof Creeper creeper
                    && (creeper.isIgnited() || creeper.getSwellDir() > 0);

                var isTargetLethal = willTargetOneShotMe || isCreeperAboutToExplode;

                if (isTargetLethal && context.distanceToSqr(target) < 4 * 4) {
                    worldState.set(GOAPConstants.COMBAT_RESPONSE, CombatResponse.flight());
                    return;
                }

                if (hasRangedWeapon) {
                    // Ranged weaponry always preferred.
                    worldState.set(GOAPConstants.COMBAT_RESPONSE, new CombatResponse.Fight(CombatResponse.FightType.RANGED));
                    return;
                }

                worldState.set(GOAPConstants.COMBAT_RESPONSE, new CombatResponse.Fight(CombatResponse.FightType.MELEE));
            }
        }
    }

    private boolean isRangedWeaponEquippedInMainHand(GOAPMutableWorldState worldState) {
        return worldState.getOrDefault(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.none()) == ItemType.rangedWeapon();
    }
}
