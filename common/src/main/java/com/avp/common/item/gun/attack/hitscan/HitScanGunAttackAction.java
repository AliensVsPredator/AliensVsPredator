package com.avp.common.item.gun.attack.hitscan;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

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

        var piercingLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.PIERCING);
        var hitEntityUUIDs = new HashSet<UUID>();
        var hitResults = new ArrayList<GunHitResult>();

        for (int i = 0; i < piercingLevel + 1; i++) {
            var hitResult = ProjectileUtil.getHitResultOnViewVector(
                shooter,
                entity -> !hitEntityUUIDs.contains(entity.getUUID()) && (entity.getType() == EntityType.END_CRYSTAL || AVPPredicates
                    .isLiving(
                        entity
                    )),
                gunAttackConfig.fireModeConfig().range()
            );

            if (shooter instanceof Player player) {
                var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;
                var recoil = gunAttackConfig.fireModeConfig().recoil();
                player.turn(baseRecoilX * 2, -recoil * 2);
            }

            switch (hitResult.getType()) {
                case BLOCK -> {
                    var blockHitResult = (BlockHitResult) hitResult;
                    var blockPos = blockHitResult.getBlockPos();
                    var direction = blockHitResult.getDirection();
                    hitResults.add(new GunHitResult.Block(blockPos, direction));
                }
                case ENTITY -> {
                    var hitEntity = ((EntityHitResult) hitResult).getEntity();
                    hitEntityUUIDs.add(hitEntity.getUUID());
                    hitResults.add(new GunHitResult.Entity(hitEntity.getUUID()));
                }
                case MISS -> { /* Do nothing */ }
            }
        }

        Services.CLIENT_NETWORKING.sendToServer(new C2SGunHitResultsPayload(hitResults));

        return GunShootResult.SHOT;
    }
}
