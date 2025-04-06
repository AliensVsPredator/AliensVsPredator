package com.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.effect.AVPEffects;
import com.avp.common.util.AVPPredicates;

@Mixin(ChestBlock.class)
public abstract class MixinChestBlock_RadioactiveChest {

    @Inject(method = "getTicker", at = @At("RETURN"), cancellable = true)
    private <T extends BlockEntity> void injectGetTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> blockEntityType,
        CallbackInfoReturnable<BlockEntityTicker<T>> cir
    ) {
        if (!level.isClientSide) {
            cir.setReturnValue(createServerTicker(blockEntityType));
        }
    }

    @Unique
    private <T extends BlockEntity> BlockEntityTicker<T> createServerTicker(BlockEntityType<T> blockEntityType) {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof ChestBlockEntity chestBlockEntity && AVPPredicates.containsIrradiatedItems(chestBlockEntity)) {
                applyRadiationEffect(level, pos);
            }
        };
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
