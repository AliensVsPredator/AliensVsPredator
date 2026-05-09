package com.blib.internal.mixin;

import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Exposes the {@code elements} field on {@link ListPoolElement} so the pool library can recurse into nested pool
 * elements when extracting template ids. Without this we'd see the {@code ListPoolElement} as opaque and miss every
 * template referenced through it.
 */
@Mixin(ListPoolElement.class)
public interface MixinListPoolElement_Accessor {

    @Accessor("elements")
    List<StructurePoolElement> blib$getElements();
}
