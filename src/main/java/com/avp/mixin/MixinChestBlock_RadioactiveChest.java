package com.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
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
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.item.AVPItemTags;
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
            if (blockEntity instanceof ChestBlockEntity chestBlockEntity && containsRadiationItems(chestBlockEntity)) {
                applyRadiationEffect(level, pos);
            }
        };
    }

    @Unique
    private static boolean containsRadiationItems(ChestBlockEntity chest) {
        for (var i = 0; i < chest.getContainerSize(); i++) {
            var stack = chest.getItem(i);
            if (!stack.isEmpty() && stack.is(AVPItemTags.RADIATION_ITEMS)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static void applyRadiationEffect(Level level, BlockPos pos) {
        var effectRadius = new AABB(pos).inflate(3);

        var nearbyEntities = level.getEntitiesOfClass(
            LivingEntity.class,
            effectRadius,
            entity -> entity.isAlive() && !entity.getType().is(AVPEntityTypeTags.RADIATION_RESISTANT)
        );

        for (var livingEntity : nearbyEntities) {
            var armorCheck = livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.FEET).is(AVPItemTags.RADIATION_RESISTANT_ARMOR);
            if (!armorCheck || !AVPPredicates.IS_IMMORTAL.test(livingEntity)) {
                livingEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0));
            }
        }
    }
}
