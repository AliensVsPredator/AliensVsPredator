package com.alien.common.registry.init;

import com.alien.common.gameplay.entity.living.alien.chestburster.Chestburster;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.model.lifecycle.infection.AlienInfection;
import com.bvanseg.just.functional.function.Lazy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.avp.service.Services;

public class AlienInfections {

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> FACEHUGGER_PRODUCES_CHESTBURSTER = register(
        () -> createFacehuggerInfection(AlienEntityTypes.FACEHUGGER.get(), AlienEntityTypes.CHESTBURSTER.get())
    );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ABERRANT_FACEHUGGER_PRODUCES_ABERRANT_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(AlienEntityTypes.ABERRANT_FACEHUGGER.get(), AlienEntityTypes.ABERRANT_CHESTBURSTER.get())
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> NETHER_FACEHUGGER_PRODUCES_NETHER_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(AlienEntityTypes.NETHER_FACEHUGGER.get(), AlienEntityTypes.NETHER_CHESTBURSTER.get())
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_FACEHUGGER_PRODUCES_ROYAL_CHESTBURSTER = register(
        () -> createFacehuggerInfection(AlienEntityTypes.ROYAL_FACEHUGGER.get(), AlienEntityTypes.ROYAL_CHESTBURSTER.get())
    );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_ABERRANT_FACEHUGGER_PRODUCES_ROYAL_ABERRANT_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get()
            )
        );

    public static final Supplier<AlienInfection<Facehugger, Chestburster>> ROYAL_NETHER_FACEHUGGER_PRODUCES_ROYAL_NETHER_CHESTBURSTER =
        register(
            () -> createFacehuggerInfection(
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get()
            )
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
        // Note the use of lazy here.
        return Services.REGISTRY.registerAlienInfection(Lazy.of(infectionSupplier));
    }

    public static void initialize() {}
}
