package org.avp.api.common.data.block;

import net.minecraft.world.level.block.Block;

public sealed interface BlockModelDataType {

    enum GenType {
        CUBE,
        FENCE,
        FENCE_GATE,
        GRASS_LIKE,
        MULTI_FACE,
        ROTATED_PILLAR,
        ROTATED_VARIANT,
        SLAB,
        STAIR,
        WALL,
        WOOD
    }

    GenType getGenType();

    record Cube(Block block) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.CUBE;
        }
    }

    record Fence(
        Block baseBlock,
        Block fenceBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.FENCE;
        }
    }

    record FenceGate(
        Block baseBlock,
        Block fenceGateBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.FENCE_GATE;
        }
    }

    record GrassLike(
        Block dirtBlock,
        Block grassBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.GRASS_LIKE;
        }
    }

    record MultiFace(Block baseBlock) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.MULTI_FACE;
        }
    }

    record RotatedPillar(Block block) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.ROTATED_PILLAR;
        }
    }

    record RotatedVariant(Block block) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.ROTATED_VARIANT;
        }
    }

    record Slab(
        Block baseBlock,
        Block slabBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.SLAB;
        }
    }

    record Stair(
        Block baseBlock,
        Block stairBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.STAIR;
        }
    }

    record Wall(
        Block baseBlock,
        Block wallBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.WALL;
        }
    }

    record Wood(
        Block baseBlock,
        Block woodBlock
    ) implements BlockModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.WOOD;
        }
    }
}
