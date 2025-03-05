package com.avp.fabric.platform.service;

import com.avp.core.AVPResources;
import com.avp.core.platform.PacketHandleContext;
import com.avp.core.platform.service.RegistryService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricRegistryService implements RegistryService {

    private final List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();

    @Override
    public <T> @NotNull Supplier<T> register(@NotNull Registry<? super T> registry, @NotNull String name, @NotNull Supplier<T> valueSupplier) {
        var registeredValue = Registry.register(registry, AVPResources.location(name), valueSupplier.get());
        return () -> registeredValue;
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        commands.add(literalArgumentBuilder);
    }

    @Override
    public void registerCompostingChance(Supplier<? extends ItemLike> itemLikeSupplier, float normalizedChance) {
        CompostingChanceRegistry.INSTANCE.add(itemLikeSupplier.get(), normalizedChance);
    }

    @Override
    public void registerEntityAttributes(Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier) {
        FabricDefaultAttributeRegistry.register(entityTypeSupplier.get(), attributeSupplierBuilderSupplier.get());
    }

    @Override
    public void registerFuel(Supplier<? extends ItemLike> itemLikeSupplier, int burnTimeInTicks) {
        FuelRegistry.INSTANCE.add(itemLikeSupplier.get(), burnTimeInTicks);
    }

    @Override
    public <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, Consumer<PacketHandleContext.Server<T>> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);

        ServerPlayNetworking.registerGlobalReceiver(
            type,
            (payload, ctx) -> ctx.server().execute(() -> handler.accept(new PacketHandleContext.Server<>(payload, ctx.player(), ctx.server())))
        );
    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CPacketCodec(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);

    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
