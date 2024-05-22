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
import org.avp.common.entity.living.DeaconAdultEngineer;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class DeaconAdultEngineerData extends EntityData<DeaconAdultEngineer> {

    public static final DeaconAdultEngineerData INSTANCE = new DeaconAdultEngineerData();

    private static final Holder<EntityType<DeaconAdultEngineer>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
        "deacon_adult_engineer",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdultEngineer::new, MobCategory.MONSTER)
            .sized(0.98F, 2.98F)
    );

    private static final AttributeSupplier ATTRIBUTE_SUPPLIER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.MAX_HEALTH, 150)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DEACON_ADULT_ENGINEER_SPEED)
        .build();

    private static final List<TagKey<EntityType<?>>> TAGS = List.of(
        AVPEntityTypeTags.ACID_BLEEDERS,
        AVPEntityTypeTags.ALIENS,
        AVPEntityTypeTags.MONSTERS
    );

    @Override
    public Holder<EntityType<DeaconAdultEngineer>> getHolder() {
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
