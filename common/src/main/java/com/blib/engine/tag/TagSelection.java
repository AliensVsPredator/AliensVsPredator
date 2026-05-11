package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable singleton holding a one-shot request to switch the {@code TagEditorPanel}'s active tag. Modeled on
 * {@code JigsawPoolSelection} — when non-null, the editor's next {@code render()} consumes the request, updates both
 * its registry and tag selectors, and clears this field. Same lifecycle: cleared on engine workspace close.
 */
@ApiStatus.Internal
public final class TagSelection {

    public record TagSelectionRequest(
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}

    private static @Nullable TagSelectionRequest requested;

    private TagSelection() {}

    public static @Nullable TagSelectionRequest requested() {
        return requested;
    }

    public static void request(ResourceLocation registryKey, ResourceLocation tagId) {
        requested = new TagSelectionRequest(registryKey, tagId);
    }

    public static void clear() {
        requested = null;
    }
}
