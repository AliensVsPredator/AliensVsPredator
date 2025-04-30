package com.avp.fabric.common.entity.ai;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;
import java.util.Set;

import com.avp.fabric.common.entity.ai.util.CombatResponse;
import com.avp.fabric.common.entity.ai.util.ItemType;
import com.avp.fabric.goap.TypedIdentifier;

// TODO: There should be a registry here to guard against accidental duplicate identifiers.
public class GOAPConstants {

    // Self state

    // Used for wandering logic.
    public static final TypedIdentifier<Boolean> IS_BORED = new TypedIdentifier<>("isBored");

    public static final TypedIdentifier<Boolean> IS_HEALTHY = new TypedIdentifier<>("isHealthy");

    // Inventory state

    public static final TypedIdentifier<Set<ItemType>> ITEM_TYPES_IN_INVENTORY = new TypedIdentifier<>("itemTypesInInventory");

    public static final TypedIdentifier<Boolean> HAS_FREE_INVENTORY_SLOT = new TypedIdentifier<>("hasFreeInventorySlot");

    public static final TypedIdentifier<ItemType> MAIN_HAND_ITEM_TYPE = new TypedIdentifier<>("mainHandItemType");

    public static final TypedIdentifier<ItemType> OFF_HAND_ITEM_TYPE = new TypedIdentifier<>("offHandItemType");

    // Combat state

    public static final TypedIdentifier<? super CombatResponse> COMBAT_RESPONSE = new TypedIdentifier<>("combatResponse");

    // Surrounding entity state

    // General entity sensory info

    public static final TypedIdentifier<List<? extends Entity>> NEARBY_ENTITIES = new TypedIdentifier<>("nearbyEntities");

    public static final TypedIdentifier<List<? extends LivingEntity>> NEARBY_LIVING_ENTITIES = new TypedIdentifier<>(
        "nearbyLivingEntities"
    );

    public static final TypedIdentifier<List<? extends ItemEntity>> NEARBY_ITEM_ENTITIES = new TypedIdentifier<>("nearbyItemEntities");

    // Entity attack targeting

    public static final TypedIdentifier<Boolean> IS_ATTACK_TARGET_ENTITY_IN_RANGE = new TypedIdentifier<>("isAttackTargetEntityInRange");

    public static final TypedIdentifier<List<? extends LivingEntity>> NEARBY_ATTACK_TARGET_ENTITIES = new TypedIdentifier<>(
        "nearbyAttackTargetEntities"
    );

    public static final TypedIdentifier<Option<? extends LivingEntity>> NEAREST_ATTACK_TARGET_ENTITY = new TypedIdentifier<>(
        "nearestAttackTargetEntity"
    );

    // Food item entity targeting

    public static final TypedIdentifier<Boolean> IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE = new TypedIdentifier<>(
        "isFoodTargetItemEntityInRange"
    );

    public static final TypedIdentifier<List<? extends ItemEntity>> NEARBY_FOOD_ITEM_ENTITIES = new TypedIdentifier<>(
        "nearbyFoodItemEntities"
    );

    public static final TypedIdentifier<Option<? extends ItemEntity>> NEAREST_FOOD_ITEM_ENTITY = new TypedIdentifier<>(
        "nearestFoodItemEntity"
    );
}
