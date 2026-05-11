package com.blib.mod.common.gameplay.jigsaw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.blib.api.common.storage.v1.DataStore;

/**
 * Per-level registry of {@link PlacedPiece} records — every jigsaw piece an engine-mode user has placed in this
 * dimension. Persists to {@code world/blib/data_storage/blib/levels/{dim}/placed_pieces.nbt} via the standard
 * {@link com.blib.api.common.storage.v1.DataStoreManager} pipeline (level-scoped, autoloaded on first access, autosaved
 * on world save).
 * <p>
 * v1 storage is a flat {@code Map<UUID, PlacedPiece>}; lookups are O(N) where N is the piece count for the dimension.
 * Authoring sessions rarely have more than a few hundred pieces and the ray-vs-AABB intersect is cheap, so the naive
 * iteration is fine. If counts ever blow up, a {@code ChunkPos -> Set<UUID>} index can sit alongside without changing
 * the public API.
 */
@ApiStatus.Internal
public class PlacedPieceStore implements DataStore {

    private static final String PIECES_KEY = "pieces";

    private static final String ID_KEY = "id";

    private static final String TEMPLATE_KEY = "template";

    private static final String DIMENSION_KEY = "dimension";

    private static final String AABB_KEY = "aabb";

    private static final String ANCHOR_KEY = "anchor";

    private static final String ROTATION_KEY = "rotation";

    private static final String MIRROR_KEY = "mirror";

    private static final String PLACED_AT_TICK_KEY = "placed_at_tick";

    private final Map<UUID, PlacedPiece> byId = new HashMap<>();

    public PlacedPieceStore() {}

    public void add(PlacedPiece piece) {
        byId.put(piece.id(), piece);
    }

    public @Nullable PlacedPiece remove(UUID id) {
        return byId.remove(id);
    }

    public @Nullable PlacedPiece get(UUID id) {
        return byId.get(id);
    }

    public Collection<PlacedPiece> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public int size() {
        return byId.size();
    }

    @Override
    public void load(CompoundTag tag) {
        byId.clear();

        if (!tag.contains(PIECES_KEY, Tag.TAG_LIST)) {
            return;
        }

        var list = tag.getList(PIECES_KEY, Tag.TAG_COMPOUND);
        for (var i = 0; i < list.size(); i++) {
            var entry = list.getCompound(i);
            var piece = readPiece(entry);
            if (piece != null) {
                byId.put(piece.id(), piece);
            }
        }
    }

    @Override
    public void save(CompoundTag tag) {
        if (byId.isEmpty()) {
            return;
        }

        var list = new ListTag();
        for (var piece : byId.values()) {
            list.add(writePiece(piece));
        }
        tag.put(PIECES_KEY, list);
    }

    private static @Nullable PlacedPiece readPiece(CompoundTag tag) {
        try {
            var id = tag.getUUID(ID_KEY);
            var templateId = ResourceLocation.tryParse(tag.getString(TEMPLATE_KEY));
            var dimensionId = ResourceLocation.tryParse(tag.getString(DIMENSION_KEY));
            if (templateId == null || dimensionId == null) {
                return null;
            }
            var dimension = ResourceKey.create(Registries.DIMENSION, dimensionId);
            var aabbArr = tag.getIntArray(AABB_KEY);
            if (aabbArr.length != 6) {
                return null;
            }
            var aabb = new BoundingBox(aabbArr[0], aabbArr[1], aabbArr[2], aabbArr[3], aabbArr[4], aabbArr[5]);
            var anchorArr = tag.getIntArray(ANCHOR_KEY);
            if (anchorArr.length != 3) {
                return null;
            }
            var anchor = new BlockPos(anchorArr[0], anchorArr[1], anchorArr[2]);
            var rotation = Rotation.values()[Math.floorMod(tag.getInt(ROTATION_KEY), Rotation.values().length)];
            var mirror = Mirror.values()[Math.floorMod(tag.getInt(MIRROR_KEY), Mirror.values().length)];
            var placedAtTick = tag.getLong(PLACED_AT_TICK_KEY);
            return new PlacedPiece(id, templateId, dimension, aabb, anchor, rotation, mirror, placedAtTick);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static CompoundTag writePiece(PlacedPiece piece) {
        var tag = new CompoundTag();
        tag.putUUID(ID_KEY, piece.id());
        tag.putString(TEMPLATE_KEY, piece.templateId().toString());
        tag.putString(DIMENSION_KEY, piece.dimension().location().toString());
        var aabb = piece.aabb();
        tag.putIntArray(
            AABB_KEY,
            new int[] { aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ() }
        );
        var anchor = piece.anchor();
        tag.putIntArray(ANCHOR_KEY, new int[] { anchor.getX(), anchor.getY(), anchor.getZ() });
        tag.putInt(ROTATION_KEY, piece.rotation().ordinal());
        tag.putInt(MIRROR_KEY, piece.mirror().ordinal());
        tag.putLong(PLACED_AT_TICK_KEY, piece.placedAtTick());
        return tag;
    }
}
