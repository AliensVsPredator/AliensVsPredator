package com.lib.common.util;

import com.lib.common.gameplay.entity.manager.GeneContainer;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneCarrier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

import com.avp.common.registry.AVPDeferredHolder;

public class GeneResistanceHurtUtil {

    public static float applyResistancesToDamage(
        GeneCarrier geneCarrier,
        DamageSource damageSource,
        float damage
    ) {
        var geneContainer = geneCarrier.getOrCreateGeneManager().getGeneContainer();

        // FIXME:
//        if (damageSource.is(AVPDamageTypeKeys.ACID)) {
//            return applyGeneResistanceToDamage(geneContainer, Genes.ACID_RESISTANCE, damage);
//        }

        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            return applyGeneResistanceToDamage(geneContainer, Genes.FIRE_RESISTANCE, damage);
        }

        if (damageSource.is(DamageTypes.FREEZE)) {
            return applyGeneResistanceToDamage(geneContainer, Genes.COLD_RESISTANCE, damage);
        }

        return damage;
    }

    private static float applyGeneResistanceToDamage(
        GeneContainer geneContainer,
        AVPDeferredHolder<Gene> gene,
        float damage
    ) {
        // Percent reduction value.
        var percentageResistance = geneContainer.getActiveGeneMap()
            .getValue(gene, GeneOperationType.MULTIPLICATIVE);
        // Damage reduction value.
        var damageResistance = geneContainer.getActiveGeneMap()
            .getValue(gene, GeneOperationType.ADDITIVE);
        var modifiedDamage = Math.max(damage - (percentageResistance * damage) - damageResistance, 0);

        return (float) modifiedDamage;
    }
}
