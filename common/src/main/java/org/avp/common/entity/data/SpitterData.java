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
import org.avp.common.entity.living.Spitter;
import org.avp.common.entity.data.loot_table.XenomorphLootTable;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class SpitterData extends EntityData<Spitter> {

    public static final SpitterData INSTANCE = new SpitterData();

    @Override
    protected Holder<EntityType<Spitter>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "spitter",
            0x010202,
            0x3CDC09,
            EntityType.Builder.of(Spitter::new, MobCategory.MONSTER)
                .sized(0.98F, 1.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPAttributeSupplier.builder()
            .add(Attributes.ATTACK_DAMAGE, 4)
            .add(Attributes.MAX_HEALTH, 30)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BASE_SPEED)
            .build());
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PRODUCES_RESIN
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEvents.INSTANCE.entityXenomorphAmbient,
                AVPSoundEvents.INSTANCE.entityXenomorphHurt,
                AVPSoundEvents.INSTANCE.entityXenomorphDeath
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.of(XenomorphLootTable.LOOT_TABLE);
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
                ($, accessor, spawnType, pos, randSrc) -> pos.getY() <= AVPConfig.Aliens.MAX_Y_LEVEL_FOR_SPITTER_SPAWNS &&
                    Monster.checkMonsterSpawnRules(getHolder().get(), accessor, spawnType, pos, randSrc)
            )
        );
    }
}
