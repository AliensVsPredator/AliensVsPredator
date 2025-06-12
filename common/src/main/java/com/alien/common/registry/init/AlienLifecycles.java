package com.alien.common.registry.init;

import com.alien.common.model.lifecycle.AlienLifecycle;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.bvanseg.just.functional.function.Lazy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.service.Services;

public class AlienLifecycles {

    public static final Supplier<AlienLifecycle> BASE_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AlienEntityTypes.CHESTBURSTER.get(),
                    AlienEntityTypes.DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.DRONE.get(),
                    AlienEntityTypes.WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.WARRIOR.get(),
                    AlienEntityTypes.PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.PRAETORIAN.get(),
                    AlienEntityTypes.QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> RUNNER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.CHESTBURSTER.get(),
                    AlienEntityTypes.RUNNER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.RUNNER.get(),
                    AlienEntityTypes.PROWLER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.PROWLER.get(),
                    AlienEntityTypes.CRUSHER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ABERRANT_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                    AlienEntityTypes.ABERRANT_DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_DRONE.get(),
                    AlienEntityTypes.ABERRANT_WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_WARRIOR.get(),
                    AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                    AlienEntityTypes.ABERRANT_QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ABERRANT_RUNNER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                    AlienEntityTypes.ABERRANT_RUNNER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_RUNNER.get(),
                    AlienEntityTypes.ABERRANT_PROWLER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.ABERRANT_PROWLER.get(),
                    AlienEntityTypes.ABERRANT_CRUSHER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> NETHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new GrowthStage(
                    AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                    AlienEntityTypes.NETHER_DRONE.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.NETHER_DRONE.get(),
                    AlienEntityTypes.NETHER_WARRIOR.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.NETHER_WARRIOR.get(),
                    AlienEntityTypes.NETHER_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.NETHER_PRAETORIAN.get(),
                    AlienEntityTypes.NETHER_QUEEN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> NETHER_RUNNER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                    AlienEntityTypes.NETHER_RUNNER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.NETHER_RUNNER.get(),
                    AlienEntityTypes.NETHER_PROWLER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
                ),
                new GrowthStage(
                    AlienEntityTypes.NETHER_PROWLER.get(),
                    AlienEntityTypes.NETHER_CRUSHER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_CRUSHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                    AlienEntityTypes.CRUSHER.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_LIFECYCLE = register(
        () -> new AlienLifecycle(
            null,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                    AlienEntityTypes.PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_ABERRANT_CRUSHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                    AlienEntityTypes.ABERRANT_CRUSHER.get(),
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
                    AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                    AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    public static final Supplier<AlienLifecycle> ROYAL_NETHER_CRUSHER_LIFECYCLE = register(
        () -> new AlienLifecycle(
            AVPEntityTypeTags.RUNNER_HOSTS,
            List.of(
                new GrowthStage(
                    AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                    AlienEntityTypes.NETHER_CRUSHER.get(),
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
                    AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                    AlienEntityTypes.NETHER_PRAETORIAN.get(),
                    (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
                )
            )
        )
    );

    private AlienLifecycles() {}

    private static Supplier<AlienLifecycle> register(Supplier<AlienLifecycle> alienLifecycleSupplier) {
        // Note the use of lazy here.
        return Services.REGISTRY.registerAlienLifecycle(Lazy.of(alienLifecycleSupplier));
    }

    public static void initialize() {}
}
