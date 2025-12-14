package com.blib.internal.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class BLibRegistries {

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
