package com.avp.core.common.lifecycle.registry;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.avp.core.common.lifecycle.infection.AlienInfectionKey;
import com.avp.core.common.lifecycle.infection.Infection;

public class AlienInfectionRegistry {

    private static final Map<AlienInfectionKey, Infection> ALIEN_INFECTION_LOOKUP_MAP = new HashMap<>();

    // TODO: Reduce garbage created in this function.
    public static @Nullable Infection getOrNull(EntityType<?> host, EntityType<?> parasite) {
        var maybeInfection = ALIEN_INFECTION_LOOKUP_MAP.get(new AlienInfectionKey(host, parasite));

        if (maybeInfection == null) {
            maybeInfection = ALIEN_INFECTION_LOOKUP_MAP.get(new AlienInfectionKey(null, parasite));
        }

        return maybeInfection;
    }

    public static Optional<Infection> get(EntityType<?> host, EntityType<?> parasite) {
        return Optional.ofNullable(getOrNull(host, parasite));
    }

    public static Infection register(Infection infection) {
        var hosts = infection.hosts();

        if (hosts == null) {
            // FIXME: .get()
            var lookupKey = new AlienInfectionKey(null, infection.parasiteSourceType().get());
            ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, infection);
        } else {
            hosts.forEach(host -> {
                // FIXME: .get()
                var lookupKey = new AlienInfectionKey(host, infection.parasiteSourceType().get());
                ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, infection);
            });
        }

        return infection;
    }
}
