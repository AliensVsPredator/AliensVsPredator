package org.avp.common.alien.infection;

import net.minecraft.world.entity.EntityType;
import org.avp.api.Holder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.infection.AlienInfection;
import org.avp.common.entity.data.BelugabursterData;
import org.avp.common.entity.data.ChestbursterData;
import org.avp.common.entity.data.ChestbursterQueenData;
import org.avp.common.entity.data.ChestbursterRunnerData;
import org.avp.common.entity.data.DeaconData;
import org.avp.common.entity.data.FacehuggerData;
import org.avp.common.entity.data.FacehuggerRoyalData;
import org.avp.common.entity.data.OctohuggerData;
import org.avp.common.entity.data.TrilobiteData;
import org.avp.common.registry.AVPDeferredRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class AVPAlienInfections extends AVPDeferredRegistry<AlienInfection> {

    public static final AVPAlienInfections INSTANCE = new AVPAlienInfections();

    private static final Map<AlienGrowthLookupKey, Holder<AlienInfection>> ALIEN_INFECTION_LOOKUP_MAP = new HashMap<>();

    public static Optional<Holder<AlienInfection>> lookup(EntityType<?> host, EntityType<?> parasite) {
        return Optional.ofNullable(ALIEN_INFECTION_LOOKUP_MAP.get(new AlienGrowthLookupKey(host, parasite)));
    }

    public final Holder<AlienInfection> facehuggerProducesChestbursterRunner;

    public final Holder<AlienInfection> facehuggerProducesChestburster;

    public final Holder<AlienInfection> facehuggerRoyalProducesChestbursterQueen;

    public final Holder<AlienInfection> octohuggerProducesBelugaburster;

    public final Holder<AlienInfection> trilobiteProducesDeacon;

    private AVPAlienInfections() {
        facehuggerProducesChestbursterRunner = createHolder("facehugger_produces_chestburster_runner", () -> new AlienInfection(
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
        ));

        facehuggerProducesChestburster = createHolder("facehugger_produces_chestburster", () -> new AlienInfection(
            FacehuggerData.INSTANCE.getHolder().get(),
            null, // Chestburster will be default for any unknown host.
            ChestbursterData.INSTANCE.getHolder().get(),
            60,
            1200,
            6000
        ));

        facehuggerRoyalProducesChestbursterQueen = createHolder("facehugger_royal_produces_chestburster_queen", () -> new AlienInfection(
            FacehuggerRoyalData.INSTANCE.getHolder().get(),
            null, // Chestburster will be default for any unknown host.
            ChestbursterQueenData.INSTANCE.getHolder().get(),
            60,
            1200,
            6000
        ));

        octohuggerProducesBelugaburster = createHolder("octohugger_produces_belugaburster", () -> new AlienInfection(
            OctohuggerData.INSTANCE.getHolder().get(),
            null, // Belugaburster will be default for any unknown host.
            BelugabursterData.INSTANCE.getHolder().get(),
            60,
            1200,
            6000
        ));

        trilobiteProducesDeacon = createHolder("trilobite_produces_deacon", () -> new AlienInfection(
            TrilobiteData.INSTANCE.getHolder().get(),
            null, // Deacon will be default for any unknown host.
            DeaconData.INSTANCE.getHolder().get(),
            60,
            1200,
            6000
        ));
    }

    @Override
    protected Holder<AlienInfection> createHolder(String registryName, Supplier<AlienInfection> supplier) {
        var holder = new Holder<>(registryName, supplier);
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
