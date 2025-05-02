package com.avp.neoforge.data;

import com.avp.AVPResources;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AVPEntitySpawnKeys {

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_OVAMORPH = createKey("add_spawns_ovamorph ");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_CHESTBURSTER = createKey("add_spawns_chestburster");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_DRONE = createKey("add_spawns_drone");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_WARRIOR = createKey("add_spawns_warrior");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_PRAETORIAN = createKey("add_spawns_praetorian");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_QUEEN = createKey("add_spawns_queen");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_OVAMORPH = createKey("add_spawns_nether_ovamorph");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_CHESTBURSTER = createKey("add_spawns_nether_chestburster");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_DRONE = createKey("add_spawns_nether_drone");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_WARRIOR = createKey("add_spawns_nether_warrior");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_PRAETORIAN = createKey("add_spawns_nether_praetorian");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_NETHER_QUEEN = createKey("add_spawns_nether_queen");

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_YAUTJA = createKey("add_spawns_yautja");

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AVPResources.location(name));
    }
}
