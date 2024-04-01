package org.avp.common.registry;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPBlockBindingRegistry extends AVPBindingRegistry<Block> {

    @Override
    protected GameObject<Block> registerGameObject(String name, Supplier<Block> supplier) {
        return Services.BLOCK_REGISTRY.register(name, supplier);
    }
}
