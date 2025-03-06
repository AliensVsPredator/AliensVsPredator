package com.avp.neoforge.platform.service;

import com.avp.core.AVP;
import com.avp.core.platform.PacketHandleContext;
import com.avp.core.platform.service.RegistryService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoForgeRegistryService implements RegistryService {

    private static DeferredRegister<ArmorMaterial> armorMaterials = DeferredRegister.create(Registries.ARMOR_MATERIAL, AVP.MOD_ID);

    private static DeferredRegister<Block> blocks = DeferredRegister.create(Registries.BLOCK, AVP.MOD_ID);

    private static DeferredRegister<BlockEntityType<?>> blockEntityTypes = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AVP.MOD_ID);

    private static DeferredRegister<CreativeModeTab> creativeModeTab = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AVP.MOD_ID);

    private static DeferredRegister<DataComponentType<?>> dataComponentTypes = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AVP.MOD_ID);

    private static DeferredRegister<DecoratedPotPattern> decoratedPotPatterns = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, AVP.MOD_ID);

    private static DeferredRegister<EntityType<?>> entityTypes = DeferredRegister.create(Registries.ENTITY_TYPE, AVP.MOD_ID);

    private static DeferredRegister<GameEvent> gameEvents = DeferredRegister.create(Registries.GAME_EVENT, AVP.MOD_ID);

    private static DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, AVP.MOD_ID);

    private static DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(Registries.MENU, AVP.MOD_ID);

    private static DeferredRegister<ParticleType<?>> particleTypes = DeferredRegister.create(Registries.PARTICLE_TYPE, AVP.MOD_ID);

    private static DeferredRegister<SoundEvent> soundEvents = DeferredRegister.create(Registries.SOUND_EVENT, AVP.MOD_ID);

    private static List<LiteralArgumentBuilder<CommandSourceStack>> commands = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends ItemLike>, Float>> compostableItemLikeChancePairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityTypeAttributePairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends ItemLike>, Integer>> fuelItemLikeBurnTimeInTicksPairs = new ArrayList<>();

    public static void initialize(IEventBus eventBus) {
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

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String entityName, Supplier<EntityType<T>> entity) {
        return entityTypes.register(entityName, entity);
    }

    @Override
    public <T extends ArmorMaterial> Supplier<T> registerArmorMaterial(String armorMaterialName, Supplier<T> armorMaterial) {
        return armorMaterials.register(armorMaterialName, armorMaterial);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String blockName, Supplier<T> block) {
        return blocks.register(blockName, block);
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String blockEntityName, Supplier<BlockEntityType<T>> blockEntityType) {
        return blockEntityTypes.register(blockEntityName, blockEntityType);
    }

    @Override
    public <T extends DataComponentType<?>> Supplier<T> registerDataComponent(String dataComponentName, Supplier<T> dataComponent) {
        return dataComponentTypes.register(dataComponentName, dataComponent);
    }

    @Override
    public <T extends GameEvent> Supplier<T> registerGameEvent(String gameEventName, Supplier<T> gameEvent) {
        return gameEvents.register(gameEventName, gameEvent);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String particleName, Supplier<T> particle) {
        return particleTypes.register(particleName, particle);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String tabName, Supplier<T> tab) {
        return creativeModeTab.register(tabName, tab) ;
    }

    @Override
    public <T extends MenuType<?>> Supplier<T> registerScreen(String screenName, Supplier<T> menuType) {
        return menuTypes.register(screenName, menuType);
    }

    @Override
    public <T extends DecoratedPotPattern> Supplier<T> registerDecoratedPotPattern(String blockName, Supplier<T> decoratedPotPattern) {
        return decoratedPotPatterns.register(blockName, decoratedPotPattern);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String itemName, Supplier<T> item) {
        return items.register(itemName, item);
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String soundName, Supplier<T> sound) {
        return soundEvents.register(soundName, sound);
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(Supplier<EntityType<E>> entityType, int primaryEggColour, int secondaryEggColour, Item.Properties itemProperties) {
        return () -> new DeferredSpawnEggItem(entityType, primaryEggColour, secondaryEggColour, itemProperties);
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
