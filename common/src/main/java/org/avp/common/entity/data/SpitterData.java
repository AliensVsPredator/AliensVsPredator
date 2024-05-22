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
import org.avp.common.entity.living.Spitter;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class SpitterData extends EntityData<Spitter> {

    public static final SpitterData INSTANCE = new SpitterData();

    private static final Holder<EntityType<Spitter>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "spitter",
        0x010202,
        0x3CDC09,
        EntityType.Builder.of(Spitter::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.MAX_HEALTH, 30)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BASE_SPEED)
        .build();

    private static final List<TagKey<EntityType<?>>> TAGS = List.of(
        AVPEntityTypeTags.ACID_BLEEDERS,
        AVPEntityTypeTags.ALIENS,
        AVPEntityTypeTags.HIVE_ALIENS,
        AVPEntityTypeTags.MONSTERS,
        AVPEntityTypeTags.PRODUCES_RESIN
    );

    @Override
    public Holder<EntityType<Spitter>> getHolder() {
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
