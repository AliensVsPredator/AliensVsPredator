package com.avp.common.registry.init.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPSpawnEggItems {

    private static final List<AVPDeferredHolder<Item>> HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<Item>> getAll() {
        return Collections.unmodifiableList(HOLDERS);
    }

    public static <E extends Mob> AVPDeferredHolder<Item> register(
        String baseId,
        Supplier<EntityType<E>> entityTypeSupplier,
        int primaryColor,
        int secondaryColor
    ) {
        AVPDeferredHolder<Item> spawnEggItemSupplier = Services.REGISTRY.register(
            BuiltInRegistries.ITEM,
            baseId + "_spawn_egg",
            Services.BRIDGE.createSpawnEggSupplier(entityTypeSupplier, primaryColor, secondaryColor, new Item.Properties())
        );

        HOLDERS.add(spawnEggItemSupplier);

        return spawnEggItemSupplier;
    }
}
