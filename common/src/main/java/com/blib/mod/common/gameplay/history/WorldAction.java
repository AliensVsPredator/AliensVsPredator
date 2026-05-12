package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * An {@link EditorAction} that mutates a specific {@link Level}'s world state — blocks, entities, chunk claims. The
 * dimension is captured so {@link ActionHistory#undo} can filter to the player's current dimension: placing in the
 * Overworld then portaling to the Nether and pressing Ctrl+Z must not restore Overworld blocks at the same coordinates
 * in the Nether.
 */
@ApiStatus.Internal
public sealed interface WorldAction extends EditorAction permits BlockRegionEdit, EntitySpawnAction, EntityRemoveAction, EntityTranslateAction, EntityScaleAction, ChunkClaimEdit {

    ResourceKey<Level> dimension();
}
