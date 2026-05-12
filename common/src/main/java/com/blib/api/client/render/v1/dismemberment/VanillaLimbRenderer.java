package com.blib.api.client.render.v1.dismemberment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.api.common.dismemberment.v1.LimbVisualsRegistry;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;
import com.blib.internal.mixin.MixinLivingEntityRenderer_Accessor;
import com.blib.internal.mixin.MixinModelPart_Accessor;

/**
 * Renders the {@link ModelPart}s of a vanilla mob that make up a {@link DismemberedLimbEntity} — the limb's
 * {@code rootBoneName} subtree plus any companion bones declared on its {@link LimbDefinition} — used when the source
 * mob's visual is a vanilla {@link EntityModel} rather than a BLib geo bake.
 * <p>
 * The source entity's model is looked up via the {@link net.minecraft.client.renderer.entity.EntityRenderDispatcher}
 * cache, the named children of its root part are borrowed, and their local pose is temporarily zeroed so the fragment
 * doesn't inherit any animation state set during the source mob's last frame.
 * <p>
 * For mobs with a nested skeleton (warden, where {@code body} contains {@code head}/{@code arms} as children), the
 * subtree walk also <em>hides</em> any descendant that is itself another limb's {@code rootBoneName} on the same source
 * entity type. Without this, a body limb's recursive {@code ModelPart.render} would re-draw the head/arms that are
 * already separate fragments — you'd see the head riding on the body fragment in addition to the standalone head
 * fragment.
 */
public final class VanillaLimbRenderer {

    private VanillaLimbRenderer() {}

