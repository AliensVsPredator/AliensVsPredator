package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.attribute.AVPEntityAttributesBindingRegistry;
import org.avp.common.entity.living.Queen;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class QueenData extends EntityData<Queen> {

    public static final QueenData INSTANCE = new QueenData();

    private static final Holder<EntityType<Queen>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "queen",
        0x010202,
        0x363534,
        EntityType.Builder.of(Queen::new, MobCategory.MONSTER)
            .sized(1.98F, 3.98F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 24)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 300)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.QUEEN_SPEED)
        .build();

    private static final List<TagKey<EntityType<?>>> TAGS = List.of(
        AVPEntityTypeTags.ACID_BLEEDERS,
        AVPEntityTypeTags.ALIENS,
        AVPEntityTypeTags.HIVE_ALIENS,
        AVPEntityTypeTags.MONSTERS,
        AVPEntityTypeTags.PRODUCES_RESIN,
        AVPEntityTypeTags.ROYAL_ALIENS
    );

    @Override
    public Holder<EntityType<Queen>> getHolder() {
        return HOLDER;
    }

    @Override
    public Optional<AttributeSupplier> getAttributeSupplier() {
        return Optional.of(ATTRIBUTE_SUPPLIER);
    }

    @Override
    public List<TagKey<EntityType<?>>> getTags() {
        return TAGS;
    }
}
