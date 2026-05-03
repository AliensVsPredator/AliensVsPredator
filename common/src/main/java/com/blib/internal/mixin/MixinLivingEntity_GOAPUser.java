package com.blib.internal.mixin;

import com.just.ai.goap.graph.Graph;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.api.common.goap.v1.LivingEntityAgent;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GOAPUser extends Entity implements GOAPUser<LivingEntity> {

    @Unique
    private LivingEntityAgent<LivingEntity> blib$goapAgent;

    public MixinLivingEntity_GOAPUser(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        var self = LivingEntity.class.cast(this);
        this.blib$goapAgent = new LivingEntityAgent<>(self, this::blib$applyGOAPAgentProperties);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        if (level().isClientSide || blib$goapAgent == null) {
            return;
        }

        var graph = blib$getGOAPGraphOrNull();

        if (graph != null) {
            blib$goapAgent.update(graph);
        }
    }

    @Override
    public @Nullable LivingEntityAgent<LivingEntity> blib$getGOAPAgentOrNull() {
        return blib$goapAgent;
    }

    @Override
    public Graph<LivingEntity> blib$getGOAPGraphOrNull() {
        return null;
    }
}
