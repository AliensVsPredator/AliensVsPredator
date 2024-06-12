package org.avp.common.registry.alien.infection;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.infection.AlienInfection;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterData;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterQueenData;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerData;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerRoyalData;
import org.avp.common.data.entity.living.alien.beluga_line.BelugabursterData;
import org.avp.common.data.entity.living.alien.beluga_line.OctohuggerData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconData;
import org.avp.common.data.entity.living.alien.deacon_line.TrilobiteData;
import org.avp.common.data.entity.living.alien.runner_line.ChestbursterRunnerData;
import org.avp.common.registry.holder.AVPHolder;

public class AVPAlienInfectionRegistry extends AVPDeferredRegistry<AlienInfection> {

    public static final AVPAlienInfectionRegistry INSTANCE = new AVPAlienInfectionRegistry();

    private static final Map<AlienGrowthLookupKey, BLHolder<AlienInfection>> ALIEN_INFECTION_LOOKUP_MAP = new HashMap<>();

    public static Optional<BLHolder<AlienInfection>> lookup(EntityType<?> host, EntityType<?> parasite) {
        return Optional.ofNullable(ALIEN_INFECTION_LOOKUP_MAP.get(new AlienGrowthLookupKey(host, parasite)));
    }

    public final BLHolder<AlienInfection> facehuggerProducesChestbursterRunner;

    public final BLHolder<AlienInfection> facehuggerProducesChestburster;

    public final BLHolder<AlienInfection> facehuggerRoyalProducesChestbursterQueen;

    public final BLHolder<AlienInfection> octohuggerProducesBelugaburster;

    public final BLHolder<AlienInfection> trilobiteProducesDeacon;

    private AVPAlienInfectionRegistry() {
        facehuggerProducesChestbursterRunner = createHolder(
            "facehugger_produces_chestburster_runner",
            () -> new AlienInfection(
                FacehuggerData.INSTANCE.getHolder().get(),
                Set.of(
                    EntityType.CAMEL,
                    EntityType.COW,
                    EntityType.DONKEY,
                    EntityType.GOAT,
                    EntityType.HOGLIN,
                    EntityType.HORSE,
                    EntityType.LLAMA,
                    EntityType.MOOSHROOM,
                    EntityType.MULE,
                    EntityType.PANDA,
                    EntityType.PIG,
                    EntityType.POLAR_BEAR,
                    EntityType.RAVAGER,
                    EntityType.SHEEP,
                    EntityType.SNIFFER,
                    EntityType.TRADER_LLAMA,
                    EntityType.WOLF
                ),
                ChestbursterRunnerData.INSTANCE.getHolder().get(),
                60,
                1200,
                4000
            )
        );

        facehuggerProducesChestburster = createHolder(
            "facehugger_produces_chestburster",
            () -> new AlienInfection(
                FacehuggerData.INSTANCE.getHolder().get(),
                null, // Chestburster will be default for any unknown host.
                ChestbursterData.INSTANCE.getHolder().get(),
                60,
                1200,
                6000
            )
        );

        facehuggerRoyalProducesChestbursterQueen = createHolder(
            "facehugger_royal_produces_chestburster_queen",
            () -> new AlienInfection(
                FacehuggerRoyalData.INSTANCE.getHolder().get(),
                null, // Chestburster will be default for any unknown host.
                ChestbursterQueenData.INSTANCE.getHolder().get(),
                60,
                1200,
                6000
            )
        );

        octohuggerProducesBelugaburster = createHolder(
            "octohugger_produces_belugaburster",
            () -> new AlienInfection(
                OctohuggerData.INSTANCE.getHolder().get(),
                null, // Belugaburster will be default for any unknown host.
                BelugabursterData.INSTANCE.getHolder().get(),
                60,
                1200,
                6000
            )
        );

        trilobiteProducesDeacon = createHolder(
            "trilobite_produces_deacon",
            () -> new AlienInfection(
                TrilobiteData.INSTANCE.getHolder().get(),
                null, // Deacon will be default for any unknown host.
                DeaconData.INSTANCE.getHolder().get(),
                60,
                1200,
                6000
            )
        );
    }

    @Override
    protected BLHolder<AlienInfection> createHolder(String registryName, Supplier<AlienInfection> supplier) {
        var holder = new AVPHolder<>(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    public void register() {
        getValues().forEach(alienInfectionHolder -> {
            var alienInfection = alienInfectionHolder.get();
            var hosts = alienInfection.hosts();

            if (hosts == null) {
                var lookupKey = new AlienGrowthLookupKey(null, alienInfection.parasite());
                ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, alienInfectionHolder);
            } else {
                hosts.forEach(host -> {
                    var lookupKey = new AlienGrowthLookupKey(host, alienInfection.parasite());
                    ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, alienInfectionHolder);
                });
            }
        });
    }
}
