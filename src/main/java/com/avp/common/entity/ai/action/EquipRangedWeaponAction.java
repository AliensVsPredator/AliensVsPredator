package com.avp.common.entity.ai.action;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPInventoryBearer;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EquipRangedWeaponAction<T extends LivingEntity & AVPInventoryBearer> extends GOAPAction<T> {

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(GOAPConstants.ITEM_TYPES_IN_INVENTORY, GOAPExpression.contains(ItemType.rangedWeapon())),
            new GOAPCondition<>(
                GOAPConstants.MAIN_HAND_ITEM_TYPE,
                GOAPExpression.where(
                    mainHandItemType -> mainHandItemType != ItemType.rangedWeapon(),
                    "main hand item is not a ranged weapon"
                )
            )
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.rangedWeapon())
        );
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var rangedWeapon = context.getInventory()
            .stream()
            .filter(item -> item.is(AVPItemTags.RANGED_WEAPONS))
            .findFirst();

        // TODO: Need to remove weapon from inventory here.
        rangedWeapon.ifPresent(itemStack -> context.setItemSlot(EquipmentSlot.MAINHAND, itemStack));

        return context.getItemBySlot(EquipmentSlot.MAINHAND).is(AVPItemTags.RANGED_WEAPONS);
    }
}
