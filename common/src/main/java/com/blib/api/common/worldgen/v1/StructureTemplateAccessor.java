package com.blib.api.common.worldgen.v1;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

/**
 * Mixin-injected getter for {@link StructureTemplate}'s private {@code palettes} list, so editor / preview code can
 * iterate every block in a template without going through {@code placeInWorld} or {@code filterBlocks(... Block)}
 * (which only returns matches for one specific block type).
 */
public interface StructureTemplateAccessor {

    List<StructureTemplate.Palette> blib$getPalettes();
}
