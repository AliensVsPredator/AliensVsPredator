package org.avp.fabric.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public final class AVPDeferredItemRegistry {

    private static final List<GameObject<Item>> entries = new ArrayList<>();

    public static void enqueue(GameObject<Item> object) {
        entries.add(object);
    }

    public static void registerAll() {
        entries.forEach(
            itemGameObject -> Registry.register(
                BuiltInRegistries.ITEM,
                itemGameObject.getResourceLocation(),
                itemGameObject.get()
            )
        );
    }

    private AVPDeferredItemRegistry() {
        throw new UnsupportedOperationException();
    }
}
