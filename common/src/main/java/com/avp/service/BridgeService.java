package com.avp.service;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import com.avp.common.item.gun.GunConfig;

public interface BridgeService {

    Supplier<Item> createGunSupplier(GunConfig gunConfig);

    Supplier<Item> createOldPainlessSupplier();

    <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColour,
        int secondaryEggColour,
        Item.Properties itemProperties
    );
}
