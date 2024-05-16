package org.avp.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerPressureSuitProvidesAir extends Player {

    protected MixinServerPlayerPressureSuitProvidesAir(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = MixinUtils.<ServerPlayer>self(this);

        if (
            self.tickCount % 20 == 0 &&
            self.getItemBySlot(EquipmentSlot.HEAD).is(AVPArmorItems.INSTANCE.pressureHelmet.get()) &&
            self.getItemBySlot(EquipmentSlot.CHEST).is(AVPArmorItems.INSTANCE.pressureBody.get()) &&
            self.getItemBySlot(EquipmentSlot.LEGS).is(AVPArmorItems.INSTANCE.pressureLeggings.get()) &&
            self.getItemBySlot(EquipmentSlot.FEET).is(AVPArmorItems.INSTANCE.pressureBoots.get())
        ) {
            self.setAirSupply(self.getMaxAirSupply());
        }
    }
}
