package com.avp.common.lifecycle;

import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;

public class Lifecycles {

    // FIXME:
    // public static final AlienLifecycle BASE_LIFECYCLE = register(
    // new AlienLifecycle(
    // null, // No hosts = this lifecycle will be the default for all chestbursters.
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.CHESTBURSTER,
    // AVPEntityTypes.DRONE,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.DRONE,
    // AVPEntityTypes.WARRIOR,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.WARRIOR,
    // AVPEntityTypes.PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.PRAETORIAN,
    // AVPEntityTypes.QUEEN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
    // )
    // )
    // )
    // );
    //
    // public static final AlienLifecycle ABERRANT_LIFECYCLE = register(
    // new AlienLifecycle(
    // null, // No hosts = this lifecycle will be the default for all chestbursters.
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.ABERRANT_CHESTBURSTER,
    // AVPEntityTypes.ABERRANT_DRONE,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.ABERRANT_DRONE,
    // AVPEntityTypes.ABERRANT_WARRIOR,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.ABERRANT_WARRIOR,
    // AVPEntityTypes.ABERRANT_PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.ABERRANT_PRAETORIAN,
    // AVPEntityTypes.ABERRANT_QUEEN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
    // )
    // )
    // )
    // );
    //
    // public static final AlienLifecycle NETHER_LIFECYCLE = register(
    // new AlienLifecycle(
    // null, // No hosts = this lifecycle will be the default for all chestbursters.
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.NETHER_CHESTBURSTER,
    // AVPEntityTypes.NETHER_DRONE,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.NETHER_DRONE,
    // AVPEntityTypes.NETHER_WARRIOR,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.DRONE_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.NETHER_WARRIOR,
    // AVPEntityTypes.NETHER_PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.WARRIOR_MAX_GROWTH_TIMER_SECONDS)
    // ),
    // new GrowthStage(
    // AVPEntityTypes.NETHER_PRAETORIAN,
    // AVPEntityTypes.NETHER_QUEEN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS)
    // )
    // )
    // )
    // );
    //
    // public static final AlienLifecycle ROYAL_LIFECYCLE = register(
    // new AlienLifecycle(
    // null,
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.ROYAL_CHESTBURSTER,
    // AVPEntityTypes.PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
    // )
    // )
    // )
    // );
    //
    // public static final AlienLifecycle ROYAL_ABERRANT_LIFECYCLE = register(
    // new AlienLifecycle(
    // null,
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
    // AVPEntityTypes.ABERRANT_PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
    // )
    // )
    // )
    // );
    //
    // public static final AlienLifecycle ROYAL_NETHER_LIFECYCLE = register(
    // new AlienLifecycle(
    // null,
    // List.of(
    // new GrowthStage(
    // AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
    // AVPEntityTypes.NETHER_PRAETORIAN,
    // (int) TimeUnit.MINUTES.toSeconds(AVP.config.hiveConfigs.PRAETORIAN_SHORTCUT_TIMER_SECONDS)
    // )
    // )
    // )
    // );

    private Lifecycles() {}

    private static AlienLifecycle register(AlienLifecycle lifecycle) {
        return AlienLifecycleRegistry.register(lifecycle);
    }

    public static void initialize() {}
}
