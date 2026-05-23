package com.blib.internal.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

@Mixin(StructureTemplate.class)
public interface MixinStructureTemplate_Accessor extends StructureTemplateAccessor {

    @Accessor("palettes")
    @Override
    List<StructureTemplate.Palette> blib$getPalettes();
}
