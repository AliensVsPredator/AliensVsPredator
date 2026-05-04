package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.BoneVisibilityFilter;
import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

/**
 * Default {@link BoneVisibilityFilter} for {@link Dismemberable} entities.
 * <p>
 * Hides any bone whose name matches the {@code rootBoneName} of a limb that has been detached. Children of the root are
 * skipped automatically because {@code AzModelRenderer.renderRecursively} early-exits on a filtered bone, so a single
 * filter check covers the whole subtree.
 * <p>
 * Per-entity-type bone-name lookups are cached so the hot render path is a single {@code Set#contains} call against an
 * empty set when nothing is detached.
 * <p>
 * The bound is {@link LivingEntity} rather than {@code LivingEntity & Dismemberable} because the {@code Dismemberable}
 * interface is added to every {@code LivingEntity} via mixin at runtime — not visible to the compiler. The instance
 * check inside {@code shouldHideBone} confirms it before reading the manager.
 */
public final class DismembermentBoneVisibilityFilter<T extends LivingEntity> implements BoneVisibilityFilter<T> {

    private final Map<EntityType<?>, Map<ResourceLocation, String>> rootBonesByEntityType = new HashMap<>();

    @Override
    public boolean shouldHideBone(AzBone bone, T animatable) {
        if (!(animatable instanceof Dismemberable dismemberable)) {
            return false;
        }

        var detached = dismemberable.getDismembermentManager().getDetachedLimbIds();

        if (detached.isEmpty()) {
            return false;
        }

        var rootBones = getOrComputeRootBones(animatable.getType());

        if (rootBones.isEmpty()) {
            return false;
        }

        return matchesAnyDetachedRoot(bone.getName(), detached, rootBones);
    }

    private Map<ResourceLocation, String> getOrComputeRootBones(EntityType<?> entityType) {
        return rootBonesByEntityType.computeIfAbsent(entityType, type -> {
            var definitions = LimbDefinitionRegistry.getDefinitions(type);
            var map = new HashMap<ResourceLocation, String>(definitions.size());

            for (var definition : definitions) {
                map.put(definition.id(), definition.rootBoneName());
            }

            return map;
        });
    }

    private static boolean matchesAnyDetachedRoot(
        String boneName,
        Set<ResourceLocation> detached,
        Map<ResourceLocation, String> rootBonesByLimbId
    ) {
        for (var limbId : detached) {
            var rootBoneName = rootBonesByLimbId.get(limbId);

            if (rootBoneName != null && rootBoneName.equals(boneName)) {
                return true;
            }
        }

        return false;
    }
}
