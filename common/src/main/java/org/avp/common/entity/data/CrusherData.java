package org.avp.common.entity.data;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.config.AVPConfig;
import org.avp.common.entity.data.constant.AVPEntitySpeedConstants;
import org.avp.common.entity.data.attribute.AVPAttributeSupplier;
import org.avp.common.entity.data.sound.EntitySoundData;
import org.avp.common.entity.living.Crusher;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class CrusherData extends EntityData<Crusher> {

    public static final CrusherData INSTANCE = new CrusherData();

    @Override
    protected Holder<EntityType<Crusher>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "crusher",
            0x2E2921,
            0x534A3B,
            EntityType.Builder.of(Crusher::new, MobCategory.MONSTER)
                .sized(1.48F, 2.48F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPAttributeSupplier.builder()
            .add(Attributes.ATTACK_DAMAGE, 12)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1)
            .add(Attributes.MAX_HEALTH, 90)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CRUSHER_SPEED)
            .build());
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PRODUCES_RESIN,
            AVPEntityTypeTags.ROYAL_ALIENS
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEvents.INSTANCE.entityPraetorianAmbient,
                AVPSoundEvents.INSTANCE.entityPraetorianHurt,
                AVPSoundEvents.INSTANCE.entityPraetorianDeath
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.of(
            new EntitySpawnData<>(
                BiomeTags.IS_OVERWORLD,
                10,
                1,
                1,
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ($, accessor, spawnType, pos, randSrc) -> pos.getY() <= AVPConfig.Aliens.MAX_Y_LEVEL_FOR_CRUSHER_SPAWNS &&
                    Monster.checkMonsterSpawnRules(getHolder().get(), accessor, spawnType, pos, randSrc)
            )
        );
    }
}
