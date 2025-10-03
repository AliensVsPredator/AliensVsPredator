package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.DrinkPotionAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestArmorAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestWaterbreathingArmorAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPStateKeys;
import net.minecraft.world.effect.MobEffects;

public class MarineGOAPActions {

    public static final Action<Marine> DRINK_FIRE_RESISTANCE_POTION = Action.<Marine>builder("DrinkFireResistancePotionAction")
        .addPrecondition(GOAPStateKeys.POTION_ENTRIES_IN_INVENTORY, Expressions.Map.containsKey(MobEffects.FIRE_RESISTANCE))
        .addEffect(GOAPStateKeys.IS_PROTECTED_FROM_FIRE.asDerived(), true)
        .withPerformCallback((a, b, c) -> DrinkPotionAction.perform(MobEffects.FIRE_RESISTANCE, a, b, c))
        .build();

    public static final Action<Marine> DRINK_INSTANT_HEALTH_POTION = Action.<Marine>builder("DrinkInstantHealthPotionAction")
        .addPrecondition(GOAPStateKeys.POTION_ENTRIES_IN_INVENTORY, Expressions.Map.containsKey(MobEffects.HEAL))
        .addEffect(GOAPStateKeys.IS_FULL_HEALTH.asDerived(), true)
        .withPerformCallback((a, b, c) -> DrinkPotionAction.perform(MobEffects.HEAL, a, b, c))
        .withCostCallback((marine, $2) -> marine.getHealth() / marine.getMaxHealth())
        .build();

    public static final Action<Marine> DRINK_WATER_BREATHING_POTION = Action.<Marine>builder("DrinkWaterBreathingPotionAction")
        .addPrecondition(GOAPStateKeys.POTION_ENTRIES_IN_INVENTORY, Expressions.Map.containsKey(MobEffects.WATER_BREATHING))
        .addEffect(GOAPStateKeys.IS_PROTECTED_FROM_DROWNING.asDerived(), true)
        .withPerformCallback((a, b, c) -> DrinkPotionAction.perform(MobEffects.WATER_BREATHING, a, b, c))
        .build();

    public static final Action<Marine> EQUIP_BEST_ARMOR = Action.<Marine>builder("EquipBestArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_ARMOR_SET, Expressions.Option.isSome())
        .addEffect(MarineGOAPKeys.BEST_ARMOR_SET.asDerived(), Option.none())
        .withPerformCallback(EquipBestArmorAction::perform)
        .build();

    public static final Action<Marine> EQUIP_BEST_WATER_BREATHING_ARMOR = Action.<Marine>builder("EquipBestWaterBreathingArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Expressions.Option.isSome())
        .addEffect(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET.asDerived(), Option.none())
        .withPerformCallback(EquipBestWaterbreathingArmorAction::perform)
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
