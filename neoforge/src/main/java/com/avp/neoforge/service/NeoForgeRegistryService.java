package com.avp.neoforge.service;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, AVP.MOD_ID);

    private final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        AVP.MOD_ID
    );

    private final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        AVP.MOD_ID
    );

    private final DeferredRegister<DecoratedPotPattern> DECORATED_POT_PATTERN_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.DECORATED_POT_PATTERN,
        AVP.MOD_ID
    );

    private final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, AVP.MOD_ID);

    private final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, AVP.MOD_ID);

    private final DeferredRegister<MobEffect> MOB_EFFECT_REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, AVP.MOD_ID);

    private final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AVP.MOD_ID);

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        if (registry == BuiltInRegistries.BLOCK) {
            return (Supplier<T>) BLOCK_REGISTRY.register(id, (Supplier<Block>) supplier);
        } else if (registry == BuiltInRegistries.CREATIVE_MODE_TAB) {
            return (Supplier<T>) CREATIVE_MODE_TAB_REGISTRY.register(id, (Supplier<CreativeModeTab>) supplier);
        } else if (registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            return (Supplier<T>) DATA_COMPONENT_TYPE_REGISTRY.register(id, (Supplier<DataComponentType<?>>) supplier);
        } else if (registry == BuiltInRegistries.DECORATED_POT_PATTERN) {
            return (Supplier<T>) DECORATED_POT_PATTERN_REGISTRY.register(id, (Supplier<DecoratedPotPattern>) supplier);
        } else if (registry == BuiltInRegistries.ITEM) {
            return (Supplier<T>) ITEM_REGISTRY.register(id, (Supplier<Item>) supplier);
        } else if (registry == BuiltInRegistries.MENU) {
            return (Supplier<T>) MENU_TYPE_REGISTRY.register(id, (Supplier<MenuType<?>>) supplier);
        } else if (registry == BuiltInRegistries.MOB_EFFECT) {
            throw new IllegalArgumentException("Registering mob effects with 'register' is not supported. Use 'registerHolder', instead.");
        } else if (registry == BuiltInRegistries.SOUND_EVENT) {
            return (Supplier<T>) SOUND_EVENT_REGISTRY.register(id, (Supplier<SoundEvent>) supplier);
        }

        throw new IllegalArgumentException("Received registration attempt for an unhandled registry. Registry: " + registry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Holder<T> registerHolder(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        if (registry == BuiltInRegistries.MOB_EFFECT) {
            return (Holder<T>) MOB_EFFECT_REGISTRY.register(id, (Supplier<MobEffect>) supplier);
        }

        throw new IllegalArgumentException("Received holder registration attempt for an unhandled registry. Registry: " + registry);
    }

    public void initialize(IEventBus modBus) {
        BLOCK_REGISTRY.register(modBus);
        CREATIVE_MODE_TAB_REGISTRY.register(modBus);
        DATA_COMPONENT_TYPE_REGISTRY.register(modBus);
        DECORATED_POT_PATTERN_REGISTRY.register(modBus);
        ITEM_REGISTRY.register(modBus);
        MENU_TYPE_REGISTRY.register(modBus);
        MOB_EFFECT_REGISTRY.register(modBus);
        SOUND_EVENT_REGISTRY.register(modBus);
    }
}
