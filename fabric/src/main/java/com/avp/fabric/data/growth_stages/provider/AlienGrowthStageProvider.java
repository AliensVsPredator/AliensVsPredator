package com.avp.fabric.data.growth_stages.provider;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.init.AlienEntityTypes;

import java.util.function.BiConsumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.fabric.data.growth_stages.GrowthConstants;

public class AlienGrowthStageProvider {

    public static void provide(BiConsumer<String, GrowthStage> biConsumer) {
        providerBaseGrowthStages(biConsumer);
        providerRunnerGrowthStages(biConsumer);

        biConsumer.accept(
            "royal_chestburster_to_praetorian",
            new GrowthStage(
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                AlienEntityTypes.PRAETORIAN.get(),
                GrowthConstants.ROYAL_CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );

        biConsumer.accept(
            "royal_chestburster_to_crusher",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                AlienEntityTypes.CRUSHER.get(),
                GrowthConstants.ROYAL_CHESTBURSTER_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }

    private static void providerRunnerGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "chestburster_to_runner",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.CHESTBURSTER.get(),
                AlienEntityTypes.RUNNER.get(),
                GrowthConstants.CHESTBURSTER_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "runner_to_prowler",
            new GrowthStage(
                AlienEntityTypes.RUNNER.get(),
                AlienEntityTypes.PROWLER.get(),
                GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "prowler_to_crusher",
            new GrowthStage(
                AlienEntityTypes.PROWLER.get(),
                AlienEntityTypes.CRUSHER.get(),
                GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }

    private static void providerBaseGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "chestburster_to_drone",
            new GrowthStage(
                AlienEntityTypes.CHESTBURSTER.get(),
                AlienEntityTypes.DRONE.get(),
                GrowthConstants.CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "drone_to_warrior",
            new GrowthStage(AlienEntityTypes.DRONE.get(), AlienEntityTypes.WARRIOR.get(), GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS)
        );
        biConsumer.accept(
            "warrior_to_praetorian",
            new GrowthStage(AlienEntityTypes.WARRIOR.get(), AlienEntityTypes.PRAETORIAN.get(), GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS)
        );
        biConsumer.accept(
            "praetorian_to_queen",
            new GrowthStage(
                AlienEntityTypes.PRAETORIAN.get(),
                AlienEntityTypes.QUEEN.get(),
                GrowthConstants.PRAETORIAN_GROWTH_TIME_IN_TICKS
            )
        );
    }
}
