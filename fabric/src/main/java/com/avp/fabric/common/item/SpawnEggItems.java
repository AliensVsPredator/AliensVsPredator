package com.avp.fabric.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class SpawnEggItems {

    private static final List<Supplier<Item>> ITEM_SUPPLIERS = new ArrayList<>();

    public static List<Supplier<Item>> getAll() {
        return Collections.unmodifiableList(ITEM_SUPPLIERS);
    }

    private static AVPDeferredHolder<Item> register(
        String baseId,
        Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier,
        int primaryColor,
        int secondaryColor
    ) {
        return Services.REGISTRY.register(
            BuiltInRegistries.ITEM,
            baseId + "_spawn_egg",
            () -> new SpawnEggItem(entityTypeSupplier.get(), primaryColor, secondaryColor, new Item.Properties())
        );
    }

    public static void initialize() {}
}
