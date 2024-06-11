package org.avp.common.data.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.entity.EntityData;
import org.avp.common.game.entity.ThrownGrenade;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.api.data.entity.EntitySpawnData;
import org.avp.api.registry.entity.AVPSimpleDeferredEntityTypeRegistry;

import java.util.List;
import java.util.Optional;

public class GrenadeData extends EntityData<ThrownGrenade> {

    public static final GrenadeData INSTANCE = new GrenadeData();

    @Override
    protected BLHolder<EntityType<ThrownGrenade>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createHolder(
            "grenade_thrown",
            EntityType.Builder.<ThrownGrenade>of(ThrownGrenade::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
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
