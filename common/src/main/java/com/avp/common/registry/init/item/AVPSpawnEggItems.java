package com.avp.common.registry.init.item;

import com.alien.common.registry.init.AlienEntityTypes;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.service.Services;

public class AVPSpawnEggItems {

    private static final List<AVPDeferredHolder<Item>> HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<Item>> getAll() {
        return Collections.unmodifiableList(HOLDERS);
    }

    public static final AVPDeferredHolder<Item> ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "aberrant_chestburster",
        AlienEntityTypes.ABERRANT_CHESTBURSTER,
        0xD9D9B8,
        0xBFBF88
    );

    public static final AVPDeferredHolder<Item> ABERRANT_DRONE_SPAWN_EGG = register(
        "aberrant_drone",
        AlienEntityTypes.ABERRANT_DRONE,
        0xE5BF05,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "aberrant_facehugger",
        AlienEntityTypes.ABERRANT_FACEHUGGER,
        0xCDCCA3,
        0xC9CE3B
    );

    public static final AVPDeferredHolder<Item> ABERRANT_OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "aberrant_ovamorph",
        AlienEntityTypes.ABERRANT_OVOMORPH,
        0x767139,
        0xC9C432
    );

    public static final AVPDeferredHolder<Item> ABERRANT_PRAETORIAN_SPAWN_EGG = register(
        "aberrant_praetorian",
        AlienEntityTypes.ABERRANT_PRAETORIAN,
        0xF1C810,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_WARRIOR_SPAWN_EGG = register(
        "aberrant_warrior",
        AlienEntityTypes.ABERRANT_WARRIOR,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_QUEEN_SPAWN_EGG = register(
        "aberrant_queen",
        AlienEntityTypes.ABERRANT_QUEEN,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> CHESTBURSTER_SPAWN_EGG = register(
        "chestburster",
        AlienEntityTypes.CHESTBURSTER,
        0xD8B877,
        0xF7E2B4
    );

    public static final AVPDeferredHolder<Item> DRONE_SPAWN_EGG = register("drone", AlienEntityTypes.DRONE, 0x010202, 0xDFE2E4);

    public static final AVPDeferredHolder<Item> FACEHUGGER_SPAWN_EGG = register(
        "facehugger",
        AlienEntityTypes.FACEHUGGER,
        0xE4D597,
        0xA55863
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_DRONE_SPAWN_EGG = register(
        "irradiated_drone",
        AlienEntityTypes.IRRADIATED_DRONE,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_PRAETORIAN_SPAWN_EGG = register(
        "irradiated_praetorian",
        AlienEntityTypes.IRRADIATED_PRAETORIAN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_QUEEN_SPAWN_EGG = register(
        "irradiated_queen",
        AlienEntityTypes.IRRADIATED_QUEEN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_WARRIOR_SPAWN_EGG = register(
        "irradiated_warrior",
        AlienEntityTypes.IRRADIATED_WARRIOR,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> MARINE_SPAWN_EGG = register("marine", AVPEntityTypes.MARINE, 0x5a5941, 0x414441);

    public static final AVPDeferredHolder<Item> NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "nether_chestburster",
        AlienEntityTypes.NETHER_CHESTBURSTER,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_DRONE_SPAWN_EGG = register(
        "nether_drone",
        AlienEntityTypes.NETHER_DRONE,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_FACEHUGGER_SPAWN_EGG = register(
        "nether_facehugger",
        AlienEntityTypes.NETHER_FACEHUGGER,
        0x27221C,
        0xFAD855
    );

    public static final AVPDeferredHolder<Item> NETHER_OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "nether_ovamorph",
        AlienEntityTypes.NETHER_OVOMORPH,
        0x20131C,
        0xFCEE4B
    );

    public static final AVPDeferredHolder<Item> NETHER_PRAETORIAN_SPAWN_EGG = register(
        "nether_praetorian",
        AlienEntityTypes.NETHER_PRAETORIAN,
        0x310808,
        0x5d1f1f
    );

    public static final AVPDeferredHolder<Item> NETHER_WARRIOR_SPAWN_EGG = register(
        "nether_warrior",
        AlienEntityTypes.NETHER_WARRIOR,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> NETHER_QUEEN_SPAWN_EGG = register(
        "nether_queen",
        AlienEntityTypes.NETHER_QUEEN,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "ovamorph",
        AlienEntityTypes.OVOMORPH,
        0x615B45,
        0xBF7872
    );

    public static final AVPDeferredHolder<Item> PRAETORIAN_SPAWN_EGG = register(
        "praetorian",
        AlienEntityTypes.PRAETORIAN,
        0x010202,
        0x363534
    );

    public static final AVPDeferredHolder<Item> QUEEN_SPAWN_EGG = register("queen", AlienEntityTypes.QUEEN, 0x010202, 0x363534);

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "royal_aberrant_chestburster",
        AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "royal_aberrant_facehugger",
        AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_aberrant_ovamorph",
        AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "royal_nether_chestburster",
        AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_CHESTBURSTER_SPAWN_EGG = register(
        "royal_chestburster",
        AlienEntityTypes.ROYAL_CHESTBURSTER,
        0x29140a,
        0xe6b57b
    );

    public static final AVPDeferredHolder<Item> ROYAL_FACEHUGGER_SPAWN_EGG = register(
        "royal_facehugger",
        AlienEntityTypes.ROYAL_FACEHUGGER,
        0x4b4946,
        0x292729
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_FACEHUGGER_SPAWN_EGG = register(
        "royal_nether_facehugger",
        AlienEntityTypes.ROYAL_NETHER_FACEHUGGER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_nether_ovamorph",
        AlienEntityTypes.ROYAL_NETHER_OVOMORPH,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_OVOMORPH_SPAWN_EGG = register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_ovamorph",
        AlienEntityTypes.ROYAL_OVOMORPH,
        0x2a2918,
        0x34341f
    );

    public static final AVPDeferredHolder<Item> WARRIOR_SPAWN_EGG = register("warrior", AlienEntityTypes.WARRIOR, 0x010202, 0x4A4E55);

    public static final AVPDeferredHolder<Item> YAUTJA_SPAWN_EGG = register("yautja", PredatorEntityTypes.YAUTJA, 0xB9A86C, 0x5A4728);

    private static <E extends Mob> AVPDeferredHolder<Item> register(
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

    public static void initialize() {}
}
