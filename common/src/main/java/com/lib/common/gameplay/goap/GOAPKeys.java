package com.lib.common.gameplay.goap;

import com.just.goap.GOAPKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;

public class GOAPKeys {

    public static final GOAPKey<Boolean> IS_FULL_HEALTH = new GOAPKey<>("is_full_health");

    public static final GOAPKey<Boolean> IS_NEAR_RADIOACTIVE_BIOME = new GOAPKey<>("is_near_radioactive_biome");

    public static final GOAPKey<Boolean> IS_ON_FIRE = new GOAPKey<>("is_on_fire");

    public static final GOAPKey<Boolean> IS_PROTECTED_FROM_DROWNING = new GOAPKey<>("is_protected_from_drowning");

    public static final GOAPKey<Boolean> IS_PROTECTED_FROM_FIRE = new GOAPKey<>("is_protected_from_fire");

    public static final GOAPKey<Boolean> IS_PROTECTED_FROM_RADIATION = new GOAPKey<>("is_protected_from_radiation");

    public static final GOAPKey<Boolean> IS_UNDERWATER = new GOAPKey<>("is_underwater");

    public static final GOAPKey<List<BlockPos>> NEARBY_BLOCK_POSITIONS = new GOAPKey<>("nearby_block_positions");

    public static final GOAPKey<List<Entity>> NEARBY_ENTITIES = new GOAPKey<>("nearby_entities");

    public static final GOAPKey<List<LivingEntity>> NEARBY_LIVING_ENTITIES = new GOAPKey<>("nearby_living_entities");

    public static final GOAPKey<List<AVPInventory.Entry>> ARMOR_ENTRIES_IN_INVENTORY = new GOAPKey<>("armor_entries_in_inventory");

    public static final GOAPKey<Map<Holder<MobEffect>, List<AVPInventory.Entry>>> POTION_ENTRIES_IN_INVENTORY = new GOAPKey<>(
        "potion_entries_in_inventory"
    );

    public static final GOAPKey<List<AVPInventory.Entry>> FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY = new GOAPKey<>(
        "fire_resistance_potion_entries_in_inventory"
    );

    public static final GOAPKey<List<AVPInventory.Entry>> WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY = new GOAPKey<>(
        "water_breathing_potion_entries_in_inventory"
    );
}
