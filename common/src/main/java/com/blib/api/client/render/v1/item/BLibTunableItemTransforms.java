package com.blib.api.client.render.v1.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

import com.blib.api.client.render.v1.BLibTransform;

/**
 * {@link BLibItemTransforms} variant whose {@link #get} consults {@link BLibItemTransformOverrides} before falling back
 * to the wrapped base — letting the {@code /blib transform-tune} command and the modeler inspector live-tweak
 * per-context values without restarting the game.
 * <p>
 * Two construction modes:
 * <ul>
 * <li>{@link #wrap} — wraps a fixed {@link BLibItemTransforms} snapshot. Used for Java-built configs where the base is
 * a compiled constant.</li>
 * <li>{@link #wrapDynamic} — wraps a {@link Supplier} that resolves the base every read. Used for asset-backed configs
 * where the base lives in {@code BLibItemRendererConfigs} and changes on every resource-pack reload — the supplier
 * lookup ensures reads always see the most recently loaded transforms.</li>
 * </ul>
 */
public class BLibTunableItemTransforms extends BLibItemTransforms {

    private final ResourceLocation itemId;

    private final BLibItemTransformMode mode;

    private final Supplier<BLibItemTransforms> baseSupplier;

    private BLibTunableItemTransforms(ResourceLocation itemId, BLibItemTransformMode mode, Supplier<BLibItemTransforms> baseSupplier) {
        super(Map.of());
        this.itemId = itemId;
        this.mode = mode;
        this.baseSupplier = baseSupplier;
    }

    public static BLibTunableItemTransforms wrap(ResourceLocation itemId, BLibItemTransformMode mode, BLibItemTransforms base) {
        Supplier<BLibItemTransforms> supplier = () -> base;
        BLibItemTransformOverrides.registerBase(itemId, mode, supplier);
        return new BLibTunableItemTransforms(itemId, mode, supplier);
    }

    /**
     * For asset-backed configs: the base is resolved from {@code BLibItemRendererConfigs} (or any other live source) on
     * every read. Reloads of the underlying asset propagate without re-creating the wrap or the renderer. Supplier may
     * legitimately return {@code null} when the asset isn't loaded yet — readers treat that as IDENTITY.
     */
    public static BLibTunableItemTransforms wrapDynamic(
        ResourceLocation itemId,
        BLibItemTransformMode mode,
        Supplier<BLibItemTransforms> baseSupplier
    ) {
        BLibItemTransformOverrides.registerBase(itemId, mode, baseSupplier);
        return new BLibTunableItemTransforms(itemId, mode, baseSupplier);
    }

    public ResourceLocation itemId() {
        return itemId;
    }

    public BLibItemTransformMode mode() {
        return mode;
    }

    public @Nullable BLibItemTransforms base() {
        return baseSupplier.get();
    }

    @Override
    public BLibTransform get(ItemDisplayContext context) {
        var override = BLibItemTransformOverrides.get(itemId, mode, context);

        if (override != null) {
            return override;
        }

        var base = baseSupplier.get();

        if (base == null) {
            return BLibTransform.IDENTITY;
        }

        return base.get(context);
    }

    @Override
    public @Nullable BLibTransform getOrNull(ItemDisplayContext context) {
        var override = BLibItemTransformOverrides.get(itemId, mode, context);

        if (override != null) {
            return override;
        }

        var base = baseSupplier.get();

        if (base == null) {
            return null;
        }

        return base.getOrNull(context);
    }

    @Override
    public @Nullable BLibTransform getFixedWallOrNull() {
        var override = BLibItemTransformOverrides.getWallFixed(itemId, mode);

        if (override != null) {
            return override;
        }

        var base = baseSupplier.get();

        if (base == null) {
            return null;
        }

        return base.getFixedWallOrNull();
    }
}
