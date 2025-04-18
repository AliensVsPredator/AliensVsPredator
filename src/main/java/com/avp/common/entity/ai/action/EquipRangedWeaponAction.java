package com.avp.common.entity.ai.action;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;

import java.util.Map;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.item.GunItem;
import com.avp.goap.GOAPAction;
import com.avp.goap.expression.GOAPCondition;
import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.expression.GOAPExpression;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EquipRangedWeaponAction<T extends LivingEntity & InventoryCarrier> extends GOAPAction<T> {

    @Override
    public GOAPConditionSet createPreconditions() {
        return GOAPConditionSet.of(
            new GOAPCondition<>(GOAPConstants.HAS_RANGED_WEAPON_IN_INVENTORY, GOAPExpression.isTrue()),
            new GOAPCondition<>(GOAPConstants.HAS_RANGED_WEAPON_EQUIPPED, GOAPExpression.isFalse())
        );
    }

    @Override
    public GOAPWorldState createEffects() {
        return new GOAPWorldState(Map.of(GOAPConstants.HAS_RANGED_WEAPON_EQUIPPED, true));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var rangedWeapon = context.getInventory().items
            .stream()
            .filter(item -> item.getItem() instanceof GunItem)
            .findFirst();

        rangedWeapon.ifPresent(itemStack -> context.setItemSlot(EquipmentSlot.MAINHAND, itemStack));

        return context.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof GunItem;
    }
}
