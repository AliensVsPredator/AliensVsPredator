package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.service.CreativeModeTabRegistry;
import org.avp.neoforge.util.ForgeGameObject;

/**
 * @author Boston Vanseghi
 */
public class NeoForgeCreativeModeTabRegistry implements CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        AVPConstants.MOD_ID
    );

    @Override
    public GameObject<CreativeModeTab> register(String registryName, Supplier<CreativeModeTab> supplier) {
        return new ForgeGameObject<>(CREATIVE_MODE_TABS, registryName, supplier);
    }
}
