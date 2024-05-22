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
import org.avp.common.entity.living.Belugamorph;
import org.avp.common.entity.data.spawn.EntitySpawnData;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class BelugamorphData extends EntityData<Belugamorph> {

    public static final BelugamorphData INSTANCE = new BelugamorphData();

    @Override
    protected Holder<EntityType<Belugamorph>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "belugamorph",
            0xBCC9C6,
            0x646E65,
            EntityType.Builder.of(Belugamorph::new, MobCategory.MONSTER)
                .sized(0.75F, 2.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPAttributeSupplier.builder()
            .add(Attributes.ATTACK_DAMAGE, 4.5)
            .add(Attributes.MAX_HEALTH, 100)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BELUGAMORPH_SPEED)
            .build());
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.MONSTERS
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
