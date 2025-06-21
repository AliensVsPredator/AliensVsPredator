package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.model.alien.Host;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlienEmbryoUtil {

    public static void runAlienEmbryoRoutines(LivingEntity hostEntity) {
        var host = (Host) hostEntity;
        var level = hostEntity.level();

        if (level.isClientSide) {
            return;
        }

        if (hostEntity instanceof Player player && (player.isCreative() || player.isSpectator() || player.isInvulnerable())) {
            host.removeEmbryo();
            return;
        }

        if (host.getEmbryoType() != null) {
            tickAlienEmbryoGrowth(hostEntity);
        } else {
            host.removeEmbryo();
        }
    }

    private static void tickAlienEmbryoGrowth(LivingEntity hostEntity) {
        var host = (Host) hostEntity;
        host.incrementEmbryoGrowthTimeInTicks();

        // TODO: Use data pack values here.
        if (host.getEmbryoGrowthTimeInTicks() <= TimeUnit.MINUTES.toSeconds(5) * 20) {
            return;
        }

        if (hostEntity.level().getDifficulty() != Difficulty.PEACEFUL) {
            var embryos = AlienEmbryoUtil.birthEmbryos(hostEntity);

            embryos.forEach(embryo -> {});

            hostEntity.kill();
        }

        // Remove the embryo no matter what.
        host.removeEmbryo();
    }

    public static List<Entity> birthEmbryos(LivingEntity parentEntity) {
        return EmbryoUtil.birthEmbryos(
            parentEntity,
            ((Host) parentEntity).getOrCreateParasiteGeneContainer(),
            AlienEmbryoUtil::alienEmbryoFactory,
            1
        );
    }

    public static @Nullable Entity alienEmbryoFactory(@NotNull LivingEntity hostEntity) {
        var level = hostEntity.level();
        var host = (Host) hostEntity;
        var embryoType = host.getEmbryoType();

        if (embryoType == null) {
            return null;
        }

        var embryo = embryoType.create(level);

        if (embryo == null) {
            return null;
        }

        if (embryo instanceof Mob mob) {
            mob.setPersistenceRequired();
        }

        if (embryo instanceof Alien alien) {
            EmbryoUtil.applyGenesToEmbryo(
                hostEntity.getType(),
                host.getOrCreateParasiteGeneContainer(),
                (GeneCarrier) alien,
                true
            );
            alien.setHostType(hostEntity.getType());
        }

        embryo.moveTo(hostEntity.position(), hostEntity.getYRot(), hostEntity.getXRot());
        embryo.setYRot(hostEntity.getYRot());
        embryo.setXRot(hostEntity.getXRot());

        if (embryo instanceof LivingEntity livingEmbryo) {
            // TODO: The genes are assigned once here, but if they're removed they don't appear on the embryo again.
            // Copies effects from previous entity to the next
            for (var effect : hostEntity.getActiveEffects()) {
                livingEmbryo.addEffect(new MobEffectInstance(effect.getEffect(), Integer.MAX_VALUE, effect.getAmplifier(), false, false));
            }
        }

        level.addFreshEntity(embryo);

        return embryo;
    }
}
