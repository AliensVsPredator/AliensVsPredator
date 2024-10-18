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
import org.avp.common.game.entity.Rocket;
import org.avp.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;

public class RocketData extends EntityData<Rocket> {

    public static final RocketData INSTANCE = new RocketData();

    @Override
    protected BLHolder<EntityType<Rocket>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createHolder(
            "rocket",
            EntityType.Builder.<Rocket>of(Rocket::new, MobCategory.MISC)
                .sized(0.1F, 0.1F)
                .clientTrackingRange(8)
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
