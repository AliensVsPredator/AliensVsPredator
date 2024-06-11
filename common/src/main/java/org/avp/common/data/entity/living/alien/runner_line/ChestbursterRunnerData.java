package org.avp.common.data.entity.living.alien.runner_line;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.data.entity.AttributeSupplierUtil;
import org.avp.api.data.entity.EntityData;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.api.data.entity.EntitySpawnData;
import org.avp.api.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.alien.runner_line.ChestbursterRunner;
import org.avp.common.game.sound.AVPSoundEventRegistry;

import java.util.List;
import java.util.Optional;

public class ChestbursterRunnerData extends EntityData<ChestbursterRunner> {

    public static final ChestbursterRunnerData INSTANCE = new ChestbursterRunnerData();

    @Override
    protected BLHolder<EntityType<ChestbursterRunner>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "chestburster_runner",
            0xD8B877,
            0xF7E2B4,
            EntityType.Builder.of(ChestbursterRunner::new, MobCategory.MONSTER)
                .sized(0.75F, 0.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 0.5)
                .add(Attributes.MAX_HEALTH, 14)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CHESTBURSTER_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                BLHolder.empty(),
                $ -> AVPSoundEventRegistry.INSTANCE.entityChestbursterRunner.hurt(),
                AVPSoundEventRegistry.INSTANCE.entityChestbursterRunner.death()
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.empty();
    }
}
