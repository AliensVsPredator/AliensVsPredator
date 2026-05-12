package com.blib.api.common.dismemberment.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Server-only registry mapping a {@link LimbDefinition#id()} to its spawn-position function, kept separate from JSON-
 * loaded definitions because {@link Function} can't be serialized. Authors who define limbs purely in JSON get the
 * {@link #DEFAULT_PROVIDER} (entity vertical center) unless they also register a custom function here.
 * <p>
 * Populated at mod init by built-in registrations and any downstream mod that wants a non-default spawn point. The
 * function executes server-side inside {@code LimbDismemberer.spawnLimbEntity} and the resulting position is sent to
 * the client via the spawned entity's position.
 */
public final class SpawnFunctionRegistry {

    /** Default spawn position when no custom function is registered: entity's vertical center. */
    public static final Function<LivingEntity, Vec3> DEFAULT_PROVIDER =
        entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0);

    private static final Map<ResourceLocation, Function<LivingEntity, Vec3>> PROVIDERS = new ConcurrentHashMap<>();

    private SpawnFunctionRegistry() {}

    public static void register(ResourceLocation limbId, Function<LivingEntity, Vec3> provider) {
        Objects.requireNonNull(limbId, "limbId");
        Objects.requireNonNull(provider, "provider");
        PROVIDERS.put(limbId, provider);
    }

    /** Returns the registered provider for {@code limbId}, falling back to {@link #DEFAULT_PROVIDER}. */
    public static Function<LivingEntity, Vec3> get(ResourceLocation limbId) {
        return PROVIDERS.getOrDefault(limbId, DEFAULT_PROVIDER);
    }
}
