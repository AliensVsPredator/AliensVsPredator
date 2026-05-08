package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.BoneVisibilityFilter;
import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

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

    private static final Map<EntityType<?>, Map<ResourceLocation, List<String>>> HIDE_BONES_BY_ENTITY_TYPE =
        new ConcurrentHashMap<>();

    @Override
    public boolean shouldHideBone(AzBone bone, T animatable) {
        return isDetachedBone(bone, animatable);
    }

    /**
     * Static check used by {@code AzModelRenderer} to make dismemberment hiding always-on. Safe to call for any
     * animatable type — non-{@link Dismemberable} animatables (items, block entities, mobs without limb defs)
     * early-return {@code false}.
     */
    public static boolean isDetachedBone(AzBone bone, Object animatable) {
        if (!(animatable instanceof Dismemberable dismemberable) || !(animatable instanceof LivingEntity living)) {
            return false;
        }

        var detached = dismemberable.getDismembermentManager().getDetachedLimbIds();

        if (detached.isEmpty()) {
            return false;
        }

        var hideBones = getOrComputeHideBones(living.getType());

        if (hideBones.isEmpty()) {
            return false;
        }

        return matchesAnyDetached(bone.getName(), detached, hideBones);
    }

    private static Map<ResourceLocation, List<String>> getOrComputeHideBones(EntityType<?> entityType) {
        return HIDE_BONES_BY_ENTITY_TYPE.computeIfAbsent(entityType, type -> {
            var definitions = LimbDefinitionRegistry.getDefinitions(type);
            var map = new HashMap<ResourceLocation, List<String>>(definitions.size());

            for (var definition : definitions) {
                var bones = new ArrayList<String>(1 + definition.companionBoneNames().size());
                bones.add(definition.rootBoneName());
                bones.addAll(definition.companionBoneNames());
                map.put(definition.id(), bones);
            }

            return map;
        });
    }

    private static boolean matchesAnyDetached(
        String boneName,
        Set<ResourceLocation> detached,
        Map<ResourceLocation, List<String>> hideBonesByLimbId
    ) {
        for (var limbId : detached) {
            var hideBones = hideBonesByLimbId.get(limbId);

            if (hideBones == null) {
                continue;
            }

            for (var name : hideBones) {
                if (name.equals(boneName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
