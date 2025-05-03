package com.avp.fabric.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.lifecycle.registry.AlienInfectionRegistry;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    public FabricRegistryService() {
        this.literalArgumentBuilders = new ArrayList<>();
    }

    @Override
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        var reference = Registry.registerForHolder(registry, AVPResources.location(id), supplier.get());
        @SuppressWarnings("unchecked")
        var holder = (Holder<T>) reference;
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
    public void registerFurnaceFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        FuelRegistry.INSTANCE.add(itemLikeSupplier.get(), burnTimeInTicks);
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
