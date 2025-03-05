package com.avp.neoforge.platform.service;

import com.avp.core.AVP;
import com.avp.core.platform.PacketHandleContext;
import com.avp.core.platform.service.RegistryService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<ArmorMaterial> armorMaterials = DeferredRegister.create(Registries.ARMOR_MATERIAL, AVP.MOD_ID);

    private final DeferredRegister<Block> blocks = DeferredRegister.create(Registries.BLOCK, AVP.MOD_ID);

    private final DeferredRegister<BlockEntityType<?>> blockEntityTypes = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AVP.MOD_ID);

    private final DeferredRegister<CreativeModeTab> creativeModeTab = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AVP.MOD_ID);

    private final DeferredRegister<DataComponentType<?>> dataComponentTypes = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AVP.MOD_ID);

    private final DeferredRegister<DecoratedPotPattern> decoratedPotPatterns = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, AVP.MOD_ID);

    private final DeferredRegister<EntityType<?>> entityTypes = DeferredRegister.create(Registries.ENTITY_TYPE, AVP.MOD_ID);

    private final DeferredRegister<GameEvent> gameEvents = DeferredRegister.create(Registries.GAME_EVENT, AVP.MOD_ID);

    private final DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, AVP.MOD_ID);

    private final DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(Registries.MENU, AVP.MOD_ID);

    private final DeferredRegister<ParticleType<?>> particleTypes = DeferredRegister.create(Registries.PARTICLE_TYPE, AVP.MOD_ID);

    private final DeferredRegister<SoundEvent> soundEvents = DeferredRegister.create(Registries.SOUND_EVENT, AVP.MOD_ID);

    private final List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends ItemLike>, Float>> compostableItemLikeChancePairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityTypeAttributePairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends ItemLike>, Integer>> fuelItemLikeBurnTimeInTicksPairs = new ArrayList<>();

    public void initialize(IEventBus eventBus) {
        armorMaterials.register(eventBus);
        blocks.register(eventBus);
        blockEntityTypes.register(eventBus);
        creativeModeTab.register(eventBus);
        dataComponentTypes.register(eventBus);
        decoratedPotPatterns.register(eventBus);
        entityTypes.register(eventBus);
        gameEvents.register(eventBus);
        items.register(eventBus);
        particleTypes.register(eventBus);
        soundEvents.register(eventBus);
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull Supplier<T> register(@NotNull Registry<? super T> registry, @NotNull String name, @NotNull Supplier<T> valueSupplier) {
        Supplier<T> supplier = null;

        if (registry == BuiltInRegistries.ARMOR_MATERIAL) {
            supplier = (Supplier<T>) armorMaterials.register(name, (Supplier<ArmorMaterial>) valueSupplier);
        } else if (registry == BuiltInRegistries.BLOCK) {
            supplier = (Supplier<T>) blocks.register(name, (Supplier<? extends Block>) valueSupplier);
        } else if (registry == BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            supplier = (Supplier<T>) blockEntityTypes.register(name, (Supplier<? extends BlockEntityType<?>>) valueSupplier);
        } else if (registry == BuiltInRegistries.CREATIVE_MODE_TAB) {
            supplier = (Supplier<T>) creativeModeTab.register(name, (Supplier<? extends CreativeModeTab>) valueSupplier);
        } else if (registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            supplier = (Supplier<T>) dataComponentTypes.register(name, (Supplier<? extends DataComponentType<?>>) valueSupplier);
        } else if (registry == BuiltInRegistries.DECORATED_POT_PATTERN) {
            supplier = (Supplier<T>) decoratedPotPatterns.register(name, (Supplier<DecoratedPotPattern>) valueSupplier);
        } else if (registry == BuiltInRegistries.ENTITY_TYPE) {
            supplier = (Supplier<T>) entityTypes.register(name, (Supplier<? extends EntityType<?>>) valueSupplier);
        } else if (registry == BuiltInRegistries.GAME_EVENT) {
            supplier = (Supplier<T>) gameEvents.register(name, (Supplier<GameEvent>) valueSupplier);
        } else if (registry == BuiltInRegistries.ITEM) {
            supplier = (Supplier<T>) items.register(name, (Supplier<? extends Item>) valueSupplier);
        } else if (registry == BuiltInRegistries.MENU) {
            supplier = (Supplier<T>) menuTypes.register(name, (Supplier<? extends MenuType<?>>) valueSupplier);
        } else if (registry == BuiltInRegistries.PARTICLE_TYPE) {
            supplier = (Supplier<T>) particleTypes.register(name, (Supplier<? extends ParticleType<?>>) valueSupplier);
        } else if (registry == BuiltInRegistries.SOUND_EVENT) {
            supplier = (Supplier<T>) soundEvents.register(name, (Supplier<SoundEvent>) valueSupplier);
        }

        return Objects.requireNonNull(supplier);
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        commands.add(literalArgumentBuilder);
    }

    @Override
    public void registerCompostingChance(Supplier<? extends ItemLike> itemLikeSupplier, float normalizedChance) {
        compostableItemLikeChancePairs.add(Map.entry(itemLikeSupplier, normalizedChance));
    }

    @Override
    public void registerEntityAttributes(Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier) {
        entityTypeAttributePairs.add(Map.entry(entityTypeSupplier, attributeSupplierBuilderSupplier));
    }

    @Override
    public void registerFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        fuelItemLikeBurnTimeInTicksPairs.add(Map.entry(itemLikeSupplier, burnTimeInTicks));
    }

    @Override
    public <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, Consumer<PacketHandleContext.Server<T>> handler) {

    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CPacketCodec(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {

    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<Map.Entry<Supplier<? extends ItemLike>, Float>> getCompostableItemLikeChancePairs() {
        return Collections.unmodifiableList(compostableItemLikeChancePairs);
    }

    public DeferredRegister<CreativeModeTab> getCreativeModeTab() {
        return creativeModeTab;
    }

    public List<Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityTypeAttributePairs() {
        return Collections.unmodifiableList(entityTypeAttributePairs);
    }

    public List<Map.Entry<Supplier<? extends ItemLike>, Integer>> getFuelItemLikeBurnTimeInTicksPairs() {
        return Collections.unmodifiableList(fuelItemLikeBurnTimeInTicksPairs);
    }
}
