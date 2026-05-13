package com.blib.engine.domain.selection.picking;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * {@link Selectable} wrapping a {@link LivingEntity}. Held weakly so a despawn / chunk-unload doesn't pin the entity
 * through us; {@link #isValid} returns false once the underlying entity is gone or dead, and the
 * {@link SelectionManager} prunes stale entries automatically.
 */
@ApiStatus.Internal
public final class EntitySelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final WeakReference<LivingEntity> ref;

    public EntitySelectable(LivingEntity entity) {
        this.ref = new WeakReference<>(entity);
    }

    public @Nullable LivingEntity entity() {
        var entity = ref.get();
        return entity != null && entity.isAlive() ? entity : null;
    }

    @Override
    public SelectableType type() {
        return SelectableType.ENTITY;
    }

    @Override
    public Component displayName() {
        var entity = entity();
        return entity != null ? entity.getName() : Component.empty();
    }

    @Override
    public AABB worldBounds() {
        var entity = entity();
        return entity != null ? entity.getBoundingBox() : EMPTY_BOUNDS;
    }

    @Override
    public boolean isValid() {
        return entity() != null;
    }
}
