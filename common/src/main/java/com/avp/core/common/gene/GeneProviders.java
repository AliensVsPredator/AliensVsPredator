package com.avp.core.common.gene;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.avp.core.common.entity.AVPEntityTypeTags;

public class GeneProviders {

    public static final Map<EntityType<?>, Map<GeneKey, Integer>> GENE_MAPS_BY_ENTITY_TYPE = Map.ofEntries(
        Map.entry(
            EntityType.CAMEL,
            Map.ofEntries(
                // TODO: Acid spitting
                Map.entry(GeneKeys.BONUS_RESIN_PRODUCTION, 15),
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, 5),
                Map.entry(GeneKeys.COLD_RESISTANCE, -5),
                Map.entry(GeneKeys.FIRE_RESISTANCE, 5),
                Map.entry(GeneKeys.GROWTH_SPEED, 5),
                Map.entry(GeneKeys.INTELLIGENCE, -5)
            )
        ),
        Map.entry(
            EntityType.COW,
            Map.ofEntries(
                Map.entry(GeneKeys.BONUS_RESIN_PRODUCTION, 10),
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, -5),
                Map.entry(GeneKeys.EGG_LAY_SPEED, 5),
                Map.entry(GeneKeys.EGG_RESIN_COST, 5),
                Map.entry(GeneKeys.GROWTH_SPEED, 10),
                Map.entry(GeneKeys.INTELLIGENCE, -10)
            )
        ),
        Map.entry(
            EntityType.DOLPHIN,
            Map.ofEntries(
                Map.entry(GeneKeys.BONUS_RESIN_PRODUCTION, -5),
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, -5),
                Map.entry(GeneKeys.EGG_RESIN_COST, -15),
                Map.entry(GeneKeys.GROWTH_SPEED, -10),
                Map.entry(GeneKeys.INTELLIGENCE, 10),
                Map.entry(GeneKeys.HATCH_SPEED, 15)
            )
        ),
        Map.entry(
            EntityType.DONKEY,
            Map.ofEntries(
                // TODO: Acid spitting
                Map.entry(GeneKeys.GROWTH_SPEED, 5),
                Map.entry(GeneKeys.INTELLIGENCE, -5),
                Map.entry(GeneKeys.HATCH_SPEED, 5)
            )
        ),
        Map.entry(
            EntityType.GOAT,
            Map.ofEntries(
                Map.entry(GeneKeys.BONUS_FACEHUGGER_COUNT, 5),
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, -5),
                Map.entry(GeneKeys.COLD_RESISTANCE, 10),
                Map.entry(GeneKeys.EGG_LAY_SPEED, 5),
                Map.entry(GeneKeys.GROWTH_SPEED, 5),
                Map.entry(GeneKeys.INTELLIGENCE, -10)
            )
        ),
        Map.entry(
            EntityType.HOGLIN,
            Map.ofEntries(
                Map.entry(GeneKeys.BONUS_FACEHUGGER_COUNT, 5),
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, 5),
                Map.entry(GeneKeys.EGG_LAY_SPEED, 10),
                Map.entry(GeneKeys.GROWTH_SPEED, 5),
                Map.entry(GeneKeys.INTELLIGENCE, -10),
                Map.entry(GeneKeys.HATCH_SPEED, 10)
            )
        ),
        Map.entry(
            EntityType.PIGLIN,
            Map.ofEntries(
                // TODO: Acid spitting
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, 5),
                Map.entry(GeneKeys.EGG_RESIN_COST, -5),
                Map.entry(GeneKeys.HATCH_SPEED, 10),
                Map.entry(GeneKeys.INTELLIGENCE, 5)
            )
        ),
        Map.entry(
            EntityType.PIGLIN_BRUTE,
            Map.ofEntries(
                // TODO: Acid spitting
                Map.entry(GeneKeys.BONUS_RESIN_RECYCLE, 5),
                Map.entry(GeneKeys.EGG_RESIN_COST, -5),
                Map.entry(GeneKeys.HATCH_SPEED, 10),
                Map.entry(GeneKeys.INTELLIGENCE, 5)
            )
        )
    );

    public static Map<GeneKey, Integer> computeInheritedGeneAdditiveValues(EntityType<? extends LivingEntity> entityType) {
        var map = new HashMap<GeneKey, Integer>();
        var supplier = DefaultAttributes.getSupplier(entityType);

        inherit(map, supplier, Attributes.ARMOR, GeneKeys.ARMOR);
        inherit(map, supplier, Attributes.ARMOR_TOUGHNESS, GeneKeys.ARMOR_TOUGHNESS);
        inherit(map, supplier, Attributes.ATTACK_DAMAGE, GeneKeys.ATTACK_DAMAGE);
        inherit(map, supplier, Attributes.MOVEMENT_SPEED, GeneKeys.MOVE_SPEED, value -> value * 100);

        if (entityType.is(AVPEntityTypeTags.ANIMALS)) {
            map.put(GeneKeys.BONUS_FACEHUGGER_COUNT, 10);
            map.put(GeneKeys.EGG_LAY_SPEED, 10);
            map.put(GeneKeys.GROWTH_SPEED, 10);
        }

        if (entityType.is(AVPEntityTypeTags.HUMANOIDS)) {
            map.put(GeneKeys.INTELLIGENCE, 10);
        }

        if (entityType.is(AVPEntityTypeTags.NETHER_CREATURES)) {
            map.put(GeneKeys.COLD_RESISTANCE, (int) Byte.MIN_VALUE);
            map.put(GeneKeys.FIRE_RESISTANCE, (int) Byte.MAX_VALUE);
        }

        return map;
    }

    private static void inherit(
        Map<GeneKey, Integer> geneMap,
        AttributeSupplier supplier,
        Holder<Attribute> attributeHolder,
        GeneKey geneKey
    ) {
        inherit(geneMap, supplier, attributeHolder, geneKey, UnaryOperator.identity());
    }

    private static void inherit(
        Map<GeneKey, Integer> geneMap,
        AttributeSupplier supplier,
        Holder<Attribute> attributeHolder,
        GeneKey geneKey,
        UnaryOperator<Integer> modifier
    ) {
        if (supplier.hasAttribute(attributeHolder)) {
            var value = modifier.apply((int) supplier.getValue(attributeHolder));
            geneMap.put(geneKey, value);
        }
    }

    private GeneProviders() {
        throw new UnsupportedOperationException();
    }
}
