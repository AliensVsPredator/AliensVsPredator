package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.world.entity.LivingEntity;

import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.BoneVisibilityFilter;
import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbVisualsRegistry;

/**
 * Default {@link BoneVisibilityFilter} for {@link Dismemberable} entities.
 * <p>
 * Hides any bone whose name matches the {@code rootBoneName} (or a companion bone) of a limb that has been detached.
 * Children of the root are skipped automatically because {@code AzModelRenderer.renderRecursively} early-exits on a
 * filtered bone, so a single filter check covers the whole subtree.
 * <p>
 * <b>Auto-applied.</b> {@code AzModelRenderer.renderRecursively} consults {@link #isDetachedBone(AzBone, Object)}
 * unconditionally on every render, so consumers no longer need to wire this filter onto each entity renderer's config
 * to get dismemberment hiding — that was easy to forget when adding new mobs.
 * {@link AzRendererConfig.Builder#setBoneVisibilityFilter} is still available on the renderer config for additional,
 * per-renderer hide rules; those compose with this one.
 * <p>
 * The instance form is preserved for callers who want to plug dismemberment hiding into their own filter pipeline (or
 * intentionally compose multiple filters); new code should usually rely on the auto-applied static check instead.
 */
public final class DismembermentBoneVisibilityFilter<T extends LivingEntity> implements BoneVisibilityFilter<T> {

    @Override
    public boolean shouldHideBone(AzBone bone, T animatable) {
        return isDetachedBone(bone, animatable);
    }

    /**
     * Static check used by {@code AzModelRenderer} to make dismemberment hiding always-on. Safe to call for any
     * animatable type — non-{@link Dismemberable} animatables (items, block entities, mobs without limb defs)
     * early-return {@code false}.
     * <p>
     * Bone names are pulled live from {@link LimbVisualsRegistry} on every call rather than cached, so resource-pack
     * reloads that change a limb's {@code rootBoneName} / {@code companionBoneNames} take effect immediately. Cost is a
     * handful of {@code HashMap.get} per render frame per detached limb — negligible in practice.
     */
    public static boolean isDetachedBone(AzBone bone, Object animatable) {
        if (!(animatable instanceof Dismemberable dismemberable) || !(animatable instanceof LivingEntity living)) {
            return false;
        }

        var detached = dismemberable.getDismembermentManager().getDetachedLimbIds();

        if (detached.isEmpty()) {
            return false;
        }

        var boneName = bone.getName();
        var sourceType = living.getType();

        for (var limbId : detached) {
            var visuals = LimbVisualsRegistry.get(sourceType, limbId);
            if (visuals == null) {
                continue;
            }
            if (visuals.rootBoneName().equals(boneName)) {
                return true;
            }
            for (var companion : visuals.companionBoneNames()) {
                if (companion.equals(boneName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
