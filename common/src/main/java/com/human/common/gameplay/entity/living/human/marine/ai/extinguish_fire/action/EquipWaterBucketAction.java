package com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.action;

import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.ExtinguishFireSensors;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class EquipWaterBucketAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var inventoryWaterBucketOption = worldState.getOrDefault(ExtinguishFireSensors.WATER_BUCKET_IN_INVENTORY.key(), Option.none());

        if (inventoryWaterBucketOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return EquipItemAction.perform(livingEntityWithInventory, inventoryWaterBucketOption.unwrap(), EquipmentSlot.MAINHAND);
    }

    private EquipWaterBucketAction() {
        throw new UnsupportedOperationException();
    }
}
