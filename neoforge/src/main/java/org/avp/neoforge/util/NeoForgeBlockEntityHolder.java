package org.avp.neoforge.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.common.registry.holder.AVPHolder;

import java.util.function.Supplier;

public class NeoForgeBlockEntityHolder<T extends BlockEntity> extends AVPHolder<BlockEntityType<T>> {

    public NeoForgeBlockEntityHolder(DeferredRegister<BlockEntityType<?>> deferredRegister, String registryName, Supplier<BlockEntityType<T>> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
