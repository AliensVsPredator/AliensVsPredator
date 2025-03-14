package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class IndustrialFurnaceBE extends AbstractFurnaceBlockEntity {

    public IndustrialFurnaceBE(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.INDUSTRIAL_FURNACE_BE, blockPos, blockState, RecipeType.BLASTING);
    }

    @Override
    public int getBurnDuration(ItemStack itemStack) {
        return super.getBurnDuration(itemStack) / 4;
    }

    public static int getTotalCookTime(Level level, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
        SingleRecipeInput singleRecipeInput = new SingleRecipeInput(abstractFurnaceBlockEntity.getItem(0));
        return abstractFurnaceBlockEntity.quickCheck.getRecipeFor(singleRecipeInput, level).map(recipeHolder -> recipeHolder.value().getCookingTime() / 2).orElse(200);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        ItemStack itemStack2 = this.items.get(i);
        boolean bl = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack2, itemStack);
        this.items.set(i, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
        if (i == 0 && !bl) {
            this.cookingTotalTime = getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
        boolean bl = abstractFurnaceBlockEntity.isLit();
        boolean bl2 = false;
        if (abstractFurnaceBlockEntity.isLit()) {
            --abstractFurnaceBlockEntity.litTime;
        }

        ItemStack itemStack = abstractFurnaceBlockEntity.items.get(1);
        ItemStack itemStack2 = abstractFurnaceBlockEntity.items.get(0);
        boolean bl3 = !itemStack2.isEmpty();
        boolean bl4 = !itemStack.isEmpty();
        if (abstractFurnaceBlockEntity.isLit() || bl4 && bl3) {
            RecipeHolder<?> recipeHolder;
            if (bl3) {
                recipeHolder = abstractFurnaceBlockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(itemStack2), level).orElse(null);
            } else {
                recipeHolder = null;
            }

            int i = abstractFurnaceBlockEntity.getMaxStackSize();
            if (!abstractFurnaceBlockEntity.isLit() && canBurn(level.registryAccess(), recipeHolder, abstractFurnaceBlockEntity.items, i)) {
                abstractFurnaceBlockEntity.litTime = abstractFurnaceBlockEntity.getBurnDuration(itemStack);
                abstractFurnaceBlockEntity.litDuration = abstractFurnaceBlockEntity.litTime;
                if (abstractFurnaceBlockEntity.isLit()) {
                    bl2 = true;
                    if (bl4) {
                        Item item = itemStack.getItem();
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getCraftingRemainingItem();
                            abstractFurnaceBlockEntity.items.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }

            if (abstractFurnaceBlockEntity.isLit() && canBurn(level.registryAccess(), recipeHolder, abstractFurnaceBlockEntity.items, i)) {
                ++abstractFurnaceBlockEntity.cookingProgress;
                if (abstractFurnaceBlockEntity.cookingProgress == abstractFurnaceBlockEntity.cookingTotalTime) {
                    abstractFurnaceBlockEntity.cookingProgress = 0;
                    abstractFurnaceBlockEntity.cookingTotalTime = getTotalCookTime(level, abstractFurnaceBlockEntity);
                    if (burn(level.registryAccess(), recipeHolder, abstractFurnaceBlockEntity.items, i)) {
                        abstractFurnaceBlockEntity.setRecipeUsed(recipeHolder);
                    }

                    bl2 = true;
                }
            } else {
                abstractFurnaceBlockEntity.cookingProgress = 0;
            }
        } else if (!abstractFurnaceBlockEntity.isLit() && abstractFurnaceBlockEntity.cookingProgress > 0) {
            abstractFurnaceBlockEntity.cookingProgress = Mth.clamp(abstractFurnaceBlockEntity.cookingProgress - 2, 0, abstractFurnaceBlockEntity.cookingTotalTime);
        }

        if (bl != abstractFurnaceBlockEntity.isLit()) {
            bl2 = true;
            blockState = blockState.setValue(AbstractFurnaceBlock.LIT, abstractFurnaceBlockEntity.isLit());
            level.setBlock(blockPos, blockState, 3);
        }

        if (bl2) {
            setChanged(level, blockPos, blockState);
        }

    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("avp.industrialfurnace.displayName");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new BlastFurnaceMenu(i, inventory, this, this.dataAccess);
    }
}
