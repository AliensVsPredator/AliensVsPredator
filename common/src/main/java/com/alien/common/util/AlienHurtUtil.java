package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.function.BiFunction;

import com.avp.common.registry.AVPDeferredHolder;

public class AlienHurtUtil {

    public static boolean isHurt(
        Alien alien,
        DamageSource damageSource,
        float damage,
        BiFunction<DamageSource, Float, Boolean> superCall
    ) {
        if (isNonDamagingSource(damageSource)) {
            return false;
        }

        var geneManager = alien.getGeneManager();

        if (isFireDamageSource(damageSource)) {
            return hurtWithResistance(
                geneManager,
                Genes.FIRE_RESISTANCE,
                damageSource,
                damage,
                superCall
            );
        }

        if (damageSource.is(DamageTypes.FREEZE)) {
            return hurtWithResistance(
                geneManager,
                Genes.COLD_RESISTANCE,
                damageSource,
                damage,
                superCall
            );
        }

        return superCall.apply(damageSource, damage);
    }

    private static boolean hurtWithResistance(
        GeneManager geneManager,
        AVPDeferredHolder<Gene> gene,
        DamageSource damageSource,
        float damage,
        BiFunction<DamageSource, Float, Boolean> superCall
    ) {
        // Percent reduction value.
        var percentageResistance = geneManager.getActiveGeneValue(gene, GeneOperationType.MULTIPLICATIVE);
        // Damage reduction value.
        var damageResistance = geneManager.getActiveGeneValue(gene, GeneOperationType.ADDITIVE);
        var modifiedDamage = Math.max(damage - (percentageResistance * damage) - damageResistance, 0);

        return modifiedDamage > 0 && superCall.apply(damageSource, (float) modifiedDamage);
    }

    private static boolean isFireDamageSource(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_FIRE) ||
            damageSource.is(DamageTypes.ON_FIRE) ||
            damageSource.is(DamageTypes.CAMPFIRE) ||
            damageSource.is(DamageTypes.HOT_FLOOR) ||
            damageSource.is(DamageTypes.LAVA);
    }

    private static boolean isNonDamagingSource(DamageSource damageSource) {
        // Xenomorphs should not drown.
        return damageSource.is(DamageTypes.DROWN) ||
        // Xenomorphs should not freeze.
            damageSource.is(DamageTypes.FREEZE) ||
            // Xenomorphs should not suffocate in walls.
            damageSource.is(DamageTypes.IN_WALL);
    }
}
