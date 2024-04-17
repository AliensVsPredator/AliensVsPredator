package org.avp.common.service;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface CreativeModeTabRegistry {

    Holder<CreativeModeTab> register(String registryName, Supplier<CreativeModeTab> supplier);
}
