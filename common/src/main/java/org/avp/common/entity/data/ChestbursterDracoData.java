package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.data.attribute.AVPAttributeSupplier;
import org.avp.common.entity.data.constant.AVPEntitySpeedConstants;
import org.avp.common.entity.data.sound.EntitySoundData;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.entity.living.ChestbursterDraco;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPEntityTypeTags;

public class ChestbursterDracoData extends EntityData<ChestbursterDraco> {

    public static final ChestbursterDracoData INSTANCE = new ChestbursterDracoData();

    @Override
    protected Holder<EntityType<ChestbursterDraco>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "chestburster_draco",
            0xD8B877,
            0xF7E2B4,
            EntityType.Builder.of(ChestbursterDraco::new, MobCategory.MONSTER)
                .sized(0.75F, 0.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AVPAttributeSupplier.builder()
                .add(Attributes.ATTACK_DAMAGE, 0.5)
                .add(Attributes.MAX_HEALTH, 25)
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
                Holder.empty(),
                $ -> AVPSoundEvents.INSTANCE.entityChestbursterHurt,
                AVPSoundEvents.INSTANCE.entityChestbursterDeath
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
