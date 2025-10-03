package com.lib.common.gameplay.goap;

import com.just.goap.StateKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;

public class GOAPStateKeys {

    public static final StateKey.Sensed<Boolean> HAS_FIRE_RESISTANCE = StateKey.sensed("has_fire_resistance");

    public static final StateKey.Sensed<Boolean> HAS_WATER_BREATHING = StateKey.sensed("has_water_breathing");

    public static final StateKey.Sensed<Boolean> IS_FULL_HEALTH = StateKey.sensed("is_full_health");

    public static final StateKey.Sensed<Boolean> IS_NEAR_RADIOACTIVE_BIOME = StateKey.sensed("is_near_radioactive_biome");

    public static final StateKey.Sensed<Boolean> IS_ON_FIRE = StateKey.sensed("is_on_fire");

    public static final StateKey.Sensed<Boolean> IS_ON_GROUND = StateKey.sensed("is_on_ground");

    public static final StateKey.Sensed<Boolean> IS_UNDERWATER = StateKey.sensed("is_underwater");

    public static final StateKey.Sensed<List<BlockPos>> NEARBY_BLOCK_POSITIONS = StateKey.sensed("nearby_block_positions");

    public static final StateKey.Sensed<List<Entity>> NEARBY_ENTITIES = StateKey.sensed("nearby_entities");

    public static final StateKey.Sensed<List<LivingEntity>> NEARBY_LIVING_ENTITIES = StateKey.sensed("nearby_living_entities");

    public static final StateKey.Sensed<List<AVPInventory.Entry>> ARMOR_ENTRIES_IN_INVENTORY = StateKey.sensed(
        "armor_entries_in_inventory"
    );

    public static final StateKey.Sensed<Map<Holder<MobEffect>, List<AVPInventory.Entry>>> POTION_ENTRIES_IN_INVENTORY = StateKey.sensed(
        "potion_entries_in_inventory"
    );
}
