package com.avp.core.common.item;

import com.avp.core.platform.service.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.type.AVPEntityTypes;

public class SpawnEggItems {

    private static final List<Supplier<Item>> ITEMS_SUPPLIERS = new ArrayList<>();

    public static List<Supplier<Item>> getAll() {
        return Collections.unmodifiableList(ITEMS_SUPPLIERS);
    }

    public static final Supplier<Item> ABERRANT_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_CHESTBURSTER, "aberrant_chestburster", 0xD9D9B8, 0xBFBF88);

    public static final Supplier<Item> ABERRANT_DRONE_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_DRONE, "aberrant_drone", 0xE5BF05, 0xD7D7DF);

    public static final Supplier<Item> ABERRANT_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_FACEHUGGER, "aberrant_facehugger", 0xCDCCA3, 0xC9CE3B);

    public static final Supplier<Item> ABERRANT_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_OVAMORPH, "aberrant_ovamorph", 0x767139, 0xC9C432);

    public static final Supplier<Item> ABERRANT_PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_PRAETORIAN, "aberrant_praetorian", 0xF1C810, 0xD7D7DF);

    public static final Supplier<Item> ABERRANT_WARRIOR_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_WARRIOR, "aberrant_warrior", 0xF3CE07, 0xD7D7DF);

    public static final Supplier<Item> CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.CHESTBURSTER, "chestburster", 0xD8B877, 0xF7E2B4);

    public static final Supplier<Item> DRONE_SPAWN_EGG = register(AVPEntityTypes.DRONE, "drone", 0x010202, 0xDFE2E4);

    public static final Supplier<Item> FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.FACEHUGGER, "facehugger", 0xE4D597, 0xA55863);

    public static final Supplier<Item> NETHER_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.NETHER_CHESTBURSTER, "nether_chestburster", 0x360101, 0x803232);

    public static final Supplier<Item> NETHER_DRONE_SPAWN_EGG = register(AVPEntityTypes.NETHER_DRONE, "nether_drone", 0x360101, 0x803232);

    public static final Supplier<Item> NETHER_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.NETHER_FACEHUGGER, "nether_facehugger", 0x27221C, 0xFAD855);

    public static final Supplier<Item> NETHER_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.NETHER_OVAMORPH, "nether_ovamorph", 0x20131C, 0xFCEE4B);

    public static final Supplier<Item> NETHER_PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.NETHER_PRAETORIAN, "nether_praetorian", 0x310808, 0x5d1f1f);

    public static final Supplier<Item> NETHER_WARRIOR_SPAWN_EGG = register(AVPEntityTypes.NETHER_WARRIOR, "nether_warrior", 0x2b0000, 0x67261f);

    public static final Supplier<Item> OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.OVAMORPH, "ovamorph", 0x615B45, 0xBF7872);

    public static final Supplier<Item> PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.PRAETORIAN, "praetorian", 0x010202, 0x363534);

    public static final Supplier<Item> QUEEN_SPAWN_EGG = register(AVPEntityTypes.QUEEN, "queen", 0x010202, 0x363534);

    public static final Supplier<Item> WARRIOR_SPAWN_EGG = register(AVPEntityTypes.WARRIOR, "warrior", 0x010202, 0x4A4E55);

    public static final Supplier<Item> YAUTJA_SPAWN_EGG = register(AVPEntityTypes.YAUTJA, "yautja", 0xB9A86C, 0x5A4728);

    private static Supplier<Item> register(Supplier<? extends EntityType<? extends Mob>> entityType, String id, int primaryColor, int secondaryColor) {
        return register(() -> new SpawnEggItem(entityType.get(), primaryColor, secondaryColor, new Item.Properties()), id);
    }

    private static Supplier<Item> register(Supplier<Item> itemSupplier, String id) {
        var registeredItem = Services.REGISTRY.register(BuiltInRegistries.ITEM, id + "_spawn_egg", itemSupplier);
        ITEMS_SUPPLIERS.add(registeredItem);
        return registeredItem;
    }

    public static void initialize() {}
}
