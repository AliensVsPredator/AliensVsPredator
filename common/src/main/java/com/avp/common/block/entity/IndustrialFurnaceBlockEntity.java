package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.avp.common.recipe.AVPRecipes;
import com.avp.mixin.AbstractFurnaceBlockEntityInvoker;

public class IndustrialFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public IndustrialFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(AVPBlockEntityTypes.INDUSTRIAL_FURNACE.get(), blockPos, blockState, AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE.get());
    }

    @Override
    public int getBurnDuration(@NotNull ItemStack itemStack) {
        return super.getBurnDuration(itemStack) / 4;
    }

    public static int getTotalCookTime(Level level, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
        SingleRecipeInput singleRecipeInput = new SingleRecipeInput(abstractFurnaceBlockEntity.getItem(0));
        return abstractFurnaceBlockEntity.quickCheck.getRecipeFor(singleRecipeInput, level)
            .map(recipeHolder -> recipeHolder.value().getCookingTime() / 2)
            .orElse(200);
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

    public static void serverTick(
        @NotNull Level level,
        @NotNull BlockPos blockPos,
        @NotNull BlockState blockState,
        AbstractFurnaceBlockEntity abstractFurnaceBlockEntity
    ) {
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
                abstractFurnaceBlockEntity.litTime = ((AbstractFurnaceBlockEntityInvoker) abstractFurnaceBlockEntity).invokeGetBurnDuration(
                    itemStack
                );
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
            abstractFurnaceBlockEntity.cookingProgress = Mth.clamp(
                abstractFurnaceBlockEntity.cookingProgress - 2,
                0,
                abstractFurnaceBlockEntity.cookingTotalTime
            );
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

    public static boolean burn(
        @NotNull RegistryAccess registryAccess,
        RecipeHolder<?> recipe,
        @NotNull NonNullList<ItemStack> inventory,
        int maxStackSize
    ) {
        if (recipe != null && canBurn(registryAccess, recipe, inventory, maxStackSize)) {
            ItemStack itemstack = inventory.get(0);
            ItemStack itemstack1 = recipe.value().getResultItem(registryAccess);
            ItemStack itemstack2 = inventory.get(2);
            if (itemstack2.isEmpty()) {
                inventory.set(2, itemstack1.copy());
            } else if (ItemStack.isSameItemSameComponents(itemstack2, itemstack1)) {
                itemstack2.grow(1);
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !inventory.get(1).isEmpty() && inventory.get(1).is(Items.BUCKET)) {
                inventory.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    public static boolean canBurn(
        @NotNull RegistryAccess registryAccess,
        RecipeHolder<?> recipe,
        NonNullList<ItemStack> inventory,
        int maxStackSize
    ) {
        if (!inventory.get(0).isEmpty() && recipe != null) {
            ItemStack itemstack = recipe.value().getResultItem(registryAccess);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = inventory.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
                    return false;
                } else {
                    return itemstack1.getCount() < maxStackSize && itemstack1.getCount() < itemstack1.getMaxStackSize() || itemstack1
                        .getCount() < itemstack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("avp.industrialfurnace.displayName");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory) {
        return new BlastFurnaceMenu(i, inventory, this, this.dataAccess);
    }
}
