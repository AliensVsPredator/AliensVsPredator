package org.avp.common.entity.ai.parasite;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.TargetSensor;
import org.avp.common.entity.ai.parasite.goal.AttachToHostGoal;
import org.avp.common.entity.ai.parasite.goal.ImpregnateHostGoal;
import org.avp.common.entity.ai.parasite.goal.MoveToHostGoal;
import org.avp.common.util.AVPPredicates;

import java.util.Set;

public class ParasiteBrain extends GOAPBrain {

    public ParasiteBrain(Monster parasite) {
        super(
            new Planner(
                Set.of(
                    // Anticipated plan for impregnation
                    new MoveToHostGoal(parasite),
                    new AttachToHostGoal(parasite),
                    new ImpregnateHostGoal(parasite)

                    // Anticipated plan for smothering
                    // TODO: DetachFromHostGoal (attached to host and not fertile)

                    // Anticipated plan for 'dead' state
                    // TODO: InfertileGoal (NOT attached to host and not fertile)
                )
            )
        );

        if (!(parasite instanceof Parasite)) {
            throw new IllegalArgumentException("Non-parasitic entity was given a ParasiteBrain: " + parasite);
        }

        var cache = getCache();
        sensors.add(new NearbyEntitiesSensor(cache, parasite));
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(new TargetSensor(cache, parasite, AVPPredicates.IS_HOST));
    }
}
