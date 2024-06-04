package org.avp.common.entity.ai.parasite;

import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.action.impl.LeapTowardsTargetAction;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.Sensor;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.ClosestTargetSensor;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.api.entity.ai.goal.impl.RideTargetGoal;
import org.avp.common.entity.ai.parasite.goal.ParasiteIdleMoveGoal;
import org.avp.common.entity.ai.parasite.goal.SmotherHostGoal;
import org.avp.common.entity.ai.parasite.goal.ImpregnateHostGoal;
import org.avp.api.entity.ai.goal.impl.MoveToTargetGoal;
import org.avp.common.util.AVPPredicates;

import java.util.List;

public class ParasiteBrain extends GOAPBrain {

    private static final int PARASITE_SMOTHER_TIME = 5 * 60 * 20;

    private final AVPAbstractParasite parasite;

    public ParasiteBrain(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    protected void addSensors(List<Sensor> sensors) {
        var cache = getCache();
        sensors.add(
            new NearbyEntitiesSensor(cache, parasite)
                // Disable sensor if facehugger is infertile.
                .disableIf(() -> !parasite.isFertile())
        );
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(
            new ClosestTargetSensor(
                cache,
                parasite,
                maybeTarget -> AVPPredicates.IS_HOST.test(maybeTarget) &&
                    // If target has no passengers or if the parasite is attached to the target.
                    (maybeTarget.getPassengers().isEmpty() || parasite.getVehicle() == maybeTarget)
            )
        );
    }

    @Override
    protected void addGoals(Planner planner) {
        // Anticipated plan for impregnation
        addImpregnationGoals(planner);

        // Anticipated plan for smothering
        addSmotherGoals(planner);
    }

    private void addImpregnationGoals(Planner planner) {
        planner.addGoal(new ParasiteIdleMoveGoal(parasite));
        planner.addGoal(
            new MoveToTargetGoal(parasite)
                .addAction(new LeapTowardsTargetAction(parasite))
        );
        planner.addGoal(new RideTargetGoal(parasite));
        planner.addGoal(new ImpregnateHostGoal(parasite));
    }

    private void addSmotherGoals(Planner planner) {
        planner.addGoal(new SmotherHostGoal(parasite, PARASITE_SMOTHER_TIME));
    }
}
