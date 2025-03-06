package com.avp.neoforge.client;

import com.avp.core.AVP;
import com.avp.core.client.AVPClient;
import com.avp.core.client.particle.AcidParticleProvider;
import com.avp.core.client.particle.BlueAcidParticleProvider;
import com.avp.core.client.render.entity.*;
import com.avp.core.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.avp.core.client.screen.ArmorCaseScreen;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.menu.MenuTypes;
import com.avp.core.common.particle.AVPParticleTypes;
import com.avp.core.platform.service.Services;
import com.avp.neoforge.platform.service.NeoForgeClientRegistryService;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVPNeoForgeClient {

    @SubscribeEvent
    public static void initialize(FMLClientSetupEvent event) {
        AVPClient.initialize();
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AVPEntityTypes.ACID.get(), AcidRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.CHESTBURSTER.get(), ChestbursterRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.DRONE.get(), DroneRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.FACEHUGGER.get(), FacehuggerRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.FLAMETHROW.get(), FlamethrowRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.OVAMORPH.get(), OvamorphRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.PRAETORIAN.get(), PraetorianRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.QUEEN.get(), QueenRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.WARRIOR.get(), WarriorRenderer::new);
        event.registerEntityRenderer(AVPEntityTypes.YAUTJA.get(), YautjaRenderer::new);
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        var pairs = ((NeoForgeClientRegistryService) Services.CLIENT_REGISTRY).getMenuTypeContainerScreenPairs();
        pairs.forEach(pair -> {
            var menuType = (MenuType<AbstractContainerMenu>) pair.getKey().get();
            var screenConstructor = (MenuScreens.ScreenConstructor<AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>>) pair.getValue();
            event.register(menuType, screenConstructor);
        });
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AVPParticleTypes.ACID.get(), AcidParticleProvider::new);
        event.registerSpriteSet(AVPParticleTypes.BLUE_ACID.get(), BlueAcidParticleProvider::new);
    }
}
