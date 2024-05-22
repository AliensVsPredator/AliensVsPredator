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
import org.avp.common.entity.living.Dracomorph;
import org.avp.common.entity.data.loot_table.QueenLootTable;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class DracomorphData extends EntityData<Dracomorph> {

    public static final DracomorphData INSTANCE = new DracomorphData();

    @Override
    protected Holder<EntityType<Dracomorph>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "dracomorph",
            0x212121,
            0x535353,
            EntityType.Builder.of(Dracomorph::new, MobCategory.MONSTER)
                .sized(1.98F, 2.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPAttributeSupplier.builder()
            .add(Attributes.ATTACK_DAMAGE, 8)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
            .add(Attributes.MAX_HEALTH, 400)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRACOMORPH_SPEED)
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
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.of(QueenLootTable.LOOT_TABLE);
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.empty();
    }
}
