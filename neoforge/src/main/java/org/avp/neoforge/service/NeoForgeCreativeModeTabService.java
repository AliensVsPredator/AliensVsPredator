package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPConstants;
import org.avp.api.service.CreativeModeTabService;
import org.avp.neoforge.util.NeoForgeHolder;

public class NeoForgeCreativeModeTabService implements CreativeModeTabService {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        AVPConstants.MOD_ID
    );

    @Override
    public BLHolder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier) {
        return new NeoForgeHolder<>(CREATIVE_MODE_TABS, registryName, supplier);
    }

    @Override
    public void register(BLHolder<CreativeModeTab> holder) { /* NO-OP FOR FORGE */ }
}
