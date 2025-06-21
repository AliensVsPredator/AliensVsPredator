package com.avp.fabric.data.gene_bonus_data;

import com.bvanseg.just.functional.tuple.Tuple2;
import com.human.common.registry.init.entity_type.HumanEntityTypes;
import com.lib.common.data.EntityTypePredicate;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneBonusData;
import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneModifier;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;

public class GeneBonusDataSubProvider extends GeneBonusDataProvider {

    private final List<EntityType<?>> touchedEntries;

    public GeneBonusDataSubProvider(FabricDataOutput output) {
        super(output);
        this.touchedEntries = new ArrayList<>();
    }

    @Override
    protected void generate() {
        var llamaGeneBonuses = List.of(
            new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.05)),
            new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
            new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.01))
        );

        add(
            EntityType.CAMEL,
            List.of(
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.05)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.1)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.02))
            )
        );
        add(
            EntityType.CREEPER,
            List.of(
                new Tuple2<>(Genes.ACID_VOLATILITY, new GeneModifier(GeneOperationType.ADDITIVE, 0.34)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 5.0))
            )
        );
        add(
            EntityType.COW,
            List.of(
                new Tuple2<>(Genes.BONUS_EMBRYO_COUNT, new GeneModifier(GeneOperationType.ADDITIVE, 0.25)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0))
            )
        );
        add(
            EntityType.DONKEY,
            List.of(
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.015))
            )
        );
        add(
            EntityType.EVOKER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 12.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.GOAT,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 1.0)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0))
            )
        );
        add(
            EntityType.HOGLIN,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 4.0)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.1)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.2)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0)),
                new Tuple2<>(Genes.KNOCKBACK_RESISTANCE, new GeneModifier(GeneOperationType.ADDITIVE, 0.1)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15))
            )
        );
        add(
            EntityType.HORSE,
            List.of(
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.025))
            )
        );
        add(
            EntityType.ILLUSIONER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 2.5)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(EntityType.LLAMA, llamaGeneBonuses);
        add(
            HumanEntityTypes.MARINE.get(),
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 1.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 30.0))
            )
        );
        add(
            EntityType.MOOSHROOM,
            List.of(
                new Tuple2<>(Genes.ARMOR, new GeneModifier(GeneOperationType.ADDITIVE, -0.25)),
                new Tuple2<>(Genes.ARMOR_TOUGHNESS, new GeneModifier(GeneOperationType.ADDITIVE, -0.25)),
                new Tuple2<>(Genes.GENETIC_INTEGRITY, new GeneModifier(GeneOperationType.ADDITIVE, -0.2)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0))
            )
        );
        add(
            EntityType.MULE,
            List.of(
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.015))
            )
        );
        add(
            EntityType.PANDA,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, -3.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, -0.005))
            )
        );
        add(
            EntityType.PIG,
            List.of(
                new Tuple2<>(Genes.BONUS_EMBRYO_COUNT, new GeneModifier(GeneOperationType.ADDITIVE, 1.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -15.0)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.1))
            )
        );
        add(
            EntityType.PIGLIN,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 4.0)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.075)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 10.0))
            )
        );
        add(
            EntityType.PIGLIN_BRUTE,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 6.5)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.075)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 5.0))
            )
        );
        add(
            EntityType.PILLAGER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 2.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.PLAYER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 1.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 30.0))
            )
        );
        add(
            EntityType.POLAR_BEAR,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 3.0)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.2)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.1))
            )
        );
        add(
            EntityType.RAVAGER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 6.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0)),
                new Tuple2<>(Genes.KNOCKBACK_RESISTANCE, new GeneModifier(GeneOperationType.ADDITIVE, 0.1)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.25)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, -0.005))
            )
        );
        add(
            EntityType.SHEEP,
            List.of(
                new Tuple2<>(Genes.BONUS_EMBRYO_COUNT, new GeneModifier(GeneOperationType.ADDITIVE, 0.4)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -10.0))
            )
        );
        add(
            EntityType.SNIFFER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, -3.0)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, -5.0)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.15)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, -0.01))
            )
        );
        add(
            EntityType.STRIDER,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, -2.0)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.34)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.34))
            )
        );
        add(EntityType.TRADER_LLAMA, llamaGeneBonuses);
        add(
            EntityType.TURTLE,
            List.of(
                new Tuple2<>(Genes.ARMOR, new GeneModifier(GeneOperationType.ADDITIVE, 4.0)),
                new Tuple2<>(Genes.ARMOR_TOUGHNESS, new GeneModifier(GeneOperationType.ADDITIVE, 2.0)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, -0.02))
            )
        );
        add(
            EntityType.VILLAGER,
            List.of(
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.VINDICATOR,
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 6.5)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.WANDERING_TRADER,
            List.of(
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.WITCH,
            List.of(
                new Tuple2<>(Genes.GENETIC_INTEGRITY, new GeneModifier(GeneOperationType.ADDITIVE, -0.2)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 15.0))
            )
        );
        add(
            EntityType.WOLF,
            List.of(
                new Tuple2<>(Genes.BONUS_EMBRYO_COUNT, new GeneModifier(GeneOperationType.ADDITIVE, 0.65)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.075)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, -0.1)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.01))
            )
        );
        add(
            PredatorEntityTypes.YAUTJA.get(),
            List.of(
                new Tuple2<>(Genes.ATTACK_DAMAGE, new GeneModifier(GeneOperationType.ADDITIVE, 7.5)),
                new Tuple2<>(Genes.COLD_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.05)),
                new Tuple2<>(Genes.FIRE_RESISTANCE, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.2)),
                new Tuple2<>(Genes.INTELLIGENCE, new GeneModifier(GeneOperationType.ADDITIVE, 30.0)),
                new Tuple2<>(Genes.KNOCKBACK_RESISTANCE, new GeneModifier(GeneOperationType.ADDITIVE, 0.15)),
                new Tuple2<>(Genes.MAX_HEALTH, new GeneModifier(GeneOperationType.MULTIPLICATIVE, 0.5)),
                new Tuple2<>(Genes.MOVE_SPEED, new GeneModifier(GeneOperationType.ADDITIVE, 0.005))
            )
        );

        // Non-hosts
        touchedEntries.add(EntityType.ALLAY);
        touchedEntries.add(EntityType.ARMADILLO);
        touchedEntries.add(EntityType.AXOLOTL);
        touchedEntries.add(EntityType.BAT);
        touchedEntries.add(EntityType.BEE);
        touchedEntries.add(EntityType.BLAZE);
        touchedEntries.add(EntityType.BOGGED);
        touchedEntries.add(EntityType.BREEZE);
        touchedEntries.add(EntityType.CAT);
        touchedEntries.add(EntityType.CAVE_SPIDER);
        touchedEntries.add(EntityType.CHICKEN);
        touchedEntries.add(EntityType.COD);
        touchedEntries.add(EntityType.CREEPER);
        touchedEntries.add(EntityType.DOLPHIN);
        touchedEntries.add(EntityType.DROWNED);
        touchedEntries.add(EntityType.ELDER_GUARDIAN);
        touchedEntries.add(EntityType.ENDER_DRAGON);
        touchedEntries.add(EntityType.ENDERMAN);
        touchedEntries.add(EntityType.ENDERMITE);
        touchedEntries.add(EntityType.FOX);
        touchedEntries.add(EntityType.FROG);
        touchedEntries.add(EntityType.GHAST);
        touchedEntries.add(EntityType.GIANT);
        touchedEntries.add(EntityType.GLOW_SQUID);
        touchedEntries.add(EntityType.GUARDIAN);
        touchedEntries.add(EntityType.HUSK);
        touchedEntries.add(EntityType.MAGMA_CUBE);
        touchedEntries.add(EntityType.OCELOT);
        touchedEntries.add(EntityType.PARROT);
        touchedEntries.add(EntityType.PHANTOM);
        touchedEntries.add(EntityType.PUFFERFISH);
        touchedEntries.add(EntityType.RABBIT);
        touchedEntries.add(EntityType.SALMON);
        touchedEntries.add(EntityType.SHULKER);
        touchedEntries.add(EntityType.SILVERFISH);
        touchedEntries.add(EntityType.SKELETON);
        touchedEntries.add(EntityType.SKELETON_HORSE);
        touchedEntries.add(EntityType.SLIME);
        touchedEntries.add(EntityType.SPIDER);
        touchedEntries.add(EntityType.SQUID);
        touchedEntries.add(EntityType.STRAY);
        touchedEntries.add(EntityType.STRIDER);
        touchedEntries.add(EntityType.TADPOLE);
        touchedEntries.add(EntityType.TROPICAL_FISH);
        touchedEntries.add(EntityType.TURTLE);
        touchedEntries.add(EntityType.VEX);
        touchedEntries.add(EntityType.WARDEN);
        touchedEntries.add(EntityType.WITHER);
        touchedEntries.add(EntityType.WITHER_SKELETON);
        touchedEntries.add(EntityType.ZOGLIN);
        touchedEntries.add(EntityType.ZOMBIE);
        touchedEntries.add(EntityType.ZOMBIE_HORSE);
        touchedEntries.add(EntityType.ZOMBIE_VILLAGER);
        touchedEntries.add(EntityType.ZOMBIFIED_PIGLIN);

        BuiltInRegistries.ENTITY_TYPE.stream()
            .filter(Predicate.not(touchedEntries::contains))
            .filter(entityType -> entityType.getCategory() != MobCategory.MISC && entityType.getCategory() != AVPEntityTypes.ALIEN_CATEGORY)
            .forEach(entityType -> AVP.LOGGER.warn("No gene bonuses provided for {}", BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
    }

    private void add(TagKey<EntityType<?>> entityTypeTagKey, List<Tuple2<AVPDeferredHolder<Gene>, GeneModifier>> geneBonusList) {
        add(
            entityTypeTagKey.location().getPath() + "_gene_bonuses",
            new GeneBonusData(
                new EntityTypePredicate.Tag(entityTypeTagKey),
                geneBonusList.stream()
                    .map(
                        tuple -> new GeneBonusDataEntry(
                            AVPResources.location(tuple.first().get().id()),
                            tuple.second().operation(),
                            tuple.second().value()
                        )
                    )
                    .toList()
            )
        );
    }

    private void add(EntityType<?> entityType, List<Tuple2<AVPDeferredHolder<Gene>, GeneModifier>> geneBonusList) {
        touchedEntries.add(entityType);
        add(
            BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "_gene_bonuses",
            new GeneBonusData(
                new EntityTypePredicate.Single(entityType),
                geneBonusList.stream()
                    .map(
                        tuple -> new GeneBonusDataEntry(
                            AVPResources.location(tuple.first().get().id()),
                            tuple.second().operation(),
                            tuple.second().value()
                        )
                    )
                    .toList()
            )
        );
    }
}
