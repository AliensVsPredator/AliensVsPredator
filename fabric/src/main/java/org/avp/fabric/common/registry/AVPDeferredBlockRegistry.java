package org.avp.fabric.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public final class AVPDeferredBlockRegistry {

    private static final List<GameObject<Block>> entries = new ArrayList<>();

    public static void enqueue(GameObject<Block> object) {
        entries.add(object);
    }

    public static void registerAll() {
        entries.forEach(
            blockGameObject -> Registry.register(
                BuiltInRegistries.BLOCK,
                blockGameObject.getResourceLocation(),
                blockGameObject.get()
            )
        );
    }

    private AVPDeferredBlockRegistry() {
        throw new UnsupportedOperationException();
    }
}
