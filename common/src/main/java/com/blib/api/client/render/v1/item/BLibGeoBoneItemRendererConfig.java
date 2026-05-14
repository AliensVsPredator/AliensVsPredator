package com.blib.api.client.render.v1.item;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.engine.gizmo.BLibTunableItemTransforms;
import com.blib.internal.client.render.item.config.BLibItemRendererConfigs;

/**
 * Configuration for a {@link BLibGeoBoneItemRenderer}. Identifies which bone subtree of which geo model to render,
 * where its texture lives, how to position it for each {@link net.minecraft.world.item.ItemDisplayContext}, and
 * (optionally) an alternate transform set to use when the holder is using the item as a shield.
 *
 * @param geoModel           Resource location of the geo JSON to source bones from. Typically an existing entity's geo
 *                           (e.g. {@code "avp_alien:geo/entity/queen.geo.json"}) so the head doesn't duplicate
 *                           resources.
 * @param texture            Texture for the rendered bone subtree. Usually the same texture the source entity uses, so
 *                           UV layout already matches.
 * @param boneName           Name of the bone whose subtree should be rendered. Every bone whose ancestor chain does not
 *                           pass through this bone is hidden via the renderer's
 *                           {@link com.blib.api.client.render.v1.BoneVisibilityFilter}. The bone itself and all its
 *                           descendants stay visible.
 * @param idleTransforms     Transforms applied per-context when the holder is not using the item as a shield (or
 *                           always, if {@code blockingTransforms} is null). Lookup falls back to
 *                           {@link BLibTransform#IDENTITY} for any context not explicitly set.
 * @param blockingTransforms Optional alternate transform set used when {@code isBlocking.test(stack)} returns true
 *                           (typically: holder is currently using the item as a shield). {@code null} means use
 *                           {@code idleTransforms} regardless of use state.
 * @param isBlocking         Predicate that decides per-frame whether to apply {@code blockingTransforms}. Called only
 *                           when {@code blockingTransforms} is non-null. Default checks the local client player's use
 *                           state, which is correct for first-person rendering of the local player only.
 * @param assetConfigId      Non-null when this config was constructed via {@link #fromAsset} — the id of the asset
 *                           entry in {@code assets/<ns>/blib/item_renderers/<id>.json} that the transforms snapshot was
 *                           lifted from. The transforms fields ({@code idleTransforms}, {@code blockingTransforms}) are
 *                           {@code BLibTunableItemTransforms} bound to a dynamic supplier in this case, so reloads of
 *                           the underlying asset propagate automatically — the {@code assetConfigId} marker is mainly
 *                           for the modeler inspector (so it knows the save target for auto-saved edits) and tooling.
 *                           Null for fully Java-built configs.
 */
