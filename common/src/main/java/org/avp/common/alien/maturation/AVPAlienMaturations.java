package org.avp.common.alien.maturation;

import net.minecraft.world.entity.EntityType;
import org.avp.api.Holder;
import org.avp.api.entity.HiveMember;
import org.avp.api.entity.RoyalJellyHolder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.maturation.AlienMaturation;
import org.avp.common.data.alien.maturation.AlienMaturationStep;
import org.avp.common.entity.data.BelugabursterData;
import org.avp.common.entity.data.BelugamorphData;
import org.avp.common.entity.data.BoilerData;
import org.avp.common.entity.data.ChestbursterData;
import org.avp.common.entity.data.ChestbursterDracoData;
import org.avp.common.entity.data.ChestbursterQueenData;
import org.avp.common.entity.data.ChestbursterRunnerData;
import org.avp.common.entity.data.CrusherData;
import org.avp.common.entity.data.DeaconAdultData;
import org.avp.common.entity.data.DeaconAdultEngineerData;
import org.avp.common.entity.data.DeaconData;
import org.avp.common.entity.data.DracomorphData;
import org.avp.common.entity.data.DroneData;
import org.avp.common.entity.data.DroneRunnerData;
import org.avp.common.entity.data.EngineerData;
import org.avp.common.entity.data.NauticomorphData;
import org.avp.common.entity.data.PraetorianData;
import org.avp.common.entity.data.QueenData;
import org.avp.common.entity.data.SpitterData;
import org.avp.common.entity.data.UltramorphData;
import org.avp.common.entity.data.WarriorData;
import org.avp.common.entity.data.WarriorRunnerData;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.server.HivemindManager;

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
                    BelugabursterData.INSTANCE.getHolder().get(),
                    BelugamorphData.INSTANCE.getHolder().get(),
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
                    ChestbursterData.INSTANCE.getHolder().get(),
                    BoilerData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        crusherMaturation = createHolder("chestburster_runner_to_crusher", () -> new AlienMaturation(
            null, // No hosts = this lifecycle will be the default for all runner chestbursters.
            List.of(
                new AlienMaturationStep(
                    ChestbursterRunnerData.INSTANCE.getHolder().get(),
                    DroneRunnerData.INSTANCE.getHolder().get(),
                    12_000
                ),
                new AlienMaturationStep(
                    DroneRunnerData.INSTANCE.getHolder().get(),
                    WarriorRunnerData.INSTANCE.getHolder().get(),
                    12_000
                ),
                new AlienMaturationStep(
                    WarriorRunnerData.INSTANCE.getHolder().get(),
                    CrusherData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        deaconAdultEngineerMaturation = createHolder("deacon_adult_to_deacon_adult_engineer", () -> new AlienMaturation(
            Set.of(
                EngineerData.INSTANCE.getHolder().get()
            ),
            List.of(
                new AlienMaturationStep(
                    DeaconAdultData.INSTANCE.getHolder().get(),
                    DeaconAdultEngineerData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        deaconAdultMaturation = createHolder("deacon_to_deacon_adult", () -> new AlienMaturation(
            null, // A deacon from any host will mature into an adult deacon.
            List.of(
                new AlienMaturationStep(
                    DeaconData.INSTANCE.getHolder().get(),
                    DeaconAdultData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        dracomorphMaturation = createHolder("chestburster_draco_to_dracomorph", () -> new AlienMaturation(
            null, // A chestburster_draco from any host will produce a dracomorph.
            List.of(
                new AlienMaturationStep(
                    ChestbursterDracoData.INSTANCE.getHolder().get(),
                    DracomorphData.INSTANCE.getHolder().get(),
                    24_000
                )
            )
        ));
        queenMaturation = createHolder("chestburster_to_queen", () -> new AlienMaturation(
            null, // No hosts = this lifecycle will be the default for all chestbursters.
            List.of(
                new AlienMaturationStep(
                    ChestbursterData.INSTANCE.getHolder().get(),
                    DroneData.INSTANCE.getHolder().get(),
                    12_000
                ),
                new AlienMaturationStep(
                    DroneData.INSTANCE.getHolder().get(),
                    WarriorData.INSTANCE.getHolder().get(),
                    12_000
                ),
                new AlienMaturationStep(
                    WarriorData.INSTANCE.getHolder().get(),
                    PraetorianData.INSTANCE.getHolder().get(),
                    12_000,
                    entity -> entity instanceof RoyalJellyHolder royalJellyHolder && royalJellyHolder.hasRoyalJelly()
                ),
                new AlienMaturationStep(
                    PraetorianData.INSTANCE.getHolder().get(),
                    QueenData.INSTANCE.getHolder().get(),
                    12_000,
                    entity -> {
                        if (entity instanceof HiveMember hiveMember && hiveMember.hasHivemind()) {
                            var hivemindOptional = HivemindManager.getByUUID(hiveMember.getHivemindSignature());

                            if (hivemindOptional.isEmpty()) {
                                return true;
                            }

                            var hivemind = hivemindOptional.get();
                            var leader = hivemind.getLeader();

                            return leader.isEmpty() || leader.get().is(entity);
                        }

                        return true;
                    }
                )
            )
        ));
        nauticomorphMaturation = createHolder("chestburster_to_nauticomorph", () -> new AlienMaturation(
            Set.of(
                EntityType.TURTLE
            ),
            List.of(
                new AlienMaturationStep(
                    ChestbursterData.INSTANCE.getHolder().get(),
                    NauticomorphData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        praetorianMaturation = createHolder("chestburster_queen_to_praetorian", () -> new AlienMaturation(
            null,
            List.of(
                new AlienMaturationStep(
                    ChestbursterQueenData.INSTANCE.getHolder().get(),
                    PraetorianData.INSTANCE.getHolder().get(),
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
                    ChestbursterRunnerData.INSTANCE.getHolder().get(),
                    SpitterData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
        ultramorphMaturation = createHolder("chestburster_to_ultramorph", () -> new AlienMaturation(
            Set.of(
                EngineerData.INSTANCE.getHolder().get()
            ),
            List.of(
                new AlienMaturationStep(
                    ChestbursterData.INSTANCE.getHolder().get(),
                    UltramorphData.INSTANCE.getHolder().get(),
                    12_000
                )
            )
        ));
    }

    @Override
    protected Holder<AlienMaturation> createHolder(String registryName, Supplier<AlienMaturation> supplier) {
        var holder = new Holder<>(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    public void register() {
        getValues().forEach(alienMaturationHolder -> {
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
