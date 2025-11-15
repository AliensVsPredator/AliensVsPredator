package com.avp.fabric.data.infections;

import com.alien.common.model.lifecycle.infection.Infection;
import com.alien.common.registry.init.AlienEntityTypes;
import com.lib.common.data.EntityTypePredicate;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class InfectionSubProvider extends InfectionDataProvider {

    private static final int IMPREGNATION_DELAY_IN_TICKS = 3 * 20;

    private static final int DETACH_DELAY_IN_TICKS = (int) TimeUnit.MINUTES.toSeconds(1) * 20;

    private static final int GESTATION_TIME_IN_TICKS = (int) TimeUnit.MINUTES.toSeconds(5) * 20;

    public InfectionSubProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    protected void generate() {
        add(
            "facehugger_produces_chestburster",
            new Infection(
                AlienEntityTypes.FACEHUGGER.get(),
                AlienEntityTypes.CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "royal_facehugger_produces_royal_chestburster",
            new Infection(
                AlienEntityTypes.ROYAL_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "facehugger_produces_adolescent",
            new Infection(
                AlienEntityTypes.FACEHUGGER.get(),
                AlienEntityTypes.ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "royal_facehugger_produces_royal_adolescent",
            new Infection(
                AlienEntityTypes.ROYAL_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "facehugger_produces_predalien_chestburster",
            new Infection(
                AlienEntityTypes.FACEHUGGER.get(),
                AlienEntityTypes.PREDALIEN_CHESTBURSTER.get(),
                Optional.of(new EntityTypePredicate.Single(PredatorEntityTypes.YAUTJA.get())),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );

        add(
            "aberrant_facehugger_produces_aberrant_chestburster",
            new Infection(
                AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "royal_aberrant_facehugger_produces_royal_aberrant_chestburster",
            new Infection(
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "aberrant_facehugger_produces_aberrant_adolescent",
            new Infection(
                AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ABERRANT_ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "royal_aberrant_facehugger_produces_royal_aberrant_adolescent",
            new Infection(
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "aberrant_facehugger_produces_aberrant_predalien_chestburster",
            new Infection(
                AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ABERRANT_PREDALIEN_CHESTBURSTER.get(),
                Optional.of(new EntityTypePredicate.Single(PredatorEntityTypes.YAUTJA.get())),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );

        add(
            "nether_facehugger_produces_nether_chestburster",
            new Infection(
                AlienEntityTypes.NETHER_FACEHUGGER.get(),
                AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "royal_nether_facehugger_produces_royal_nether_chestburster",
            new Infection(
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
        add(
            "nether_facehugger_produces_nether_adolescent",
            new Infection(
                AlienEntityTypes.NETHER_FACEHUGGER.get(),
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "royal_nether_facehugger_produces_royal_nether_adolescent",
            new Infection(
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                Optional.of(new EntityTypePredicate.Tag(AVPEntityTypeTags.RUNNER_HOSTS)),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS / 2
            )
        );
        add(
            "nether_facehugger_produces_nether_predalien_chestburster",
            new Infection(
                AlienEntityTypes.NETHER_FACEHUGGER.get(),
                AlienEntityTypes.NETHER_PREDALIEN_CHESTBURSTER.get(),
                Optional.of(new EntityTypePredicate.Single(PredatorEntityTypes.YAUTJA.get())),
                IMPREGNATION_DELAY_IN_TICKS,
                DETACH_DELAY_IN_TICKS,
                GESTATION_TIME_IN_TICKS
            )
        );
    }
}
