package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.CreativeModeTabService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricCreativeModeTabService implements CreativeModeTabService {

    @Override
    public BLHolder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register(BLHolder<CreativeModeTab> holder) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, holder.getResourceLocation(), holder.get());
    }
}
