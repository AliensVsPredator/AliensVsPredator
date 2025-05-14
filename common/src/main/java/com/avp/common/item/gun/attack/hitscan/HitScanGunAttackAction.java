package com.avp.common.item.gun.attack.hitscan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import com.avp.common.item.gun.attack.GunAttackAction;
import com.avp.common.item.gun.attack.GunAttackConfig;
import com.avp.common.item.gun.attack.GunHitResult;
import com.avp.common.item.gun.pipeline.GunShootResult;
import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.util.AVPPredicates;
import com.avp.common.util.EnchantmentUtil;
import com.avp.service.Services;

public class HitScanGunAttackAction implements GunAttackAction {

    public static final HitScanGunAttackAction INSTANCE = new HitScanGunAttackAction();

    private HitScanGunAttackAction() {}

    @Override
    public GunShootResult shoot(GunAttackConfig gunAttackConfig) {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();

        if (!level.isClientSide) {
            return GunShootResult.FAILURE;
        }

        final var stepSize = 0.25;
        final var maxDistance = (double) gunAttackConfig.fireModeConfig().range();
        final var origin = shooter.getEyePosition();
        final var direction = shooter.getLookAngle().normalize();

        var hitEntityUUIDs = new HashSet<UUID>();
        var hitBlockPositions = new HashSet<BlockPos>();
        var hitResults = new ArrayList<GunHitResult>();

        var totalPierces = 0;
        var piercingBudget = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.PIERCING) + 1;

        var current = origin;
        var distanceTraveled = 0.0;

        while (distanceTraveled < maxDistance && totalPierces < piercingBudget) {
            var next = current.add(direction.scale(stepSize));
            distanceTraveled += stepSize;

            // Check for entity in this segment.
            var entityHit = ProjectileUtil.getEntityHitResult(
                level,
                shooter,
                current,
                next,
                shooter.getBoundingBox().expandTowards(direction.scale(maxDistance)).inflate(1.0),
                entity -> !hitEntityUUIDs.contains(entity.getUUID()) &&
                    (entity.getType() == EntityType.END_CRYSTAL || AVPPredicates.isLiving(entity))
            );

            if (entityHit != null) {
                var entity = entityHit.getEntity();
                hitEntityUUIDs.add(entity.getUUID());
                hitResults.add(new GunHitResult.Entity(entity.getUUID()));
                totalPierces++;

                // Don't skip block check – entities and blocks can be hit in same step.
            }

            // Check for block hits.
            var blockHit = level.clip(new ClipContext(current, next, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, shooter));

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                var blockPos = blockHit.getBlockPos();
                if (!hitBlockPositions.contains(blockPos)) {
                    hitBlockPositions.add(blockPos);
                    hitResults.add(new GunHitResult.Block(blockPos, blockHit.getDirection()));
                    totalPierces++;
                }
            }

            current = next;
        }

        if (shooter instanceof Player player) {
            var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;
            var recoil = gunAttackConfig.fireModeConfig().recoil();
            player.turn(baseRecoilX * 2, -recoil * 2);
        }

        Services.CLIENT_NETWORKING.sendToServer(new C2SGunHitResultsPayload(hitResults));

        return GunShootResult.SHOT;
    }
}
