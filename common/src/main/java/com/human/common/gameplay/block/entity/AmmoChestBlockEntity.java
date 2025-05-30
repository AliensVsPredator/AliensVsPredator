package com.human.common.gameplay.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.AVPBlocks;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;

public class AmmoChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {

    private final ChestLidController chestLidController = new ChestLidController();

    private final ContainerOpenersCounter openersCounter;

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);

    public AmmoChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.AMMO_CHEST.get(), pos, blockState);

        this.openersCounter = new ContainerOpenersCounter() {

            @Override
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                level.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.CHEST_OPEN,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F
                );
            }

            @Override
            protected void onClose(Level level, BlockPos pos, BlockState state) {
                level.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.CHEST_CLOSE,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F
                );
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
                level.blockEvent(AmmoChestBlockEntity.this.worldPosition, AVPBlocks.AMMO_CHEST.get(), 1, openCount);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                }

                Container container = ((ChestMenu) player.containerMenu).getContainer();
                return container == AmmoChestBlockEntity.this;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.itemStacks, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.itemStacks, registries);
        }
    }

    @SuppressWarnings("unused")
    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, AmmoChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return chestLidController.getOpenness(partialTicks);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        }

        return super.triggerEvent(id, type);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.ammo_chest");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.itemStacks = items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (isValidForChest(stack)) {
            super.setItem(slot, stack);
            this.setChanged();
        } else {
            if (!stack.isEmpty() && this.level != null) {
                this.dropItem(stack);
            }
        }
    }

    private void dropItem(ItemStack stack) {
        if (this.level != null && !this.level.isClientSide) {
            this.level.addFreshEntity(
                new ItemEntity(
                    this.level,
                    this.worldPosition.getX() + 0.5,
                    this.worldPosition.getY() + 0.5,
                    this.worldPosition.getZ() + 0.5,
                    stack
                )
            );
        }
    }

    private boolean isValidForChest(ItemStack stack) {
        return stack.isEmpty() || stack.is(AVPItemTags.AMMO_ITEMS);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    public boolean hasAmmo() {
        return !this.itemStacks.isEmpty() && this.itemStacks.stream().anyMatch(item -> item.is(AVPItems.MEDIUM_BULLET.get()));
    }

    public boolean consumeAmmo(int count) {
        for (ItemStack itemStack : this.itemStacks) {
            if (itemStack.is(AVPItems.MEDIUM_BULLET.get())) {
                var available = itemStack.getCount();
                if (available >= count) {
                    itemStack.shrink(count);
                    this.setChanged();
                    return true;
                }
            }
        }
        return false;
    }
}
