package org.avp.common.entity.ai.parasite;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.ai.goal.impl.IdleMoveGoal;
import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.impl.NearbyEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.NearbyLivingEntitiesSensor;
import org.avp.api.entity.ai.sensor.impl.TargetSensor;
import org.avp.common.entity.ai.parasite.goal.fertile.AttachToHostGoal;
import org.avp.common.entity.ai.parasite.goal.infertile.DetachFromHostGoal;
import org.avp.common.entity.ai.parasite.goal.fertile.ImpregnateHostGoal;
import org.avp.common.entity.ai.parasite.goal.fertile.MoveToHostGoal;
import org.avp.common.util.AVPPredicates;

import java.util.Set;

public class ParasiteBrain extends GOAPBrain {

    public ParasiteBrain(Monster parasite) {
        super(
            new Planner(
                Set.of(
                    new IdleMoveGoal(parasite),

                    // Anticipated plan for impregnation
                    new MoveToHostGoal(parasite),
                    new AttachToHostGoal(parasite),
                    new ImpregnateHostGoal(parasite),

                    // Anticipated plan for smothering
                    new DetachFromHostGoal(parasite, 5 * 60 * 20)
                )
            )
        );

        if (!(parasite instanceof Parasite)) {
            throw new IllegalArgumentException("Non-parasitic entity was given a ParasiteBrain: " + parasite);
        }

        var cache = getCache();
        sensors.add(
            new NearbyEntitiesSensor(cache, parasite)
                // Disable sensor if facehugger is infertile.
                .disableIf(() -> !((Parasite) parasite).isFertile())
        );
        sensors.add(new NearbyLivingEntitiesSensor(cache));
        sensors.add(new TargetSensor(cache, parasite, AVPPredicates.IS_HOST));
    }
}
