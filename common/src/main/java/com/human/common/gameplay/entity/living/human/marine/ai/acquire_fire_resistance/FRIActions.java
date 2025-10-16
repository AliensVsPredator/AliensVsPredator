package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action.EquipFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action.MoveToFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action.PickUpFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action.UseFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class FRIActions {

    public static final Action<PathfinderMob> MOVE_TO_BEST_FRI = Action.<PathfinderMob>builder("MoveToBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.WORLD))
        .addPrecondition(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key(), Expressions.Boolean.isFalse())
        .addEffect(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key().asDerived(), true)
        .withPerformCallback(MoveToFRIAction::perform)
        .withFinishCallback(MoveToFRIAction::onFinish)
        .build();

    public static <T extends LivingEntity & AVPInventoryHolder> Action<T> pickUpBestFRIFactory() {
        return Action.<T>builder("PickUpBestFRIAction")
            .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.WORLD))
            .addPrecondition(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key(), Expressions.Boolean.isTrue())
            .addEffect(FRISensors.BEST_FRI_LOCATION.key().asDerived(), ItemTarget.Location.INVENTORY)
            .withPerformCallback(PickUpFRIAction::perform)
            .build();
    }

    public static <T extends LivingEntity & AVPInventoryHolder> Action<T> equipBestFRIFactory() {
        return Action.<T>builder("EquipBestFRIAction")
            .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.INVENTORY))
            .addEffect(FRISensors.BEST_FRI_LOCATION.key().asDerived(), ItemTarget.Location.EQUIPPED)
            .withPerformCallback(EquipFRIAction::perform)
            .build();
    }

    public static final Action<LivingEntity> USE_BEST_FRI = Action.<LivingEntity>builder("UseBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.EQUIPPED))
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback(UseFRIAction::perform)
        .build();
}
