package com.blib.fabric.internal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import com.blib.azurelib.fabric.FabricAzureLibMod;
import com.blib.mod.BLib;

public class BLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricAzureLibMod.onInitialize();
        BLib.initialize();

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!BLib.checkBlockInteractProtection(level, player, hitResult.getBlockPos())) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!BLib.checkEntityInteractProtection(level, player, entity.blockPosition())) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (entity instanceof Player) {
                if (!BLib.checkPvpProtection(level, entity.blockPosition())) {
                    return InteractionResult.FAIL;
                }
            } else if (!(entity instanceof LivingEntity)) {
                if (!BLib.checkNonLivingEntityAttackProtection(level, player, entity.blockPosition())) {
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;
        });
    }
}