public record BLibGeoBoneItemRendererConfig(
    ResourceLocation geoModel,
    ResourceLocation texture,
    String boneName,
    BLibItemTransforms idleTransforms,
    @Nullable BLibItemTransforms blockingTransforms,
    Predicate<ItemStack> isBlocking,
    @Nullable ResourceLocation assetConfigId
) {

    public BLibGeoBoneItemRendererConfig {
        if (boneName == null || boneName.isBlank()) {
            throw new IllegalArgumentException("boneName must be non-blank");
        }
    }

    public static Builder builder(ResourceLocation geoModel, ResourceLocation texture, String boneName) {
        return new Builder(geoModel, texture, boneName);
    }

    /**
     * Build a config whose transforms come from a JSON asset entry rather than Java code. The asset (already loaded by
     * {@code BLibItemRendererConfigLoader} on resource reload) supplies geo model id, texture, bone name, and both
     * transform sets. Transform reads consult the live registry every frame via
     * {@link com.blib.engine.gizmo.BLibTunableItemTransforms#wrapDynamic}, so resource-pack reloads (and inspector
     * edits written into the per-project resource pack) propagate without re-instantiating the renderer.
     * <p>
     * The {@code itemId} is the {@link net.minecraft.world.item.Item}'s registry id; it's needed at construction time
     * because {@code BLibTunableItemTransforms} keys overrides and bases by item id. The renderer's structural fields
     * (model / texture / bone) are baked at construction — if the asset isn't loaded yet they're left as placeholders,
     * which only matters in pathological cases (item registered to a config id that no pack ships).
     */
    public static BLibGeoBoneItemRendererConfig fromAsset(
        ResourceLocation itemId,
        ResourceLocation configId,
        Predicate<ItemStack> isBlocking
    ) {
        var resolved = BLibItemRendererConfigs.getOrNull(configId);

        Supplier<BLibItemTransforms> idleSupplier = () -> {
            var live = BLibItemRendererConfigs.getOrNull(configId);
            return live != null ? live.idleTransforms() : null;
        };

        Supplier<BLibItemTransforms> blockingSupplier = () -> {
            var live = BLibItemRendererConfigs.getOrNull(configId);
            return live != null ? live.blockingTransforms() : null;
        };

        var idleTunable = BLibTunableItemTransforms.wrapDynamic(itemId, BLibItemTransformMode.IDLE, idleSupplier);
        var blockingTunable = BLibTunableItemTransforms.wrapDynamic(itemId, BLibItemTransformMode.BLOCKING, blockingSupplier);

        // Placeholder structural fields when the asset isn't loaded yet — boneName must satisfy the non-blank
        // invariant.
        var model = resolved != null ? resolved.model() : ResourceLocation.fromNamespaceAndPath("blib", "missing");
        var texture = resolved != null ? resolved.texture() : ResourceLocation.fromNamespaceAndPath("blib", "missing");
        var bone = resolved != null ? resolved.boneName() : "missing";

        return new BLibGeoBoneItemRendererConfig(model, texture, bone, idleTunable, blockingTunable, isBlocking, configId);
    }

    public static final class Builder {

        private final ResourceLocation geoModel;

        private final ResourceLocation texture;

        private final String boneName;

        private BLibItemTransforms idleTransforms = BLibItemTransforms.builder().build();

        private @Nullable BLibItemTransforms blockingTransforms = null;

        private Predicate<ItemStack> isBlocking = BLibGeoBoneItemRendererConfig::defaultIsBlockingPredicate;

        private Builder(ResourceLocation geoModel, ResourceLocation texture, String boneName) {
            this.geoModel = geoModel;
            this.texture = texture;
            this.boneName = boneName;
        }

        public Builder idleTransforms(BLibItemTransforms transforms) {
            this.idleTransforms = transforms;
            return this;
        }

        public Builder blockingTransforms(BLibItemTransforms transforms) {
            this.blockingTransforms = transforms;
            return this;
        }

        public Builder isBlocking(Predicate<ItemStack> predicate) {
            this.isBlocking = predicate;
            return this;
        }

        public BLibGeoBoneItemRendererConfig build() {
            return new BLibGeoBoneItemRendererConfig(
                geoModel,
                texture,
                boneName,
                idleTransforms,
                blockingTransforms,
                isBlocking,
                null
            );
        }
    }

    /**
     * The Builder's default blocking predicate: true when the local client player is actively using {@code stack}
     * (e.g., holding right-mouse on a shield). Exposed so callers of {@link #fromAsset} and
     * {@code BLibClientRegistryAccess#registerGeoBoneItemRendererFromAsset} can reuse it without rewriting the body.
     * <p>
     * Correct for first-person rendering of the local player only — third-person views of remote players hit the same
     * predicate but the {@code player} reference is always the local player, so remote use state isn't reflected.
     */
    public static boolean defaultIsBlockingPredicate(ItemStack stack) {
        var mc = Minecraft.getInstance();
        var player = mc.player;

        if (player == null) {
            return false;
        }

        if (!player.isUsingItem()) {
            return false;
        }

        return player.getUseItem() == stack;
    }
}
