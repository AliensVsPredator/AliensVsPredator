package com.avp.common.entity.ai.action;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.item.AVPItemTags;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EquipMeleeWeaponAction<T extends LivingEntity & InventoryCarrier> extends GOAPAction<T> {

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(GOAPConstants.ITEM_TYPES_IN_INVENTORY, GOAPExpression.contains(ItemType.meleeWeapon())),
            new GOAPCondition<>(
                GOAPConstants.MAIN_HAND_ITEM_TYPE,
                GOAPExpression.where(
                    mainHandItemType -> mainHandItemType != ItemType.meleeWeapon(),
                    "main hand item is not a melee weapon"
                )
            )
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.meleeWeapon())
        );
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var meleeWeapon = context.getInventory().items
            .stream()
            .filter(item -> item.is(AVPItemTags.MELEE_WEAPONS))
            .findFirst();

        // TODO: Need to remove weapon from inventory here.
        meleeWeapon.ifPresent(itemStack -> context.setItemSlot(EquipmentSlot.MAINHAND, itemStack));

        return context.getItemBySlot(EquipmentSlot.MAINHAND).is(AVPItemTags.MELEE_WEAPONS);
    }
}
