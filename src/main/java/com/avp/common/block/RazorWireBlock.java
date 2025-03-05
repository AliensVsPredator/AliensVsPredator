package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import com.avp.common.damage.AVPDamageTypes;

public class RazorWireBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(0.8, 0.0, 0.8, 15.2, 15.0, 15.2);

    private static final Vec3 MOVEMENT_MODIFIER = new Vec3(0.8F, 0.75, 0.8F);

    public RazorWireBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(
        BlockState blockState,
        BlockGetter blockGetter,
        BlockPos blockPos,
        CollisionContext collisionContext
    ) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        entity.makeStuckInBlock(blockState, MOVEMENT_MODIFIER);

        if (!level.isClientSide && (entity.xOld != entity.getX() || entity.yOld != entity.getY() || entity.zOld != entity.getZ())) {
            var deltaX = Math.abs(entity.getX() - entity.xOld);
            var deltaY = Math.abs(entity.getY() - entity.yOld);
            var deltaZ = Math.abs(entity.getZ() - entity.zOld);

            if (deltaX >= 0.003F || deltaY >= 0.003F || deltaZ >= 0.003F) {
                var registry = entity.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.RAZOR_WIRE));
                entity.hurt(damageSource, 4.0F);
            }
        }
    }
}
