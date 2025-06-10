package com.avp.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.model.Crawler;

@Mixin(Player.class)
public abstract class MixinPlayer_CrawlPosing extends LivingEntity implements Crawler {

    @Unique
    private boolean isCrawling;

    protected MixinPlayer_CrawlPosing(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void updatePlayerPose(CallbackInfo ci) {
        if (isCrawling) {
            this.setPose(Pose.SWIMMING);
            ci.cancel();
        }
    }

    @Override
    public boolean isCrawling() {
        return isCrawling;
    }

    @Override
    public void setCrawling(boolean isCrawling) {
        this.isCrawling = isCrawling;
    }
}
