package org.avp.common.data.entity.living.yautja;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

import org.avp.api.data.entity.AttributeSupplierUtil;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.entity.EntityData;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.api.data.entity.EntitySpawnData;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.registry.item.AVPArmorItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.util.YautjaSpawning;
import org.avp.common.game.entity.living.yautja.Yautja;
import org.avp.common.registry.item.AVPToolItemRegistry;
import org.avp.api.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.data.tag.AVPEntityTypeTags;

public class YautjaData extends EntityData<Yautja> {

    public static final YautjaData INSTANCE = new YautjaData();

    @Override
    protected BLHolder<EntityType<Yautja>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "yautja",
            0xB9A86C,
            0x5A4728,
            EntityType.Builder.of(Yautja::new, MobCategory.MONSTER)
                .sized(0.98F, 2.48F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.MAX_HEALTH, 80)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.YAUTJA_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PREDATORS,
            AVPEntityTypeTags.MEDIUM_GUNS_IMMUNE
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEventRegistry.INSTANCE.entityYautja.ambient(),
                $ -> AVPSoundEventRegistry.INSTANCE.entityYautja.hurt(),
                AVPSoundEventRegistry.INSTANCE.entityYautja.death()
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.of(
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItemRegistry.INSTANCE.veritaniumShard.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 3)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(AVPArmorItemRegistry.INSTANCE.veritanium.helmet().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItemRegistry.INSTANCE.veritanium.body().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItemRegistry.INSTANCE.veritanium.leggings().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItemRegistry.INSTANCE.veritanium.boots().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPToolItemRegistry.INSTANCE.veritaniumAxe.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPToolItemRegistry.INSTANCE.veritaniumHoe.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPToolItemRegistry.INSTANCE.veritaniumPickaxe.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPToolItemRegistry.INSTANCE.veritaniumShovel.get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPToolItemRegistry.INSTANCE.veritaniumSword.get()).setWeight(1))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1F, 0.033F))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItemRegistry.INSTANCE.laserMine.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(
                            LootItem.lootTableItem(AVPItemRegistry.INSTANCE.shuriken.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(LootItem.lootTableItem(AVPItemRegistry.INSTANCE.smartDisc.get()))
                        .add(LootItem.lootTableItem(AVPItemRegistry.INSTANCE.yautjaArtifact.get()))
                )
        );
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.of(
            new EntitySpawnData<>(
                BiomeTags.IS_JUNGLE,
                30,
                1,
                1,
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                YautjaSpawning::checkPredatorSpawnRules
            )
        );
    }
}
