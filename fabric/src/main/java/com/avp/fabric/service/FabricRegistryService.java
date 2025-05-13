package com.avp.fabric.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.entity.spawning.AVPEntitySpawnData;
import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.lifecycle.registry.AlienInfectionRegistry;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.network.NetworkHandler;
import com.avp.common.network.PacketDirection;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    public FabricRegistryService() {
        this.literalArgumentBuilders = new ArrayList<>();
    }

    @Override
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        var object = supplier.get();

        if (object instanceof PoiType poiType) {
            // We have to do special handling for PoiType registration on the Fabric side, since Fabric wants
            // Poi registrations to go through their "PointOfInterestHelper" type.
            return registerPoiType(id, poiType);
        }

        var reference = Registry.registerForHolder(registry, AVPResources.location(id), object);
        @SuppressWarnings("unchecked")
        var holder = (Holder<T>) reference;
        return new AVPDeferredHolder<>(holder::value, () -> holder);
    }

    private <T> @NotNull AVPDeferredHolder<T> registerPoiType(String id, PoiType poiType) {
        var location = AVPResources.location(id);
        PointOfInterestHelper.register(location, poiType.maxTickets(), poiType.validRange(), poiType.matchingStates());
        // Immediately get the holder or throw. This should be safe to do since we registered the PoiType in the last
        // line. This is necessary because PointOfInterestHelper doesn't return back a holder (which we need for
        // AVPDeferredHolder) after registration.
        @SuppressWarnings("unchecked")
        var holder = (Holder<T>) BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(location).orElseThrow();
        return new AVPDeferredHolder<>(holder::value, () -> holder);
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public <S extends LivingEntity, P extends LivingEntity> Supplier<AlienInfection<S, P>> registerAlienInfection(
        Supplier<AlienInfection<S, P>> alienInfectionSupplier
    ) {
        var alienInfection = AlienInfectionRegistry.register(alienInfectionSupplier.get());
        return () -> alienInfection;
    }

    @Override
    public Supplier<AlienLifecycle> registerAlienLifecycle(Supplier<AlienLifecycle> alienLifecycleSupplier) {
        var alienLifecycle = AlienLifecycleRegistry.register(alienLifecycleSupplier.get());
        return () -> alienLifecycle;
    }

    @Override
    public void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier) {
        AzIdentityRegistry.register(itemSupplier.get());
    }

    @Override
    public void registerCompostableItem(
        Supplier<? extends ItemLike> itemLikeSupplier,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        CompostingChanceRegistry.INSTANCE.add(itemLikeSupplier.get(), chance);
    }

    public void registerEntityAttributes(
        Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        FabricDefaultAttributeRegistry.register(entityTypeSupplier.get(), attributeSupplierBuilderSupplier.get());
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(AVPEntitySpawnData<T> spawnData) {
        var spawnSettings = spawnData.getConfigData().spawnSettings();
        var entityType = spawnData.getEntityType();

        if (!spawnData.isPlacementDisabled()) {
            var placement = spawnData.getPlacementData().type();
            var heightMap = spawnData.getPlacementData().heightmapType();
            var spawnPredicate = spawnData.getPlacementData().spawnPredicate();

            SpawnPlacements.register(entityType, placement, heightMap, spawnPredicate);
        }

        if (!spawnData.isConfigDisabled()) {
            Predicate<BiomeSelectionContext> biomeSelector = biomeSelectionContext -> biomeSelectionContext.hasTag(
                spawnData.getConfigData().biomeTagKey()
            );
            var spawnGroup = entityType.getCategory();
            var weight = spawnSettings.weight;
            var minGroupSize = spawnSettings.minGroupSize;
            var maxGroupSize = spawnSettings.maxGroupSize;

            BiomeModifications.addSpawn(biomeSelector, spawnGroup, entityType, weight, minGroupSize, maxGroupSize);
        }
    }

    @Override
    public void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        FuelRegistry.INSTANCE.add(itemLikeSupplier.get(), burnTimeInTicks);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketHandlers(NetworkHandler<T> networkHandler) {
        switch (networkHandler) {
            case NetworkHandler.FromClient<T> handler -> ServerPlayNetworking.registerGlobalReceiver(
                networkHandler.type(),
                (payload, context) -> context.server().execute(() -> handler.payloadConsumer().accept(payload, context.player()))
            );
            case NetworkHandler.FromEither<T> handler -> {
                ServerPlayNetworking.registerGlobalReceiver(
                    networkHandler.type(),
                    (payload, context) -> context.server()
                        .execute(() -> handler.fromClientPayloadConsumer().accept(payload, context.player()))
                );
                ClientPlayNetworking.registerGlobalReceiver(
                    networkHandler.type(),
                    (payload, context) -> context.client()
                        .execute(() -> handler.fromServerPayloadConsumer().accept(payload, context.player()))
                );
            }
            case NetworkHandler.FromServer<T> handler -> ClientPlayNetworking.registerGlobalReceiver(
                networkHandler.type(),
                (payload, context) -> context.client().execute(() -> handler.payloadConsumer().accept(payload, context.player()))
            );
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection) {
        var handleClient = false;
        var handleServer = false;
        var codec = packetDirection.codec();
        var type = packetDirection.type();

        switch (packetDirection) {
            case PacketDirection.BI<T> ignored -> {
                handleClient = true;
                handleServer = true;
            }
            case PacketDirection.C2S<T> ignored -> handleServer = true;
            case PacketDirection.S2C<T> ignored -> handleClient = true;
        }

        if (handleClient) {
            PayloadTypeRegistry.playS2C().register(type, codec);
        }

        if (handleServer) {
            PayloadTypeRegistry.playC2S().register(type, codec);
        }
    }

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        TradeOfferHelper.registerVillagerOffers(
            villagerProfessionSupplier.get(),
            level,
            factories -> factories.addAll(villagerTradeItemListings)
        );
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return literalArgumentBuilders;
    }
}
