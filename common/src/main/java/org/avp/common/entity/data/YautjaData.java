package org.avp.common.entity.data;

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
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.data.attribute.AVPAttributeSupplier;
import org.avp.common.entity.data.constant.AVPEntitySpeedConstants;
import org.avp.common.entity.data.sound.EntitySoundData;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.entity.data.spawn.YautjaSpawning;
import org.avp.common.entity.living.Yautja;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPItems;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPEntityTypeTags;

public class YautjaData extends EntityData<Yautja> {

    public static final YautjaData INSTANCE = new YautjaData();

    @Override
    protected Holder<EntityType<Yautja>> createHolder() {
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
            AVPAttributeSupplier.builder()
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
            AVPEntityTypeTags.PREDATORS
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEvents.INSTANCE.entityYautjaAmbient,
                $ -> AVPSoundEvents.INSTANCE.entityYautjaHurt,
                AVPSoundEvents.INSTANCE.entityYautjaDeath
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
                            LootItem.lootTableItem(AVPItems.INSTANCE.veritaniumShard.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 3)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(EmptyLootItem.emptyItem().setWeight(36))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritanium.helmet().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritanium.body().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritanium.leggings().get()).setWeight(1))
                        .add(LootItem.lootTableItem(AVPArmorItems.INSTANCE.veritanium.boots().get()).setWeight(1))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.laserMine.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(
                            LootItem.lootTableItem(AVPItems.INSTANCE.shuriken.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1, 4)))
                        )
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.smartDisc.get()))
                        .add(LootItem.lootTableItem(AVPItems.INSTANCE.yautjaArtifact.get()))
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
