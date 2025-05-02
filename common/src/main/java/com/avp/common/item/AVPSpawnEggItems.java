package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPSpawnEggItems {

    public static final AVPDeferredHolder<Item> MARINE_SPAWN_EGG = register("marine", TempAVPEntityTypes.MARINE, 0x5a5941, 0x414441);

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
