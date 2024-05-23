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
import org.avp.common.entity.living.FacehuggerRoyal;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

public class FacehuggerRoyalData extends EntityData<FacehuggerRoyal> {

    public static final FacehuggerRoyalData INSTANCE = new FacehuggerRoyalData();

    @Override
    protected Holder<EntityType<FacehuggerRoyal>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "facehugger_royal",
            0x81785E,
            0x583A3A,
            EntityType.Builder.of(FacehuggerRoyal::new, MobCategory.MONSTER)
                .sized(0.75F, 0.3F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AVPAttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 14)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.FACEHUGGER_ROYAL_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PARASITES,
            AVPEntityTypeTags.ROYAL_ALIENS
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.empty();
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
