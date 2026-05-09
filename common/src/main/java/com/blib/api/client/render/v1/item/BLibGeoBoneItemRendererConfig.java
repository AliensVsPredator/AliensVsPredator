package com.blib.api.client.render.v1.item;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import com.blib.api.client.render.v1.BLibTransform;

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
 */
public record BLibGeoBoneItemRendererConfig(
    ResourceLocation geoModel,
    ResourceLocation texture,
    String boneName,
    BLibItemTransforms idleTransforms,
    @Nullable BLibItemTransforms blockingTransforms,
    Predicate<ItemStack> isBlocking
) {

    public BLibGeoBoneItemRendererConfig {
        if (boneName == null || boneName.isBlank()) {
            throw new IllegalArgumentException("boneName must be non-blank");
        }
    }

    public static Builder builder(ResourceLocation geoModel, ResourceLocation texture, String boneName) {
        return new Builder(geoModel, texture, boneName);
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
            return new BLibGeoBoneItemRendererConfig(geoModel, texture, boneName, idleTransforms, blockingTransforms, isBlocking);
        }
    }

    private static boolean defaultIsBlockingPredicate(ItemStack stack) {
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
