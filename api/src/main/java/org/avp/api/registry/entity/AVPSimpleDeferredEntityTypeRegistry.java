package org.avp.api.registry.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.game.entity.SilencedEntityTypeBuilder;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.Services;

public class AVPSimpleDeferredEntityTypeRegistry extends AVPAbstractDeferredEntityTypeRegistry {

    public static final AVPSimpleDeferredEntityTypeRegistry INSTANCE = new AVPSimpleDeferredEntityTypeRegistry();

    @Override
    protected <T extends Entity> BLHolder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier) {
        var holder = Services.ENTITY_TYPE_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    public <T extends Entity> BLHolder<EntityType<T>> createHolder(String registryName, EntityType.Builder<T> builder) {
        var silencedBuilder = (SilencedEntityTypeBuilder) builder;
        return createHolder(registryName, silencedBuilder::buildWithoutDataFixerCheck);
    }

    public <T extends Mob> BLHolder<EntityType<T>> createMobHolder(
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
        // FIXME:
//        AVPSpawnEggItems.INSTANCE.addHolder(registryName, spawnEggItemHolder);

        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void register() {
        entries.forEach(holder -> Services.ENTITY_TYPE_SERVICE.register((BLHolder<EntityType<?>>) holder));
    }
}
