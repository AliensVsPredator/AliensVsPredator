package com.avp.core.platform.service;

import com.avp.core.platform.PacketHandleContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface RegistryService {

    <T> @NotNull Supplier<T> register(@NotNull Registry<? super T> registry, @NotNull String name, @NotNull Supplier<T> valueSupplier);

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    void registerCompostingChance(Supplier<? extends ItemLike> itemLikeSupplier, float normalizedChance);

    void registerEntityAttributes(Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier);

    void registerFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks);

    <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, Consumer<PacketHandleContext.Server<T>> handler);

    <T extends CustomPacketPayload> void registerS2CPacketCodec(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec);
}