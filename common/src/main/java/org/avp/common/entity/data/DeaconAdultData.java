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
import org.avp.common.entity.living.DeaconAdult;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class DeaconAdultData extends EntityData<DeaconAdult> {

    public static final DeaconAdultData INSTANCE = new DeaconAdultData();

    private static final Holder<EntityType<DeaconAdult>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "deacon_adult",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdult::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 3)
        .add(Attributes.MAX_HEALTH, 120)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DEACON_ADULT_SPEED)
        .build();

    private static final List<TagKey<EntityType<?>>> TAGS = List.of(
        AVPEntityTypeTags.ACID_BLEEDERS,
        AVPEntityTypeTags.ALIENS,
        AVPEntityTypeTags.MONSTERS
    );

    @Override
    public Holder<EntityType<DeaconAdult>> getHolder() {
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
