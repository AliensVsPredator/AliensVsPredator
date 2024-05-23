package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.data.constant.AVPEntitySpeedConstants;
import org.avp.common.entity.data.attribute.AVPAttributeSupplier;
import org.avp.common.entity.data.sound.EntitySoundData;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.entity.living.TrilobiteBaby;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class TrilobiteBabyData extends EntityData<TrilobiteBaby> {

    public static final TrilobiteBabyData INSTANCE = new TrilobiteBabyData();

    @Override
    protected Holder<EntityType<TrilobiteBaby>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "trilobite_baby",
            0xCCC2A5,
            0x987379,
            EntityType.Builder.of(TrilobiteBaby::new, MobCategory.MONSTER)
                .sized(0.5F, 0.25F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPAttributeSupplier.builder()
            .add(Attributes.ATTACK_DAMAGE, 0.5)
            .add(Attributes.MAX_HEALTH, 16)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.TRILOBITE_BABY_SPEED)
            .build());
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
