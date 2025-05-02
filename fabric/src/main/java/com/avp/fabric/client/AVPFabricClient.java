package com.avp.fabric.client;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.AVPClient;
import com.avp.client.render.item.SimpleItemRenderer;
import com.avp.fabric.client.input.keybind.AVPKeybindingRegistry;
import com.avp.fabric.client.network.AVPClientPacketHandlerRegistry;
import com.avp.fabric.client.particle.AcidParticleProvider;
import com.avp.fabric.client.particle.BlueAcidParticleProvider;
import com.avp.fabric.client.particle.IrradiatedAcidParticleProvider;
import com.avp.fabric.client.render.entity.EmptyRenderer;
import com.avp.fabric.client.render.item.F903weItemRenderer;
import com.avp.fabric.client.render.item.FlameThrowerItemRenderer;
import com.avp.fabric.client.render.item.M3712ShotgunItemRenderer;
import com.avp.fabric.client.render.item.M41APulseRifleItemRenderer;
import com.avp.fabric.client.render.item.M42a3SniperRifleItemRenderer;
import com.avp.fabric.client.render.item.M4raBattleRifileItemRenderer;
import com.avp.fabric.client.render.item.M56SmartgunItemRenderer;
import com.avp.fabric.client.render.item.M6BRLItemRenderer;
import com.avp.fabric.client.render.item.M88Mod4CombatPistolItemRenderer;
import com.avp.fabric.client.render.item.OldPainlessItemRenderer;
import com.avp.fabric.client.render.item.ZX76ShotgunItemRenderer;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.particle.AVPParticleTypes;

public class AVPFabricClient implements ClientModInitializer {

    private static final Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        // Items
        registerItemRenderer(AVPItems.F903WE_RIFLE, name -> () -> new F903weItemRenderer(name));
        registerItemRenderer(AVPItems.FLAMETHROWER_SEVASTOPOL, name -> () -> new FlameThrowerItemRenderer(name));
        registerItemRenderer(AVPItems.M37_12_SHOTGUN, name -> () -> new M3712ShotgunItemRenderer(name));
        registerItemRenderer(AVPItems.M41A_PULSE_RIFLE, name -> () -> new M41APulseRifleItemRenderer(name));
        registerItemRenderer(AVPItems.M42A3_SNIPER_RIFLE, name -> () -> new M42a3SniperRifleItemRenderer(name));
        registerItemRenderer(AVPItems.M4RA_BATTLE_RIFLE, name -> () -> new M4raBattleRifileItemRenderer(name));
        registerItemRenderer(AVPItems.M56_SMARTGUN, name -> () -> new M56SmartgunItemRenderer(name));
        registerItemRenderer(AVPItems.M6B_ROCKET_LAUNCHER, name -> () -> new M6BRLItemRenderer(name));
        registerItemRenderer(AVPItems.M88MOD4_COMBAT_PISTOL, name -> () -> new M88Mod4CombatPistolItemRenderer(name));
        registerItemRenderer(AVPItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        registerItemRenderer(AVPItems.ZX_76_SHOTGUN, name -> () -> new ZX76ShotgunItemRenderer(name));

        // Entities

        // Block-like entities (like primed TNT)

        EntityRendererRegistry.register(AVPEntityTypes.BULLET, EmptyRenderer::new);

        // Particles
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.ACID, AcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.IRRADIATED_ACID, IrradiatedAcidParticleProvider::new);

        // Keybindings
        AVPKeybindingRegistry.initialize();

        // Networking
        AVPClientPacketHandlerRegistry.initialize();
    }

    private void registerItemRenderer(Item item) {
        registerItemRenderer(item, ITEM_RENDERER_SUPPLIER_FACTORY);
    }

    private void registerItemRenderer(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }
}
