package org.avp.common.registry.alien.maturation;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.alien.AlienGrowthLookupKey;
import org.avp.common.data.alien.maturation.AlienMaturation;
import org.avp.common.data.alien.maturation.AlienMaturationStep;
import org.avp.common.data.entity.living.alien.base_line.BoilerData;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterData;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterQueenData;
import org.avp.common.data.entity.living.alien.base_line.DroneData;
import org.avp.common.data.entity.living.alien.base_line.NauticomorphData;
import org.avp.common.data.entity.living.alien.base_line.PraetorianData;
import org.avp.common.data.entity.living.alien.base_line.QueenData;
import org.avp.common.data.entity.living.alien.base_line.SpitterData;
import org.avp.common.data.entity.living.alien.base_line.UltramorphData;
import org.avp.common.data.entity.living.alien.base_line.WarriorData;
import org.avp.common.data.entity.living.alien.beluga_line.BelugabursterData;
import org.avp.common.data.entity.living.alien.beluga_line.BelugamorphData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconAdultData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconAdultEngineerData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconData;
import org.avp.common.data.entity.living.alien.draco_line.ChestbursterDracoData;
import org.avp.common.data.entity.living.alien.draco_line.DracomorphData;
import org.avp.common.data.entity.living.alien.runner_line.ChestbursterRunnerData;
import org.avp.common.data.entity.living.alien.runner_line.CrusherData;
import org.avp.common.data.entity.living.alien.runner_line.DroneRunnerData;
import org.avp.common.data.entity.living.alien.runner_line.WarriorRunnerData;
import org.avp.common.data.entity.living.engineer.EngineerData;
import org.avp.common.game.entity.type.HiveMember;
import org.avp.common.game.entity.type.RoyalJellyHolder;
import org.avp.common.registry.holder.AVPHolder;
import org.avp.server.HivemindManager;

public class AVPAlienMaturationRegistry extends AVPDeferredRegistry<AlienMaturation> {

    public static final AVPAlienMaturationRegistry INSTANCE = new AVPAlienMaturationRegistry();

    private static final Map<AlienGrowthLookupKey, AlienMaturationStep> ALIEN_MATURATION_LOOKUP_MAP = new HashMap<>();

    public static Optional<AlienMaturationStep> lookup(EntityType<?> host, EntityType<?> currentForm) {
        return Optional.ofNullable(
            // Try and get a direct mapping, first.
            ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthLookupKey(host, currentForm))
        ).or(() ->
        // If a direct mapping could not be found, then try and look up a default mapping for an unknown host.
        Optional.ofNullable(ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthLookupKey(null, currentForm)))
        );
    }

    public final BLHolder<AlienMaturation> belugamorphMaturation;

    public final BLHolder<AlienMaturation> boilerMaturation;

    public final BLHolder<AlienMaturation> crusherMaturation;

    public final BLHolder<AlienMaturation> deaconAdultEngineerMaturation;

    public final BLHolder<AlienMaturation> deaconAdultMaturation;

    public final BLHolder<AlienMaturation> dracomorphMaturation;

    public final BLHolder<AlienMaturation> nauticomorphMaturation;

    public final BLHolder<AlienMaturation> praetorianMaturation;

    public final BLHolder<AlienMaturation> queenMaturation;

    public final BLHolder<AlienMaturation> spitterMaturation;

    public final BLHolder<AlienMaturation> ultramorphMaturation;

    private AVPAlienMaturationRegistry() {
        belugamorphMaturation = createHolder(
            "belugaburster_to_belugamorph",
            () -> new AlienMaturation(
                null,
                List.of(
                    new AlienMaturationStep(
                        BelugabursterData.INSTANCE.getHolder().get(),
                        BelugamorphData.INSTANCE.getHolder().get(),
                        12_000
                    )
                )
            )
        );
        boilerMaturation = createHolder(
            "chestburster_to_boiler",
            () -> new AlienMaturation(
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
            )
        );
        crusherMaturation = createHolder(
            "chestburster_runner_to_crusher",
            () -> new AlienMaturation(
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
            )
        );
        deaconAdultEngineerMaturation = createHolder(
            "deacon_adult_to_deacon_adult_engineer",
            () -> new AlienMaturation(
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
            )
        );
        deaconAdultMaturation = createHolder(
            "deacon_to_deacon_adult",
            () -> new AlienMaturation(
                null, // A deacon from any host will mature into an adult deacon.
                List.of(
                    new AlienMaturationStep(
                        DeaconData.INSTANCE.getHolder().get(),
                        DeaconAdultData.INSTANCE.getHolder().get(),
                        12_000
                    )
                )
            )
        );
        dracomorphMaturation = createHolder(
            "chestburster_draco_to_dracomorph",
            () -> new AlienMaturation(
                null, // A chestburster_draco from any host will produce a dracomorph.
                List.of(
                    new AlienMaturationStep(
                        ChestbursterDracoData.INSTANCE.getHolder().get(),
                        DracomorphData.INSTANCE.getHolder().get(),
                        24_000
                    )
                )
            )
        );
        queenMaturation = createHolder(
            "chestburster_to_queen",
            () -> new AlienMaturation(
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
            )
        );
        nauticomorphMaturation = createHolder(
            "chestburster_to_nauticomorph",
            () -> new AlienMaturation(
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
            )
        );
        praetorianMaturation = createHolder(
            "chestburster_queen_to_praetorian",
            () -> new AlienMaturation(
                null,
                List.of(
                    new AlienMaturationStep(
                        ChestbursterQueenData.INSTANCE.getHolder().get(),
                        PraetorianData.INSTANCE.getHolder().get(),
                        12_000
                    )
                )
            )
        );
        spitterMaturation = createHolder(
            "chestburster_runner_to_spitter",
            () -> new AlienMaturation(
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
            )
        );
        ultramorphMaturation = createHolder(
            "chestburster_to_ultramorph",
            () -> new AlienMaturation(
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
            )
        );
    }

    @Override
    protected BLHolder<AlienMaturation> createHolder(String registryName, Supplier<AlienMaturation> supplier) {
        var holder = new AVPHolder<>(registryName, supplier);
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
