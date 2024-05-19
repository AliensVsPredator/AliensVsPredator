package org.avp.common.alien.infection;

import net.minecraft.world.entity.EntityType;
import org.avp.api.Holder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.infection.AlienInfection;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
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

    public final Holder<AlienInfection> octohuggerProducesBelugaburster;

    public final Holder<AlienInfection> trilobiteProducesDeacon;

    private AVPAlienInfections() {
        facehuggerProducesChestbursterRunner = createHolder("facehugger_produces_chestburster_runner", () -> new AlienInfection(
            AVPBaseAlienEntityTypes.INSTANCE.facehugger.get(),
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
            AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner.get(),
            60,
            1200,
            4000
        ));

        facehuggerProducesChestburster = createHolder("facehugger_produces_chestburster", () -> new AlienInfection(
            AVPBaseAlienEntityTypes.INSTANCE.facehugger.get(),
            null, // Chestburster will be default for any unknown host.
            AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
            60,
            1200,
            6000
        ));

        octohuggerProducesBelugaburster = createHolder("octohugger_produces_belugaburster", () -> new AlienInfection(
            AVPExoticAlienEntityTypes.INSTANCE.octohugger.get(),
            null, // Belugaburster will be default for any unknown host.
            AVPEntityTypes.INSTANCE.belugaburster.get(),
            60,
            1200,
            6000
        ));

        trilobiteProducesDeacon = createHolder("trilobite_produces_deacon", () -> new AlienInfection(
            AVPPrometheusAlienEntityTypes.INSTANCE.trilobite.get(),
            null, // Deacon will be default for any unknown host.
            AVPPrometheusAlienEntityTypes.INSTANCE.deacon.get(),
            60,
            1200,
            6000
        ));
    }

    @Override
    protected Holder<AlienInfection> createHolder(String registryName, Supplier<AlienInfection> supplier) {
        var holder = new Holder<>(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    public void register() {
        entries.forEach(alienInfectionHolder -> {
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
