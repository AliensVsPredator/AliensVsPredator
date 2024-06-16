package org.avp.common.data.entity.living.engineer;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

import org.avp.api.common.data.entity.AttributeSupplierUtil;
import org.avp.api.common.data.entity.EntityData;
import org.avp.api.common.data.entity.EntitySoundData;
import org.avp.api.common.data.entity.EntitySpawnData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.engineer.Engineer;
import org.avp.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;

public class EngineerData extends EntityData<Engineer> {

    public static final EngineerData INSTANCE = new EngineerData();

    @Override
    protected BLHolder<EntityType<Engineer>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "engineer",
            0xB8B1B6,
            0x99AFBD,
            EntityType.Builder.of(Engineer::new, MobCategory.MONSTER)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.MAX_HEALTH, 160)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.ENGINEER_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ENGINEERS,
            AVPEntityTypeTags.SMALL_GUNS_IMMUNE
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
