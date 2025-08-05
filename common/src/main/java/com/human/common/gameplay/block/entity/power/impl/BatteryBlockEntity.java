package com.human.common.gameplay.block.entity.power.impl;

import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class BatteryBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerStore {

    private static final String NBT_STORED_POWER = "storedPower";

    private long storedPower;

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(AVPBlockEntityTypes.BATTERY.get(), pos, state);
    }

    @Override
    public long getRequestedPower() {
        // Batteries do not request any power.
        return 0;
    }

    @Override
    public long receivePower(long maxAmount) {
        // Received power is stored inside the battery.
        this.storedPower += maxAmount;
        return 0;
    }

    @Override
    public long getAvailablePower() {
        // The available power offered by the battery is its stored power.
        return storedPower;
    }

    @Override
    public long extractPower(long maxAmount) {
        // Power extracted from the battery cannot exceed its stored power.
        var amountExtracted = Math.min(storedPower, maxAmount);
        this.storedPower -= amountExtracted;
        return amountExtracted;
    }

    public long getStoredPower() {
        return storedPower;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong(NBT_STORED_POWER, storedPower);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.storedPower = tag.getLong(NBT_STORED_POWER);
    }
}
