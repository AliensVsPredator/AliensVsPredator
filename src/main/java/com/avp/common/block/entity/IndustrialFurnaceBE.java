package com.avp.common.block.entity;

import com.avp.common.block.entity.base.BaseTickingBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IndustrialFurnaceBE extends BaseTickingBE {

    public static Set<Item> fasterSmeltableItems = new HashSet<>();
    public static Set<Item> fasterSmeltableBlocks = new HashSet<>();

    private static int smeltTime = 0;
    private static final int BASE_SMELT_TIME = 200; // Vanilla Furnace time
    public static NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public IndustrialFurnaceBE(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.INDUSTRIAL_FURNACE_BE, blockPos, blockState);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        smeltTime = compoundTag.getInt("smeltTime");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putInt("smeltTime", smeltTime);
    }

    public void registerFasterSmeltables() {
        fasterSmeltableBlocks.add(Blocks.COBBLESTONE.asItem());
        fasterSmeltableBlocks.add(Blocks.COBBLED_DEEPSLATE.asItem());
        fasterSmeltableBlocks.add(Blocks.TERRACOTTA.asItem());
        fasterSmeltableBlocks.add(Blocks.QUARTZ_BLOCK.asItem());
        fasterSmeltableBlocks.add(Blocks.SANDSTONE.asItem());
        fasterSmeltableBlocks.add(Blocks.STONE.asItem());
        fasterSmeltableBlocks.add(Blocks.BASALT.asItem());
        fasterSmeltableBlocks.add(Blocks.RED_SANDSTONE.asItem());
        fasterSmeltableBlocks.add(Blocks.STONE_BRICKS.asItem());
        fasterSmeltableBlocks.add(Blocks.DEEPSLATE_BRICKS.asItem());
        fasterSmeltableBlocks.add(Blocks.DEEPSLATE_TILES.asItem());
        fasterSmeltableBlocks.add(Blocks.POLISHED_BLACKSTONE_BRICKS.asItem());
        fasterSmeltableBlocks.add(Blocks.NETHER_BRICKS.asItem());
        fasterSmeltableBlocks.add(Blocks.WET_SPONGE.asItem());
        fasterSmeltableBlocks.add(Blocks.SAND.asItem());
        fasterSmeltableBlocks.add(Blocks.RED_SAND.asItem());
        fasterSmeltableBlocks.add(Blocks.MUD.asItem());
        fasterSmeltableBlocks.add(Blocks.CLAY.asItem());
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t)
    {
        if (level.isClientSide) return;

        ItemStack input = items.get(0);
        ItemStack fuel = items.get(1);
        ItemStack output = items.get(2);

        if (!input.isEmpty()) {
            Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);

            if (recipeOpt.isPresent()) {
                SmeltingRecipe recipe = recipeOpt.get().value();
                ItemStack result = recipe.getResultItem(level.registryAccess());

                if (!result.isEmpty()) {
                    int smeltTimeTotal = BASE_SMELT_TIME / 2;
                    if (!fasterSmeltableBlocks.contains(input.getItem())) {
                        smeltTimeTotal = BASE_SMELT_TIME;
                    }

                    smeltTime++;

                    if (smeltTime >= smeltTimeTotal) {
                        input.shrink(1);
                        smeltTime = 0;

                        if (output.isEmpty()) {
                            items.set(2, result.copy());
                        } else if (output.is(result.getItem())) {
                            output.grow(result.getCount());
                        }
                    }
                }
            }
        } else {
            smeltTime = 0;
        }
    }
}
