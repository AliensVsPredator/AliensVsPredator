package com.blib;

import com.blib.common.DefaultDispenseSpawnEggItemBehavior;
import com.blib.event.key.BLibEventKey;
import com.blib.service.BLibServices;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BLibMod {

    private final String id;

    private final Map<Registry<?>, List<BLibRegistry<?>>> registryToRegistriesMap;

    /* package-private */ BLibMod(String id) {
        this.id = id;
        this.registryToRegistriesMap = new ConcurrentHashMap<>();
    }

    public <T> void addEventListener(BLibEventKey<T> key, Consumer<T> consumer) {
        BLibServices.EVENT.addListener(key, consumer);
    }

    public <T> BLibRegistry<T> createRegistry(Registry<? super T> registry) {
        var newRegistry = new BLibRegistry<T>(this, registry);

        registryToRegistriesMap.compute(registry, ($, registries) -> {
            var nonNullRegistries = registries == null
                ? new ArrayList<BLibRegistry<?>>()
                : registries;

            nonNullRegistries.add(newRegistry);

            return nonNullRegistries;
        });

        if (registry == BuiltInRegistries.ITEM && Objects.equals("Fabric", BLibServices.MOD_LOADER.getModLoaderName())) {
            newRegistry.addListener(this::autoRegisterDispenserBehavior);
        }

        return newRegistry;
    }

    public <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, createResourceLocation(path));
    }

    public ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public <T> Collection<BLibHolder<? extends T>> getAllHolders(Registry<? super T> registry) {
        @SuppressWarnings("unchecked")
        var subRegistries = (List<BLibRegistry<T>>) (List<?>) registryToRegistriesMap.getOrDefault(registry, List.of());

        return subRegistries
            .stream()
            .map(BLibRegistry::getAll)
            .flatMap(Collection::stream)
            .toList();
    }

    public String getId() {
        return id;
    }

    private <T> void autoRegisterDispenserBehavior(BLibHolder<? super T> holder) {
        @SuppressWarnings("unchecked")
        var itemHolder = (BLibHolder<Item>) holder;

        if (!(itemHolder.get() instanceof SpawnEggItem spawnEggItem)) {
            return;
        }

        DispenserBlock.registerBehavior(spawnEggItem, DefaultDispenseSpawnEggItemBehavior.INSTANCE);
    }
}
