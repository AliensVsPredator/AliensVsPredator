package org.avp.api.service;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;

public interface CreativeModeTabService {

    BLHolder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier);

    void register(BLHolder<CreativeModeTab> holder);
}
