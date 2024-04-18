package org.avp.common.service;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface CreativeModeTabService {

    Holder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier);

    void register(Holder<CreativeModeTab> holder);
}
