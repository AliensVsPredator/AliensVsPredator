package com.avp.fabric.common.lifecycle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import com.avp.fabric.common.entity.living.alien.chestburster.Chestburster;
import com.avp.fabric.common.entity.living.alien.parasite.facehugger.Facehugger;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.lifecycle.infection.Infection;
import com.avp.fabric.common.lifecycle.registry.AlienInfectionRegistry;

public class Infections {

    public static final Infection<Facehugger, Chestburster> FACEHUGGER_PRODUCES_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.FACEHUGGER, AVPEntityTypes.CHESTBURSTER)
    );

    public static final Infection<Facehugger, Chestburster> ABERRANT_FACEHUGGER_PRODUCES_ABERRANT_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.ABERRANT_FACEHUGGER, AVPEntityTypes.ABERRANT_CHESTBURSTER)
    );

    public static final Infection<Facehugger, Chestburster> NETHER_FACEHUGGER_PRODUCES_NETHER_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.NETHER_FACEHUGGER, AVPEntityTypes.NETHER_CHESTBURSTER)
    );

    public static final Infection<Facehugger, Chestburster> ROYAL_FACEHUGGER_PRODUCES_ROYAL_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.ROYAL_FACEHUGGER, AVPEntityTypes.ROYAL_CHESTBURSTER)
    );

    public static final Infection<Facehugger, Chestburster> ROYAL_ABERRANT_FACEHUGGER_PRODUCES_ROYAL_ABERRANT_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER, AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER)
    );

    public static final Infection<Facehugger, Chestburster> ROYAL_NETHER_FACEHUGGER_PRODUCES_ROYAL_NETHER_CHESTBURSTER = register(
        createFacehuggerInfection(AVPEntityTypes.ROYAL_NETHER_FACEHUGGER, AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER)
    );

    private static @NotNull Infection<Facehugger, Chestburster> createFacehuggerInfection(
        EntityType<Facehugger> facehugger,
        EntityType<Chestburster> chestburster
    ) {
        return new Infection<>(
            facehugger,
            null, // Chestburster will be default for any unknown host.
            chestburster,
            60,
            (int) TimeUnit.MINUTES.toSeconds(1) * 20,
            (int) TimeUnit.MINUTES.toSeconds(5) * 20
        );
    }

    private static <S extends LivingEntity, P extends LivingEntity> Infection<S, P> register(Infection<S, P> infection) {
        return AlienInfectionRegistry.register(infection);
    }

    public static void initialize() {}
}
