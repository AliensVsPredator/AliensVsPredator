package org.avp.api.common.registry;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.Services;

public class AVPDeferredBlockRegistry extends AVPDeferredRegistry<Block> {

    public static final AVPDeferredBlockRegistry INSTANCE = new AVPDeferredBlockRegistry();

    @Override
    public BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = Services.BLOCK_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    public BLHolder<Block> tempCreateHolder(String registryName, Supplier<Block> supplier) {
        return Services.BLOCK_SERVICE.createHolder(registryName, supplier);
    }

    @Override
    public final void register() {
        getValues().forEach(Services.BLOCK_SERVICE::register);
    }
}
