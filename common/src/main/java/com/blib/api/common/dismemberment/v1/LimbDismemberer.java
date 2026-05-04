package com.blib.api.common.dismemberment.v1;

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
 */
public final class LimbDismemberer {

    private LimbDismemberer() {}

    /**
     * Detaches the limb with the given id from {@code entity} if it has not already been detached. Returns the spawned
     * entity (if any).
     */
    public static Optional<DismemberedLimbEntity> detach(
        LivingEntity entity,
        ResourceLocation limbId,
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
    ) {
        return detach(entity, limbId, modelLocation, textureLocation, null);
    }

    public static Optional<DismemberedLimbEntity> detach(
        LivingEntity entity,
        ResourceLocation limbId,
        ResourceLocation modelLocation,
        ResourceLocation textureLocation,
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

        return Optional.ofNullable(spawnLimbEntity(entity, definition, modelLocation, textureLocation, limbConfigurer));
    }

    /**
     * Detaches the first limb of the given category from {@code entity} that has not already been detached. Returns the
     * spawned entity (if any).
     */
    public static Optional<DismemberedLimbEntity> detachFirstOfCategory(
        LivingEntity entity,
        LimbCategory category,
        ResourceLocation modelLocation,
        ResourceLocation textureLocation,
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

            return Optional.ofNullable(spawnLimbEntity(entity, definition, modelLocation, textureLocation, limbConfigurer));
        }

        return Optional.empty();
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

    private static @Nullable DismemberedLimbEntity spawnLimbEntity(
        LivingEntity entity,
        LimbDefinition definition,
        ResourceLocation modelLocation,
        ResourceLocation textureLocation,
        @Nullable Consumer<DismemberedLimbEntity> limbConfigurer
    ) {
        var level = entity.level();
        var limbType = BLibEntityTypes.DISMEMBERED_LIMB.get();
        var limb = limbType.create(level);

        if (limb == null) {
            return null;
        }

        limb.configure(
            entity.getType(),
            modelLocation,
            textureLocation,
            definition.rootBoneName(),
            definition.renderOffset(),
            definition.renderRotation(),
            20 * 30
        );

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
