package com.blib.internal.registry.impl;

import com.blib.BLib;
import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.BLibRegistry;
import com.blib.common.gameplay.DefaultDispenseSpawnEggItemBehavior;
import com.blib.mod.loader.model.ModLoaderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BLibItemRegistry extends BLibRegistry<Item> {

    public BLibItemRegistry(BLibMod mod) {
        super(mod, BuiltInRegistries.ITEM);
    }

    @Override
    public <U extends Item> BLibHolder<U> createHolder(String path, Supplier<U> valueSupplier) {
        var supplier = valueSupplier;

        if (BLib.getModLoaderType() == ModLoaderType.FABRIC) {
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
