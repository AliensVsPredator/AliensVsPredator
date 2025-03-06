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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricRegistryService implements RegistryService {

    private final List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();

    private static <T, R extends Registry<? super T>> Supplier<T> registerSupplier(
            R registry,
            String id,
            Supplier<T> object
    ) {
        final T registeredObject = Registry.register(
                (Registry<T>) registry,
                AVPResources.location(id),
                object.get()
        );

        return () -> registeredObject;
    }

    private static <T, R extends Registry<? super T>> Holder<T> registerHolder(R registry, String id, Supplier<T> object) {
        return Registry.registerForHolder(
                (Registry<T>) registry,
                AVPResources.location(id),
                object.get()
        );
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String entityName, Supplier<EntityType<T>> entity) {
        return registerSupplier(BuiltInRegistries.ENTITY_TYPE, entityName, entity);
    }

    @Override
    public <T extends ArmorMaterial> Supplier<T> registerArmorMaterial(String armorMaterialName, Supplier<T> armorMaterial) {
        return registerSupplier(BuiltInRegistries.ARMOR_MATERIAL, armorMaterialName, armorMaterial);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String blockName, Supplier<T> block) {
        return registerSupplier(BuiltInRegistries.BLOCK, blockName, block);
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String blockEntityName, Supplier<BlockEntityType<T>> blockEntityType) {
        return registerSupplier(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntityName, blockEntityType);
    }

    @Override
    public <T extends DataComponentType<?>> Supplier<T> registerDataComponent(String dataComponentName, Supplier<T> dataComponent) {
        return registerSupplier(BuiltInRegistries.DATA_COMPONENT_TYPE, dataComponentName, dataComponent);
    }

    @Override
    public <T extends GameEvent> Supplier<T> registerGameEvent(String gameEventName, Supplier<T> gameEvent) {
        return registerSupplier(BuiltInRegistries.GAME_EVENT, gameEventName, gameEvent);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String particleName, Supplier<T> particle) {
        return registerSupplier(BuiltInRegistries.PARTICLE_TYPE, particleName, particle);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String tabName, Supplier<T> tab) {
        return registerSupplier(BuiltInRegistries.CREATIVE_MODE_TAB, tabName, tab);
    }

    @Override
    public <T extends MenuType<?>> Supplier<T> registerScreen(String screenName, Supplier<T> menuType) {
        return registerSupplier(BuiltInRegistries.MENU, screenName, menuType);
    }

    @Override
    public <T extends DecoratedPotPattern> Supplier<T> registerDecoratedPotPattern(String blockName, Supplier<T> decoratedPotPattern) {
        return registerSupplier(BuiltInRegistries.DECORATED_POT_PATTERN, blockName, decoratedPotPattern);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String itemName, Supplier<T> item) {
        return registerSupplier(BuiltInRegistries.ITEM, itemName, item);
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String soundName, Supplier<T> sound) {
        return registerSupplier(BuiltInRegistries.SOUND_EVENT, soundName, sound);
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(Supplier<EntityType<E>> entityType, int primaryEggColour, int secondaryEggColour, Item.Properties itemProperties) {
        return () -> new SpawnEggItem(entityType.get(), primaryEggColour, secondaryEggColour, itemProperties);
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
