package com.avp.fabric.data.lang.en_us.provider;

import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.Genes;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnUsGeneProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addGene(builder, Genes.ACID_VOLATILITY, "Acid Volatility");
        addGene(builder, Genes.ARMOR, "Armor");
        addGene(builder, Genes.ARMOR_TOUGHNESS, "Armor Toughness");
        addGene(builder, Genes.ATTACK_DAMAGE, "Attack Damage");
        addGene(builder, Genes.BONUS_EMBRYO_COUNT, "Bonus Embryo Count");
        addGene(builder, Genes.BONUS_PARASITE_COUNT, "Bonus Parasite Count");
        addGene(builder, Genes.COLD_RESISTANCE, "Cold Resistance");
        addGene(builder, Genes.FIRE_RESISTANCE, "Fire Resistance");
        addGene(builder, Genes.GENETIC_INTEGRITY, "Genetic Integrity");
        addGene(builder, Genes.INTELLIGENCE, "Intelligence");
        addGene(builder, Genes.KNOCKBACK_RESISTANCE, "Knockback Resistance");
        addGene(builder, Genes.MAX_HEALTH, "Max Health");
        addGene(builder, Genes.MOVE_SPEED, "Move Speed");
    };

    private static void addGene(
        FabricLanguageProvider.TranslationBuilder translationBuilder,
        Supplier<? extends Gene> geneSupplier,
        String value
    ) {
        addGene(translationBuilder, geneSupplier.get(), value);
    }

    private static void addGene(FabricLanguageProvider.TranslationBuilder translationBuilder, Gene gene, String value) {
        translationBuilder.add(gene.getTranslationKey(), value);
    }
}
