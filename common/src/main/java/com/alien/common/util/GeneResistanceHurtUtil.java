package com.alien.common.util;

import com.alien.common.model.alien.GeneCarrier;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

import com.avp.common.registry.AVPDeferredHolder;

public class GeneResistanceHurtUtil {

    public static float applyResistancesToDamage(
        GeneCarrier geneCarrier,
        DamageSource damageSource,
        float damage
    ) {
        var geneManager = geneCarrier.getOrCreateGeneManager();

        if (isFireDamageSource(damageSource)) {
            return applyGeneResistanceToDamage(
                geneManager,
                Genes.FIRE_RESISTANCE,
                damageSource,
                damage
            );
        }

        if (damageSource.is(DamageTypes.FREEZE)) {
            return applyGeneResistanceToDamage(
                geneManager,
                Genes.COLD_RESISTANCE,
                damageSource,
                damage
            );
        }

        return damage;
    }

    private static float applyGeneResistanceToDamage(
        GeneManager geneManager,
        AVPDeferredHolder<Gene> gene,
        DamageSource damageSource,
        float damage
    ) {
        // Percent reduction value.
        var percentageResistance = geneManager.getActiveGeneValue(gene, GeneOperationType.MULTIPLICATIVE);
        // Damage reduction value.
        var damageResistance = geneManager.getActiveGeneValue(gene, GeneOperationType.ADDITIVE);
        var modifiedDamage = Math.max(damage - (percentageResistance * damage) - damageResistance, 0);

        return (float) modifiedDamage;
    }

    private static boolean isFireDamageSource(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_FIRE) ||
            damageSource.is(DamageTypes.ON_FIRE) ||
            damageSource.is(DamageTypes.CAMPFIRE) ||
            damageSource.is(DamageTypes.HOT_FLOOR) ||
            damageSource.is(DamageTypes.LAVA);
    }
}
