package org.avp.api.data.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

import org.avp.api.registry.holder.BLHolder;

public abstract class EntityData<T extends Entity> {

    private final BLHolder<EntityType<T>> holder = createHolder();

    private final AttributeSupplier attributeSupplier = createAttributeSupplier().orElse(null);

    private final List<TagKey<EntityType<?>>> tags = createTags();

    private final EntitySoundData soundData = createSoundData().orElse(null);

    private final LootTable.Builder lootTableBuilder = createLootTable().orElse(null);

    private final EntitySpawnData<?> spawnData = createSpawnData().orElse(null);

    protected abstract BLHolder<EntityType<T>> createHolder();

    protected abstract Optional<AttributeSupplier> createAttributeSupplier();

    protected abstract List<TagKey<EntityType<?>>> createTags();

    protected abstract Optional<EntitySoundData> createSoundData();

    protected abstract Optional<LootTable.Builder> createLootTable();

    protected abstract Optional<EntitySpawnData<?>> createSpawnData();

    public final BLHolder<EntityType<T>> getHolder() {
        return holder;
    }

    public final Optional<AttributeSupplier> getAttributeSupplier() {
        return Optional.ofNullable(attributeSupplier);
    }

    public final List<TagKey<EntityType<?>>> getTags() {
        return tags;
    }

    public final Optional<EntitySoundData> getSoundData() {
        return Optional.ofNullable(soundData);
    }

    public final Optional<LootTable.Builder> getLootTableBuilder() {
        return Optional.ofNullable(lootTableBuilder);
    }

    public final Optional<EntitySpawnData<?>> getSpawnData() {
        return Optional.ofNullable(spawnData);
    }

    public final String getRegistryName() {
        return getHolder().getResourceLocation().getPath();
    }
}
