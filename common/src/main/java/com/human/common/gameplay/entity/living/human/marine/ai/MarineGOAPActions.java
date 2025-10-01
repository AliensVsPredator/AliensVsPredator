package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.DrinkPotionAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestArmorAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestWaterbreathingArmorAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expression;
import com.lib.common.gameplay.goap.GOAPKeys;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;

public class MarineGOAPActions {

    public static final Action<Marine> DRINK_FIRE_RESISTANCE_POTION = Action.<Marine>builder(
        "DrinkFireResistancePotionAction"
    ).<Map<Holder<MobEffect>, List<AVPInventory
        .Entry>>>addPrecondition(
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            Expression.where(
                potionEntriesByEffect -> potionEntriesByEffect.containsKey(MobEffects.FIRE_RESISTANCE),
                "has fire resistance potion"
            )
        )
        .addEffect(GOAPKeys.IS_PROTECTED_FROM_FIRE, true)
        .withPerformPredicate((a, b, c) -> DrinkPotionAction.perform(MobEffects.FIRE_RESISTANCE, a, b, c))
        .build();

    public static final Action<Marine> DRINK_INSTANT_HEALTH_POTION = Action.<Marine>builder(
            "DrinkInstantHealthPotionAction"
        ).<Map<Holder<MobEffect>, List<AVPInventory
            .Entry>>>addPrecondition(
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            Expression.where(
                potionEntriesByEffect -> potionEntriesByEffect.containsKey(MobEffects.HEAL),
                "has instant health potion"
            )
        )
        .addEffect(GOAPKeys.IS_FULL_HEALTH, true)
        .withPerformPredicate((a, b, c) -> DrinkPotionAction.perform(MobEffects.HEAL, a, b, c))
        .withCostCallback((marine, $2) -> marine.getHealth() / marine.getMaxHealth())
        .build();

    public static final Action<Marine> DRINK_WATER_BREATHING_POTION = Action.<Marine>builder(
        "DrinkWaterBreathingPotionAction"
    ).<Map<Holder<MobEffect>, List<AVPInventory
        .Entry>>>addPrecondition(
            GOAPKeys.POTION_ENTRIES_IN_INVENTORY,
            Expression.where(
                potionEntriesByEffect -> potionEntriesByEffect.containsKey(MobEffects.WATER_BREATHING),
                "has water breathing potion"
            )
        )
        .addEffect(GOAPKeys.IS_PROTECTED_FROM_DROWNING, true)
        .withPerformPredicate((a, b, c) -> DrinkPotionAction.perform(MobEffects.WATER_BREATHING, a, b, c))
        .build();

    public static final Action<Marine> EQUIP_BEST_ARMOR = Action.<Marine>builder("EquipBestArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_ARMOR_SET, Expression.isSome())
        .addEffect(MarineGOAPKeys.BEST_ARMOR_SET, Option.none())
        .withPerformPredicate(EquipBestArmorAction::perform)
        .build();

    public static final Action<Marine> EQUIP_BEST_WATER_BREATHING_ARMOR = Action.<Marine>builder("EquipBestWaterBreathingArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Expression.isSome())
        .addEffect(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Option.none())
        .withPerformPredicate(EquipBestWaterbreathingArmorAction::perform)
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
