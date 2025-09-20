package com.alien.common.registry;

import com.alien.common.model.lifecycle.infection.Infection;
import com.alien.common.model.lifecycle.infection.InfectionKey;
import com.just.core.functional.option.Option;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfectionRegistry {

    private static final List<Infection> INFECTIONS = new ArrayList<>();

    private static final Map<InfectionKey, Infection> INFECTION_KEY_TO_INFECTION = new HashMap<>();

    public static @Nullable Infection getOrNull(EntityType<?> host, EntityType<?> parasite) {
        var maybeInfection = INFECTION_KEY_TO_INFECTION.get(new InfectionKey(host, parasite));

        if (maybeInfection == null) {
            maybeInfection = INFECTION_KEY_TO_INFECTION.get(new InfectionKey(null, parasite));
        }

        return maybeInfection;
    }

    public static void clear() {
        INFECTIONS.clear();
    }

    public static void register(Infection infection) {
        INFECTIONS.add(infection);
    }

    public static void rebuildLookupMappings() {
        INFECTION_KEY_TO_INFECTION.clear();
        INFECTIONS.forEach(InfectionRegistry::compute);
    }

    public static Option<Infection> get(EntityType<?> host, EntityType<?> parasite) {
        return Option.ofNullable(getOrNull(host, parasite));
    }

    public static void compute(Infection infection) {
        var hostTypePredicate = infection.hostTypePredicate().orElse(null);

        if (hostTypePredicate == null) {
            var lookupKey = new InfectionKey(null, infection.parasiteType());
            INFECTION_KEY_TO_INFECTION.put(lookupKey, infection);
        } else {
            BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(hostTypePredicate::test)
                .forEach(entityType -> {
                    // map the lookup key to the infection.
                    var lookupKey = new InfectionKey(entityType, infection.parasiteType());
                    INFECTION_KEY_TO_INFECTION.put(lookupKey, infection);
                });
        }
    }
}
