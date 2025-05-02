package com.avp.neoforge.service;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.ARMOR_MATERIAL,
        AVP.MOD_ID
    );

    private final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, AVP.MOD_ID);

    private final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        AVP.MOD_ID
    );

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

    private final DeferredRegister<GameEvent> GAME_EVENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.GAME_EVENT, AVP.MOD_ID);

    private final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, AVP.MOD_ID);

    private final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, AVP.MOD_ID);

    private final DeferredRegister<MobEffect> MOB_EFFECT_REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, AVP.MOD_ID);

    private final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.RECIPE_SERIALIZER,
        AVP.MOD_ID
    );

    private final DeferredRegister<RecipeType<?>> RECIPE_TYPE_REGISTRY = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, AVP.MOD_ID);

    private final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AVP.MOD_ID);

    @Override
    @SuppressWarnings("unchecked")
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        if (registry == BuiltInRegistries.ARMOR_MATERIAL) {
            return adapt((DeferredHolder<T, T>) ARMOR_MATERIAL_REGISTRY.register(id, (Supplier<ArmorMaterial>) supplier));
        } else if (registry == BuiltInRegistries.BLOCK) {
            return adapt((DeferredHolder<T, T>) BLOCK_REGISTRY.register(id, (Supplier<Block>) supplier));
        } else if (registry == BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            return adapt((DeferredHolder<T, T>) BLOCK_ENTITY_TYPE_REGISTRY.register(id, (Supplier<BlockEntityType<?>>) supplier));
        } else if (registry == BuiltInRegistries.CREATIVE_MODE_TAB) {
            return adapt((DeferredHolder<T, T>) CREATIVE_MODE_TAB_REGISTRY.register(id, (Supplier<CreativeModeTab>) supplier));
        } else if (registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            return adapt((DeferredHolder<T, T>) DATA_COMPONENT_TYPE_REGISTRY.register(id, (Supplier<DataComponentType<?>>) supplier));
        } else if (registry == BuiltInRegistries.DECORATED_POT_PATTERN) {
            return adapt((DeferredHolder<T, T>) DECORATED_POT_PATTERN_REGISTRY.register(id, (Supplier<DecoratedPotPattern>) supplier));
        } else if (registry == BuiltInRegistries.GAME_EVENT) {
            return adapt((DeferredHolder<T, T>) GAME_EVENT_REGISTRY.register(id, (Supplier<GameEvent>) supplier));
        } else if (registry == BuiltInRegistries.ITEM) {
            return adapt((DeferredHolder<T, T>) ITEM_REGISTRY.register(id, (Supplier<Item>) supplier));
        } else if (registry == BuiltInRegistries.MENU) {
            return adapt((DeferredHolder<T, T>) MENU_TYPE_REGISTRY.register(id, (Supplier<MenuType<?>>) supplier));
        } else if (registry == BuiltInRegistries.MOB_EFFECT) {
            return adapt((DeferredHolder<T, T>) MOB_EFFECT_REGISTRY.register(id, (Supplier<MobEffect>) supplier));
        } else if (registry == BuiltInRegistries.RECIPE_SERIALIZER) {
            return adapt((DeferredHolder<T, T>) RECIPE_SERIALIZER_REGISTRY.register(id, (Supplier<RecipeSerializer<?>>) supplier));
        } else if (registry == BuiltInRegistries.RECIPE_TYPE) {
            return adapt((DeferredHolder<T, T>) RECIPE_TYPE_REGISTRY.register(id, (Supplier<RecipeType<?>>) supplier));
        } else if (registry == BuiltInRegistries.SOUND_EVENT) {
            return adapt((DeferredHolder<T, T>) SOUND_EVENT_REGISTRY.register(id, (Supplier<SoundEvent>) supplier));
        }

        throw new IllegalArgumentException("Received registration attempt for an unhandled registry. Registry: " + registry);
    }

    private <T> AVPDeferredHolder<T> adapt(DeferredHolder<T, T> deferredHolder) {
        return new AVPDeferredHolder<T>(deferredHolder, () -> deferredHolder);
    }

    public void initialize(IEventBus modBus) {
        ARMOR_MATERIAL_REGISTRY.register(modBus);
        BLOCK_REGISTRY.register(modBus);
        BLOCK_ENTITY_TYPE_REGISTRY.register(modBus);
        CREATIVE_MODE_TAB_REGISTRY.register(modBus);
        DATA_COMPONENT_TYPE_REGISTRY.register(modBus);
        DECORATED_POT_PATTERN_REGISTRY.register(modBus);
        GAME_EVENT_REGISTRY.register(modBus);
        ITEM_REGISTRY.register(modBus);
        MENU_TYPE_REGISTRY.register(modBus);
        MOB_EFFECT_REGISTRY.register(modBus);
        RECIPE_SERIALIZER_REGISTRY.register(modBus);
        RECIPE_TYPE_REGISTRY.register(modBus);
        SOUND_EVENT_REGISTRY.register(modBus);
    }
}
