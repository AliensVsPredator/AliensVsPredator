package com.blib.api.client.registry.v1;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.blib.api.client.render.v1.armor.AzArmorRenderer;

public class AzArmorRendererRegistry {

    private record ArmorKey(
        Item item,
        int customModelData
    ) {}

    private static final Map<ArmorKey, AzArmorRenderer> ITEM_TO_RENDERER = new HashMap<>();

    private static final Map<ArmorKey, Supplier<AzArmorRenderer>> ITEM_TO_RENDERER_SUPPLIER =
        new HashMap<>();

    public static void register(Item item, Supplier<AzArmorRenderer> armorRendererSupplier) {
        register(item, -1, armorRendererSupplier); // -1 indicates no specific CustomModelData
    }

    public static void register(
        Item item,
        int customModelData,
        Supplier<AzArmorRenderer> armorRendererSupplier
    ) {
        ITEM_TO_RENDERER_SUPPLIER.put(new ArmorKey(item, customModelData), armorRendererSupplier);
    }

    public static void register(
        Supplier<AzArmorRenderer> armorRendererSupplier,
        Item item,
        Item... items
    ) {
        register(item, armorRendererSupplier);
        for (var otherItem : items) {
            register(otherItem, armorRendererSupplier);
        }
    }

    public static void register(
        int customModelData,
        Supplier<AzArmorRenderer> armorRendererSupplier,
        Item item,
        Item... items
    ) {
        register(item, customModelData, armorRendererSupplier);
        for (var otherItem : items) {
            register(otherItem, customModelData, armorRendererSupplier);
        }
    }

    public static @Nullable AzArmorRenderer getOrNull(Item item, int customModelData) {
        ArmorKey specificKey = new ArmorKey(item, customModelData);
        return ITEM_TO_RENDERER.computeIfAbsent(specificKey, (key) -> {
            var rendererSupplier = ITEM_TO_RENDERER_SUPPLIER.get(specificKey);
            if (rendererSupplier != null) {
                return rendererSupplier.get(); // Instantiate renderer if supplier exists for specific key
            }
            // Fallback to generic renderer for the item (no specific CustomModelData)
            ArmorKey genericKey = new ArmorKey(item, -1);
            rendererSupplier = ITEM_TO_RENDERER_SUPPLIER.get(genericKey);
            return rendererSupplier != null ? rendererSupplier.get() : null;
        });
    }

    public static @Nullable AzArmorRenderer getOrNull(ItemStack stack) {
        int customModelData = getCustomModelDataId(stack);
        return getOrNull(stack.getItem(), customModelData);
    }

    public static int getCustomModelDataId(ItemStack itemStack) {
        int customModelDataId = 0;
        if (itemStack.getComponents().get(DataComponents.CUSTOM_MODEL_DATA) != null) {
            customModelDataId = itemStack.getComponents().get(DataComponents.CUSTOM_MODEL_DATA).value();
        }
        return customModelDataId;
    }
}
