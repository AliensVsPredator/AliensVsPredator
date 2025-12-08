package com.blib.common.gameplay.goap.sensor;

import com.blib.common.gameplay.model.inventory.BLibInventory;
import com.blib.common.gameplay.model.inventory.BLibInventoryHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.PotionItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PotionEntriesInInventorySensor {

    public static @NotNull HashMap<Holder<MobEffect>, List<BLibInventory.Entry>> sense(BLibInventoryHolder inventoryHolder) {
        var potionItemEntries = inventoryHolder.getInventory()
            .filterEntriesByItem(item -> item instanceof PotionItem);
        var map = new HashMap<Holder<MobEffect>, List<BLibInventory.Entry>>();

        for (var entry : potionItemEntries) {
            var potionContents = entry.get(DataComponents.POTION_CONTENTS);

            if (potionContents == null) {
                continue;
            }

            potionContents.getAllEffects()
                .forEach(
                    mobEffectInstance -> map.computeIfAbsent(mobEffectInstance.getEffect(), $ -> new ArrayList<>()).add(entry)
                );
        }

        return map;
    }
}
