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
import org.avp.common.entity.living.Dracomorph;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class DracomorphData extends EntityData<Dracomorph> {

    public static final DracomorphData INSTANCE = new DracomorphData();

    private static final Holder<EntityType<Dracomorph>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "dracomorph",
        0x212121,
        0x535353,
        EntityType.Builder.of(Dracomorph::new, MobCategory.MONSTER)
            .sized(1.98F, 2.98F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 8)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
        .add(Attributes.MAX_HEALTH, 400)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRACOMORPH_SPEED)
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
    public Holder<EntityType<Dracomorph>> getHolder() {
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
