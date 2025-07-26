package com.avp.fabric.data.growth_stages.provider;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.init.AlienEntityTypes;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.BiConsumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.fabric.data.growth_stages.GrowthConstants;

public class NetherAlienGrowthStageProvider {

    public static void provide(BiConsumer<String, GrowthStage> biConsumer) {
        provideBaseNetherGrowthStages(biConsumer);
        providerRunnerNetherGrowthStages(biConsumer);

        biConsumer.accept(
            "royal_nether_chestburster_to_royal_nether_adolescent",
            new GrowthStage(
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                GrowthConstants.ROYAL_CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "royal_nether_adolescent_to_nether_praetorian",
            new GrowthStage(
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_PRAETORIAN.get(),
                GrowthConstants.ROYAL_ADOLESCENT_GROWTH_TIME_IN_TICKS
            )
        );

        biConsumer.accept(
            "royal_nether_adolescent_to_nether_crusher",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_CRUSHER.get(),
                GrowthConstants.ROYAL_ADOLESCENT_GROWTH_TIME_IN_TICKS / 2
            )
        );

        biConsumer.accept(
            "nether_adolescent_to_nether_spitter",
            new GrowthStage(
                List.of(EntityType.LLAMA, EntityType.TRADER_LLAMA),
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_SPITTER.get(),
                GrowthConstants.ADOLESCENT_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }

    private static void provideBaseNetherGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "nether_chestburster_to_nether_adolescent",
            new GrowthStage(
                AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                GrowthConstants.CHESTBURSTER_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "nether_adolescent_to_nether_drone",
            new GrowthStage(
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_DRONE.get(),
                GrowthConstants.ADOLESCENT_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "nether_drone_to_nether_warrior",
            new GrowthStage(
                AlienEntityTypes.NETHER_DRONE.get(),
                AlienEntityTypes.NETHER_WARRIOR.get(),
                GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "nether_warrior_to_nether_praetorian",
            new GrowthStage(
                AlienEntityTypes.NETHER_WARRIOR.get(),
                AlienEntityTypes.NETHER_PRAETORIAN.get(),
                GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS
            )
        );
        biConsumer.accept(
            "nether_praetorian_to_nether_queen",
            new GrowthStage(
                AlienEntityTypes.NETHER_PRAETORIAN.get(),
                AlienEntityTypes.NETHER_QUEEN.get(),
                GrowthConstants.PRAETORIAN_GROWTH_TIME_IN_TICKS
            )
        );
    }

    private static void providerRunnerNetherGrowthStages(BiConsumer<String, GrowthStage> biConsumer) {
        biConsumer.accept(
            "nether_adolescent_to_nether_runner",
            new GrowthStage(
                AVPEntityTypeTags.RUNNER_HOSTS,
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_RUNNER.get(),
                GrowthConstants.ADOLESCENT_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "nether_runner_to_nether_prowler",
            new GrowthStage(
                AlienEntityTypes.NETHER_RUNNER.get(),
                AlienEntityTypes.NETHER_PROWLER.get(),
                GrowthConstants.DRONE_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "nether_prowler_to_nether_crusher",
            new GrowthStage(
                AlienEntityTypes.NETHER_PROWLER.get(),
                AlienEntityTypes.NETHER_CRUSHER.get(),
                GrowthConstants.WARRIOR_GROWTH_TIME_IN_TICKS / 2
            )
        );
        biConsumer.accept(
            "nether_crusher_to_nether_queen",
            new GrowthStage(
                AlienEntityTypes.NETHER_CRUSHER.get(),
                AlienEntityTypes.NETHER_QUEEN.get(),
                GrowthConstants.PRAETORIAN_GROWTH_TIME_IN_TICKS / 2
            )
        );
    }
}
