package org.avp.common.entity.ai.ovamorph;

import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.ClosestTargetSensor;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.entity.ai.ovamorph.goal.DisturbedGoal;
import org.avp.common.entity.ai.ovamorph.goal.OpenGoal;
import org.avp.common.entity.ai.ovamorph.goal.ReleaseFacehuggerGoal;
import org.avp.common.util.AVPPredicates;

import java.util.Set;

public class OvamorphBrain extends GOAPBrain {

    public OvamorphBrain(AVPAbstractOvamorph ovamorph) {
        super(
            new Planner(
                Set.of(
                    new DisturbedGoal(ovamorph),
                    new OpenGoal(ovamorph),
                    new ReleaseFacehuggerGoal(ovamorph)
                )
            )
        );

        var cache = getCache();
        sensors.add(
            new NearbyEntitiesSensor(cache, ovamorph)
                // Disable sensor if ovamorph has no facehugger.
                .disableIf(() -> !ovamorph.hasFacehugger())
        );
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(new ClosestTargetSensor(cache, ovamorph, AVPPredicates.IS_HOST));
    }
}
