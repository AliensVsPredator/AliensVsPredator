package com.blib.engine.gizmo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.api.client.render.v1.item.BLibItemTransforms;

/**
 * {@link BLibItemTransforms} variant whose {@link #get} consults {@link BLibItemTransformOverrides} before falling back
 * to the wrapped base transforms — letting the {@code /blib transform-tune} command live-tweak per-context values for a
 * specific item without restarting the game.
 * <p>
 * Wrap your renderer's idle and blocking transform constants with {@link #wrap} during development, run the tuner
 * command to dial in pose values, then dump the final values via the command and paste them back into the base
 * constants. Once tuned, you can leave the wrapping in place (overrides default to no-op) or strip it back to the base
 * {@link BLibItemTransforms} for a tighter production setup.
 */
public class BLibTunableItemTransforms extends BLibItemTransforms {

    private final ResourceLocation itemId;

    private final BLibItemTransformMode mode;

    private final BLibItemTransforms base;

    private BLibTunableItemTransforms(ResourceLocation itemId, BLibItemTransformMode mode, BLibItemTransforms base) {
        super(Map.of());
        this.itemId = itemId;
        this.mode = mode;
        this.base = base;
    }

    public static BLibTunableItemTransforms wrap(ResourceLocation itemId, BLibItemTransformMode mode, BLibItemTransforms base) {
        BLibItemTransformOverrides.registerBase(itemId, mode, base);
        return new BLibTunableItemTransforms(itemId, mode, base);
    }

    public ResourceLocation itemId() {
        return itemId;
    }

    public BLibItemTransformMode mode() {
        return mode;
    }

    public BLibItemTransforms base() {
        return base;
    }

    @Override
    public BLibTransform get(ItemDisplayContext context) {
        var override = BLibItemTransformOverrides.get(itemId, mode, context);

        if (override != null) {
            return override;
        }

        return base.get(context);
    }

    @Override
    public @Nullable BLibTransform getOrNull(ItemDisplayContext context) {
        var override = BLibItemTransformOverrides.get(itemId, mode, context);

        if (override != null) {
            return override;
        }

        return base.getOrNull(context);
    }

    @Override
    public @Nullable BLibTransform getFixedWallOrNull() {
        var override = BLibItemTransformOverrides.getWallFixed(itemId, mode);

        if (override != null) {
            return override;
        }

        return base.getFixedWallOrNull();
    }
}
