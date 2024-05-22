package org.avp.common.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.entity.SilencedEntityTypeBuilder;
import org.avp.common.item.AVPSpawnEggItems;
import org.avp.common.service.Services;

public class AVPSimpleDeferredEntityTypeRegistry extends AVPAbstractDeferredEntityTypeRegistry {
    public static final AVPSimpleDeferredEntityTypeRegistry INSTANCE = new AVPSimpleDeferredEntityTypeRegistry();

    @Override
    protected <T extends Entity> Holder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier) {
        var holder = Services.ENTITY_TYPE_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    public <T extends Entity> Holder<EntityType<T>> createHolder(String registryName, EntityType.Builder<T> builder) {
        var silencedBuilder = (SilencedEntityTypeBuilder) builder;
        return createHolder(registryName, silencedBuilder::buildWithoutDataFixerCheck);
    }

    public <T extends Mob> Holder<EntityType<T>> createMobHolder(
        String registryName,
        int backgroundColor,
        int highlightColor,
        EntityType.Builder<T> builder
    ) {
        var silencedBuilder = (SilencedEntityTypeBuilder) builder;
        var holder = createHolder(registryName, silencedBuilder::<T>buildWithoutDataFixerCheck);

        var spawnEggItemHolder = Services.ITEM_SERVICE.createHolder(
            "spawn_egg_" + registryName,
            () -> Services.ENTITY_TYPE_SERVICE.createSpawnEggItem(holder, backgroundColor, highlightColor, new Item.Properties())
        );
        AVPSpawnEggItems.INSTANCE.addHolder(spawnEggItemHolder);

        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void register() {
        entries.forEach(holder -> Services.ENTITY_TYPE_SERVICE.register((Holder<EntityType<?>>) holder));
    }
}
