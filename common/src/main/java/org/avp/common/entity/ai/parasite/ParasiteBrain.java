package org.avp.common.entity.ai.parasite;

import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.action.impl.LeapTowardsTargetAction;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.TargetSensor;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.api.entity.ai.goal.impl.RideTargetGoal;
import org.avp.common.entity.ai.parasite.goal.ParasiteIdleMoveGoal;
import org.avp.common.entity.ai.parasite.goal.DetachFromHostGoal;
import org.avp.common.entity.ai.parasite.goal.ImpregnateHostGoal;
import org.avp.api.entity.ai.goal.impl.MoveToTargetGoal;
import org.avp.common.util.AVPPredicates;

import java.util.Set;

public class ParasiteBrain extends GOAPBrain {

    private static final int PARASITE_SMOTHER_TIME = 5 * 60 * 20;

    public ParasiteBrain(AVPAbstractParasite parasite) {
        super(
            new Planner(
                Set.of(
                    new ParasiteIdleMoveGoal(parasite),

                    // Anticipated plan for impregnation
                    new MoveToTargetGoal(parasite)
                        .addAction(new LeapTowardsTargetAction(parasite)),
                    new RideTargetGoal(parasite),
                    new ImpregnateHostGoal(parasite),

                    // Anticipated plan for smothering
                    new DetachFromHostGoal(parasite, PARASITE_SMOTHER_TIME)
                )
            )
        );

        var cache = getCache();
        sensors.add(
            new NearbyEntitiesSensor(cache, parasite)
                // Disable sensor if facehugger is infertile.
                .disableIf(() -> !parasite.isFertile())
        );
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(
            new TargetSensor(
                cache,
                parasite,
                maybeTarget -> AVPPredicates.IS_HOST.test(maybeTarget) &&
                    // If target has no passengers or if the parasite is attached to the target.
                    (maybeTarget.getPassengers().isEmpty() || parasite.getVehicle() == maybeTarget)
            )
        );
    }
}
