package com.blib.api.common.mod.v1.model.access;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibInternalServices;

public class BLibFactoryAccess {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibFactoryAccess(BLibMod mod) {
        this.mod = mod;
    }

    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityTypeSupplier,
        int primaryColor,
        int secondaryColor,
        Item.Properties properties
    ) {
        return BLibInternalServices.FACTORY.createSpawnEggSupplier(entityTypeSupplier, primaryColor, secondaryColor, properties);
    }
}
