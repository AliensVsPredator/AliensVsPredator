package com.blib.api.common.dismemberment.v1;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.api.common.data_sync.v1.DataAccessor;
import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.nbt.v1.model.NBTSerializable;
import com.blib.mod.common.registry.init.BLibDataSyncKeys;

/**
 * Per-entity component tracking which limbs have been detached.
 * <p>
 * The authoritative state lives on the server. The same set is mirrored to watching clients through
 * {@code BLibDataSyncKeys.ENTITY_DETACHED_LIMBS} so the renderer's bone visibility filter can hide the corresponding
 * bones.
 * <p>
 * NBT persistence is handled directly here (rather than through {@code DataContainer}) because the underlying container
 * only persists primitive values today.
 */
public final class DismembermentManager implements NBTSerializable {

    private static final String NBT_KEY = "DetachedLimbs";

    private final LivingEntity entity;

    private final DataAccessor<List<ResourceLocation>> detachedLimbsAccessor;

    private Set<ResourceLocation> detachedLimbs;

    public DismembermentManager(LivingEntity entity) {
        if (!(entity instanceof DataUser)) {
            throw new IllegalArgumentException(
                "Entity " + entity.getType() + " must implement DataUser to use a DismembermentManager"
            );
        }

        this.entity = entity;
        this.detachedLimbsAccessor = new DataAccessor<>((DataUser) entity, BLibDataSyncKeys.ENTITY_DETACHED_LIMBS.get());
        this.detachedLimbs = new HashSet<>();
        this.detachedLimbsAccessor.onChange(this::onDetachedLimbsChanged);
        this.detachedLimbsAccessor.onLoad(this::onDetachedLimbsChanged);
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Set<ResourceLocation> getDetachedLimbIds() {
        return Collections.unmodifiableSet(detachedLimbs);
    }

    public boolean isDetached(ResourceLocation limbId) {
        return detachedLimbs.contains(limbId);
    }

    public boolean isDetached(LimbDefinition limbDefinition) {
        return detachedLimbs.contains(limbDefinition.id());
    }

    public boolean hasAnyDetached() {
        return !detachedLimbs.isEmpty();
    }

    /**
     * Server-side: marks a limb as detached. Returns true if state changed.
     */
    public boolean markDetached(ResourceLocation limbId) {
        if (entity.level().isClientSide) {
            return false;
        }

        if (!detachedLimbs.add(limbId)) {
            return false;
        }

        publishToAccessor();
        return true;
    }

    /**
     * Server-side: marks a limb as reattached/restored. Returns true if state changed.
     */
    public boolean markReattached(ResourceLocation limbId) {
        if (entity.level().isClientSide) {
            return false;
        }

        if (!detachedLimbs.remove(limbId)) {
            return false;
        }

        publishToAccessor();
        return true;
    }

    public void clear() {
        if (entity.level().isClientSide || detachedLimbs.isEmpty()) {
            return;
        }

        detachedLimbs.clear();
        publishToAccessor();
    }

    private void publishToAccessor() {
        detachedLimbsAccessor.set(List.copyOf(detachedLimbs));
    }

    private void onDetachedLimbsChanged(List<ResourceLocation> incoming) {
        detachedLimbs = new HashSet<>(incoming);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (!compoundTag.contains(NBT_KEY, Tag.TAG_LIST)) {
            return;
        }

        var listTag = compoundTag.getList(NBT_KEY, Tag.TAG_STRING);
        var loaded = new HashSet<ResourceLocation>(listTag.size());

        for (var i = 0; i < listTag.size(); i++) {
            var raw = listTag.getString(i);
            var parsed = ResourceLocation.tryParse(raw);

            if (parsed != null) {
                loaded.add(parsed);
            }
        }

        detachedLimbs = loaded;

        if (!entity.level().isClientSide) {
            publishToAccessor();
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        if (detachedLimbs.isEmpty()) {
            return;
        }

        var listTag = new ListTag();

        for (var id : detachedLimbs) {
            listTag.add(StringTag.valueOf(id.toString()));
        }

        compoundTag.put(NBT_KEY, listTag);
    }

    public static boolean isDetached(Entity entity, ResourceLocation limbId) {
        return entity instanceof Dismemberable dismemberable
            && dismemberable.getDismembermentManager().isDetached(limbId);
    }
}
