package com.avp.fabric.data.infections;

import com.alien.common.model.lifecycle.infection.Infection;
import com.alien.common.registry.init.AlienEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.concurrent.TimeUnit;

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
    }
}
