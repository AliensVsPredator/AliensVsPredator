package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class DrinkPotionAction {

    public static <T extends LivingEntity & AVPInventoryHolder> boolean perform(
        Holder<MobEffect> mobEffectHolder,
        T entity,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        return ConsumeItemAction.perform(SoundEvents.GENERIC_DRINK, entity, blackboard, () -> {
            // TODO: Fix this once getOrDefault is supported.
            var potionEntriesByMobEffect = worldState.getOrNull(GOAPKeys.POTION_ENTRIES_IN_INVENTORY);

            if (
                potionEntriesByMobEffect == null || potionEntriesByMobEffect.isEmpty() || !potionEntriesByMobEffect.containsKey(
                    mobEffectHolder
                )
            ) {
                // TODO: This is technically a failure, but we return true here to stop the plan.
                return true;
            }

            var potionEntries = potionEntriesByMobEffect.get(mobEffectHolder);

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
            potionContents.getAllEffects().forEach(mobEffectInstance -> {
                if (mobEffectHolder.value().isInstantenous()) {
                    mobEffectHolder.value().applyInstantenousEffect(null, null, entity, mobEffectInstance.getAmplifier(), entity.getHealth());
                } else {
                    entity.addEffect(mobEffectInstance);
                }
            });
            return true;
        });
    }
}
