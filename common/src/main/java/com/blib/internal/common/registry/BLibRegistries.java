package com.blib.internal.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.BLib;
import com.blib.common.network.data.DataSyncKey;

@ApiStatus.Internal
public class BLibRegistries {

    public static final ResourceKey<Registry<DataSyncKey<?>>> DATA_SYNC_KEYS = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("data_sync_keys")
    );

    public static final List<Registry<?>> REGISTRATION_ORDER = List.of(
        // Independent registries.
        BuiltInRegistries.BLOCK,
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        BuiltInRegistries.DECORATED_POT_PATTERN,
        BuiltInRegistries.ENTITY_TYPE,
        BuiltInRegistries.GAME_EVENT,
        BuiltInRegistries.MENU,
        BuiltInRegistries.MOB_EFFECT,
        BuiltInRegistries.PARTICLE_TYPE,
        BuiltInRegistries.POINT_OF_INTEREST_TYPE,
        BuiltInRegistries.RECIPE_SERIALIZER,
        BuiltInRegistries.RECIPE_TYPE,
        BuiltInRegistries.SOUND_EVENT,
        BuiltInRegistries.VILLAGER_PROFESSION,
        // Depends on sound events.
        BuiltInRegistries.ARMOR_MATERIAL,
        // Depends on blocks.
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        // Potentially depends on blocks (block items), entities (spawn egg items) or armor materials (armor items).
        BuiltInRegistries.ITEM,
        // Depends on blocks and items.
        BuiltInRegistries.CREATIVE_MODE_TAB
    );

}
