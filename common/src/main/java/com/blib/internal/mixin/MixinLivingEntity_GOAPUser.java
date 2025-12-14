package com.blib.internal.mixin;

import com.blib.common.gameplay.goap.GOAPUser;
import com.blib.common.gameplay.goap.LivingEntityAgent;
import com.just.goap.graph.Graph;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GOAPUser extends Entity implements GOAPUser<LivingEntity> {

    @Unique
    private LivingEntityAgent<LivingEntity> agent;

    public MixinLivingEntity_GOAPUser(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.agent = new LivingEntityAgent<>();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        if (level().isClientSide || agent == null) {
            return;
        }

        var graph = getCurrentGraph();

        if (graph != null) {
            var self = LivingEntity.class.cast(this);
            agent.update(graph, self);
        }
    }

    @Override
    public Graph<LivingEntity> getCurrentGraph() {
        return null;
    }
}
