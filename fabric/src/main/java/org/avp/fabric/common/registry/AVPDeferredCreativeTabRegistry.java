package org.avp.fabric.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;

public final class AVPDeferredCreativeTabRegistry {

    private static final List<GameObject<CreativeModeTab>> entries = new ArrayList<>();

    public static void enqueue(GameObject<CreativeModeTab> object) {
        entries.add(object);
    }

    public static void registerAll() {
        entries.forEach(
            creativeModeTabGameObject -> Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                creativeModeTabGameObject.getResourceLocation(),
                creativeModeTabGameObject.get()
            )
        );
    }

    private AVPDeferredCreativeTabRegistry() {
        throw new UnsupportedOperationException();
    }
}
