package com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPSensors;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.action.EquipWaterBucketAction;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.action.PlaceWaterAtFeetAction;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;

public class ExtinguishFireActions {

    public static final Action<Marine> EQUIP_WATER_BUCKET_ACTION = Action.<Marine>builder("EquipWaterBucketAction")
        .addPrecondition(ExtinguishFireSensors.WATER_BUCKET_IN_INVENTORY.key(), Expressions.Option.isSome())
        .addEffect(ExtinguishFireSensors.HAS_WATER_BUCKET_EQUIPPED.key().asDerived(), true)
        .withPerformCallback(EquipWaterBucketAction::perform)
        .build();

    public static final Action<Marine> PLACE_WATER_AT_FEET_ACTION = Action.<Marine>builder("PlaceWaterAtFeetAction")
        .addPrecondition(GOAPSensors.IS_ON_GROUND.key(), Expressions.Boolean.isTrue())
        .addPrecondition(ExtinguishFireSensors.HAS_WATER_BUCKET_EQUIPPED.key(), Expressions.Boolean.isTrue())
        .addPrecondition(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE.key(), Expressions.Boolean.isTrue())
        .addEffect(GOAPSensors.IS_ON_FIRE.key().asDerived(), false)
        .withPerformCallback(PlaceWaterAtFeetAction::perform)
        .withFinishCallback(PlaceWaterAtFeetAction::onFinish)
        .build();

    private ExtinguishFireActions() {
        throw new UnsupportedOperationException();
    }
}
