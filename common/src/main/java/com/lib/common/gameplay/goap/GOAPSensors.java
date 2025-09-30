package com.lib.common.gameplay.goap;

import com.just.goap.Sensor;
import com.lib.common.gameplay.goap.sensor.PotionEntriesInInventorySensor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

import java.util.List;
import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryHolder;
import com.avp.common.registry.tag.AVPBiomeTags;
import com.avp.common.util.AVPPredicates;

public class GOAPSensors {

    public static final Sensor<Entity, Boolean> IS_ON_FIRE = Sensor.direct(GOAPKeys.IS_ON_FIRE, Entity::isOnFire);

    public static final Sensor<LivingEntity, Boolean> IS_PROTECTED_FROM_DROWNING = Sensor.direct(GOAPKeys.IS_PROTECTED_FROM_DROWNING, entity -> entity.hasEffect(MobEffects.WATER_BREATHING));

    public static final Sensor<LivingEntity, Boolean> IS_PROTECTED_FROM_FIRE = Sensor.direct(GOAPKeys.IS_PROTECTED_FROM_FIRE, entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE));

    public static final Sensor<Entity, Boolean> IS_UNDERWATER = Sensor.direct(GOAPKeys.IS_UNDERWATER, Entity::isUnderWater);

    public static final Sensor<Entity, List<BlockPos>> NEARBY_BLOCK_POSITIONS = Sensor.direct(
        GOAPKeys.NEARBY_BLOCK_POSITIONS,
        entity -> BlockPos.betweenClosedStream(entity.getBoundingBox().inflate(1)).toList()
    );

    public static final Sensor<Entity, Boolean> IS_NEAR_RADIOACTIVE_BIOME = Sensor.derived(
        GOAPKeys.IS_NEAR_RADIOACTIVE_BIOME,
        GOAPKeys.NEARBY_BLOCK_POSITIONS,
        (entity, nearbyBlockPositions) -> nearbyBlockPositions.stream()
            .anyMatch(blockPos -> entity.level().getBiome(blockPos).is(AVPBiomeTags.IS_IRRADIATED))
    );

    public static final Sensor<Entity, List<Entity>> NEARBY_ENTITIES = Sensor.direct(
        GOAPKeys.NEARBY_ENTITIES,
        entity -> entity.level()
            .getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(16), AVPPredicates.alwaysTrue())
    );

    public static final Sensor<Entity, List<LivingEntity>> NEARBY_LIVING_ENTITIES = Sensor.derived(
        GOAPKeys.NEARBY_LIVING_ENTITIES,
        GOAPKeys.NEARBY_ENTITIES,
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> e instanceof LivingEntity)
            .map(e -> (LivingEntity) e)
            .toList()
    );

    public static final Sensor<AVPInventoryHolder, List<AVPInventory.Entry>> ARMOR_ENTRIES_IN_INVENTORY = Sensor.direct(
        GOAPKeys.ARMOR_ENTRIES_IN_INVENTORY,
        inventoryHolder -> inventoryHolder.getInventory().filterEntriesByItem(item -> item instanceof ArmorItem)
    );

    public static final Sensor<AVPInventoryHolder, Map<Holder<MobEffect>, List<AVPInventory.Entry>>> POTION_ENTRIES_IN_INVENTORY = Sensor
        .direct(
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            PotionEntriesInInventorySensor::sense
        );

    public static final Sensor<AVPInventoryHolder, List<AVPInventory.Entry>> FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY =
        Sensor.derived(
            GOAPKeys.FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY,
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            (inventoryHolder, potionItemsInInventory) -> potionItemsInInventory.getOrDefault(MobEffects.FIRE_RESISTANCE, List.of())
        );

    public static final Sensor<AVPInventoryHolder, List<AVPInventory.Entry>> WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY =
        Sensor.derived(
            GOAPKeys.WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY,
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            (inventoryHolder, potionItemsInInventory) -> potionItemsInInventory.getOrDefault(MobEffects.WATER_BREATHING, List.of())
        );

    private GOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
