package com.avp.fabric;

import com.avp.core.AVP;
import com.avp.core.common.item.AVPEnchantmentTags;
import com.avp.core.common.item.GunItem;
import com.avp.core.platform.service.Services;
import com.avp.fabric.data.loot.LootTableModifier;
import com.avp.fabric.platform.service.FabricRegistryService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

public class AVPFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AVP.initialize();

        LootTableModifier.initialize();

        // TODO: Ensure that this works properly.
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) -> {
            if (!(target.getItem() instanceof GunItem)) {
                return TriState.DEFAULT;
            }

            return enchantment.is(AVPEnchantmentTags.GUN_ENCHANTMENTS) ? TriState.TRUE : TriState.FALSE;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ((FabricRegistryService) Services.REGISTRY).getCommands().forEach(dispatcher::register));
    }
}
