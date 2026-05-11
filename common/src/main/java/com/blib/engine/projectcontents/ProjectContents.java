package com.blib.engine.projectcontents;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Client-side cache for the project content browser's three lists. Populated by S2C handlers
 * ({@code BLibClientListener.handlePoolList} / {@code handleStructureList}) and read each frame by
 * {@code ContentBrowserPanel}.
 * <p>
 * Captures intentionally aren't mirrored here — they remain in {@link com.blib.engine.blockselection.BlockSelection}
 * which is where the existing Capture Panel sources them, and the content browser reads through to keep one
 * authoritative source. Pools and structures are full namespaced ids since the project's datapack can carry multiple
 * namespaces.
 * <p>
 * Cleared on workspace close (and on active-project change implicitly because the workspace recreates) so a stale list
 * from a previous project doesn't bleed into the next session.
 */
@ApiStatus.Internal
public final class ProjectContents {

    private static List<ResourceLocation> pools = List.of();

    private static List<ResourceLocation> structures = List.of();

    private ProjectContents() {}

    public static List<ResourceLocation> pools() {
        return pools;
    }

    public static List<ResourceLocation> structures() {
        return structures;
    }

    public static void setPools(List<ResourceLocation> newPools) {
        pools = newPools == null ? List.of() : List.copyOf(newPools);
    }

    public static void setStructures(List<ResourceLocation> newStructures) {
        structures = newStructures == null ? List.of() : List.copyOf(newStructures);
    }

    public static void clear() {
        pools = List.of();
        structures = List.of();
    }
}
