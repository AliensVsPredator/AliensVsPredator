package com.blib.api.common.entity.v1;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.Set;

public class EntityTransitionUtil {

    public static final Set<String> DEFAULT_NBT_KEY_BLACKLIST = Set.of("id", "UUID", "Pos", "Motion", "Rotation", "Health", "attributes");

    public static <T extends Entity> EntityTransitionResult transitionInto(Entity oldEntity, EntityType<T> newEntityType) {
        return transitionInto(oldEntity, newEntityType, DEFAULT_NBT_KEY_BLACKLIST);
    }

    public static <T extends Entity> EntityTransitionResult transitionInto(
        Entity oldEntity,
        EntityType<T> newEntityType,
        Set<String> blacklistedKeys
    ) {
        var level = oldEntity.level();

        if (level.isClientSide) {
            return EntityTransitionResult.ClientSide.INSTANCE;
        }

        var newEntity = newEntityType.create(level);

        if (newEntity == null) {
            // The newly created entity is null, nothing we can do here, so return.
            return EntityTransitionResult.EntityCreation.INSTANCE;
        }

        // It's important that we set the new entity's position here BEFORE checking obstructions and collisions.
        newEntity.setPos(oldEntity.position());

        // Transitioned entity must not be colliding with blocks.
        var canFitAtPosition = level.noBlockCollision(newEntity, newEntity.getBoundingBox())
            // Transitioned entity cannot spawn in water.
            && !level.containsAnyLiquid(newEntity.getBoundingBox());

        if (!canFitAtPosition) {
            // The new entity exists in a place where it can't be transitioned to.
            return EntityTransitionResult.Obstructed.INSTANCE;
        }

        // Migrate entity data from the old entity to the new entity.
        migrateEntityData(oldEntity, newEntity, blacklistedKeys);
        // Add the new entity to the level.
        level.addFreshEntity(newEntity);
        // Remove the old entity from the level.
        oldEntity.discard();

        return new EntityTransitionResult.Success<>(newEntity);
    }

    public static void migrateEntityData(Entity oldEntity, Entity newEntity) {
        migrateEntityData(oldEntity, newEntity, DEFAULT_NBT_KEY_BLACKLIST);
    }

    public static void migrateEntityData(Entity oldEntity, Entity newEntity, Set<String> blacklistedKeys) {
        // Step 1: Save old entity's data to a CompoundTag.
        var oldEntityData = new CompoundTag();
        oldEntity.save(oldEntityData);

        // Step 2: Removes specific fields that shouldn't be copied.
        blacklistedKeys.forEach(oldEntityData::remove);

        // Step 3: Load the data into the new entity.
        newEntity.load(oldEntityData);

        // Step 4: Migrate other stuff over

        // Move the next form to the entity's current position. Set rotation angles as well.
        newEntity.moveTo(oldEntity.position(), oldEntity.getYRot(), oldEntity.getXRot());

        // Explicitly set the yaw and pitch to ensure accurate orientation.
        newEntity.setYRot(oldEntity.getYRot());
        newEntity.setXRot(oldEntity.getXRot());

        // Synchronize movement manually.
        newEntity.setDeltaMovement(oldEntity.getDeltaMovement());

        // Synchronize the visual body rotation (if applicable for living entities).
        if (
            oldEntity instanceof LivingEntity oldLivingEntity
                && newEntity instanceof LivingEntity newLivingEntity
        ) {
            // Body rotation.
            newLivingEntity.yBodyRot = oldLivingEntity.yBodyRot;
            // Head rotation.
            newLivingEntity.yHeadRot = oldLivingEntity.yHeadRot;

            // Copies effects from the previous entity to the next.
            for (var effect : oldLivingEntity.getActiveEffects()) {
                newLivingEntity.addEffect(new MobEffectInstance(effect));
            }
        }

        // Synchronize persistence (if applicable for mobs).
        if (
            oldEntity instanceof Mob oldMobEntity
                && newEntity instanceof Mob newMobEntity
                && oldMobEntity.isPersistenceRequired()
        ) {
            newMobEntity.setPersistenceRequired();
        }
    }

    public sealed interface EntityTransitionResult {

        enum ClientSide implements EntityTransitionResult {
            INSTANCE
        }

        enum EntityCreation implements EntityTransitionResult {
            INSTANCE
        }

        enum Obstructed implements EntityTransitionResult {
            INSTANCE
        }

        record Success<T extends Entity>(T newEntity) implements EntityTransitionResult {}
    }
}
