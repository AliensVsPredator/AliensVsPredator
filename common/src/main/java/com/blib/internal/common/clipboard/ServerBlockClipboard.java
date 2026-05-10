package com.blib.internal.common.clipboard;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Server-side block clipboard. Holds the most recent Cut/Copy snapshot as a {@link StructureTemplate} (so block-entity
 * NBT, palette compression, and registry-aware state are all handled by vanilla machinery), plus the metadata the
 * client mirrors for UI ({@link #size}, {@link #sourceDim}, {@link #filledAtMs}).
 * <p>
 * Single static slot, no per-player partitioning — BLib is single-player only and the engine workspace is single-user
 * per session. Paste reads from this slot and places at the requested destination; new Cut/Copy overwrites the slot.
 * <p>
 * Lives in memory only — cleared on server stop. Treating clipboard as session-scoped matches user expectation
 * ("clipboard goes away when I quit") and avoids serializing potentially-large structures to disk on every copy.
 */
@ApiStatus.Internal
public final class ServerBlockClipboard {

    private static @Nullable StructureTemplate template;

    private static @Nullable Vec3i size;

    private static @Nullable ResourceKey<Level> sourceDim;

    private static long filledAtMs;

    private ServerBlockClipboard() {}

    public static boolean hasContents() {
        return template != null;
    }

    public static @Nullable StructureTemplate template() {
        return template;
    }

    public static @Nullable Vec3i size() {
        return size;
    }

    public static @Nullable ResourceKey<Level> sourceDim() {
        return sourceDim;
    }

    public static long filledAtMs() {
        return filledAtMs;
    }

    public static void put(StructureTemplate t, Vec3i s, ResourceKey<Level> dim) {
        template = t;
        size = s;
        sourceDim = dim;
        filledAtMs = System.currentTimeMillis();
    }

    public static void clear() {
        template = null;
        size = null;
        sourceDim = null;
        filledAtMs = 0L;
    }
}
