package com.avp.common.entity.living.human.marine.ai;

import com.avp.common.entity.ai.action.EatFoodToHealAction;
import com.avp.common.entity.ai.goal.HealthyGoal;
import com.avp.common.entity.ai.sensor.HasFoodSensor;
import com.avp.common.entity.ai.sensor.IsHealthySensor;
import com.avp.common.entity.ai.sensor.NearbyEntitySensor;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.util.AVPPredicates;
import com.avp.goap.GOAP;

public class MarineGOAP extends GOAP<Marine> {

    public MarineGOAP(Marine marine) {
        // TODO: Actually check the marine's inventory for food.
        addSensor(new HasFoodSensor<>(AVPPredicates.alwaysTrue()));
        addSensor(new IsHealthySensor<>(0.5F));
        addSensor(new NearbyEntitySensor<>());

        addGoal(new HealthyGoal());

        addAction(new EatFoodToHealAction<>(marine.getMaxHealth() * 0.2F));
    }

    @Override
    public void update(Marine context) {
        if (!context.isAlive() || context.isDeadOrDying()) {
            return;
        }

        super.update(context);
    }
}
