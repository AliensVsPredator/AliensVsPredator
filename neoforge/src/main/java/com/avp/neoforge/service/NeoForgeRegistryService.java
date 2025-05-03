package com.avp.neoforge.service;

import com.bvanseg.just.functional.tuple.Tuple2;
import com.bvanseg.just.functional.tuple.Tuple3;
import com.bvanseg.just.functional.tuple.Tuple4;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.network.NetworkHandler;
import com.avp.common.network.PacketDirection;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<ArmorMaterial> armorMaterialRegistry = DeferredRegister.create(
        BuiltInRegistries.ARMOR_MATERIAL,
        AVP.MOD_ID
    );

    private final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, AVP.MOD_ID);

    private final DeferredRegister<BlockEntityType<?>> blockEntityTypeRegistry = DeferredRegister.create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        AVP.MOD_ID
    );

    private final DeferredRegister<CreativeModeTab> creativeModeTabRegistry = DeferredRegister.create(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        AVP.MOD_ID
    );

    private final DeferredRegister<DataComponentType<?>> dataComponentTypeRegistry = DeferredRegister.create(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        AVP.MOD_ID
    );

    private final DeferredRegister<DecoratedPotPattern> decoratedPotPatternRegistry = DeferredRegister.create(
        BuiltInRegistries.DECORATED_POT_PATTERN,
        AVP.MOD_ID
    );

    private final DeferredRegister<EntityType<?>> entityTypeRegistry = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AVP.MOD_ID);

    private final DeferredRegister<GameEvent> gameEventRegistry = DeferredRegister.create(BuiltInRegistries.GAME_EVENT, AVP.MOD_ID);

    private final DeferredRegister<Item> itemRegistry = DeferredRegister.create(BuiltInRegistries.ITEM, AVP.MOD_ID);

    private final DeferredRegister<MenuType<?>> menuTypeRegistry = DeferredRegister.create(BuiltInRegistries.MENU, AVP.MOD_ID);

    private final DeferredRegister<MobEffect> mobEffectRegistry = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, AVP.MOD_ID);

    private final DeferredRegister<ParticleType<?>> particleTypeRegistry = DeferredRegister.create(
        BuiltInRegistries.PARTICLE_TYPE,
        AVP.MOD_ID
    );

    private final DeferredRegister<PoiType> poiTypeRegistry = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, AVP.MOD_ID);

    private final DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry = DeferredRegister.create(
        BuiltInRegistries.RECIPE_SERIALIZER,
        AVP.MOD_ID
    );

    private final DeferredRegister<RecipeType<?>> recipeTypeRegistry = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, AVP.MOD_ID);

    private final DeferredRegister<SoundEvent> soundEventRegistry = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AVP.MOD_ID);

    private final DeferredRegister<VillagerProfession> villagerProfessionRegistry = DeferredRegister.create(
        BuiltInRegistries.VILLAGER_PROFESSION,
        AVP.MOD_ID
    );

    private final List<Supplier<? extends AlienInfection<?, ?>>> alienInfectionSuppliers;

    private final List<Supplier<AlienLifecycle>> alienLifecycleSuppliers;

    private final List<Supplier<? extends Item>> azureLibItemIdentitySuppliers;

    private final List<Tuple4<Supplier<? extends ItemLike>, Float, Boolean, Boolean>> compostableData;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    private final List<Tuple2<Supplier<? extends ItemLike>, Integer>> furnaceFuelPairs;

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    private final List<NetworkHandler<?>> networkHandlers;

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    // TODO: Assign other final fields here.
    public NeoForgeRegistryService() {
        this.alienInfectionSuppliers = new ArrayList<>();
        this.alienLifecycleSuppliers = new ArrayList<>();
        this.azureLibItemIdentitySuppliers = new ArrayList<>();
        this.compostableData = new ArrayList<>();
        this.entityAttributeSupplierPairs = new ArrayList<>();
        this.furnaceFuelPairs = new ArrayList<>();
        this.literalArgumentBuilders = new ArrayList<>();
        this.networkHandlers = new ArrayList<>();
        this.villagerTradeData = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        if (registry == BuiltInRegistries.ARMOR_MATERIAL) {
            return adapt((DeferredHolder<T, T>) armorMaterialRegistry.register(id, (Supplier<ArmorMaterial>) supplier));
        } else if (registry == BuiltInRegistries.BLOCK) {
            return adapt((DeferredHolder<T, T>) BLOCK_REGISTRY.register(id, (Supplier<Block>) supplier));
        } else if (registry == BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            return adapt((DeferredHolder<T, T>) blockEntityTypeRegistry.register(id, (Supplier<BlockEntityType<?>>) supplier));
        } else if (registry == BuiltInRegistries.CREATIVE_MODE_TAB) {
            return adapt((DeferredHolder<T, T>) creativeModeTabRegistry.register(id, (Supplier<CreativeModeTab>) supplier));
        } else if (registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            return adapt((DeferredHolder<T, T>) dataComponentTypeRegistry.register(id, (Supplier<DataComponentType<?>>) supplier));
        } else if (registry == BuiltInRegistries.DECORATED_POT_PATTERN) {
            return adapt((DeferredHolder<T, T>) decoratedPotPatternRegistry.register(id, (Supplier<DecoratedPotPattern>) supplier));
        } else if (registry == BuiltInRegistries.ENTITY_TYPE) {
            return adapt((DeferredHolder<T, T>) entityTypeRegistry.register(id, (Supplier<EntityType<?>>) supplier));
        } else if (registry == BuiltInRegistries.GAME_EVENT) {
            return adapt((DeferredHolder<T, T>) gameEventRegistry.register(id, (Supplier<GameEvent>) supplier));
        } else if (registry == BuiltInRegistries.ITEM) {
            return adapt((DeferredHolder<T, T>) itemRegistry.register(id, (Supplier<Item>) supplier));
        } else if (registry == BuiltInRegistries.MENU) {
            return adapt((DeferredHolder<T, T>) menuTypeRegistry.register(id, (Supplier<MenuType<?>>) supplier));
        } else if (registry == BuiltInRegistries.MOB_EFFECT) {
            return adapt((DeferredHolder<T, T>) mobEffectRegistry.register(id, (Supplier<MobEffect>) supplier));
        } else if (registry == BuiltInRegistries.PARTICLE_TYPE) {
            return adapt((DeferredHolder<T, T>) particleTypeRegistry.register(id, (Supplier<ParticleType<?>>) supplier));
        } else if (registry == BuiltInRegistries.POINT_OF_INTEREST_TYPE) {
            return adapt((DeferredHolder<T, T>) poiTypeRegistry.register(id, (Supplier<PoiType>) supplier));
        } else if (registry == BuiltInRegistries.RECIPE_SERIALIZER) {
            return adapt((DeferredHolder<T, T>) recipeSerializerRegistry.register(id, (Supplier<RecipeSerializer<?>>) supplier));
        } else if (registry == BuiltInRegistries.RECIPE_TYPE) {
            return adapt((DeferredHolder<T, T>) recipeTypeRegistry.register(id, (Supplier<RecipeType<?>>) supplier));
        } else if (registry == BuiltInRegistries.SOUND_EVENT) {
            return adapt((DeferredHolder<T, T>) soundEventRegistry.register(id, (Supplier<SoundEvent>) supplier));
        } else if (registry == BuiltInRegistries.VILLAGER_PROFESSION) {
            return adapt((DeferredHolder<T, T>) villagerProfessionRegistry.register(id, (Supplier<VillagerProfession>) supplier));
        }

        throw new IllegalArgumentException("Received registration attempt for an unhandled registry. Registry: " + registry);
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public <S extends LivingEntity, P extends LivingEntity> Supplier<AlienInfection<S, P>> registerAlienInfection(
        Supplier<AlienInfection<S, P>> alienInfectionSupplier
    ) {
        alienInfectionSuppliers.add(alienInfectionSupplier);
        return alienInfectionSupplier;
    }

    @Override
    public Supplier<AlienLifecycle> registerAlienLifecycle(Supplier<AlienLifecycle> alienLifecycleSupplier) {
        alienLifecycleSuppliers.add(alienLifecycleSupplier);
        return alienLifecycleSupplier;
    }

    @Override
    public void registerAzureLibIdentity(Supplier<? extends Item> itemSupplier) {
        azureLibItemIdentitySuppliers.add(itemSupplier);
    }

    @Override
    public void registerCompostableItem(
        Supplier<? extends ItemLike> itemLikeSupplier,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        compostableData.add(new Tuple4<>(itemLikeSupplier, chance, villagersCanCompost, replace));
    }

    @Override
    public void registerEntityAttributes(
        Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        entityAttributeSupplierPairs.add(new Tuple2<>(entityTypeSupplier, attributeSupplierBuilderSupplier));
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
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(villagerProfessionSupplier, level, villagerTradeItemListings));
    }

    private <T> AVPDeferredHolder<T> adapt(DeferredHolder<T, T> deferredHolder) {
        return new AVPDeferredHolder<>(deferredHolder, () -> deferredHolder);
    }

    public void initialize(IEventBus modBus) {
        armorMaterialRegistry.register(modBus);
        BLOCK_REGISTRY.register(modBus);
        blockEntityTypeRegistry.register(modBus);
        creativeModeTabRegistry.register(modBus);
        dataComponentTypeRegistry.register(modBus);
        decoratedPotPatternRegistry.register(modBus);
        entityTypeRegistry.register(modBus);
        gameEventRegistry.register(modBus);
        itemRegistry.register(modBus);
        menuTypeRegistry.register(modBus);
        mobEffectRegistry.register(modBus);
        particleTypeRegistry.register(modBus);
        poiTypeRegistry.register(modBus);
        recipeSerializerRegistry.register(modBus);
        recipeTypeRegistry.register(modBus);
        soundEventRegistry.register(modBus);
        villagerProfessionRegistry.register(modBus);
    }

    public List<Supplier<? extends AlienInfection<?, ?>>> getAlienInfectionSuppliers() {
        return alienInfectionSuppliers;
    }

    public List<Supplier<AlienLifecycle>> getAlienLifecycleSuppliers() {
        return alienLifecycleSuppliers;
    }

    public List<Supplier<? extends Item>> getAzureLibItemIdentitySuppliers() {
        return azureLibItemIdentitySuppliers;
    }

    public List<Tuple4<Supplier<? extends ItemLike>, Float, Boolean, Boolean>> getCompostableData() {
        return compostableData;
    }

    public List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityAttributeSupplierPairs() {
        return entityAttributeSupplierPairs;
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

    public List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }
}
