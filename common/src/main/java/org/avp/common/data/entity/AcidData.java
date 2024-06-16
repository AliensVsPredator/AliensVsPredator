package org.avp.common.data.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

import org.avp.api.common.data.entity.EntityData;
import org.avp.api.common.data.entity.EntitySoundData;
import org.avp.api.common.data.entity.EntitySpawnData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.game.entity.Acid;
import org.avp.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;

public class AcidData extends EntityData<Acid> {

    public static final AcidData INSTANCE = new AcidData();

    @Override
    protected BLHolder<EntityType<Acid>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createHolder(
            "acid",
            EntityType.Builder.of(Acid::new, MobCategory.MISC)
                .sized(0.66F, 0.05F)
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
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.empty();
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
