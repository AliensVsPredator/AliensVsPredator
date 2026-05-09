package com.blib.internal.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the {@code template} field on {@link SinglePoolElement} so the engine's pool library can extract template ids
 * from pool elements without going through {@code getDataMarkers} / {@code getShuffledJigsawBlocks} (which require a
 * {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager} and resolve the
 * template, defeating the point of "what does this pool reference").
 */
@Mixin(SinglePoolElement.class)
public interface MixinSinglePoolElement_Accessor {

    @Accessor("template")
    Either<ResourceLocation, StructureTemplate> blib$getTemplate();
}
