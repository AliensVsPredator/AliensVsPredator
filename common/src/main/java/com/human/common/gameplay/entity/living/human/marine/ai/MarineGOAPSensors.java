package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ConsumableFireResistanceItemStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ThrowableFireResistanceItemStrategies;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

import java.util.List;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.registry.tag.AVPBiomeTags;

public class MarineGOAPSensors {

    // TODO: There are cases where there may be multiple intents. Ex. if in a nuclear biome and underwater.
    public static final Sensor.Mono<Marine, ArmorIntent> ARMOR_INTENT = Sensor.map(
        StateKey.sensed("armor_intent"),
        marine -> {
            // TODO: Fix this check to check nearby blocks.
            if (marine.level().getBiome(marine.blockPosition()).is(AVPBiomeTags.IS_IRRADIATED)) {
                return ArmorIntent.RADIATION_PROTECTION;
            }

            if (marine.isOnFire()) {
                return ArmorIntent.FIRE_PROTECTION;
            }

            if (marine.isUnderWater()) {
                return ArmorIntent.DROWNING_PROTECTION;
            }

            return ArmorIntent.BEST_DEFENSE;
        }
    );

    public static final Sensor.Mono<Marine, List<AVPInventory.Entry>> CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY = Sensor.map(
        StateKey.sensed("consumable_fire_resistance_items_in_inventory"),
        marine -> marine.getInventory()
            .filterEntriesByStack(ConsumableFireResistanceItemStrategies::isConsumableFireResistanceItem)
    );

    public static final Sensor.Mono<Marine, Boolean> HAS_CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY = Sensor.compose(
        CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(),
        StateKey.sensed("has_consumable_fire_resistance_items_in_inventory"),
        (marine, entries) -> !entries.isEmpty()
    );

    public static final Sensor.Mono<Marine, Boolean> HAS_CONSUMABLE_FIRE_RESISTANCE_ITEM_EQUIPPED = Sensor.map(
        StateKey.sensed("has_consumable_fire_resistance_item_equipped"),
        marine -> ConsumableFireResistanceItemStrategies.strategyFor(marine.getMainHandItem()).isSome()
    );

    public static final Sensor.Mono<Marine, Boolean> HAS_WATER_BUCKET_IN_INVENTORY = Sensor.map(
        StateKey.sensed("has_water_bucket_in_inventory"),
        marine -> marine.getInventory().hasItem(Items.WATER_BUCKET)
    );

    public static final Sensor.Mono<LivingEntity, Boolean> HAS_WATER_BUCKET_EQUIPPED = Sensor.map(
        StateKey.sensed("has_water_bucket_equipped"),
        livingEntity -> livingEntity.getMainHandItem().is(Items.WATER_BUCKET)
    );

    public static final Sensor.Mono<Marine, List<AVPInventory.Entry>> THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY = Sensor.map(
        StateKey.sensed("throwable_fire_resistance_items_in_inventory"),
        marine -> marine.getInventory()
            .filterEntriesByStack(ThrowableFireResistanceItemStrategies::isThrowableFireResistanceItem)
    );

    public static final Sensor.Mono<Marine, Boolean> HAS_THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY = Sensor.compose(
        THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(),
        StateKey.sensed("has_throwable_fire_resistance_items_in_inventory"),
        (marine, entries) -> !entries.isEmpty()
    );

    public static final Sensor.Mono<Marine, Boolean> HAS_THROWABLE_FIRE_RESISTANCE_ITEM_EQUIPPED = Sensor.map(
        StateKey.sensed("has_throwable_fire_resistance_item_equipped"),
        marine -> ThrowableFireResistanceItemStrategies.strategyFor(marine.getMainHandItem()).isSome()
    );

    public static final Sensor.Mono<Marine, Boolean> IS_CURRENT_BLOCK_POS_REPLACEABLE = Sensor.map(
        StateKey.sensed("is_current_block_pos_replaceable"),
        marine -> marine.level().getBlockState(marine.blockPosition()).canBeReplaced()
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
