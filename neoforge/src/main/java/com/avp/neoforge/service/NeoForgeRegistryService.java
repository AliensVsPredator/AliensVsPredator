package com.avp.neoforge.service;

import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple3;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.avp.AVP;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(Registry<T> registry) {
        return DeferredRegister.create(registry, AVP.MOD_ID);
    }

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Supplier<? extends Item>> azureLibItemIdentitySuppliers;

    private final List<Tuple2<Supplier<? extends ItemLike>, Integer>> furnaceFuelPairs;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final List<NetworkHandler<?>> networkHandlers;

    private final List<PreparableReloadListener> reloadListeners;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public NeoForgeRegistryService() {
        this.registryToDeferredRegisterMap = Stream.of(
            BuiltInRegistries.ARMOR_MATERIAL,
            BuiltInRegistries.BLOCK,
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            BuiltInRegistries.CREATIVE_MODE_TAB,
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            BuiltInRegistries.DECORATED_POT_PATTERN,
            BuiltInRegistries.ENTITY_TYPE,
            BuiltInRegistries.GAME_EVENT,
            BuiltInRegistries.ITEM,
            BuiltInRegistries.MENU,
            BuiltInRegistries.MOB_EFFECT,
            BuiltInRegistries.PARTICLE_TYPE,
            BuiltInRegistries.POINT_OF_INTEREST_TYPE,
            BuiltInRegistries.RECIPE_SERIALIZER,
            BuiltInRegistries.RECIPE_TYPE,
            BuiltInRegistries.SOUND_EVENT,
            BuiltInRegistries.VILLAGER_PROFESSION
        ).collect(Collectors.toMap(Function.identity(), NeoForgeRegistryService::createDeferredRegistry));

        this.azureLibItemIdentitySuppliers = new ArrayList<>();
        this.furnaceFuelPairs = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.reloadListeners = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier) {
        azureLibItemIdentitySuppliers.add(itemSupplier);
    }

    @Override
    public void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        furnaceFuelPairs.add(new Tuple2<>(itemLikeSupplier, burnTimeInTicks));
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler) {
        networkHandlers.add(networkHandler);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection) {
        /* NO-OP */
    }

    @Override
    public PreparableReloadListener registerReloadListener(String id, PreparableReloadListener listener) {
        reloadListeners.add(listener);
        return listener;
    }

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(villagerProfessionSupplier, level, villagerTradeItemListings));
    }

    public void initialize(IEventBus modBus) {
        registryToDeferredRegisterMap.values().forEach(deferredRegister -> deferredRegister.register(modBus));
    }

    public List<Supplier<? extends Item>> getAzureLibItemIdentitySuppliers() {
        return azureLibItemIdentitySuppliers;
    }

    public List<Tuple2<Supplier<? extends ItemLike>, Integer>> getFurnaceFuelPairs() {
        return furnaceFuelPairs;
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return literalArgumentBuilders;
    }

    public List<NetworkHandler<?>> getNetworkHandlers() {
        return networkHandlers;
    }

    public List<PreparableReloadListener> getReloadListeners() {
        return reloadListeners;
    }

    public List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }
}
