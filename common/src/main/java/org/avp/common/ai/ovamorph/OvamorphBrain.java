package org.avp.common.ai.ovamorph;

import org.avp.api.ai.GOAPBrain;
import org.avp.api.ai.plan.Planner;
import org.avp.api.ai.sensor.Sensor;
import org.avp.api.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.ai.sensor.impl.ClosestTargetSensor;
import org.avp.common.game.entity.AbstractOvamorph;
import org.avp.common.ai.ovamorph.goal.DisturbedGoal;
import org.avp.common.ai.ovamorph.goal.OpenGoal;
import org.avp.common.ai.ovamorph.goal.ReleaseFacehuggerGoal;
import org.avp.common.util.AVPPredicates;

import java.util.List;

public class OvamorphBrain extends GOAPBrain {

    private final AbstractOvamorph ovamorph;

    public OvamorphBrain(AbstractOvamorph ovamorph) {
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
