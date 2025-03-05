package com.avp.core.common.lifecycle;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.lifecycle.growth.GrowthStage;
import com.avp.core.common.lifecycle.registry.AlienLifecycleRegistry;

public class Lifecycles {

    public static final AlienLifecycle DEFAULT = register(
        new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.CHESTBURSTER,
                    AVPEntityTypes.DRONE,
                    (int) TimeUnit.MINUTES.toSeconds(20) * 20
                ),
                new GrowthStage(
                    AVPEntityTypes.DRONE,
                    AVPEntityTypes.WARRIOR,
                    (int) TimeUnit.MINUTES.toSeconds(40) * 20
                ),
                new GrowthStage(
                    AVPEntityTypes.WARRIOR,
                    AVPEntityTypes.PRAETORIAN,
                    (int) TimeUnit.MINUTES.toSeconds(80) * 20
                ),
                new GrowthStage(
                    AVPEntityTypes.PRAETORIAN,
                    AVPEntityTypes.QUEEN,
                    (int) TimeUnit.MINUTES.toSeconds(160) * 20
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
                    12_000
                )
            )
        )
    );

    private static AlienLifecycle register(AlienLifecycle lifecycle) {
        return AlienLifecycleRegistry.register(lifecycle);
    }

    public static void initialize() {}
}
