package com.blib.api.client.render.v1.item;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Renders a single named bone subtree of an existing geo model as an item — designed for cases where you
 * want to reuse a mob's geo head (or arm, or some other subtree) as a held item without forking the JSON.
 * <p>
 * Configuration comes from a {@link BLibGeoBoneItemRendererConfig}. The renderer:
 * <ul>
 *   <li>Loads the config's geo model and texture through the standard AzureLib item pipeline.</li>
 *   <li>Hides every bone whose ancestor chain doesn't pass through {@link BLibGeoBoneItemRendererConfig#boneName}
 *       — sibling/cousin subtrees of the target are pruned entirely via the standard
 *       {@link com.blib.api.client.render.v1.BoneVisibilityFilter}.</li>
 *   <li>Suppresses cube-rendering on each ancestor of the target without skipping the recursion (so the
 *       transform chain root → … → target stays composed, but the chest/torso bones above the target's head
 *       don't draw their own cubes alongside it). State is snapshot-and-restored across each render so the
 *       same baked model can be safely shared with an entity renderer using the same geo.</li>
 *   <li>Freezes animation in every {@link net.minecraft.world.item.ItemDisplayContext} so the bone stays at
 *       bind pose — there's no entity tick to drive an animator anyway.</li>
 *   <li>Applies the config's {@link BLibItemTransforms} (or {@link BLibGeoBoneItemRendererConfig#blockingTransforms}
 *       when {@link BLibGeoBoneItemRendererConfig#isBlocking} returns true) before bone rendering, letting you
 *       position the bone differently per render context.</li>
 * </ul>
 * <p>
 * Register one instance per shield item via the loader's normal item-renderer hook
 * ({@code BuiltinItemRendererRegistry.register} on Fabric, {@code IClientItemExtensions.getCustomRenderer}
 * on NeoForge), as you would any other {@link AzItemRenderer} subclass.
 */
public class BLibGeoBoneItemRenderer extends AzItemRenderer {

    /**
     * Shared snapshot buffer reused across renders. Mc's item rendering is single-threaded and
     * pre/post-render hooks are always paired around a single render call, so a static buffer is safe.
     */
    private static final List<BoneSnapshot> ANCESTOR_SNAPSHOT = new ArrayList<>(8);

    private final BLibGeoBoneItemRendererConfig config;

    public BLibGeoBoneItemRenderer(BLibGeoBoneItemRendererConfig config) {
        super(buildConfig(config));
        this.config = config;
    }

    public BLibGeoBoneItemRendererConfig geoBoneItemConfig() {
        return config;
    }

    private static AzItemRendererConfig buildConfig(BLibGeoBoneItemRendererConfig config) {
        return AzItemRendererConfig.builder(config.geoModel(), config.texture())
            .setBoneVisibilityFilter((bone, stack) -> isOffChain(bone, config.boneName()))
            .disableAnimationInAllContexts()
            .setPrerenderEntry(context -> {
                hideAncestorCubes(context.bakedModel(), config.boneName());
                applyTransforms(config, context);
                return context;
            })
            .setPostRenderEntry(context -> {
                restoreAncestorCubes();
                return context;
            })
            .build();
    }

    private static void hideAncestorCubes(AzBakedModel model, String targetName) {
        var target = model.getBoneOrNull(targetName);

        if (target == null) {
            return;
        }

        for (var ancestor = target.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            ANCESTOR_SNAPSHOT.add(new BoneSnapshot(ancestor, ancestor.isHidden(), ancestor.isHidingChildren()));
            ancestor.setHidden(true);
            ancestor.setChildrenHidden(false);
        }
    }

    private static void restoreAncestorCubes() {
        for (int i = ANCESTOR_SNAPSHOT.size() - 1; i >= 0; i--) {
            var snap = ANCESTOR_SNAPSHOT.get(i);
            snap.bone.setHidden(snap.hidden);
            snap.bone.setChildrenHidden(snap.childrenHidden);
        }

        ANCESTOR_SNAPSHOT.clear();
    }

    private static void applyTransforms(BLibGeoBoneItemRendererConfig config, AzRendererPipelineContext<UUID, ItemStack> context) {
        var itemContext = (AzItemRendererPipelineContext) context;
        var displayContext = itemContext.getTransformType();
        var stack = context.animatable();

        BLibTransform transform = null;

        if (config.blockingTransforms() != null && config.isBlocking().test(stack)) {
            transform = config.blockingTransforms().getOrNull(displayContext);
        }

        if (transform == null) {
            // Cascade: blocking is treated as a per-context override layered on top of idle. Any context the
            // user didn't explicitly set on the blocking transforms falls through to idle, instead of snapping
            // to IDENTITY (which would yank the item to model origin during a block, the previous behavior).
            transform = config.idleTransforms().get(displayContext);
        }

        transform.apply(itemContext.poseStack());
    }

    private static boolean isOffChain(AzBone bone, String targetName) {
        if (bone.getName().equals(targetName)) {
            return false;
        }

        for (var ancestor = bone.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor.getName().equals(targetName)) {
                return false;
            }
        }

        return !hasDescendantNamed(bone, targetName);
    }

    private static boolean hasDescendantNamed(AzBone bone, String name) {
        for (var child : bone.getChildBones()) {
            if (child.getName().equals(name) || hasDescendantNamed(child, name)) {
                return true;
            }
        }

        return false;
    }

    private record BoneSnapshot(AzBone bone, boolean hidden, boolean childrenHidden) {}
}
