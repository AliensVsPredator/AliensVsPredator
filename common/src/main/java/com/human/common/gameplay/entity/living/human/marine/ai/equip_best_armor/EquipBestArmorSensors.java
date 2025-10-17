package com.human.common.gameplay.entity.living.human.marine.ai.equip_best_armor;

import com.alien.common.registry.init.item.AlienArmorItems;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorSet;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorSetTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.util.ItemSenseUtil;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.init.item.AVPArmorItems;

public class EquipBestArmorSensors {

    private static final ArmorSet MK50_ARMOR_SET = new ArmorSet(
        AVPArmorItems.MK50_HELMET,
        AVPArmorItems.MK50_CHESTPLATE,
        AVPArmorItems.MK50_LEGGINGS,
        AVPArmorItems.MK50_BOOTS
    );

    private static final ArmorSet NETHER_CHITIN_ARMOR_SET = new ArmorSet(
        AlienArmorItems.NETHER_CHITIN_HELMET,
        AlienArmorItems.NETHER_CHITIN_CHESTPLATE,
        AlienArmorItems.NETHER_CHITIN_LEGGINGS,
        AlienArmorItems.NETHER_CHITIN_BOOTS
    );

    private static final ArmorSet PLATED_NETHER_CHITIN_ARMOR_SET = new ArmorSet(
        AlienArmorItems.PLATED_NETHER_CHITIN_HELMET,
        AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
        AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
        AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS
    );

    private static final ArmorSet PRESSURE_SUIT_ARMOR_SET = new ArmorSet(
        AVPArmorItems.PRESSURE_HELMET,
        AVPArmorItems.PRESSURE_CHESTPLATE,
        AVPArmorItems.PRESSURE_LEGGINGS,
        AVPArmorItems.PRESSURE_BOOTS
    );

    public static final Sensor.Mono<LivingEntity, ArmorSetTarget> MK50_ARMOR_SET_TARGET = Sensors.lazyCompose(
        StateKey.sensed("mk50_armor_set"),
        (livingEntity, worldState) -> getFullArmorSetOrEmpty(livingEntity, worldState, MK50_ARMOR_SET)
    );

    public static final Sensor.Mono<LivingEntity, ArmorSetTarget> NETHER_CHITIN_ARMOR_SET_TARGET = Sensors.lazyCompose(
        StateKey.sensed("nether_chitin_armor_set"),
        (livingEntity, worldState) -> getFullArmorSetOrEmpty(livingEntity, worldState, NETHER_CHITIN_ARMOR_SET)
    );

    public static final Sensor.Mono<LivingEntity, ArmorSetTarget> PLATED_NETHER_CHITIN_ARMOR_SET_TARGET = Sensors.lazyCompose(
        StateKey.sensed("plated_nether_chitin_armor_set"),
        (livingEntity, worldState) -> getFullArmorSetOrEmpty(livingEntity, worldState, PLATED_NETHER_CHITIN_ARMOR_SET)
    );

    public static final Sensor.Mono<LivingEntity, ArmorSetTarget> PRESSURE_SUIT_ARMOR_SET_TARGET = Sensors.lazyCompose(
        StateKey.sensed("pressure_suit_armor_set"),
        (livingEntity, worldState) -> getFullArmorSetOrEmpty(livingEntity, worldState, PRESSURE_SUIT_ARMOR_SET)
    );

