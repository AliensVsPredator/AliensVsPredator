package com.avp.common.block.entity.resin_node;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.level.gameevent.listener.ResinSpreadListener;

public class ResinNodeBlockEntity extends BlockEntity implements GameEventListener.Provider<ResinSpreadListener> {

    private final ResinSpreadListener resinSpreadListener;

    public ResinNodeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.RESIN_NODE, blockPos, blockState);

        var positionSource = new BlockPositionSource(blockPos);
        var spreaderType = new ResinSpreadListener.SpreaderType.Block(blockPos);

        this.resinSpreadListener = new ResinSpreadListener(positionSource, spreaderType);
    }

    public static void serverTick(Level level, BlockPos nodePos, BlockState blockState, ResinNodeBlockEntity resinNodeBlockEntity) {
        resinNodeBlockEntity.resinSpreadListener.getResinSpreader().updateCursors(level, nodePos, level.getRandom(), true);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        resinSpreadListener.getResinSpreader().load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        resinSpreadListener.getResinSpreader().save(compoundTag);
        super.saveAdditional(compoundTag, provider);
    }

    @Override
    public @NotNull ResinSpreadListener getListener() {
        return resinSpreadListener;
    }
}
