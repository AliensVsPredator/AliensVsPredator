package com.avp.common.lifecycle.registry;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.lifecycle.infection.AlienInfectionKey;

public class AlienInfectionRegistry {

    private static final Map<AlienInfectionKey, AlienInfection<?, ?>> ALIEN_INFECTION_LOOKUP_MAP = new HashMap<>();

    public static @Nullable AlienInfection<?, ?> getOrNull(EntityType<?> host, EntityType<?> parasite) {
        var maybeInfection = ALIEN_INFECTION_LOOKUP_MAP.get(new AlienInfectionKey(host, parasite));

        if (maybeInfection == null) {
            maybeInfection = ALIEN_INFECTION_LOOKUP_MAP.get(new AlienInfectionKey(null, parasite));
        }

        return maybeInfection;
    }

    public static Option<AlienInfection<?, ?>> get(EntityType<?> host, EntityType<?> parasite) {
        return Option.ofNullable(getOrNull(host, parasite));
    }

    public static <S extends LivingEntity, P extends LivingEntity> AlienInfection<S, P> register(AlienInfection<S, P> alienInfection) {
        var hosts = alienInfection.hosts();

        if (hosts == null) {
            var lookupKey = new AlienInfectionKey(null, alienInfection.parasiteType());
            ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, alienInfection);
        } else {
            hosts.forEach(host -> {
                var lookupKey = new AlienInfectionKey(host, alienInfection.parasiteType());
                ALIEN_INFECTION_LOOKUP_MAP.put(lookupKey, alienInfection);
            });
        }

        return alienInfection;
    }
}
