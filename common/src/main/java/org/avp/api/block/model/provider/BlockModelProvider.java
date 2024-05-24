package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import org.avp.api.block.model.render_type.BlockModelRenderType;

public abstract class BlockModelProvider {
    public abstract void run(BlockModelGenerators blockModelGenerators);

    public BlockModelRenderType getRenderType() {
        return BlockModelRenderType.NORMAL;
    }
}
