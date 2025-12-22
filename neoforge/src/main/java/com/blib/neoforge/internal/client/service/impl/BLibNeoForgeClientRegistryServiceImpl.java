package com.blib.neoforge.internal.client.service.impl;

import com.just.core.functional.function.Lazy;
import com.just.core.functional.tuple.Tuple2;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.client.input.keybind.util.KeyMappingUtil;
import com.blib.client.model.KeyInteractType;
import com.blib.internal.client.input.keybind.KeyPressHandler;
import com.blib.internal.client.service.BLibClientRegistryService;

@ApiStatus.Internal
public class BLibNeoForgeClientRegistryServiceImpl implements BLibClientRegistryService {

    @Override
    public void registerArmorRenderer(
        BLibClientMod mod,
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    ) {
        getModContainer(mod)
            .registerArmorRenderer(armorRendererSupplier, itemSuppliers);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(
        BLibClientMod mod,
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    ) {
        getModContainer(mod)
            .registerBlockEntityRenderer(blockEntityTypeSupplier, renderProvider);
    }

    @Override
    public void registerBlockRenderLayer(BLibClientMod mod, Supplier<? extends Block> blockSupplier, RenderType renderType) {
        getModContainer(mod)
            .registerBlockRenderLayer(blockSupplier, renderType);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(
        BLibClientMod mod,
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    ) {
        getModContainer(mod)
            .registerEntityRenderer(entityTypeSupplier, entityRendererFactory);
    }

    @Override
    public void registerItemColor(BLibClientMod mod, ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers) {
        getModContainer(mod)
            .registerItemColor(itemColor, itemSuppliers);
    }

    @Override
    public void registerItemRenderer(
        BLibClientMod mod,
        Supplier<? extends Item> itemSupplier,
        Function<String, Supplier<AzItemRenderer>> rendererFactory
    ) {
        getModContainer(mod)
            .registerItemRenderer(itemSupplier, rendererFactory);
    }

    @Override
    public Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> registerKeyMapping(
        BLibClientMod mod,
        ResourceLocation resourceLocation,
        String category,
        int key,
        Consumer<KeyInteractType> keyInteractTypeConsumer
    ) {
        // Note the use of Lazy.of(...) here. This is deliberate so that the key mapping is only created once.
        Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> supplier = Lazy.of(
            () -> new Tuple2<>(KeyMappingUtil.createKeyMapping(resourceLocation, category, key), keyInteractTypeConsumer)
        );

        getModContainer(mod)
            .registerKeyMapping(supplier);

        return supplier;
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        BLibClientMod mod,
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    ) {
        getModContainer(mod)
            .registerMenuScreen(menuTypeSupplier, screenConstructor);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProviderFactory(
        BLibClientMod mod,
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    ) {
        getModContainer(mod)
            .registerParticleProviderFactory(particleTypeSupplier, spriteParticleRegistration);
    }

    /* package-private */ void initialize(BLibClientMod mod, IEventBus eventBus) {
        var modContainer = getModContainer(mod);

        eventBus.<FMLClientSetupEvent>addListener(event -> {
            modContainer
                .getArmorRendererPairs()
                .forEach(pair -> registerArmorRendererImmediately(mod, pair.v1(), pair.v2()));

            modContainer
                .getItemRendererPairs()
                .forEach(pair -> registerItemRendererImmediately(mod, pair.v1().get(), pair.v2()));

            modContainer
                .getBlockRenderLayerPairs()
                .forEach(pair -> ItemBlockRenderTypes.setRenderLayer(pair.v1().get(), pair.v2()));
        });

        eventBus.<RegisterColorHandlersEvent.Item>addListener(
            event -> modContainer
                .getItemColorPairs()
                .forEach(
                    pair -> pair.v2()
                        .forEach(
                            itemSupplier -> event.getItemColors()
                                .register(pair.v1(), itemSupplier.get())
                        )
                )
        );

        eventBus.<EntityRenderersEvent.RegisterRenderers>addListener(event -> {
            // Entities
            modContainer
                .getEntityRendererPairs()
                .forEach(pair -> {
                    var entityType = pair.v1().get();
                    @SuppressWarnings("unchecked")
                    var entityRendererProvider = (EntityRendererProvider<Entity>) pair.v2();
                    event.registerEntityRenderer(entityType, entityRendererProvider);
                });

            // Block Entities
            modContainer
                .getBlockEntityRendererPairs()
                .forEach(pair -> {
                    var blockEntityType = pair.v1().get();
                    @SuppressWarnings("unchecked")
                    var blockEntityRendererProvider = (BlockEntityRendererProvider<BlockEntity>) pair.v2();
                    event.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
                });
        });

        eventBus.<RegisterKeyMappingsEvent>addListener(
            event -> modContainer
                .getKeyMappingHandlerPairSuppliers()
                .forEach(keyMappingSupplier -> event.register(keyMappingSupplier.get().v1()))
        );

        eventBus.<RegisterMenuScreensEvent>addListener(event -> {
            modContainer
                .getMenuScreenConstructorPairs()
                .forEach(pair -> {
                    var menuType = pair.v1().get();
                    @SuppressWarnings("unchecked")
                    var screenConstructor = (MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>) pair.v2();
                    event.register(menuType, screenConstructor);
                });
        });

        eventBus.<RegisterParticleProvidersEvent>addListener(event -> {
            modContainer
                .getParticleProviderFactoryPairs()
                .forEach(pair -> {
                    @SuppressWarnings("unchecked")
                    var particleType = (ParticleType<ParticleOptions>) pair.v1().get();
                    @SuppressWarnings("unchecked")
                    var spriteParticleRegistration = (ParticleEngine.SpriteParticleRegistration<ParticleOptions>) pair.v2();
                    event.registerSpriteSet(particleType, spriteParticleRegistration);
                });
        });

        eventBus.<FMLClientSetupEvent>addListener(
            event -> modContainer.onClientSetup()
                .getListeners()
                .forEach(BLibClientSetupEvent::invoke)
        );

        NeoForge.EVENT_BUS.<ClientTickEvent.Post>addListener(
            event -> modContainer
                .getKeyMappingHandlerPairSuppliers()
                .forEach(keyMappingSupplier -> {
                    var keyMapping = keyMappingSupplier.get().v1();
                    var keyInteractTypeConsumer = keyMappingSupplier.get().v2();

                    KeyPressHandler.handle(keyMapping, keyInteractTypeConsumer);
                })
        );
    }

    private BLibNeoForgeClientModContainer getModContainer(BLibClientMod mod) {
        return BLibNeoForgeClientModContainerLookup.INSTANCE.get(mod);
    }
}
