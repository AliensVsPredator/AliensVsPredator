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
import org.avp.common.entity.living.Facehugger;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

public class FacehuggerData extends EntityData<Facehugger> {

    public static final FacehuggerData INSTANCE = new FacehuggerData();

    @Override
    protected Holder<EntityType<Facehugger>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "facehugger",
            0xE4D597,
            0xA55863,
            EntityType.Builder.of(Facehugger::new, MobCategory.MONSTER)
                .sized(0.75F, 0.3F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AVPAttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.FACEHUGGER_SPEED)
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
            AVPEntityTypeTags.PARASITES
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
