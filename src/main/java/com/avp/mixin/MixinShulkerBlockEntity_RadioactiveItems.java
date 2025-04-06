package com.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.effect.AVPEffects;
import com.avp.common.util.AVPPredicates;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class MixinShulkerBlockEntity_RadioactiveItems extends BlockEntity {

    public MixinShulkerBlockEntity_RadioactiveItems(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    private static <T extends BlockEntity> void injectGetTicker(
        Level level,
        BlockPos pos,
        BlockState state,
        ShulkerBoxBlockEntity blockEntity,
        CallbackInfo ci
    ) {
        if (
            !level.isClientSide && blockEntity instanceof ShulkerBoxBlockEntity chestBlockEntity && AVPPredicates.containsIrradiatedItems(
                chestBlockEntity
            )
        ) {
            applyRadiationEffect(level, pos);
        }
    }

    @Unique
    private static void applyRadiationEffect(Level level, BlockPos pos) {
        var effectRadius = new AABB(pos).inflate(3);

        level.getEntitiesOfClass(LivingEntity.class, effectRadius, AVPPredicates::canBeIrradiated)
            .forEach(target -> {
                var mobEffectInstance = new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0);
                target.addEffect(mobEffectInstance);
            });
    }
}
