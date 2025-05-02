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
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.service.Services;

public class SpawnEggItems {

    private static final List<Supplier<Item>> ITEM_SUPPLIERS = new ArrayList<>();

    public static List<Supplier<Item>> getAll() {
        return Collections.unmodifiableList(ITEM_SUPPLIERS);
    }

    public static final AVPDeferredHolder<Item> ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "aberrant_chestburster",
        () -> AVPEntityTypes.ABERRANT_CHESTBURSTER,
        0xD9D9B8,
        0xBFBF88
    );

    public static final AVPDeferredHolder<Item> ABERRANT_DRONE_SPAWN_EGG = register(
        "aberrant_drone",
        () -> AVPEntityTypes.ABERRANT_DRONE,
        0xE5BF05,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "aberrant_facehugger",
        () -> AVPEntityTypes.ABERRANT_FACEHUGGER,
        0xCDCCA3,
        0xC9CE3B
    );

    public static final AVPDeferredHolder<Item> ABERRANT_OVAMORPH_SPAWN_EGG = register(
        "aberrant_ovamorph",
        () -> AVPEntityTypes.ABERRANT_OVAMORPH,
        0x767139,
        0xC9C432
    );

    public static final AVPDeferredHolder<Item> ABERRANT_PRAETORIAN_SPAWN_EGG = register(
        "aberrant_praetorian",
        () -> AVPEntityTypes.ABERRANT_PRAETORIAN,
        0xF1C810,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_WARRIOR_SPAWN_EGG = register(
        "aberrant_warrior",
        () -> AVPEntityTypes.ABERRANT_WARRIOR,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_QUEEN_SPAWN_EGG = register(
        "aberrant_queen",
        () -> AVPEntityTypes.ABERRANT_QUEEN,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> CHESTBURSTER_SPAWN_EGG = register(
        "chestburster",
        () -> AVPEntityTypes.CHESTBURSTER,
        0xD8B877,
        0xF7E2B4
    );

    public static final AVPDeferredHolder<Item> DRONE_SPAWN_EGG = register("drone", () -> AVPEntityTypes.DRONE, 0x010202, 0xDFE2E4);

    public static final AVPDeferredHolder<Item> FACEHUGGER_SPAWN_EGG = register(
        "facehugger",
        () -> AVPEntityTypes.FACEHUGGER,
        0xE4D597,
        0xA55863
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_DRONE_SPAWN_EGG = register(
        "irradiated_drone",
        () -> AVPEntityTypes.IRRADIATED_DRONE,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_PRAETORIAN_SPAWN_EGG = register(
        "irradiated_praetorian",
        () -> AVPEntityTypes.IRRADIATED_PRAETORIAN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_QUEEN_SPAWN_EGG = register(
        "irradiated_queen",
        () -> AVPEntityTypes.IRRADIATED_QUEEN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_WARRIOR_SPAWN_EGG = register(
        "irradiated_warrior",
        () -> AVPEntityTypes.IRRADIATED_WARRIOR,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "nether_chestburster",
        () -> AVPEntityTypes.NETHER_CHESTBURSTER,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_DRONE_SPAWN_EGG = register(
        "nether_drone",
        () -> AVPEntityTypes.NETHER_DRONE,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_FACEHUGGER_SPAWN_EGG = register(
        "nether_facehugger",
        () -> AVPEntityTypes.NETHER_FACEHUGGER,
        0x27221C,
        0xFAD855
    );

    public static final AVPDeferredHolder<Item> NETHER_OVAMORPH_SPAWN_EGG = register(
        "nether_ovamorph",
        () -> AVPEntityTypes.NETHER_OVAMORPH,
        0x20131C,
        0xFCEE4B
    );

    public static final AVPDeferredHolder<Item> NETHER_PRAETORIAN_SPAWN_EGG = register(
        "nether_praetorian",
        () -> AVPEntityTypes.NETHER_PRAETORIAN,
        0x310808,
        0x5d1f1f
    );

    public static final AVPDeferredHolder<Item> NETHER_WARRIOR_SPAWN_EGG = register(
        "nether_warrior",
        () -> AVPEntityTypes.NETHER_WARRIOR,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> NETHER_QUEEN_SPAWN_EGG = register(
        "nether_queen",
        () -> AVPEntityTypes.NETHER_QUEEN,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> ROYAL_CHESTBURSTER_SPAWN_EGG = register(
        "royal_chestburster",
        () -> AVPEntityTypes.ROYAL_CHESTBURSTER,
        0x29140a,
        0xe6b57b
    );

    public static final AVPDeferredHolder<Item> ROYAL_OVAMORPH_SPAWN_EGG = register(
        "royal_ovamorph",
        () -> AVPEntityTypes.ROYAL_OVAMORPH,
        0x2a2918,
        0x34341f
    );

    public static final AVPDeferredHolder<Item> ROYAL_FACEHUGGER_SPAWN_EGG = register(
        "royal_facehugger",
        () -> AVPEntityTypes.ROYAL_FACEHUGGER,
        0x4b4946,
        0x292729
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "royal_nether_chestburster",
        () -> AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_FACEHUGGER_SPAWN_EGG = register(
        "royal_nether_facehugger",
        () -> AVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_OVAMORPH_SPAWN_EGG = register(
        "royal_nether_ovamorph",
        () -> AVPEntityTypes.ROYAL_NETHER_OVAMORPH,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "royal_aberrant_chestburster",
        () -> AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "royal_aberrant_facehugger",
        () -> AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG = register(
        "royal_aberrant_ovamorph",
        () -> AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> OVAMORPH_SPAWN_EGG = register(
        "ovamorph",
        () -> AVPEntityTypes.OVAMORPH,
        0x615B45,
        0xBF7872
    );

    public static final AVPDeferredHolder<Item> PRAETORIAN_SPAWN_EGG = register(
        "praetorian",
        () -> AVPEntityTypes.PRAETORIAN,
        0x010202,
        0x363534
    );

    public static final AVPDeferredHolder<Item> QUEEN_SPAWN_EGG = register("queen", () -> AVPEntityTypes.QUEEN, 0x010202, 0x363534);

    public static final AVPDeferredHolder<Item> WARRIOR_SPAWN_EGG = register("warrior", () -> AVPEntityTypes.WARRIOR, 0x010202, 0x4A4E55);

    public static final AVPDeferredHolder<Item> YAUTJA_SPAWN_EGG = register("yautja", () -> AVPEntityTypes.YAUTJA, 0xB9A86C, 0x5A4728);

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
