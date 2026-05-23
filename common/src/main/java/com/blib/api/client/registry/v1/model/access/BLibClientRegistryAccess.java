package com.blib.api.client.registry.v1.model.access;

import com.just.core.functional.tuple.Tuple2;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blib.api.client.input.v1.model.KeyInteractType;
import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.client.render.v1.armor.AzArmorRenderer;
import com.blib.api.client.render.v1.item.AzItemRenderer;
import com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer;
import com.blib.api.client.render.v1.item.BLibGeoBoneItemRendererConfig;
import com.blib.internal.client.service.BLibInternalClientServices;

public class BLibClientRegistryAccess {

    private final BLibClientMod mod;

    @ApiStatus.Internal
    public BLibClientRegistryAccess(BLibClientMod mod) {
        this.mod = mod;
    }

    public void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<? extends Item>> itemSuppliers) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerArmorRenderer(mod, armorRendererSupplier, itemSuppliers);
    }

    public void registerArmorRendererImmediately(
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerArmorRendererImmediately(mod, armorRendererSupplier, itemSuppliers);
    }

    public <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerBlockEntityRenderer(mod, blockEntityTypeSupplier, renderProvider);
    }

    public void registerBlockRenderLayer(Supplier<? extends Block> blockSupplier, RenderType renderType) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerBlockRenderLayer(mod, blockSupplier, renderType);
    }

    public <E extends Entity> void registerEntityRenderer(
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerEntityRenderer(mod, entityTypeSupplier, entityRendererFactory);
    }

    public void registerItemColor(ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerItemColor(mod, itemColor, itemSuppliers);
    }

    public void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerItemRenderer(mod, itemSupplier, rendererFactory);
    }

    /**
     * Bind an item to a {@link BLibGeoBoneItemRenderer} whose config is loaded from
     * {@code assets/<ns>/blib/item_renderers/<configId>.json}. The asset's model id, texture, bone name, and idle /
     * blocking transforms drive rendering; resource-pack reloads (and inspector-driven edits saved into the per-project
     * resource pack) propagate without restarting the game.
     *
     * @param itemSupplier Deferred item lookup — invoked once on first render.
     * @param configId     Id of the JSON entry under {@code assets/<ns>/blib/item_renderers/}.
     * @param isBlocking   Per-frame predicate that decides when to apply the blocking transform set. Pass
     *                     {@code stack -> false} for items that never block.
     */
    public void registerGeoBoneItemRendererFromAsset(
        Supplier<? extends Item> itemSupplier,
        ResourceLocation configId,
        Predicate<ItemStack> isBlocking
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerItemRenderer(mod, itemSupplier, name -> () -> {
            var item = itemSupplier.get();
            var itemId = BuiltInRegistries.ITEM.getKey(item);

            return new BLibGeoBoneItemRenderer(BLibGeoBoneItemRendererConfig.fromAsset(itemId, configId, isBlocking));
        });
    }

    /**
     * Convenience overload that defaults the blocking predicate to
     * {@link BLibGeoBoneItemRendererConfig#defaultIsBlockingPredicate} — true when the local client player is actively
     * using the item stack. Correct for shield-style items; trophy-style items (no blocking transforms) never consult
     * the predicate so the default is harmless. One-call shape for both kinds.
     */
    public void registerGeoBoneItemRendererFromAsset(Supplier<? extends Item> itemSupplier, ResourceLocation configId) {
        registerGeoBoneItemRendererFromAsset(itemSupplier, configId, BLibGeoBoneItemRendererConfig::defaultIsBlockingPredicate);
    }

    public void registerItemRendererImmediately(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerItemRendererImmediately(mod, item, rendererFactory);
    }

    public Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> registerKeyMapping(
        ResourceLocation resourceLocation,
        String category,
        int key,
        Consumer<KeyInteractType> keyInteractTypeConsumer
    ) {
        return BLibInternalClientServices.CLIENT_REGISTRY.registerKeyMapping(mod, resourceLocation, category, key, keyInteractTypeConsumer);
    }

    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerMenuScreen(mod, menuTypeSupplier, screenConstructor);
    }

    public <T extends ParticleOptions> void registerParticleProviderFactory(
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    ) {
        BLibInternalClientServices.CLIENT_REGISTRY.registerParticleProviderFactory(mod, particleTypeSupplier, spriteParticleRegistration);
    }
}
