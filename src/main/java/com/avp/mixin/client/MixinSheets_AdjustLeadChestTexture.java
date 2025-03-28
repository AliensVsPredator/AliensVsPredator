package com.avp.mixin.client;

import com.avp.common.block.entity.AmmoChestBE;
import com.avp.common.block.entity.LeadChestBE;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public class MixinSheets_AdjustLeadChestTexture {

    @Shadow
    private static Material chestMaterial(String chestName) {return null;}

    @Unique
    private static final Material LEAD_CHEST = chestMaterial("lead_chest");

    @Unique
    private static final Material AMMO_CHEST = chestMaterial("ammo_chest");

    @Inject(method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;", at = @At("TAIL"), cancellable = true)
    private static void getChestTextureMixin(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<Material> info) {
        if (blockEntity instanceof LeadChestBE) {
            info.setReturnValue(LEAD_CHEST);
        }
        if (blockEntity instanceof AmmoChestBE) {
            info.setReturnValue(AMMO_CHEST);
        }
    }
}
