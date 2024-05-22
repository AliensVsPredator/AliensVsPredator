package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.attribute.AVPEntityAttributesBindingRegistry;
import org.avp.common.entity.living.Ultramorph;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class UltramorphData extends EntityData<Ultramorph> {

    public static final UltramorphData INSTANCE = new UltramorphData();

    @Override
    protected Holder<EntityType<Ultramorph>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "ultramorph",
            0x3E474E,
            0x696E76,
            EntityType.Builder.of(Ultramorph::new, MobCategory.MONSTER)
                .sized(0.98F, 1.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPEntityAttributesBindingRegistry.builder()
            .add(Attributes.ATTACK_DAMAGE, 5.5)
            .add(Attributes.MAX_HEALTH, 230)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.ULTRAMORPH_SPEED)
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
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }
}
