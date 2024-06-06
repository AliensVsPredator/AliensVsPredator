package org.avp.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.avp.api.entity.GOAPBrainUser;
import org.avp.api.entity.ai.GOAPBrain;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MixinMob_PerformGOAPLogic extends LivingEntity {

    private GOAPBrain goapBrain;

    protected MixinMob_PerformGOAPLogic(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void tick(CallbackInfo callbackInfo) {
        var self = MixinUtils.<Mob>self(this);
        var level = self.level();

        if (level.isClientSide)
            return;
        if (!(self instanceof GOAPBrainUser goapBrainUser))
            return;

        if (goapBrain == null) {
            goapBrain = goapBrainUser.createGOAPBrain();
            goapBrain.init();
        }

        goapBrain.tick();
    }
}
