package com.avp.common.block;

import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.block.entity.IndustrialFurnaceBE;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IndustrialFurnaceBlock extends AbstractFurnaceBlock {


    public IndustrialFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return simpleCodec(IndustrialFurnaceBlock::new);
    }

    @Override
    protected void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity instanceof IndustrialFurnaceBE be)
        {
            be.registerFasterSmeltables();
        }
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);
        if(level.getBlockEntity(blockPos) instanceof IndustrialFurnaceBE be)
        {
            Containers.dropContents(level,blockPos, IndustrialFurnaceBE.items);
        }
    }

    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        ((ServerPlayer) player).openMenu(new MenuProvider(){

            @Override
            public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new FurnaceMenu(i,inventory);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("avp.industrialfurnace.displayName"); // Place Holder
            }
        });
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IndustrialFurnaceBE(blockPos,blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockEntityTypes.INDUSTRIAL_FURNACE_BE
                ? (BlockEntityTicker<T>) IndustrialFurnaceBE::tick
                : null;
    }

}