    public static void render(
        DismemberedLimbEntity limb,
        PoseStack poseStack,
        VertexConsumer vertexConsumer,
        int packedLight,
        int packedOverlay
    ) {
        var visuals = limb.resolveVisuals();
        var ghost = limb.getOrCreateGhost();

        if (visuals == null || ghost == null) {
            return;
        }

        var rootPart = resolveModelPart(ghost, visuals.rootBoneName());

        if (rootPart == null) {
            return;
        }

        var skipSet = computeSkipSet(limb);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - limb.getYRot()));

        var renderRotation = visuals.renderRotation();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));

        // Authored scale applied around the limb anchor, before the offset translates the fragment into the hitbox.
        var renderScale = visuals.renderScale();
        poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);

        var renderOffset = visuals.renderOffset();
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);

        // Vanilla entity models are authored upside-down relative to world axes;
        // LivingEntityRenderer applies this scale before rendering the model, so we
        // do the same here when isolating a part.
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        renderPartIsolated(rootPart, skipSet, poseStack, vertexConsumer, packedLight, packedOverlay);

        // Companion bones (e.g. chicken's beak/wattle siblings of `head`) live outside the root subtree but ride along
        // with this limb. Render each at the same pose stack as the root so they sit in the same fragment frame.
        if (!visuals.companionBoneNames().isEmpty()) {
            for (var companionBoneName : visuals.companionBoneNames()) {
                var companionPart = resolveModelPart(ghost, companionBoneName);

                if (companionPart != null) {
                    renderPartIsolated(companionPart, skipSet, poseStack, vertexConsumer, packedLight, packedOverlay);
                }
            }
        }

        poseStack.popPose();
    }

    /**
     * Names of every other limb's {@code rootBoneName} (and companion bones) on the same source entity type. While
     * rendering this limb, any descendant ModelPart with a name in this set should be hidden so it doesn't get
     * double-rendered (here as part of an ancestor's subtree, and elsewhere as its own limb fragment).
     */
    private static Set<String> computeSkipSet(DismemberedLimbEntity limb) {
        var sourceType = limb.getSourceEntityType();

        if (sourceType == null) {
            return Set.of();
        }

        var thisLimbId = limb.getLimbId();
        var skip = new HashSet<String>();

        for (var def : LimbDefinitionRegistry.getDefinitions(sourceType)) {
            if (def.id().equals(thisLimbId)) {
                continue;
            }

            var visuals = LimbVisualsRegistry.get(sourceType, def.id());
            if (visuals == null) {
                continue;
            }
            skip.add(visuals.rootBoneName());
            skip.addAll(visuals.companionBoneNames());
        }

        return skip;
    }

    private static void renderPartIsolated(
        ModelPart part,
        Set<String> skipDescendantNames,
        PoseStack poseStack,
        VertexConsumer vertexConsumer,
        int packedLight,
        int packedOverlay
    ) {
        // Snapshot every part we may touch — root + every descendant — so the model state is fully restored after
        // render. The root gets zeroed (anchor its cubes at the limb origin); descendants get resetPose (preserve
        // their bind-pose offsets relative to the root, drop animation drift). Skip-targets get visibility cleared.
        var snapshots = new ArrayList<PartSnapshot>();
        try {
            collectAndApply(part, null, true, skipDescendantNames, snapshots);
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        } finally {
            for (var snapshot : snapshots) {
                snapshot.restore();
            }
        }
    }

    private static void collectAndApply(
        ModelPart part,
        @Nullable String partName,
        boolean isRoot,
        Set<String> skipDescendantNames,
        List<PartSnapshot> snapshots
    ) {
        snapshots.add(PartSnapshot.capture(part));

        // Skip-targets get hidden (visibility off) so ModelPart.render won't draw them or recurse into them. We
        // intentionally don't walk the skipped part's subtree — anything under it would also be hidden via the
        // visibility short-circuit and is irrelevant to this fragment.
        if (partName != null && skipDescendantNames.contains(partName)) {
            part.visible = false;
            return;
        }

        if (isRoot) {
            // Anchor the limb's root part's cubes at the limb origin: zero pose. Without this, mobs whose limb root
            // has a non-zero bind-pose translation (warden head at PartPose.offset(0, -13, 0), warden body at
            // (0, -21, 0), chicken head at (0, 15, -4), etc.) would render their cubes offset from the limb's
            // hitbox — looking like the head/body is floating away from where it should be.
            part.x = 0;
            part.y = 0;
            part.z = 0;
            part.xRot = 0;
            part.yRot = 0;
            part.zRot = 0;
        } else {
            // Descendants restore their bind pose so they're positioned correctly relative to the (now-anchored)
            // root, but without the animation drift (head bob, arm sway, ribcage flare) carried from the source
            // mob's last frame.
            part.resetPose();
        }

        var children = ((MixinModelPart_Accessor) (Object) part).blib$getChildren();

        for (var entry : children.entrySet()) {
            collectAndApply(entry.getValue(), entry.getKey(), false, skipDescendantNames, snapshots);
        }
    }

    private static @Nullable ModelPart resolveModelPart(LivingEntity ghost, String partName) {
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers().get(ghost.getType());

        if (!(renderer instanceof LivingEntityRenderer<?, ?>)) {
            return null;
        }

        EntityModel<?> model = ((MixinLivingEntityRenderer_Accessor) renderer).blib$getModel();

        if (model == null) {
            return null;
        }

        return findModelPart(model, partName);
    }

    /**
     * Resolves a named {@link ModelPart} on a vanilla {@link EntityModel} via the {@link ModelPartResolverRegistry}.
     * Callers should pass the {@code LayerDefinition} child name (typically {@code snake_case}, e.g. {@code "head"} /
     * {@code "right_arm"}); the registered resolver for the model's class hierarchy is responsible for translating it
     * into a part reference.
     */
    public static @Nullable ModelPart findModelPart(EntityModel<?> model, String partName) {
        return ModelPartResolverRegistry.resolve(model, partName);
    }

    /**
     * Snapshot of a {@link ModelPart}'s local pose + visibility, captured before mutation and restored after render.
     */
    private record PartSnapshot(
        ModelPart part,
        boolean visible,
        float x,
        float y,
        float z,
        float xRot,
        float yRot,
        float zRot
    ) {

        static PartSnapshot capture(ModelPart part) {
            return new PartSnapshot(part, part.visible, part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
        }

        void restore() {
            part.visible = visible;
            part.x = x;
            part.y = y;
            part.z = z;
            part.xRot = xRot;
            part.yRot = yRot;
            part.zRot = zRot;
        }
    }
}
