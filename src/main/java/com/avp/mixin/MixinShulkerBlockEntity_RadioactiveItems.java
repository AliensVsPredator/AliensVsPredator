package com.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
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
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.item.AVPItemTags;
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
            !level.isClientSide && blockEntity instanceof ShulkerBoxBlockEntity chestBlockEntity && containsRadiationItems(chestBlockEntity)
        ) {
            applyRadiationEffect(level, pos);
        }
    }

    @Unique
    private static boolean containsRadiationItems(ShulkerBoxBlockEntity chest) {
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
