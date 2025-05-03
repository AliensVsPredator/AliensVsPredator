package com.avp.neoforge.service;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

import com.avp.common.item.gun.GunConfig;
import com.avp.neoforge.common.item.NeoForgeGunItem;
import com.avp.neoforge.common.item.NeoForgeOldPainlessItem;
import com.avp.service.BridgeService;

public class NeoForgeBridgeService implements BridgeService {

    @Override
    public Supplier<Item> createGunSupplier(GunConfig gunConfig) {
        return () -> new NeoForgeGunItem(gunConfig);
    }

    @Override
    public Supplier<Item> createOldPainlessSupplier() {
        return NeoForgeOldPainlessItem::new;
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColour,
        int secondaryEggColour,
        Item.Properties itemProperties
    ) {
        return () -> new DeferredSpawnEggItem(entityType, primaryEggColour, secondaryEggColour, itemProperties);
    }
}
