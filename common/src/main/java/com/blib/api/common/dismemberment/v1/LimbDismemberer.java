package com.blib.api.common.dismemberment.v1;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.mod.common.registry.init.BLibEntityTypes;

/**
 * Server-side entry point for triggering dismemberment.
 * <p>
 * This is a stateless utility: it inspects the registry, mutates the entity's manager, and spawns a
 * {@link DismemberedLimbEntity}. Callers can pass an optional {@code limbConfigurer} to customize the spawned entity
 * (velocity, lifetime, side-effects) without subclassing.
 * <p>
 * Texture and model are no longer passed in: at spawn time the source entity's NBT is captured and shipped on the limb.
 * The client reconstructs a transient ghost copy and pulls texture/model directly from the source's renderer, so
 * variant-specific or runtime-state-dependent visuals carry over without any consumer-side mapping.
 */
public final class LimbDismemberer {

    private LimbDismemberer() {}

    /**
     * Detaches the limb with the given id from {@code entity} if it has not already been detached. Returns the spawned
     * entity (if any).
     */
    public static Optional<DismemberedLimbEntity> detach(LivingEntity entity, ResourceLocation limbId) {
        return detach(entity, limbId, null);
    }

    public static Optional<DismemberedLimbEntity> detach(
        LivingEntity entity,
        ResourceLocation limbId,
        @Nullable Consumer<DismemberedLimbEntity> limbConfigurer
    ) {
        if (entity.level().isClientSide) {
            return Optional.empty();
        }

        if (!(entity instanceof Dismemberable dismemberable)) {
            return Optional.empty();
        }

        var definition = LimbDefinitionRegistry.getDefinition(entity.getType(), limbId);

        if (definition == null) {
            return Optional.empty();
        }

        var manager = dismemberable.getDismembermentManager();

        if (!manager.markDetached(definition.id())) {
            return Optional.empty();
        }

        var spawned = Optional.ofNullable(spawnLimbEntity(entity, definition, limbConfigurer));
        applyFatalSideEffect(entity, definition);
        return spawned;
    }

    /**
     * Detaches the first limb of the given category from {@code entity} that has not already been detached. Returns the
     * spawned entity (if any).
     */
    public static Optional<DismemberedLimbEntity> detachFirstOfCategory(
        LivingEntity entity,
        LimbCategory category,
        @Nullable Consumer<DismemberedLimbEntity> limbConfigurer
    ) {
        if (entity.level().isClientSide) {
            return Optional.empty();
        }

        if (!(entity instanceof Dismemberable dismemberable)) {
            return Optional.empty();
        }

        var manager = dismemberable.getDismembermentManager();
        var candidates = LimbDefinitionRegistry.getDefinitionsByCategory(entity.getType(), category);

        for (var definition : candidates) {
            if (manager.isDetached(definition)) {
                continue;
            }

            if (!manager.markDetached(definition.id())) {
                continue;
            }

            var spawned = Optional.ofNullable(spawnLimbEntity(entity, definition, limbConfigurer));
            applyFatalSideEffect(entity, definition);
            return spawned;
        }

        return Optional.empty();
    }

    /**
     * If the limb's definition is marked {@code fatal}, kill the source entity. Done after the limb spawns so the
     * fragment is in the world before the body collapses, and skipped if the entity is already dead so we don't
     * double-kill on a hit that simultaneously triggers detach + death from another source (e.g. ravager's last claw).
     */
    private static void applyFatalSideEffect(LivingEntity entity, LimbDefinition definition) {
        if (definition.fatal() && entity.isAlive()) {
            entity.kill();
        }
    }

    public static List<LimbDefinition> getRemainingDefinitionsByCategory(LivingEntity entity, LimbCategory category) {
        if (!(entity instanceof Dismemberable dismemberable)) {
            return List.of();
        }

        var manager = dismemberable.getDismembermentManager();

        return LimbDefinitionRegistry.getDefinitionsByCategory(entity.getType(), category)
            .stream()
            .filter(definition -> !manager.isDetached(definition))
            .toList();
    }

    /**
     * Returns every limb registered for {@code entity}'s type that hasn't been detached yet — uncategorized. Used by UI
     * surfaces (engine workspace's right-click "Dismember…" submenu) that present individual limbs to the user
     * regardless of category. Safe to call from either side: the underlying {@link DismembermentManager} state is
     * NBT-synced.
     */
    public static List<LimbDefinition> getRemainingDefinitions(LivingEntity entity) {
        if (!(entity instanceof Dismemberable dismemberable)) {
            return List.of();
        }

        var manager = dismemberable.getDismembermentManager();

        return LimbDefinitionRegistry.getDefinitions(entity.getType())
            .stream()
            .filter(definition -> !manager.isDetached(definition))
            .toList();
    }

    private static @Nullable DismemberedLimbEntity spawnLimbEntity(
        LivingEntity entity,
        LimbDefinition definition,
        @Nullable Consumer<DismemberedLimbEntity> limbConfigurer
    ) {
        var level = entity.level();
        var limbType = BLibEntityTypes.DISMEMBERED_LIMB.get();
        var limb = limbType.create(level);

        if (limb == null) {
            return null;
        }

        var sourceNbt = new CompoundTag();
        entity.saveWithoutId(sourceNbt);

        limb.configure(entity.getType(), sourceNbt, definition.id(), 20 * 30);

        var spawnOffset = definition.spawnOffsetProvider().apply(entity);
        var spawnPos = entity.position().add(spawnOffset);
        limb.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, entity.getYRot(), 0.0F);
        limb.launch(randomLimbVelocity(entity));

        if (limbConfigurer != null) {
            limbConfigurer.accept(limb);
        }

        level.addFreshEntity(limb);
        return limb;
    }

    private static Vec3 randomLimbVelocity(LivingEntity entity) {
        var random = entity.getRandom();
        var horizontal = 0.15;
        return new Vec3(
            (random.nextDouble() - 0.5) * horizontal,
            0.25 + random.nextDouble() * 0.1,
            (random.nextDouble() - 0.5) * horizontal
        );
    }

}
