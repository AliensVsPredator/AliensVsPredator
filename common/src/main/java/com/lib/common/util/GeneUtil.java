package com.lib.common.util;

import com.alien.common.model.alien.GeneCarrier;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneDecayLevelType;

public class GeneUtil {

    public static GeneDecayLevelType getGeneDecayLevel(GeneCarrier geneCarrier) {
        var totalGeneticIntegrity = getTotalGeneticIntegrity(geneCarrier);

        if (totalGeneticIntegrity < -GeneDecayLevelType.FATAL.getMagnitude()) {
            // Infertility level.
            return GeneDecayLevelType.FATAL;
        } else if (totalGeneticIntegrity < -GeneDecayLevelType.VOLATILE.getMagnitude()) {
            // Aberrant boiler level.
            return GeneDecayLevelType.VOLATILE;
        } else if (totalGeneticIntegrity < -GeneDecayLevelType.UNSTABLE.getMagnitude()) {
            // Aberrant level.
            return GeneDecayLevelType.UNSTABLE;
        }

        // If genetic integrity is above 0, then there's no way this xeno can become aberrant.
        return GeneDecayLevelType.STABLE;
    }

    public static double getTotalGeneticIntegrity(GeneCarrier geneCarrier) {
        var geneManager = geneCarrier.getOrCreateGeneManager();
        var additiveGeneticIntegrity = geneManager.getActiveGeneValue(Genes.GENETIC_INTEGRITY, GeneOperationType.ADDITIVE);
        var multiplicativeGeneticIntegrity = geneManager.getActiveGeneValue(Genes.GENETIC_INTEGRITY, GeneOperationType.MULTIPLICATIVE);
        // TODO: Maybe don't add multiplicative here
        return additiveGeneticIntegrity + multiplicativeGeneticIntegrity;
    }

}
