package org.avp.mixin.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.util.time.Tick;
import org.avp.common.registry.item.AVPArmorItemRegistry;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer_PressureSuitProvidesAir extends Player {

    protected MixinServerPlayer_PressureSuitProvidesAir(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = ServerPlayer.class.cast(this);

        if (
            self.tickCount % Tick.PER_SECOND == 0 &&
                self.getItemBySlot(EquipmentSlot.HEAD).is(AVPArmorItemRegistry.INSTANCE.pressure.helmet().get()) &&
                self.getItemBySlot(EquipmentSlot.CHEST).is(AVPArmorItemRegistry.INSTANCE.pressure.body().get()) &&
                self.getItemBySlot(EquipmentSlot.LEGS).is(AVPArmorItemRegistry.INSTANCE.pressure.leggings().get()) &&
                self.getItemBySlot(EquipmentSlot.FEET).is(AVPArmorItemRegistry.INSTANCE.pressure.boots().get())
        ) {
            self.setAirSupply(self.getMaxAirSupply());
        }
    }
}
