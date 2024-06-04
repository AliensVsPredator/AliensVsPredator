package org.avp.common.entity.ai.ovamorph;

import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.Sensor;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.ClosestTargetSensor;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.entity.ai.ovamorph.goal.DisturbedGoal;
import org.avp.common.entity.ai.ovamorph.goal.OpenGoal;
import org.avp.common.entity.ai.ovamorph.goal.ReleaseFacehuggerGoal;
import org.avp.common.util.AVPPredicates;

import java.util.List;
import java.util.Set;

public class OvamorphBrain extends GOAPBrain {

    private final AVPAbstractOvamorph ovamorph;

    public OvamorphBrain(AVPAbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
    }

    @Override
    protected void addSensors(List<Sensor> sensors) {
        var cache = getCache();
        sensors.add(
            new NearbyEntitiesSensor(cache, ovamorph)
                // Disable sensor if ovamorph has no facehugger.
                .disableIf(() -> !ovamorph.hasFacehugger())
        );
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(new ClosestTargetSensor(cache, ovamorph, AVPPredicates.IS_HOST));
    }

    @Override
    protected void addGoals(Planner planner) {
        planner.addGoal(new DisturbedGoal(ovamorph));
        planner.addGoal(new OpenGoal(ovamorph));
        planner.addGoal(new ReleaseFacehuggerGoal(ovamorph));
    }
}
