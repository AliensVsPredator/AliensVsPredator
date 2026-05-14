package com.blib.internal.common.faction.io;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.blib.internal.common.faction.FactionMemberLastSeen;

@ApiStatus.Internal
public final class FactionMemberLocationIO {

    private static final String KEY_MEMBERS = "members";

    private static final String KEY_UUID = "uuid";

    private static final String KEY_DIMENSION = "dimension";

    private static final String KEY_CHUNK_X = "chunkX";

    private static final String KEY_CHUNK_Z = "chunkZ";

    private static final String KEY_TICK = "tick";

    private FactionMemberLocationIO() {
        throw new UnsupportedOperationException();
    }

    public static List<FactionMemberLastSeen> load(MinecraftServer server) {
        var path = FactionIO.getMemberLocationsPath(server);
        if (!Files.exists(path)) {
            return List.of();
        }

        var root = FactionIO.readCompressed(path);
        var list = root.getList(KEY_MEMBERS, Tag.TAG_COMPOUND);
        var entries = new ArrayList<FactionMemberLastSeen>();

        for (var i = 0; i < list.size(); i++) {
            var tag = list.getCompound(i);
            if (!tag.contains(KEY_UUID) || !tag.contains(KEY_DIMENSION)) {
                continue;
            }

            var uuid = UUIDUtil.uuidFromIntArray(tag.getIntArray(KEY_UUID));
            var dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(KEY_DIMENSION)));
            entries.add(
                new FactionMemberLastSeen(
                    uuid,
                    dimension,
                    tag.getInt(KEY_CHUNK_X),
                    tag.getInt(KEY_CHUNK_Z),
                    tag.getLong(KEY_TICK)
                )
            );
        }

        return entries;
    }

    public static void save(MinecraftServer server, List<FactionMemberLastSeen> entries) {
        if (entries.isEmpty()) {
            FactionIO.writeCompressed(FactionIO.getMemberLocationsPath(server), new CompoundTag());
            return;
        }

        var list = new ListTag();
        for (var entry : entries) {
            var tag = new CompoundTag();
            tag.put(KEY_UUID, new IntArrayTag(UUIDUtil.uuidToIntArray(entry.uuid())));
            tag.putString(KEY_DIMENSION, entry.dimension().location().toString());
            tag.putInt(KEY_CHUNK_X, entry.chunkX());
            tag.putInt(KEY_CHUNK_Z, entry.chunkZ());
            tag.putLong(KEY_TICK, entry.tick());
            list.add(tag);
        }

        var root = new CompoundTag();
        root.put(KEY_MEMBERS, list);
        FactionIO.writeCompressed(FactionIO.getMemberLocationsPath(server), root);
    }
}
