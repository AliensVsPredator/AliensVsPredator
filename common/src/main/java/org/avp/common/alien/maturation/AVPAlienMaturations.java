package org.avp.common.alien.maturation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;
import org.avp.api.Holder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.maturation.AlienMaturation;
import org.avp.common.data.alien.maturation.AlienMaturationStep;
import org.avp.common.entity.living.Queen;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPEngineerEntityTypes;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.registry.AVPDeferredRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class AVPAlienMaturations extends AVPDeferredRegistry<AlienMaturation> {

    public static final AVPAlienMaturations INSTANCE = new AVPAlienMaturations();

    private static final Map<AlienGrowthLookupKey, AlienMaturationStep> ALIEN_MATURATION_LOOKUP_MAP = new HashMap<>();

    public static Optional<AlienMaturationStep> lookup(EntityType<?> host, EntityType<?> currentForm) {
        return
            Optional.ofNullable(
                // Try and get a direct mapping, first.
                ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthLookupKey(host, currentForm))
            ).or(() ->
                // If a direct mapping could not be found, then try and look up a default mapping for an unknown host.
                Optional.ofNullable(ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthLookupKey(null, currentForm)))
            );
    }

    public final Holder<AlienMaturation> belugamorphMaturation;

    public final Holder<AlienMaturation> boilerMaturation;

    public final Holder<AlienMaturation> crusherMaturation;

    public final Holder<AlienMaturation> deaconAdultEngineerMaturation;

    public final Holder<AlienMaturation> deaconAdultMaturation;

    public final Holder<AlienMaturation> dracomorphMaturation;

    public final Holder<AlienMaturation> nauticomorphMaturation;

    public final Holder<AlienMaturation> praetorianMaturation;

    public final Holder<AlienMaturation> queenMaturation;

    public final Holder<AlienMaturation> spitterMaturation;

    public final Holder<AlienMaturation> ultramorphMaturation;

    private AVPAlienMaturations() {
        belugamorphMaturation = createHolder("belugaburster_to_belugamorph", () -> new AlienMaturation(
            null,
            List.of(
                new AlienMaturationStep(
                    AVPEntityTypes.INSTANCE.belugaburster.get(),
                    AVPEntityTypes.INSTANCE.belugamorph.get(),
                    12_000
                )
            )
        ));
        boilerMaturation = createHolder("chestburster_to_boiler", () -> new AlienMaturation(
            Set.of(
                EntityType.CREEPER
            ),
            List.of(
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.boiler.get(),
                    12_000
                )
            )
        ));
        crusherMaturation = createHolder("chestburster_runner_to_crusher", () -> new AlienMaturation(
            null, // No hosts = this lifecycle will be the default for all runner chestbursters.
            List.of(
                new AlienMaturationStep(
                    AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner.get(),
                    AVPRunnerAlienEntityTypes.INSTANCE.droneRunner.get(),
                    12_000
                ),
                new AlienMaturationStep(
                    AVPRunnerAlienEntityTypes.INSTANCE.droneRunner.get(),
                    AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner.get(),
                    12_000
                ),
                new AlienMaturationStep(
                    AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner.get(),
                    AVPRunnerAlienEntityTypes.INSTANCE.crusher.get(),
                    12_000
                )
            )
        ));
        deaconAdultEngineerMaturation = createHolder("deacon_adult_to_deacon_adult_engineer", () -> new AlienMaturation(
            Set.of(
                AVPEngineerEntityTypes.INSTANCE.engineer.get()
            ),
            List.of(
                new AlienMaturationStep(
                    AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult.get(),
                    AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer.get(),
                    12_000
                )
            )
        ));
        deaconAdultMaturation = createHolder("deacon_to_deacon_adult", () -> new AlienMaturation(
            null, // A deacon from any host will mature into an adult deacon.
            List.of(
                new AlienMaturationStep(
                    AVPPrometheusAlienEntityTypes.INSTANCE.deacon.get(),
                    AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult.get(),
                    12_000
                )
            )
        ));
        dracomorphMaturation = createHolder("chestburster_draco_to_dracomorph", () -> new AlienMaturation(
            null, // A chestburster_draco from any host will produce a dracomorph.
            List.of(
                new AlienMaturationStep(
                    AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco.get(),
                    AVPExoticAlienEntityTypes.INSTANCE.dracomorph.get(),
                    24_000
                )
            )
        ));
        queenMaturation = createHolder("chestburster_to_queen", () -> new AlienMaturation(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.drone.get(),
                    12_000
                ),
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.drone.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.warrior.get(),
                    12_000
                ),
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.warrior.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.praetorian.get(),
                    12_000,
                    entity -> true // TODO: Test for royal jelly presence here.
                ),
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.praetorian.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.queen.get(),
                    12_000,
                    // TODO: It would be cheaper to check if this entity is part of a hive with an existing queen.
                    entity -> !Queen.anyNearbyQueens((ServerLevelAccessor) entity.level(), entity.blockPosition())
                )
            )
        ));
        nauticomorphMaturation = createHolder("chestburster_to_nauticomorph", () -> new AlienMaturation(
            Set.of(
                EntityType.TURTLE
            ),
            List.of(
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.nauticomorph.get(),
                    12_000
                )
            )
        ));
        praetorianMaturation = createHolder("chestburster_queen_to_praetorian", () -> new AlienMaturation(
            null,
            List.of(
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.chestbursterQueen.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.praetorian.get(),
                    12_000
                )
            )
        ));
        spitterMaturation = createHolder("chestburster_runner_to_spitter", () -> new AlienMaturation(
            Set.of(
                EntityType.LLAMA,
                EntityType.TRADER_LLAMA
            ),
            List.of(
                new AlienMaturationStep(
                    AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner.get(),
                    AVPBaseAlienEntityTypes.INSTANCE.spitter.get(),
                    12_000
                )
            )
        ));
        ultramorphMaturation = createHolder("chestburster_to_ultramorph", () -> new AlienMaturation(
            Set.of(
                AVPEngineerEntityTypes.INSTANCE.engineer.get()
            ),
            List.of(
                new AlienMaturationStep(
                    AVPBaseAlienEntityTypes.INSTANCE.chestburster.get(),
                    AVPExoticAlienEntityTypes.INSTANCE.ultramorph.get(),
                    12_000
                )
            )
        ));
    }

    @Override
    protected Holder<AlienMaturation> createHolder(String registryName, Supplier<AlienMaturation> supplier) {
        var holder = new Holder<>(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    public void register() {
        entries.forEach(alienMaturationHolder -> {
            var alienMaturation = alienMaturationHolder.get();
            var hosts = alienMaturation.hosts();
            var steps = alienMaturation.steps();

            if (hosts == null) {
                steps.forEach(step -> {
                    var lookupKey = new AlienGrowthLookupKey(null, step.from());
                    ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, step);
                });
            } else {
                // For every host, for every step, map the lookup key to the step.
                // Aliens will use their host + self type combination to look up what step they are currently on.
                hosts.forEach(host -> steps.forEach(step -> {
                    var lookupKey = new AlienGrowthLookupKey(host, step.from());
                    ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, step);
                }));
            }
        });
    }
}
