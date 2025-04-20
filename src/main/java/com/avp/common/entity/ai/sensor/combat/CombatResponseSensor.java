package com.avp.common.entity.ai.sensor.combat;

import com.bvanseg.just.functional.option.None;
import com.bvanseg.just.functional.option.Option;
import com.bvanseg.just.functional.option.Some;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class CombatResponseSensor<T extends LivingEntity> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var hasRangedWeapon = worldState.getOrDefault(GOAPConstants.HAS_RANGED_WEAPON_IN_INVENTORY, false)
            || isRangedWeaponEquippedInMainHand(worldState);
        var nearestTargetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        switch (nearestTargetOption) {
            case None<? extends LivingEntity> none -> { /* NO-OP */ }
            case Some<? extends LivingEntity> some -> {
                var target = some.unwrap();

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
        return worldState.getOrDefault(
            GOAPConstants.MAIN_HAND_ITEM_TYPE,
            ItemType.none()
        ) instanceof ItemType.Weapon(CombatResponse.FightType fightType) && fightType == CombatResponse.FightType.RANGED;
    }
}
