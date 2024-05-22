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
import org.avp.common.entity.living.Yautja;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class YautjaData extends EntityData<Yautja> {

    public static final YautjaData INSTANCE = new YautjaData();

    private static final Holder<EntityType<Yautja>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "yautja",
        0xB9A86C,
        0x5A4728,
        EntityType.Builder.of(Yautja::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
        .add(Attributes.MAX_HEALTH, 80)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.YAUTJA_SPEED)
        .build();

    private static final List<TagKey<EntityType<?>>> TAGS = List.of(
        AVPEntityTypeTags.MONSTERS,
        AVPEntityTypeTags.PREDATORS
    );

    @Override
    public Holder<EntityType<Yautja>> getHolder() {
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
