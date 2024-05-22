package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.Acid;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

import java.util.List;
import java.util.Optional;

public class AcidData extends EntityData<Acid> {

    public static final AcidData INSTANCE = new AcidData();

    @Override
    protected Holder<EntityType<Acid>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createHolder(
            "acid",
            EntityType.Builder.of(Acid::new, MobCategory.MISC)
                .sized(0.8F, 0.05F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.empty();
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of();
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }
}
