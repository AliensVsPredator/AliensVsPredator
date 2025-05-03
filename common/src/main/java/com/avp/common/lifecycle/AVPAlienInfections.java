package com.avp.common.lifecycle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.avp.common.entity.living.alien.chestburster.Chestburster;
import com.avp.common.entity.living.alien.parasite.facehugger.Facehugger;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.service.Services;

public class AVPAlienInfections {

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> FACEHUGGER_PRODUCES_CHESTBURSTER = register(
        () -> createFacehuggerInfection(AVPEntityTypes.FACEHUGGER.get(), AVPEntityTypes.CHESTBURSTER.get())
    );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ABERRANT_FACEHUGGER_PRODUCES_ABERRANT_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(AVPEntityTypes.ABERRANT_FACEHUGGER.get(), AVPEntityTypes.ABERRANT_CHESTBURSTER.get())
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> NETHER_FACEHUGGER_PRODUCES_NETHER_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(AVPEntityTypes.NETHER_FACEHUGGER.get(), AVPEntityTypes.NETHER_CHESTBURSTER.get())
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_FACEHUGGER_PRODUCES_ROYAL_CHESTBURSTER = register(
        () -> createFacehuggerInfection(AVPEntityTypes.ROYAL_FACEHUGGER.get(), AVPEntityTypes.ROYAL_CHESTBURSTER.get())
    );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_ABERRANT_FACEHUGGER_PRODUCES_ROYAL_ABERRANT_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get()
            )
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_NETHER_FACEHUGGER_PRODUCES_ROYAL_NETHER_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(AVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get(), AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get())
        );

    private static @NotNull AlienInfection<Facehugger, Chestburster> createFacehuggerInfection(
        EntityType<Facehugger> facehugger,
        EntityType<Chestburster> chestburster
    ) {
        return new AlienInfection<>(
            facehugger,
            null, // Chestburster will be default for any unknown host.
            chestburster,
            60,
            (int) TimeUnit.MINUTES.toSeconds(1) * 20,
            (int) TimeUnit.MINUTES.toSeconds(5) * 20
        );
    }

    private static <S extends LivingEntity, P extends LivingEntity> Supplier<AlienInfection<S, P>> register(
        Supplier<AlienInfection<S, P>> infectionSupplier
    ) {
        return Services.REGISTRY.registerAlienInfection(infectionSupplier);
    }

    public static void initialize() {}
}
