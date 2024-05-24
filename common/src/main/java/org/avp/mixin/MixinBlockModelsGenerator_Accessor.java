package org.avp.mixin;

import com.google.gson.JsonElement;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(BlockModelGenerators.class)
public interface MixinBlockModelsGenerator_Accessor {

    @Accessor
    Consumer<BlockStateGenerator> getBlockStateOutput();

    @Accessor
    BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelOutput();

    @Invoker("createSimpleFlatItemModel")
    void createSimpleFlatItemModel(Block block);

    @Invoker("createAxisAlignedPillarBlock")
    static BlockStateGenerator createAxisAlignedPillarBlock(Block block, ResourceLocation rl1) {
        throw new AssertionError();
    }

    @Invoker("createFence")
    static BlockStateGenerator createFence(Block block, ResourceLocation rl1, ResourceLocation rl2) {
        throw new AssertionError();
    }

    @Invoker("createFenceGate")
    static BlockStateGenerator createFenceGate(Block block, ResourceLocation rl1, ResourceLocation rl2, ResourceLocation rl3, ResourceLocation rl4, boolean flag) {
        throw new AssertionError();
    }

    @Invoker("createGrassLikeBlock")
    void createGrassLikeBlock(Block block, ResourceLocation resourceLocation, Variant variant);

    @Invoker("createRotatedVariant")
    static MultiVariantGenerator createRotatedVariant(Block block, ResourceLocation rl1) {
        throw new AssertionError();
    }

    @Invoker("createRotatedPillarWithHorizontalVariant")
    static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block block, ResourceLocation rl1, ResourceLocation rl2) {
        throw new AssertionError();
    }

    @Invoker("createSlab")
    static BlockStateGenerator createSlab(Block block, ResourceLocation rl1, ResourceLocation rl2, ResourceLocation rl3) {
        throw new AssertionError();
    }

    @Invoker("createStairs")
    static BlockStateGenerator createStairs(Block block, ResourceLocation rl1, ResourceLocation rl2, ResourceLocation rl3) {
        throw new AssertionError();
    }

    @Invoker("createWall")
    static BlockStateGenerator createWall(Block block, ResourceLocation rl1, ResourceLocation rl2, ResourceLocation rl3) {
        throw new AssertionError();
    }
}
