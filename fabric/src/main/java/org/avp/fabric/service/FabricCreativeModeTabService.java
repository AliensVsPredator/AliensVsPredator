package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.CreativeModeTabService;

public class FabricCreativeModeTabService implements CreativeModeTabService {

    @Override
    public Holder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register(Holder<CreativeModeTab> holder) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, holder.getResourceLocation(), holder.get());
    }
}
