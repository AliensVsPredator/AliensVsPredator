package org.avp.neoforge.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.Holder;

import java.util.function.Supplier;

public class NeoForgeBlockEntityHolder<T extends BlockEntity> extends Holder<BlockEntityType<T>> {

    public NeoForgeBlockEntityHolder(DeferredRegister<BlockEntityType<?>> deferredRegister, String registryName, Supplier<BlockEntityType<T>> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
