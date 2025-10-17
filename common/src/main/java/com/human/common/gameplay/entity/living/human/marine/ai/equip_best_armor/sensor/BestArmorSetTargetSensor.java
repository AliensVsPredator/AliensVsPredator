package com.human.common.gameplay.entity.living.human.marine.ai.equip_best_armor.sensor;

import com.human.common.gameplay.entity.living.human.marine.ai.equip_best_armor.EquipBestArmorSensors;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorSetTarget;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.LivingEntity;

public class BestArmorSetTargetSensor {

    public static ArmorSetTarget sense(LivingEntity livingEntity, ReadableWorldState worldState) {
        if (
            worldState.getOrDefault(GOAPSensors.IS_IN_LAVA.key(), false)
                && !worldState.getOrDefault(GOAPSensors.HAS_FIRE_RESISTANCE.key(), false)
        ) {
            return worldState.getOrDefault(
                EquipBestArmorSensors.PLATED_NETHER_CHITIN_ARMOR_SET_TARGET.key(),
                worldState.getOrDefault(EquipBestArmorSensors.NETHER_CHITIN_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY)
            );
        }

        if (worldState.getOrDefault(GOAPSensors.IS_NEAR_RADIOACTIVE_BIOME.key(), false)) {
            return worldState.getOrDefault(EquipBestArmorSensors.MK50_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);
        }

        if (
            worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false)
                && !worldState.getOrDefault(GOAPSensors.HAS_FIRE_RESISTANCE.key(), false)
        ) {
            return worldState.getOrDefault(
                EquipBestArmorSensors.PLATED_NETHER_CHITIN_ARMOR_SET_TARGET.key(),
                worldState.getOrDefault(EquipBestArmorSensors.NETHER_CHITIN_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY)
            );
        }

        if (
            worldState.getOrDefault(GOAPSensors.IS_UNDERWATER.key(), false)
                && !worldState.getOrDefault(GOAPSensors.HAS_WATER_BREATHING.key(), false)
        ) {
            return worldState.getOrDefault(EquipBestArmorSensors.PRESSURE_SUIT_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);
        }

        // TODO: Select "best" armor pieces (depending on defense, enchantments, etc.)
        // TODO: Equip facehugger-resistant helmets if facehuggers are nearby.
        // TODO: Equip slow-falling boots if entity is falling.
        // TODO: Equip sneak speed boots if sneaking around.
        // TODO: Equip leather boots if walking near powdered snow.
        // TODO: Favor fire protection if entity is on fire and has no fire resistance.
        // TODO: Favor blast protection if a creeper, boiler, TNT or grenade is nearby and about to explode.
        // var helmet = ...
        // TODO: Get score of current helmet
        // TODO: Get score of helmets in inventory
        // TODO: Get score of helmets in world
        // TODO: Get item target or result w/ best score.
        // TODO: Assign item target to helmet slot in ArmorSetTarget returned here.
        // TODO: Repeat with chestplate, leggings and boots.
        // var chestplate = ...
        // var leggings = ...
        // var boots = ...
        return ArmorSetTarget.EMPTY;
    }
}
