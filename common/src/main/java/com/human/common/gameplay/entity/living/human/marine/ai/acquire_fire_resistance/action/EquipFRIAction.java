package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipItemAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class EquipFRIAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var inventoryItemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI_IN_INVENTORY.key(), Option.none());

        if (inventoryItemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return EquipItemAction.perform(livingEntityWithInventory, inventoryItemTargetOption.unwrap().entry(), EquipmentSlot.MAINHAND);
    }

    private EquipFRIAction() {
        throw new UnsupportedOperationException();
    }
}
