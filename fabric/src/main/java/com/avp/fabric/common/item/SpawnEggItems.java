package com.avp.fabric.common.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avp.AVPResources;
import com.avp.common.entity.type.SilencedEntityTypeBuilder;
import com.avp.fabric.common.entity.type.AVPEntityTypes;

public class SpawnEggItems {

    private static final List<Item> ITEMS = new ArrayList<>();

    public static List<Item> getAll() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static final Item ABERRANT_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_CHESTBURSTER, 0xD9D9B8, 0xBFBF88);

    public static final Item ABERRANT_DRONE_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_DRONE, 0xE5BF05, 0xD7D7DF);

    public static final Item ABERRANT_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_FACEHUGGER, 0xCDCCA3, 0xC9CE3B);

    public static final Item ABERRANT_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_OVAMORPH, 0x767139, 0xC9C432);

    public static final Item ABERRANT_PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_PRAETORIAN, 0xF1C810, 0xD7D7DF);

    public static final Item ABERRANT_WARRIOR_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_WARRIOR, 0xF3CE07, 0xD7D7DF);

    public static final Item ABERRANT_QUEEN_SPAWN_EGG = register(AVPEntityTypes.ABERRANT_QUEEN, 0xF3CE07, 0xD7D7DF);

    public static final Item CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.CHESTBURSTER, 0xD8B877, 0xF7E2B4);

    public static final Item DRONE_SPAWN_EGG = register(AVPEntityTypes.DRONE, 0x010202, 0xDFE2E4);

    public static final Item FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.FACEHUGGER, 0xE4D597, 0xA55863);

    public static final Item IRRADIATED_DRONE_SPAWN_EGG = register(AVPEntityTypes.IRRADIATED_DRONE, 0xD5F2ED, 0x73D9CF);

    public static final Item IRRADIATED_PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.IRRADIATED_PRAETORIAN, 0xD5F2ED, 0x73D9CF);

    public static final Item IRRADIATED_QUEEN_SPAWN_EGG = register(AVPEntityTypes.IRRADIATED_QUEEN, 0xD5F2ED, 0x73D9CF);

    public static final Item IRRADIATED_WARRIOR_SPAWN_EGG = register(AVPEntityTypes.IRRADIATED_WARRIOR, 0xD5F2ED, 0x73D9CF);

    public static final Item NETHER_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.NETHER_CHESTBURSTER, 0x360101, 0x803232);

    public static final Item NETHER_DRONE_SPAWN_EGG = register(AVPEntityTypes.NETHER_DRONE, 0x360101, 0x803232);

    public static final Item NETHER_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.NETHER_FACEHUGGER, 0x27221C, 0xFAD855);

    public static final Item NETHER_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.NETHER_OVAMORPH, 0x20131C, 0xFCEE4B);

    public static final Item NETHER_PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.NETHER_PRAETORIAN, 0x310808, 0x5d1f1f);

    public static final Item NETHER_WARRIOR_SPAWN_EGG = register(AVPEntityTypes.NETHER_WARRIOR, 0x2b0000, 0x67261f);

    public static final Item NETHER_QUEEN_SPAWN_EGG = register(AVPEntityTypes.NETHER_QUEEN, 0x2b0000, 0x67261f);

    public static final Item ROYAL_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.ROYAL_CHESTBURSTER, 0x29140a, 0xe6b57b);

    public static final Item ROYAL_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.ROYAL_OVAMORPH, 0x2a2918, 0x34341f);

    public static final Item ROYAL_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.ROYAL_FACEHUGGER, 0x4b4946, 0x292729);

    public static final Item ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG = register(AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER, 0x331715, 0xfcee4b);

    public static final Item ROYAL_NETHER_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.ROYAL_NETHER_FACEHUGGER, 0x331715, 0xfcee4b);

    public static final Item ROYAL_NETHER_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.ROYAL_NETHER_OVAMORPH, 0x331715, 0xfcee4b);

    public static final Item ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
        0x706c36,
        0xd3cf51
    );

    public static final Item ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG = register(AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER, 0x706c36, 0xd3cf51);

    public static final Item ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH, 0x706c36, 0xd3cf51);

    public static final Item OVAMORPH_SPAWN_EGG = register(AVPEntityTypes.OVAMORPH, 0x615B45, 0xBF7872);

    public static final Item PRAETORIAN_SPAWN_EGG = register(AVPEntityTypes.PRAETORIAN, 0x010202, 0x363534);

    public static final Item QUEEN_SPAWN_EGG = register(AVPEntityTypes.QUEEN, 0x010202, 0x363534);

    public static final Item WARRIOR_SPAWN_EGG = register(AVPEntityTypes.WARRIOR, 0x010202, 0x4A4E55);

    public static final Item YAUTJA_SPAWN_EGG = register(AVPEntityTypes.YAUTJA, 0xB9A86C, 0x5A4728);

    public static final Item MARINE_SPAWN_EGG = register(AVPEntityTypes.MARINE, 0x5a5941, 0x414441);

    private static Item register(EntityType<? extends Mob> entityType, int primaryColor, int secondaryColor) {
        var entityTypeResourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        var entityTypeName = entityTypeResourceLocation.getPath();
        var properties = new Item.Properties();
        var spawnEggItem = new SpawnEggItem(entityType, primaryColor, secondaryColor, properties);
        return register(spawnEggItem, entityTypeName);
    }

    private static Item register(Item item, String id) {
        var resourceLocation = AVPResources.location(id + "_spawn_egg");
        var registeredItem = Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
        ITEMS.add(registeredItem);
        return registeredItem;
    }

    private static <T extends Entity> EntityType<T> registerEntityType(String name, EntityType.Builder<T> builder) {
        var entityType = ((SilencedEntityTypeBuilder) builder).<T>buildWithoutDataFixerCheck();
        var resourceLocation = AVPResources.location(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return entityType;
    }

    public static void initialize() {}
}
