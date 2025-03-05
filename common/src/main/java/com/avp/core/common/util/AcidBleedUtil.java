package com.avp.core.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.entity.acid.Acid;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.entity.type.AVPEntityTypes;

public class AcidBleedUtil {

    public static void spawnAcid(LivingEntity livingEntity, float damage, Vec3 randomPos) {
        var level = livingEntity.level();
        var acidEntityType = AVPEntityTypes.ACID.get();

        var acidEntity = acidEntityType.create(level);

        if (acidEntity == null) {
            return;
        }

        acidEntity.setNetherAfflicted(livingEntity instanceof Alien alien && alien.isNetherAfflicted());

        var quarterHealth = Math.max(livingEntity.getMaxHealth() / 4, 1);
        var factor = damage / quarterHealth;

        acidEntity.moveTo(randomPos, livingEntity.getYRot(), livingEntity.getXRot());
        level.addFreshEntity(acidEntity);
        // Acid multiplier scales with how hurt the alien is.
        var inverseMultiplier = (int) Mth.map(livingEntity.getHealth(), 0, livingEntity.getMaxHealth(), 1, 10);
        var clampedInverseMultiplier = Math.clamp(inverseMultiplier, 1, Acid.MAX_MULTIPLIER);
        var multiplier = factor * (10 - clampedInverseMultiplier);
        acidEntity.setMultiplier((int) multiplier);
    }

    public static @NotNull Vec3 computeRandomPosFromBoundingBox(LivingEntity livingEntity) {
        var random = livingEntity.getRandom();
        var box = livingEntity.getBoundingBox();
        var pos = livingEntity.position();
        var xSize = (int) Math.ceil(box.maxX - box.minX);
        var zSize = (int) Math.ceil(box.maxZ - box.minZ);
        var randomX = random.nextInt(xSize + 1) * (random.nextBoolean() ? -1 : 1);
        var randomZ = random.nextInt(zSize + 1) * (random.nextBoolean() ? -1 : 1);
        return pos.add(randomX, 0, randomZ);
    }
}
