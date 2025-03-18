package com.avp.common.lifecycle;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.growth.GrowthStage;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;

public class Lifecycles {

    public static final AlienLifecycle DEFAULT = register(
        new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.CHESTBURSTER,
                    AVPEntityTypes.DRONE,
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.DRONE,
                    AVPEntityTypes.WARRIOR,
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.WARRIOR,
                    AVPEntityTypes.PRAETORIAN,
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.PRAETORIAN,
                    AVPEntityTypes.QUEEN,
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final AlienLifecycle PRAETORIAN_SHORTCUT = register(
        new AlienLifecycle(
            null,
            List.of(
                new GrowthStage(
                    AVPEntityTypes.CHESTBURSTER_QUEEN,
                    AVPEntityTypes.PRAETORIAN,
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    private Lifecycles() {}

    private static AlienLifecycle register(AlienLifecycle lifecycle) {
        return AlienLifecycleRegistry.register(lifecycle);
    }

    public static void initialize() {}
}
