package com.avp.fabric.data.growth_stages.provider;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.init.AlienEntityTypes;

import java.util.function.BiConsumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.fabric.data.growth_stages.GrowthConstants;

public class AberrantAlienGrowthStageProvider {

    public static void provide(BiConsumer<String, GrowthStage> biConsumer) {
        provideBaseAberrantGrowthStages(biConsumer);
        provideRunnerAberrantGrowthStages(biConsumer);

        biConsumer.accept(
            "royal_aberrant_chestburster_to_aberrant_praetorian",
            new GrowthStage(
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                GrowthConstants.ROYAL_CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );

        biConsumer.accept(
            "royal_aberrant_chestburster_to_aberrant_crusher",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ABERRANT_CRUSHER.get(),
                GrowthConstants.ROYAL_CHESTBURSTER_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }

    private static void provideBaseAberrantGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "aberrant_chestburster_to_aberrant_drone",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ABERRANT_DRONE.get(),
                GrowthConstants.CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "aberrant_drone_to_aberrant_warrior",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_DRONE.get(),
                AlienEntityTypes.ABERRANT_WARRIOR.get(),
                GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "aberrant_warrior_to_aberrant_praetorian",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_WARRIOR.get(),
                AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "aberrant_praetorian_to_aberrant_queen",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                AlienEntityTypes.ABERRANT_QUEEN.get(),
                GrowthConstants.PRAETORIAN_GROWTH_TIME_IN_TICKS
            )
        );
    }

    private static void provideRunnerAberrantGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "aberrant_chestburster_to_aberrant_runner",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ABERRANT_RUNNER.get(),
                GrowthConstants.CHESTBURSTER_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "aberrant_runner_to_aberrant_prowler",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_RUNNER.get(),
                AlienEntityTypes.ABERRANT_PROWLER.get(),
                GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "aberrant_prowler_to_aberrant_crusher",
            new GrowthStage(
                AlienEntityTypes.ABERRANT_PROWLER.get(),
                AlienEntityTypes.ABERRANT_CRUSHER.get(),
                GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }

}
