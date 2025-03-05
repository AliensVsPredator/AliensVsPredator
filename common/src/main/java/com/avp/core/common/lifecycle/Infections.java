package com.avp.core.common.lifecycle;

import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.lifecycle.infection.Infection;
import com.avp.core.common.lifecycle.registry.AlienInfectionRegistry;

public class Infections {

    public static final Infection FACEHUGGER_PRODUCES_CHESTBURSTER = register(
        new Infection(
            AVPEntityTypes.FACEHUGGER,
            null, // Chestburster will be default for any unknown host.
            AVPEntityTypes.CHESTBURSTER,
            60,
            1200,
            6000
        )
    );

    private static Infection register(Infection infection) {
        return AlienInfectionRegistry.register(infection);
    }

    public static void initialize() {}
}
