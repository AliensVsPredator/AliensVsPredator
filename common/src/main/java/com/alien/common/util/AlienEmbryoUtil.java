package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.model.alien.Host;
import net.minecraft.sounds.SoundSource;
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

import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.key.AVPDamageTypeKeys;

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

        if (hostEntity.level().getDifficulty() == Difficulty.PEACEFUL) {
            host.removeEmbryo();
            return;
        }

        host.incrementEmbryoGrowthTimeInTicks();

        // TODO: Use data pack values here.
        var burstTimeInTicks = TimeUnit.MINUTES.toSeconds(5) * 20;
        if (host.getEmbryoGrowthTimeInTicks() <= burstTimeInTicks) {

            if (hostEntity instanceof Player player) {
                // TODO: Use data pack values here.
                if (host.getEmbryoGrowthTimeInTicks() > TimeUnit.MINUTES.toSeconds(4) * 20 + 30 * 20) {
                    if (player.tickCount % 10 == 0) {
                        player.playNotifySound(AVPSoundEvents.EFFECT_HEARTBEAT_3.get(), SoundSource.MASTER, 1, 1);
                    }
                    // TODO: Use data pack values here.
                } else if (host.getEmbryoGrowthTimeInTicks() > TimeUnit.MINUTES.toSeconds(4) * 20) {
                    if (player.tickCount % 20 == 0) {
                        player.playNotifySound(AVPSoundEvents.EFFECT_HEARTBEAT_2.get(), SoundSource.MASTER, 0.75F, 1);
                    }
                    // TODO: Use data pack values here.
                } else if (host.getEmbryoGrowthTimeInTicks() > TimeUnit.MINUTES.toSeconds(3) * 20 + 30 * 20) {
                    if (player.tickCount % 30 == 0) {
                        player.playNotifySound(AVPSoundEvents.EFFECT_HEARTBEAT_1.get(), SoundSource.MASTER, 0.5F, 1);
                    }
                    // TODO: Use data pack values here.
                } else if (host.getEmbryoGrowthTimeInTicks() > TimeUnit.MINUTES.toSeconds(3) * 20) {
                    if (player.tickCount % 40 == 0) {
                        player.playNotifySound(AVPSoundEvents.EFFECT_HEARTBEAT_0.get(), SoundSource.MASTER, 0.25F, 1);
                    }
                }
            }

            if (host.getEmbryoGrowthTimeInTicks() >= burstTimeInTicks - 8 * 20) {
                if (hostEntity.tickCount % 10 == 0) {
                    hostEntity.level()
                        .playSound(null, hostEntity, AVPSoundEvents.EFFECT_BONE_CRUNCH.get(), SoundSource.HOSTILE, 0.2F, 1);
                    hostEntity.hurt(hostEntity.damageSources().source(AVPDamageTypeKeys.CHESTBURSTING), 0.01F);
                }
            }

            return;
        }

        var embryos = AlienEmbryoUtil.birthEmbryos(hostEntity);

        embryos.forEach(embryo -> {});

        hostEntity.level().playSound(null, hostEntity, AVPSoundEvents.ENTITY_CHESTBURSTER_BURST.get(), SoundSource.HOSTILE, 0.25F, 1);

        hostEntity.hurt(hostEntity.damageSources().source(AVPDamageTypeKeys.CHESTBURSTING), Float.MAX_VALUE);

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