    public static final Sensor.Mono<LivingEntity, ArmorSetTarget> BEST_ARMOR_SET_TARGET = Sensors.lazyCompose(
        StateKey.sensed("best_armor_set"),
        (livingEntity, worldState) -> {

            if (
                worldState.getOrDefault(GOAPSensors.IS_IN_LAVA.key(), false)
                    && !worldState.getOrDefault(GOAPSensors.HAS_FIRE_RESISTANCE.key(), false)
            ) {
                return worldState.getOrDefault(
                    PLATED_NETHER_CHITIN_ARMOR_SET_TARGET.key(),
                    worldState.getOrDefault(NETHER_CHITIN_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY)
                );
            }

            if (worldState.getOrDefault(GOAPSensors.IS_NEAR_RADIOACTIVE_BIOME.key(), false)) {
                return worldState.getOrDefault(MK50_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);
            }

            if (
                worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false)
                    && !worldState.getOrDefault(GOAPSensors.HAS_FIRE_RESISTANCE.key(), false)
            ) {
                return worldState.getOrDefault(
                    PLATED_NETHER_CHITIN_ARMOR_SET_TARGET.key(),
                    worldState.getOrDefault(NETHER_CHITIN_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY)
                );
            }

            if (
                worldState.getOrDefault(GOAPSensors.IS_UNDERWATER.key(), false)
                    && !worldState.getOrDefault(GOAPSensors.HAS_WATER_BREATHING.key(), false)
            ) {
                return worldState.getOrDefault(PRESSURE_SUIT_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);
            }

            // TODO: Equip facehugger-resistant helmets if facehuggers are nearby.
            return ArmorSetTarget.EMPTY;
        }
    );

    public static final Sensor.Mono<LivingEntity, Boolean> IS_ANY_BEST_ARMOR_SET_PIECE_IN_WORLD = Sensors.compose(
        BEST_ARMOR_SET_TARGET.key(),
        StateKey.sensed("is_any_best_armor_set_piece_in_world"),
        (livingEntity, bestArmorSetTarget) -> bestArmorSetTarget.anyMatch(ItemTarget.Location.WORLD)
    );

    public static final Sensor.Mono<LivingEntity, Boolean> IS_ANY_BEST_ARMOR_SET_PIECE_IN_INVENTORY = Sensors.compose(
        BEST_ARMOR_SET_TARGET.key(),
        StateKey.sensed("is_any_best_armor_set_piece_in_inventory"),
        (livingEntity, bestArmorSetTarget) -> bestArmorSetTarget.anyMatch(ItemTarget.Location.INVENTORY)
    );

    public static final Sensor.Mono<LivingEntity, Boolean> ARE_ALL_BEST_ARMOR_SET_PIECES_EQUIPPED = Sensors.compose(
        BEST_ARMOR_SET_TARGET.key(),
        StateKey.sensed("are_all_best_armor_set_pieces_equipped"),
        (livingEntity, bestArmorSetTarget) -> bestArmorSetTarget.allNoneOrMatch(ItemTarget.Location.EQUIPPED)
    );

    private static @NotNull ArmorSetTarget getFullArmorSetOrEmpty(
        LivingEntity livingEntity,
        ReadableWorldState worldState,
        ArmorSet armorSet
    ) {
        var helmetTarget = ItemSenseUtil.findItemInWorldState(livingEntity, worldState, armorSet.helmet().get());

        if (helmetTarget.location() == ItemTarget.Location.NONE) {
            return ArmorSetTarget.EMPTY;
        }

        var chestplateTarget = ItemSenseUtil.findItemInWorldState(livingEntity, worldState, armorSet.chestplate().get());

        if (chestplateTarget.location() == ItemTarget.Location.NONE) {
            return ArmorSetTarget.EMPTY;
        }

        var chitinLeggingsTarget = ItemSenseUtil.findItemInWorldState(livingEntity, worldState, armorSet.leggings().get());

        if (chitinLeggingsTarget.location() == ItemTarget.Location.NONE) {
            return ArmorSetTarget.EMPTY;
        }

        var chitinBootsTarget = ItemSenseUtil.findItemInWorldState(livingEntity, worldState, armorSet.boots().get());

        if (chitinBootsTarget.location() == ItemTarget.Location.NONE) {
            return ArmorSetTarget.EMPTY;
        }

        return new ArmorSetTarget(
            helmetTarget,
            chestplateTarget,
            chitinLeggingsTarget,
            chitinBootsTarget
        );
    }

    private EquipBestArmorSensors() {
        throw new UnsupportedOperationException();
    }
}
