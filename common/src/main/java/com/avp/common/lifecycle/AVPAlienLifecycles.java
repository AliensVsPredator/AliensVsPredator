package com.avp.common.lifecycle;

import com.bvanseg.just.functional.function.Lazy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.growth.GrowthStage;
import com.avp.service.Services;

public class AVPAlienLifecycles {

    public static final Supplier<AlienLifecycle> BASE_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.CHESTBURSTER.get(),
                    AVPEntityTypes.DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.DRONE.get(),
                    AVPEntityTypes.WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.WARRIOR.get(),
                    AVPEntityTypes.PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.PRAETORIAN.get(),
                    AVPEntityTypes.QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ABERRANT_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.ABERRANT_CHESTBURSTER.get(),
                    AVPEntityTypes.ABERRANT_DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.ABERRANT_DRONE.get(),
                    AVPEntityTypes.ABERRANT_WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.ABERRANT_WARRIOR.get(),
                    AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                    AVPEntityTypes.ABERRANT_QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> NETHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AVPEntityTypes.NETHER_CHESTBURSTER.get(),
                    AVPEntityTypes.NETHER_DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.NETHER_DRONE.get(),
                    AVPEntityTypes.NETHER_WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.NETHER_WARRIOR.get(),
                    AVPEntityTypes.NETHER_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AVPEntityTypes.NETHER_PRAETORIAN.get(),
                    AVPEntityTypes.NETHER_QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null,
            List.of(
                new GrowthStage(
                    AVPEntityTypes.ROYAL_CHESTBURSTER.get(),
                    AVPEntityTypes.PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_ABERRANT_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null,
            List.of(
                new GrowthStage(
                    AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                    AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_NETHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null,
            List.of(
                new GrowthStage(
                    AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                    AVPEntityTypes.NETHER_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    private AVPAlienLifecycles() {}

    private static Supplier<AlienLifecycle> register(Supplier<AlienLifecycle> alienLifecycleSupplier) {
        // Note the use of lazy here.
        return Services.REGISTRY.registerAlienLifecycle(Lazy.of(alienLifecycleSupplier));
    }

    public static void initialize() {}
}
