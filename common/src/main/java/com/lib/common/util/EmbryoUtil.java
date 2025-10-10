package com.lib.common.util;

import com.lib.common.gameplay.entity.manager.GeneContainer;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneCarrier;
import com.lib.common.registry.GeneBonusDataRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EmbryoUtil {

    public static List<Entity> birthEmbryos(
        LivingEntity parentEntity,
        GeneContainer parentGeneContainer,
        Function<LivingEntity, @Nullable Entity> embryoFactory,
        int baseBirthCount
    ) {
        var embryoList = new ArrayList<Entity>();

        // 1 added here to guarantee 1 birth by default.
        var birthBonus = baseBirthCount + parentGeneContainer.getActiveGeneMap()
            .getValue(Genes.BONUS_EMBRYO_COUNT);
        var baseOffspring = (int) birthBonus;
        var fractionalChance = birthBonus - baseOffspring;

        // Guaranteed births based on whole number values.
        for (int i = 0; i < baseOffspring; i++) {
            var embryo = embryoFactory.apply(parentEntity);

            if (embryo != null) {
                embryoList.add(embryo);
            }
        }

        // Probabilistic birth based on fractional values.
        if (parentEntity.getRandom().nextDouble() < fractionalChance) {
            var embryo = embryoFactory.apply(parentEntity);

            if (embryo != null) {
                embryoList.add(embryo);
            }
        }

        return embryoList;
    }

    public static void applyGenesToEmbryo(
        EntityType<?> parentType,
        GeneContainer parentGeneContainer,
        GeneCarrier offspring,
        boolean addBonusGenes
    ) {
        var offspringGeneContainer = offspring.getOrCreateGeneManager().getGeneContainer();

        // Transfer genes.
        parentGeneContainer.transfer(offspringGeneContainer, true);

        if (addBonusGenes) {
            GeneBonusDataRegistry.getOrDefault(parentType)
                .forEach(offspringGeneContainer.getActiveGeneMap()::add);
        }
    }
}
