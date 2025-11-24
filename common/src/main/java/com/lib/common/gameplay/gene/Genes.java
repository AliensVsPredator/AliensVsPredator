package com.lib.common.gameplay.gene;

import com.just.core.functional.function.Function2;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;

public class Genes {

    public static final AVPDeferredHolder<Gene> ACID_RESISTANCE = registerSimple(
        "acid_resistance",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -100, 100);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 1);
        }
    );

    // Controls how explosive xenomorph acid is. This results in boilers.
    public static final AVPDeferredHolder<Gene> ACID_VOLATILITY = registerSimple(
        "acid_volatility",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 100);
            case MULTIPLICATIVE -> Math.clamp(value, 0, 1);
        }
    );

    public static final AVPDeferredHolder<Gene> ACIDIC_BLOOD = registerSimple(
        "acidic_blood",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> value;
        }
    );

    public static final AVPDeferredHolder<Gene> ARMOR = registerAttribute(
        "armor",
        Attributes.ARMOR,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 24);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> ARMOR_TOUGHNESS = registerAttribute(
        "armor_toughness",
        Attributes.ARMOR_TOUGHNESS,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 24);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> ATTACK_DAMAGE = registerAttribute(
        "attack_damage",
        Attributes.ATTACK_DAMAGE,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -50, 50);
            case MULTIPLICATIVE -> Math.clamp(value, -0.9, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> BONUS_EMBRYO_COUNT = registerSimple(
        "bonus_embryo_count",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> Math.clamp(value, 0, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> BONUS_PARASITE_COUNT = registerEffect(
        "bonus_parasite_count",
        Genes::handleBonusParasiteCount,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> Math.clamp(value, 0, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> COLD_RESISTANCE = registerSimple(
        "cold_resistance",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -100, 100);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 1);
        }
    );

    public static final AVPDeferredHolder<Gene> FIRE_RESISTANCE = registerSimple(
        "fire_resistance",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -100, 100);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 1);
        }
    );

    public static final AVPDeferredHolder<Gene> GENETIC_INTEGRITY = registerSimple(
        "genetic_integrity",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -300, 100);
            case MULTIPLICATIVE -> Math.clamp(value, -3, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> INTELLIGENCE = registerSimple(
        "intelligence",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, -100, 100);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 1);
        }
    );

    public static final AVPDeferredHolder<Gene> KNOCKBACK_RESISTANCE = registerAttribute(
        "knockback_resistance",
        Attributes.KNOCKBACK_RESISTANCE,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 1);
            case MULTIPLICATIVE -> Math.clamp(value, -1, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> MAX_HEALTH = registerAttribute(
        "max_health",
        Attributes.MAX_HEALTH,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 200);
            case MULTIPLICATIVE -> Math.clamp(value, -0.9, 3);
        }
    );

    public static final AVPDeferredHolder<Gene> MOVE_SPEED = registerAttribute(
        "move_speed",
        Attributes.MOVEMENT_SPEED,
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE -> Math.clamp(value, 0, 0.6);
            case MULTIPLICATIVE -> Math.clamp(value, -0.9, 2.0);
        }
    );

    public static final AVPDeferredHolder<Gene> POISON = registerSimple(
        "poison",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> value;
        }
    );

    public static final AVPDeferredHolder<Gene> POISONOUS_BARBS = registerSimple(
        "poisonous_barbs",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> value;
        }
    );

    public static final AVPDeferredHolder<Gene> THORNS = registerSimple(
        "thorns",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> value;
        }
    );

    public static final AVPDeferredHolder<Gene> WARP = registerSimple(
        "warp",
        (value, geneOperationType) -> switch (geneOperationType) {
            case ADDITIVE, MULTIPLICATIVE -> Math.clamp(value, 0, 1.0);
        }
    );

    public static void initialize() {}

    private static AVPDeferredHolder<Gene> registerAttribute(
        String name,
        Holder<Attribute> attribute,
        Function2<Double, GeneOperationType, Double> transformer
    ) {
        return GeneRegistry.register(() -> new Gene.Attribute(AVPResources.location(name), attribute, transformer));
    }

    private static AVPDeferredHolder<Gene> registerEffect(
        String name,
        Consumer<LivingEntity> onChange,
        Function2<Double, GeneOperationType, Double> transformer
    ) {
        return GeneRegistry.register(() -> new Gene.Effect(AVPResources.location(name), onChange, transformer));
    }

    private static AVPDeferredHolder<Gene> registerSimple(String name, Function2<Double, GeneOperationType, Double> transformer) {
        return GeneRegistry.register(() -> new Gene.Simple(AVPResources.location(name), transformer));
    }

    // FIXME:
    private static void handleBonusParasiteCount(LivingEntity entity) {
        // if (!(entity instanceof Ovomorph ovomorph)) {
        // return;
        // }
        //
        // var scaleAttribute = ovomorph.getAttribute(Attributes.SCALE);
        //
        // if (scaleAttribute != null) {
        // var geneContainer = ((GeneCarrier) ovomorph).getOrCreateGeneManager().getGeneContainer();
        // var totalParasiteCount = geneContainer.getActiveGeneMap().getValue(Genes.BONUS_PARASITE_COUNT);
        // var modifier = new AttributeModifier(
        // Genes.BONUS_PARASITE_COUNT.get().id(),
        // totalParasiteCount / 2.0,
        // AttributeModifier.Operation.ADD_VALUE
        // );
        //
        // ovomorph.maxSpawnCount.set((byte) (1 + totalParasiteCount));
        // scaleAttribute.addOrReplacePermanentModifier(modifier);
        // }
    }
}
