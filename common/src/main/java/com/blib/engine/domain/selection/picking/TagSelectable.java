package com.blib.engine.domain.selection.picking;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

/**
 * {@link Selectable} wrapping one tag in one registry. Tags have no world position, so {@link #worldBounds} is empty
 * and the world-space selection-highlight renderer has nothing to draw — the inspector picks up the selection and shows
 * the editable tag view via {@link com.blib.engine.ui.panel.details.DetailsPanel}'s {@code case TAG} branch.
 * <p>
 * Equality is value-based on {@code (registryKey, tagId)} so the inspector can detect "same tag still selected" across
 * frames and avoid re-fetching its draft when the selection is stable.
 */
@ApiStatus.Internal
public final class TagSelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final ResourceLocation registryKey;

    private final ResourceLocation tagId;

    public TagSelectable(ResourceLocation registryKey, ResourceLocation tagId) {
        this.registryKey = registryKey;
        this.tagId = tagId;
    }

    public ResourceLocation registryKey() {
        return registryKey;
    }

    public ResourceLocation tagId() {
        return tagId;
    }

    @Override
    public SelectableType type() {
        return SelectableType.TAG;
    }

    @Override
    public Component displayName() {
        return Component.literal("#" + tagId);
    }

    @Override
    public AABB worldBounds() {
        return EMPTY_BOUNDS;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagSelectable other)) {
            return false;
        }
        return registryKey.equals(other.registryKey) && tagId.equals(other.tagId);
    }

    @Override
    public int hashCode() {
        return 31 * registryKey.hashCode() + tagId.hashCode();
    }
}
