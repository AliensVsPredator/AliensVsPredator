package com.blib.api.common.dismemberment.v1.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache that interns transient "ghost" {@link LivingEntity} instances by ({@link EntityType}, NBT-content)
 * key, so multiple {@link DismemberedLimbEntity}s spawned from the same source mob share a single ghost rather than
 * each constructing their own.
 * <p>
 * Cache values are {@link WeakReference}s. Each limb that resolves a ghost holds its own strong reference (in
 * {@code DismemberedLimbEntity#cachedGhost}), so the ghost stays alive as long as any limb references it; once every
 * referencing limb is removed and GC'd, the weak reference clears and the ghost can be collected too.
 * <p>
 * The ghost itself is created via {@link EntityType#create(Level)} and is never added to the level — it exists purely
 * as a typed data carrier for the renderer to call {@code getTextureLocation} / {@code setupAnim} / etc. against.
 */
public final class LimbGhostCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimbGhostCache.class);

    private static final Map<GhostKey, WeakReference<LivingEntity>> CACHE = new ConcurrentHashMap<>();

    private LimbGhostCache() {}

    public static @Nullable LivingEntity getOrCreate(EntityType<?> sourceType, CompoundTag sourceNbt, Level level) {
        var nbtBytes = serializeNbt(sourceNbt);

        if (nbtBytes == null) {
            return null;
        }

        var key = new GhostKey(sourceType, nbtBytes);
        var ref = CACHE.get(key);
        var existing = ref != null ? ref.get() : null;

        if (existing != null) {
            return existing;
        }

        var ghost = createGhost(sourceType, sourceNbt, level);

        if (ghost == null) {
            return null;
        }

        CACHE.put(key, new WeakReference<>(ghost));
        sweepClearedEntries();
        return ghost;
    }

    private static @Nullable LivingEntity createGhost(EntityType<?> sourceType, CompoundTag sourceNbt, Level level) {
        var entity = sourceType.create(level);

        if (!(entity instanceof LivingEntity living)) {
            return null;
        }

        try {
            living.load(sourceNbt);
        } catch (Throwable t) {
            LOGGER.debug("Failed to load ghost NBT for {}", sourceType, t);
            return null;
        }

        // Reset any "dying" state captured at the moment of dismemberment so the ghost renders
        // in a neutral pose. The renderer reads health/deathTime/hurtTime to decide hurt flashes
        // and the death rotation; we don't want either applied to a static limb fragment.
        living.setHealth(living.getMaxHealth());
        living.deathTime = 0;
        living.hurtTime = 0;

        return living;
    }

    private static @Nullable byte[] serializeNbt(CompoundTag tag) {
        try {
            var out = new ByteArrayOutputStream();
            NbtIo.write(tag, new DataOutputStream(out));
            return out.toByteArray();
        } catch (Exception e) {
            LOGGER.debug("Failed to serialize NBT for ghost cache key", e);
            return null;
        }
    }

    private static void sweepClearedEntries() {
        // Opportunistic cleanup of stale weak references. Cheap because the map is only as large
        // as the number of distinct sources currently visualized.
        CACHE.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

    private static final class GhostKey {

        private final EntityType<?> type;

        private final byte[] nbtBytes;

        private final int hash;

        GhostKey(EntityType<?> type, byte[] nbtBytes) {
            this.type = type;
            this.nbtBytes = nbtBytes;
            this.hash = Objects.hash(type, Arrays.hashCode(nbtBytes));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof GhostKey other)) {
                return false;
            }
            return type == other.type && Arrays.equals(nbtBytes, other.nbtBytes);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
