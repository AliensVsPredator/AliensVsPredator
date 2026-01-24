package com.blib.internal.common.registry.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import com.blib.api.BLibAPI;
import com.blib.api.common.item.v1.DefaultDispenseSpawnEggItemBehavior;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.loader.ModLoaderType;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;

@ApiStatus.Internal
public class BLibItemRegistry extends BLibRegistry<Item> {

    public BLibItemRegistry(BLibMod mod) {
        super(mod, BuiltInRegistries.ITEM);
    }

    @Override
    public <U extends Item> BLibHolder<U> createHolder(String path, Supplier<U> valueSupplier) {
        var supplier = valueSupplier;

        if (BLibAPI.getModLoaderType() == ModLoaderType.FABRIC) {
            supplier = injectSpawnEggItemDispenserBehaviorRegistration(valueSupplier);
        }

        return super.createHolder(path, supplier);
    }

    private <U extends Item> @NotNull Supplier<U> injectSpawnEggItemDispenserBehaviorRegistration(Supplier<U> valueSupplier) {
        return () -> {
            var item = valueSupplier.get();

            if (item instanceof SpawnEggItem spawnEggItem) {
                DispenserBlock.registerBehavior(spawnEggItem, DefaultDispenseSpawnEggItemBehavior.INSTANCE);
            }

            return item;
        };
    }
}
