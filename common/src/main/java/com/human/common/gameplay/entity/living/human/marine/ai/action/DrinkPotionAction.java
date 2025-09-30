package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryHolder;
import com.just.goap.GOAPKey;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPKeys;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Objects;

public class DrinkPotionAction {

    public static <T extends LivingEntity & AVPInventoryHolder> boolean perform(GOAPKey<List<AVPInventory.Entry>> potionEntriesKey, T entity, ReadableWorldState worldState, Blackboard ignored) {
        // TODO: Fix this once getOrDefault is supported.
        var potionEntries = worldState.getOrNull(potionEntriesKey);

        if (potionEntries == null || potionEntries.isEmpty()) {
            // TODO: This is technically a failure, but we return true here to stop the plan.
            return true;
        }

        var potionEntry = potionEntries.getFirst();
        // Extract the potion contents BEFORE we remove the item from the inventory.
        var potionContents = Objects.requireNonNull(potionEntry.get(DataComponents.POTION_CONTENTS));
        // Remove the potion from the entity's inventory.
        entity.getInventory().removeItem(potionEntry.getItem());
        // Apply all of the mob effect instances to the marine.
        potionContents.getAllEffects().forEach(entity::addEffect);

        return true;
    }
}
