package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.model.alien.Host;
import com.alien.common.registry.GeneBonusDataRegistry;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmbryoUtil {

    public static void runEmbryoRoutines(LivingEntity hostEntity) {
        var host = (Host) hostEntity;
        var geneCarrier = (GeneCarrier) hostEntity;
        var level = hostEntity.level();

        if (level.isClientSide) {
            return;
        }

        if (hostEntity instanceof Player player && (player.isCreative() || player.isSpectator() || player.isInvulnerable())) {
            host.removeEmbryo();
            host.setEmbryoGrowthTimeInTicks(0);
            // TODO: We don't want to clear this, this is the host's genes.
            // TODO: Give the embryo its own gene manager.
            geneCarrier.setGeneManager(null);
            return;
        }

        if (host.getEmbryoType() != null) {
            tickEmbryoGrowth(hostEntity);
        } else {
            host.setEmbryoGrowthTimeInTicks(0);
        }
    }

    private static void tickEmbryoGrowth(LivingEntity hostEntity) {
        var host = (Host) hostEntity;
        host.incrementEmbryoGrowthTimeInTicks();

        // TODO: Use data pack values here.
        if (host.getEmbryoGrowthTimeInTicks() <= TimeUnit.MINUTES.toSeconds(5) * 20) {
            return;
        }

        if (hostEntity.level().getDifficulty() != Difficulty.PEACEFUL) {
            birthEmbryos(hostEntity);
            hostEntity.kill();
        }

        // Remove the embryo no matter what.
        host.removeEmbryo();
        // Reset the embryo growth time (in ticks) no matter what.
        host.setEmbryoGrowthTimeInTicks(0);
    }

    public static List<Entity> birthEmbryos(LivingEntity hostEntity) {
        var host = (Host) hostEntity;
        var embryoList = new ArrayList<Entity>();

        // 1 added here to guarantee 1 birth by default.
        var birthBonus = 1 + Math.clamp(
            host.getOrCreateGeneManager().getActiveGeneValue(Genes.BONUS_EMBRYO_COUNT, GeneOperationType.ADDITIVE),
            0.0,
            3.0
        );
        var baseOffspring = (int) birthBonus;
        var fractionalChance = birthBonus - baseOffspring;

        // Guaranteed births based on whole number values.
        for (int i = 0; i < baseOffspring; i++) {
            var embryo = birthEmbryo(hostEntity);

            if (embryo != null) {
                embryoList.add(embryo);
            }
        }

        // Probabilistic birth based on fractional values.
        if (hostEntity.getRandom().nextDouble() < fractionalChance) {
            var embryo = birthEmbryo(hostEntity);
            embryoList.add(embryo);
        }

        return embryoList;
    }

    public static @Nullable Entity birthEmbryo(@NotNull LivingEntity hostEntity) {
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
            applyGenesToEmbryo(hostEntity, alien);
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

    private static void applyGenesToEmbryo(@NotNull LivingEntity hostEntity, Alien embryo) {
        var host = (Host) hostEntity;
        var alienGeneManager = embryo.getGeneManager();

        // Transfer genes.
        host.getOrCreateGeneManager().transfer(alienGeneManager, true);

        // Transfer genes from host to embryo.
        var hostType = hostEntity.getType();
        var hostSpecificBonusGenesMap = GeneBonusDataRegistry.getOrDefault(hostType);
        hostSpecificBonusGenesMap.forEach(alienGeneManager::addActiveGene);
    }
}
