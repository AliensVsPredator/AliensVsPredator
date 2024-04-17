package org.avp.fabric.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.Holder;

public final class AVPDeferredCreativeTabRegistry {

    private static final List<Holder<CreativeModeTab>> entries = new ArrayList<>();

    public static void enqueue(Holder<CreativeModeTab> object) {
        entries.add(object);
    }

    public static void registerAll() {
        entries.forEach(
            holder -> Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                holder.getResourceLocation(),
                holder.get()
            )
        );
    }

    private AVPDeferredCreativeTabRegistry() {
        throw new UnsupportedOperationException();
    }
}
