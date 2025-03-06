package com.avp.core.platform.service;

import com.avp.core.platform.PacketHandleContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface RegistryService {

    <T extends Entity> Supplier<EntityType<T>> registerEntity(String entityName, Supplier<EntityType<T>> entity);

    <T extends ArmorMaterial> Supplier<T> registerArmorMaterial(String armorMaterialName, Supplier<T> armorMaterial);

    <T extends Block> Supplier<T> registerBlock(String blockName, Supplier<T> block);

    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(
            String blockEntityName,
            Supplier<BlockEntityType<T>> blockEntityType
    );

    <T extends DataComponentType<?>> Supplier<T> registerDataComponent(String dataComponentName, Supplier<T> dataComponent);

    <T extends GameEvent> Supplier<T> registerGameEvent(String gameEventName, Supplier<T> gameEvent);

    <T extends ParticleType<?>> Supplier<T> registerParticle(String particleName, Supplier<T> particle);

    <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String tabName, Supplier<T> tab);

    <T extends MenuType<?>> Supplier<T> registerScreen(String screenName, Supplier<T> menuType);

    <T extends DecoratedPotPattern> Supplier<T> registerDecoratedPotPattern(String blockName, Supplier<T> decoratedPotPattern);

    <T extends Item> Supplier<T> registerItem(String itemName, Supplier<T> item);

    <T extends SoundEvent> Supplier<T> registerSound(String soundName, Supplier<T> sound);

    <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(Supplier<EntityType<E>> entityType, int primaryEggColour, int secondaryEggColour, Item.Properties itemProperties);

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    void registerCompostingChance(Supplier<? extends ItemLike> itemLikeSupplier, float normalizedChance);

    void registerEntityAttributes(Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier);

    void registerFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks);

    <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, Consumer<PacketHandleContext.Server<T>> handler);

    <T extends CustomPacketPayload> void registerS2CPacketCodec(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec);
}