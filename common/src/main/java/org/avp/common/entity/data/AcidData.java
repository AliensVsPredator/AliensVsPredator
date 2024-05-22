package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.Acid;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

import java.util.List;
import java.util.Optional;

public class AcidData extends EntityData<Acid> {

    public static final AcidData INSTANCE = new AcidData();

    private static final Holder<EntityType<Acid>> HOLDER = AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createHolder(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.8F, 0.05F)
    );

    private static final List<TagKey<EntityType<?>>> TAGS = List.of();

    @Override
    public Holder<EntityType<Acid>> getHolder() {
        return HOLDER;
    }

    @Override
    public Optional<AttributeSupplier> getAttributeSupplier() {
        return Optional.empty();
    }

    @Override
    public List<TagKey<EntityType<?>>> getTags() {
        return TAGS;
    }
}
