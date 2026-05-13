package com.blib.api.client.registry.v1;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.blib.api.client.render.v1.item.AzItemRenderer;

public class AzItemRendererRegistry {

    private static final Map<Item, AzItemRenderer> ITEM_TO_RENDERER = new HashMap<>();

    private static final Map<Item, Supplier<AzItemRenderer>> ITEM_TO_RENDERER_SUPPLIER =
        new HashMap<>();

    public static void register(Item item, Supplier<AzItemRenderer> itemRendererSupplier) {
        ITEM_TO_RENDERER_SUPPLIER.put(item, itemRendererSupplier);
    }

    public static void register(
        Supplier<AzItemRenderer> itemRendererSupplier,
        Item item,
        Item... items
    ) {
        register(item, itemRendererSupplier);

        for (var otherItem : items) {
            register(otherItem, itemRendererSupplier);
        }
    }

    public static @Nullable AzItemRenderer getOrNull(Item item) {
        return ITEM_TO_RENDERER.computeIfAbsent(item, ($) -> {
            var rendererSupplier = ITEM_TO_RENDERER_SUPPLIER.get(item);
            return rendererSupplier == null ? null : rendererSupplier.get();
        });
    }

    /**
     * Items whose renderer has been registered via {@link #register} (suppliers, not yet instantiated). Useful for
     * tooling that wants to force-instantiate every registered renderer up front — e.g. the Modeler item-config
     * inspector, which needs the renderers' constructors to run so any {@code BLibTunableItemTransforms.wrap} calls
     * fire and register the items with {@code BLibItemTransformOverrides}.
     */
    public static Set<Item> registeredItems() {
        return Set.copyOf(ITEM_TO_RENDERER_SUPPLIER.keySet());
    }
}
