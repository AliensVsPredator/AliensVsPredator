package com.avp.common.lifecycle;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.config.property.ConfigPropertyContainer;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.growth.GrowthStage;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;

public class Lifecycles {

    private static ConfigPropertyContainer properties = AVP.HIVES_CONFIG.properties();

    public static final AlienLifecycle DEFAULT = register(
        new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.CHESTBURSTER,
                    AVPEntityTypes.DRONE,
                    (int) TimeUnit.MINUTES.toSeconds(properties.getOrDefault(ConfigProperties.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS, 1200))
                ),
                new GrowthStage(
                    AVPEntityTypes.DRONE,
                    AVPEntityTypes.WARRIOR,
                    (int) TimeUnit.MINUTES.toSeconds(properties.getOrDefault(ConfigProperties.DRONE_MAX_GROWTH_TIMER_SECONDS, 800))
                ),
                new GrowthStage(
                    AVPEntityTypes.WARRIOR,
                    AVPEntityTypes.PRAETORIAN,
                    (int) TimeUnit.MINUTES.toSeconds(properties.getOrDefault(ConfigProperties.WARRIOR_MAX_GROWTH_TIMER_SECONDS, 1600))
                ),
                new GrowthStage(
                    AVPEntityTypes.PRAETORIAN,
                    AVPEntityTypes.QUEEN,
                    (int) TimeUnit.MINUTES.toSeconds(properties.getOrDefault(ConfigProperties.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS, 3200))
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
                    (int) TimeUnit.MINUTES.toSeconds(properties.getOrDefault(ConfigProperties.PRAETORIAN_SHORTCUT_TIMER_SECONDS, 600))
                )
            )
        )
    );

    private static AlienLifecycle register(AlienLifecycle lifecycle) {
        return AlienLifecycleRegistry.register(lifecycle);
    }

    public static void initialize() {}
}
